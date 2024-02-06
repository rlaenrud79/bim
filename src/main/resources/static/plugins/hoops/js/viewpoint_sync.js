function syncViewer(){
    this._viewer;
    this._socket;
    this._sharedCamera;
    this._isSharedCamera = false;
    this._classRedlineText = 'Communicator.Markup.Redline.RedlineText';
}

syncViewer.prototype.start = function(){
    var _this = this;
    _this._createViewer();
    _this._initEvent();
}

syncViewer.prototype._createViewer = function(){
    var _this = this;
    _this._viewer = hwv;
    _this._socket = io("ws://" + location.hostname + ":23100"); // 내부:3100, 외부:23100

    _this._viewer.setCallbacks({
        camera: cameraFunc,
        selection: selectionChanged,
        viewCreated: viewCreated,
        redlineCreated: newRedlineCallback,
        redlineUpdated: redlineUpdated
    });

    //setup sockets
    _this._socket.on('userConnectChange', function (numUsersConnected, sharedCamera) {
        if( !!! isCoWorkPage() ) return;
        console.log('User count: ' + numUsersConnected);
    });

    _this._socket.on('cameraChange', function (coWorkId, camera) {
        if( !!! isCoWorkPage() || coWorkId != getCoWorkId() ) return;
        var camObj = JSON.parse(camera);
        _this._sharedCamera = Communicator.Camera.construct(camObj);

        if (_this._isSharedCamera) {
            _this._viewer.unsetCallbacks({ camera: cameraFunc });
            _this._viewer.getView().setCamera(_this._sharedCamera);
            _this._viewer.setCallbacks({ camera: cameraFunc });
        }
    });

    _this._socket.on('selectionChange', function (coWorkId, selection) {
        if( !!! isCoWorkPage() || coWorkId != getCoWorkId() ) return;
        _this._viewer.unsetCallbacks({ selection: selectionChanged });
        if (selection._nodeId != null) {
            _this._viewer.getSelectionManager().selectNode(selection._nodeId);
        }
        else {
            _this._viewer.getSelectionManager().clear();
        }
        _this._viewer.setCallbacks({ selection: selectionChanged });
    });

    _this._socket.on('viewCreated', function (coWorkId, markupView) {
        if( !!! isCoWorkPage() || coWorkId != getCoWorkId() ) return;
        // [TODO] 초기에 RedlineText를 그렸을 때 동기화하지 않게 하기
        if (!_this._isSharedCamera)
            return;

        var markupViewObj = JSON.parse(markupView);
        _this._viewer.unsetCallbacks({ viewCreated: viewCreated });
        _this._viewer.markupManager.loadMarkupData({ views: [markupViewObj] }).then(function (ret) {
            _this._viewer.markupManager.activateMarkupView(markupViewObj.uniqueId);
            _this._viewer.markupManager.refreshMarkup();
            _this._viewer.setCallbacks({ viewCreated: viewCreated });
        });
    });

    _this._socket.on('handleRedline', function (coWorkId, redlineItem, className, updateExisting) {
        if( !!! isCoWorkPage() || coWorkId != getCoWorkId() ) return;
        if (!_this._isSharedCamera || className === _this._classRedlineText){ return; }

        var markupObj = JSON.parse(redlineItem);
        _this._viewer.unsetCallbacks({ redlineCreated: newRedlineCallback, });

        var markupClass = ClassForString(className);
        if (markupClass) {
            var newMarkup = markupClass.construct(markupObj, _this._viewer);
            if (newMarkup) {
                if (updateExisting) {
                    let ind = -1;
                    let markupItems = _this._viewer.markupManager.getActiveMarkupView().getMarkup();
                    for (let i = 0; i < markupItems.length; i++) {
                        // Access private variable directly cause markup serialization is buggy AF
                        if (markupItems[i]._uniqueId === newMarkup._uniqueId) {
                            ind = i;
                            break;
                        }
                    }
                    _this._viewer.markupManager.getActiveMarkupView().removeMarkup(markupItems[ind]);
                }
                _this._viewer.markupManager.getActiveMarkupView().addMarkupItem(newMarkup);
                _this._viewer.markupManager.refreshMarkup();
            }
        }
        _this._viewer.setCallbacks({ redlineCreated: newRedlineCallback, });
    });

    function ClassForString(className) {// helper function for creating redline programaticlly
        var arr = className.split(".");
        var fn = window || this;
        for (var i = 0, len = arr.length; i < len; i++) {
            fn = fn[arr[i]];
        }

        if (typeof fn !== "function") {
            return null;
        }
        return fn;
    }

    function cameraFunc(camera) {
        if (_this._isSharedCamera) {
            _this._socket.emit('cameraChange', getCoWorkId(), JSON.stringify(camera.forJson()));
            _this._sharedCamera = camera;
        }
    }

    function selectionChanged(selection) {
        _this._socket.emit('selectionChange', getCoWorkId(), selection.getSelection());
    }

    function viewCreated(view) {
        if (_this._isSharedCamera) {
            if (view.getMarkup().length >= 1) {
                //if ( view.getMarkup().length == 1 && view.getMarkup()[0].getClassName() === _this._classRedlineText){
                //    // [TODO] view에서 RedlineText View만 제거
                //}
                _this._socket.emit('viewCreated', getCoWorkId(), JSON.stringify(view.forJson()));
            }
        }
    }

    function newRedlineCallback(redlineItem) {
        var className = redlineItem.getClassName();
        if (_this._isSharedCamera && className !== _this._classRedlineText) {
            var view = _this._viewer.markupManager.getActiveMarkupView();
            if (view.getMarkup().length > 1) {
                _this._socket.emit('handleRedline', getCoWorkId(), JSON.stringify(redlineItem.toJson()), className, false);
            }
        }
    }

    function redlineUpdated(redlineItem) {
        var className = redlineItem.getClassName();
        if (_this._isSharedCamera && className !== _this._classRedlineText) {
            _this._socket.emit('handleRedline', getCoWorkId(), JSON.stringify(redlineItem.toJson()), className, true);
        }
    }

    function disableRedlineTracking(view) {
        var basefunc = Communicator.Markup.MarkupView.prototype.addMarkupItem;
        view.addMarkupItem = function (markupItem) {
            basefunc.call(view, markupItem);
        }
    }

}

syncViewer.prototype._initEvent = function(){
    if( !!! isCoWorkPage() ) {
        $("#btnSyncModel").parent("div").parent("div").parent("div").hide();
        return;
    }else {
        $("#btnSyncModel").parent("div").parent("div").parent("div").show();
    }

    var _this = this;
    $("#btnSyncModel").on("click",function(){
        var wasOn = $(this).data("on"); // default is false
        if (wasOn) { // 비동기화 시작
            _this._isSharedCamera = false;
            $(this).data("on", false);
            $(this).removeClass("on");
        }
        else { // 동기화 시작
            _this._sharedCamera = hwv.view.getCamera().forJson();
            _this._isSharedCamera = true;
            $(this).data("on", true);
            $(this).addClass("on");
        }
    });
}

let isCoWorkPage = function(){
    return location.href.includes("/coWork/");
}

let getCoWorkId = function(){
    let splitUrl = location.href.split('/');  // ex) http://localhost:8080/coWork/modelingView/73
    let rtn = splitUrl[splitUrl.length -1];
    if ( rtn == undefined){
        rtn = "";
    }
    return rtn;
}
