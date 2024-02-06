(function ($) {
    var o = {

        _this: $(this),

        init: function () {
            $(document).on("click", '.openUpdateModal', function () {
                location.href = "/coWork/freeboardView?freeboardId=" + freeboardId + "&" + o.setSendData(o.pageNo, $("#pageSize").val());
            });

            $(document).on("click", ".download-attach-file", function (e) {
                e.preventDefault();
                executeFileDownloadModal($(this).data("file-id"), "FREEBOARD_FILE");
            });

            $(document).on("click", "#btnDeleteFreegoard", function () {
                if (confirm(confirmDeleteFreeboard)) {
                    let id = $(this).data('freeboard-id');
                    o.deleteFreeboard(id);
                }
            });

            $(document).on("click", ".btn-del-sel", function () {
                if ($(".list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm(confirmSelDeleteFreeboard)) {
                        o.deleteSelFreeboard();
                    }
                } else {
                    alert(confirmSelectDeleteFreeboard);
                }
            });

            $(document).on("click", ".itemCheck", function () {
                const menuName = $(this).attr("data-name");
                $("." + menuName).prop("checked", ($(this).prop("checked")));
            });

            $.nfx.on("click", "#btnSearchFreeboard", function () {
                o.reloadCardBody();
            });

            $.nfx.on("click", "#btnResetSearchCondition", function () {
                o.resetSearchCondition();
                o.reloadCardBody();
            });

            $.nfx.on("click", "#searchUserDisplayName, #btnSearchUser", function () {
                let param = "formElementIdForUserId=searchUserId";
                param += "&formElementIdForUserName=searchUserDisplayName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                selectPopupUser(param);
            });

            $(document).on("click", "#pageSize", function () {
                o.reloadCardBody();
            });

            $(document).on("click", '#btnPagePrevious, a[id^=\'btnPageNo_\'], #btnPageNext', function () {
                $("#pageNo").val($(this).data("page-no"));
                o.reloadCardBody();
            });
        },

        reloadCardBody: function () {
            reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
        },

        resetSearchCondition: function() {
            $("#searchWorkId").val(0);
            $("#searchType").val('none');
            $("#searchValue").val('');
            $("#searchUserId").val(0);
            $("#searchUserDisplayName").val('');
            $("#startDate").val('');
            $("#endDate").val('');
            $("#sortProp").val('');
            $("#pageNo").val(0);
            $("#pageSize").val(20);
            $("#searchCategory").val('');
        },

        getSearchCondition: function () {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchWorkId=${parseInt($("#searchWorkId").val())}`;
            param += `&searchType=${$("#searchType").val()}`;
            param += `&searchValue=${$("#searchValue").val()}`;
            param += `&searchUserId=${parseInt($("#searchUserId").val()) || 0}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`;
            param += `&startDateString=${$("#startDate").val()}`;
            param += `&endDateString=${$("#endDate").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            param += `&searchCategory=${parseInt($("#searchCategoryId").val())}`;
            return param;
        },

        deleteFreeboard: function(id) {
            reqDelete(`/coWorkApi/deleteFreeboard?id=${id}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        //reloadComponent($.nfx.listCardBody, "#divCardBody", "");
                        //o.resetSearchCondition();
                        o.reloadCardBody();
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            )
        },

        deleteSelFreeboard: function() {
            reqDeleteJSON(`/coWorkApi/deleteSelFreeboard`, o.setSendDeleteData(),
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        //o.resetSearchCondition();
                        o.reloadCardBody();
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        setSendDeleteData: function() {
            let val = [];
            $(".list-item-checkbox:checkbox:checked").each(function () {
                val.push({
                    id: $(this).val() + "",
                });
            });

            return JSON.stringify(val);
        }
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/coWork/freeboardListCardBody',
        });

        o.init();
    });

})(jQuery);