(function ($) {
    var o = {

        PageFunction: {},
        _this: $(this),
        modelIds: [],

        init: function () {
            $.nfx.click('#btnAdd', function () {
                const params = {};

                const callback = function (data) {
                    $('#modalUploadFile').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, '/contentsModal/addModeling');
            });

            $.nfx.click("a[id^='btnDownloadIfcFile_']", function () {
                executeFileDownloadModal($(this).attr("data-id"), "MODELING_IFC_FILE");
            });

            $.nfx.click("a[id^='btnDownloadModelFile']", function () {
                executeFileDownloadModal($(this).attr("data-id"), "MODELING_FILE");
            });

            // 사용자 검색 모달창 열기
            $.nfx.click('#writerName, #btnSearchUser', function () {
                let param = "formElementIdForUserId=writerId";
                param += "&formElementIdForUserName=writerName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                selectPopupUser(param);
            });

            $.nfx.click("#btnSearch", function () {
                o.searchModelingList();
            });

            $.nfx.click("#btnInit", function () {
                o.initSearchCondition();
                o.searchModelingList();
            });

            $.nfx.click("#btnView", function () {
                o.modelIds = o.getViewModelingIds();

                if(o.modelIds === '') {
                    alert(noSelectBimModel);
                    return false;
                }

                window.open("/contents/modelingView?modelIds=" + o.modelIds);
            });

            $.nfx.click("#btnCoWork", function () {
                o.modelIds = o.getViewModelingIds();

                if (o.modelIds === '') {
                    alert(noSelectBimModel);
                    return false;
                }

                if (confirm(confirmAddCoWork)) {
                    window.open("/coWork/modelingView?modelIds=" + o.modelIds);
                }
            });

            $(document).on("click", 'a[id^=\'btnDelete_\']', function (e) {
                $("#tmpModelingId").val($(this).attr("data-id"));
                if (confirm(confirmAddCompanyRole)) {
                    o.deleteModelingItem();
                }
            });

            $.nfx.on('change', '#allCheck', function (e) {
                executeAllCheck($(this), this, ".itemCheck");
            });

            $(document).on("click", "a[id^='btnDownloadIfcFile_']", function (e) {
                executeFileDownloadModal($(this).attr("data-id"), "MODELING_IFC_FILE");
            });

            $(document).on("click", "a[id^='btnDownloadModelFile']", function (e) {
                executeFileDownloadModal($(this).attr("data-id"), "MODELING_FILE");
            });
        },

        modalShowAndDraggable: function (obj) {
            $(obj).modal('show');
            $(obj).draggable({'cancel': '.modal-body'});
        },

        setSendData: function() {
            let param = "";

            //if ($("#searchLatest").is(":checked")) param += "searchLatest=true";
            //else param += "searchLatest=false";

            param += "searchLatest=true";
            param += "&workId=" + $("#workId").val();
            param += "&fileName=" + $("#fileName").val();
            param += "&writerName=" + $("#writerName").val();
            param += "&writerId=" + $("#writerId").val();
            param += "&writeDateFrom=" + $("#writeDateFrom").val();
            param += "&writeDateEnd=" + $("#writeDateEnd").val();
            return param;
        },

        searchModelingList: function() {
            reloadComponent("/contents/modelingListCardBody", "#divCardBody", o.setSendData());
        },

        initSearchCondition: function() {
            //$("#searchLatest").attr("checked", true);
            $("#workId").val("0");
            $("#fileName").val("");
            $("#writerName").val("");
            $("#writerId").val("0");
            $("#writeDateFrom").val("");
            $("#writeDateEnd").val("");
        },

        sortModelIds: function(modelIds) {
            modelIds.sort(function (a, b) {
                if (a > b) return 1;
                if (a === b) return 0;
                if (a < b) return -1;
            });
        },

        makeModelingIdsArray: function(modelIds) {
            $("input.itemCheck").each(function () {
                if (this.checked) modelIds.push(parseInt(this.value));
            });
        },

        getViewModelingIds: function() {
            o.makeModelingIdsArray(o.modelIds);
            o.sortModelIds(o.modelIds);

            return o.modelIds.toString();
        },

        deleteModelingItem: function() {
            reqPost('/contentsApi/putModelingInfoDelete'
                , { "id": $("#tmpModelingId").val() }
                ,  function (data) {
                    if (data.result) {
                        reloadComponent("/contents/modelingListCardBody", "#divCardBody", o.setSendData());
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/contents/modelingListCardBody',
        });

        o.init();
    });

})(jQuery);