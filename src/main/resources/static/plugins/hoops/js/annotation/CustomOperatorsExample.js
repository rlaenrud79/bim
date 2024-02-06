/// <reference path="../../../typescript/hoops_web_viewer.d.ts"/>
var UserViewer;
(function (UserViewer) {
    var AnnotationExample = /** @class */ (function () {
        function AnnotationExample() {
            this._operators = {};
        }
        AnnotationExample.prototype.start = function (viewer/*viewerOptions*/) {
            var _this = this;
            _this._viewer = viewer;
            if(_this._viewer){
                _this._annotationRegistry = new UserViewer.AnnotationRegistry(_this._viewer);
                _this._initEvents();
                _this._initOperators();
            }
            /* monad
            return Example.createViewer(viewerOptions).then(function (viewer) {
                _this._viewer = viewer;
                _this._annotationRegistry = new Example.AnnotationRegistry(_this._viewer);
                _this._initEvents();
                _this._initOperators();
                _this._viewer.start();
            });
            */
        };
        AnnotationExample.prototype._initEvents = function () {
            var _this = this;
            this._viewer.setCallbacks({
                sceneReady: function () {
                    _this._viewer.view.setBackfacesVisible(true);
                },
            });
        };
        AnnotationExample.prototype._initOperators = function () {
            var annotationOperator = new UserViewer.AnnotationOperator(this._viewer, this._annotationRegistry);
			var operatorId = this._viewer.registerCustomOperator(annotationOperator);
			this._viewer.operatorManager.set(operatorId, 1);
        };
        return AnnotationExample;
    }());
    UserViewer.AnnotationExample = AnnotationExample;
})(UserViewer || (UserViewer = {}));
