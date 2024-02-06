(function ($) {
    var o = {

        _this: $(this),

        init: function () {

            $(document).on("click", "#btnDeleteBulletin", function () {
                if (confirm(confirmDeleteBulletin)) {
                    let id = $(this).data('bulletin-id');
                    o.deleteBulletinMain(id);
                }
            });

            $(document).on("click", ".btn-del-sel", function () {
                if ($(".list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm(confirmSelDeleteFreeboard)) {
                        o.deleteSelBulletin();
                    }
                } else {
                    alert(confirmSelectDeleteFreeboard);
                }
            });

            $(document).on("click", ".itemCheck", function () {
                const menuName = $(this).attr("data-name");
                $("." + menuName).prop("checked", ($(this).prop("checked")));
            });

            $.nfx.on("click", "#btnSearchBulletin", function () {
                reloadComponent("/coWork/bulletinListCardBody", "#divCardBody", o.getSearchCondition());
            });

            $.nfx.on("click", "#btnResetSearchCondition", function () {
                o.resetSearchCondition();
                reloadComponent("/coWork/bulletinListCardBody", "#divCardBody", o.getSearchCondition());
            });

            $.nfx.on("click", "#searchUserDisplayName, #btnSearchUser", function () {
                let param = "formElementIdForUserId=searchUserId";
                param += "&formElementIdForUserName=searchUserDisplayName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                const params = {};
                const callback = function (data) {
                    $('#modalSearchSingleUser').find('.popup-con').html(data);
                    $('#modalSearchSingleUser').show();
                };

                $.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
            });

            $(document).on("click", "#pageSize", function () {
                $("#pageNo").val(0);
                reloadComponent("/coWork/bulletinListCardBody", "#divCardBody", o.getSearchCondition())
            });

            $(document).on("click", '#btnPagePrevious, a[id^=\'btnPageNo_\'], #btnPageNext', function () {
                $("#pageNo").val($(this).data("page-no"));
                reloadComponent("/coWork/bulletinListCardBody", "#divCardBody", o.getSearchCondition())
            });

            $.nfx.click("#mBtnAdd", function () {
                if ($("#title").val() == "") {
                    alert(confirmErrorNoTitle);
                    return;
                }
                if ($("#contents").val() == "") {
                    alert(confirmErrorNoContents);
                    return;
                }

                o.addBulletin();
            });

            $.nfx.click("#mBtnUpdate", function () {
                if ($("#title").val() == "") {
                    alert(confirmErrorNoTitle);
                    return;
                }

                if ($("#contents").val() == "") {
                    alert(confirmErrorNoContents);
                    return;
                }

                if (confirm(confirmUpdateFreeboard)) {
                    o.updateBulletin();
                }
            });

            $.nfx.click(".download-attach-file", function (e) {
                e.preventDefault();

                const fileExt = $(this).data('file-ext');
                const filePath = $(this).data('file-path');

                if (previewFileExtList.includes(fileExt.toLowerCase())) {
                    window.open(filePath);
                } else {
                    executeFileDownloadModal($(this).data("file-id"), "BULLETIN_FILE");
                }
            });

            $.nfx.click(".btnDeleteFile", function () {
                if (confirm(confirmDeleteBulletinFile)) {
                    o.deleteBulletinFile($(this));
                }
            });

            $.nfx.click("#btnAddReply", function () {
                if ($("#replyContent").val() === "") {
                    alert(alertReply);
                    return false;
                }
                o.addBulletinReply();
            });

            $(document).on("click", '#btnDeleteReply', function () {
                if (confirm(confirmDeleteBulletinReply)) {
                    o.deleteBulletinReply($(this));
                }
            });

            $.nfx.click("#btnDelete", function () {
                if (confirm(confirmDeleteBulletin)) {
                    o.deleteBulletin();
                }
            });

            $(document).on("click", "#btnLike", function () {
                o.postBulletinLike(true);
            });

            $(document).on("click", "#btnDislike", function () {
                o.postBulletinLike(false);
            });

            $.nfx.click("#btnMoveEditPage", function () {
                window.location.href = `/coWork/bulletinUpdate?id=${bulletinId}`;
            });

            $.nfx.click("#btnGotoList", function () {
                window.location.href = "/coWork/bulletinList?keepCondition=true";
            });

            $.nfx.click(".zoom-in-image", function () {
                let html = "<a href='" + $(this).attr("src") + "'/>";
                $(html).ekkoLightbox({ alwyasShowClose: true});
            });
        },

        reloadCardBody: function () {
            reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
        },

        resetSearchCondition: function() {
            $("#searchType").val('none');
            $("#searchValue").val('');
            $("#searchUserId").val(0);
            $("#searchUserDisplayName").val('');
            $("#startDate").val('');
            $("#endDate").val('');
            $("#sortProp").val('');
            $("#pageNo").val(0);
            $("#pageSize").val(20);
        },

        getSearchCondition: function () {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchType=${$("#searchType").val()}`;
            param += `&searchValue=${$("#searchValue").val()}`;
            param += `&searchUserId=${parseInt($("#searchUserId").val()) || 0}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`;
            param += `&startDateString=${$("#startDate").val()}`;
            param += `&endDateString=${$("#endDate").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },

        addBulletin: function() {
            reqPostJSON('/coWorkApi/postBulletin'
                , o.setSendData()
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        bulletinId = data.returnId;
                        startFileUpload();
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        updateBulletin: function() {
            reqPostJSON('/coWorkApi/putBulletin'
                , o.setSendData()
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        startFileUpload();
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        setSendData: function() {
            return JSON.stringify({
                "id": bulletinId,
                "title": $("#title").val(),
                "contents": $("#contents").val()
            });
        },

        deleteBulletinMain: function(id) {
            reqPost(`/coWorkApi/deleteBulletin`,  { "id" : id },
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        o.reloadCardBody();
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            )
        },

        deleteBulletin: function() {
            reqPost(`/coWorkApi/deleteBulletin`,  { "id" : bulletinId },
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        window.location.href = "/coWork/bulletinList?keepCondition=true"
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            )
        },

        deleteSelBulletin: function() {
            reqDeleteJSON(`/coWorkApi/deleteSelBulletin`, o.setSendDeleteData(),
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

        deleteBulletinFile: function(obj) {
            const fileId = obj.data('file-id');

            reqPost('/coWorkApi/deleteBulletinFile'
                , { "id": fileId }
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        obj.parent().remove();
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
        },

        deleteBulletinReply: function(obj) {
            reqPost('/coWorkApi/deleteBulletinReply'
                , { "id": obj.data('reply-id') }
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        reloadComponent("/coWork/bulletinViewReply", "#divBulletinReply", `bulletinId=${bulletinId}`);
                    }
                }
                ,  function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
        },

        setSendDeleteData: function() {
            let val = [];
            $(".list-item-checkbox:checkbox:checked").each(function () {
                val.push({
                    id: $(this).val() + "",
                });
            });

            return JSON.stringify(val);
        },

        addBulletinReply: function() {
            reqPostJSON('/coWorkApi/postBulletinReply'
                , o.setPostBulletinReplyData()
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        $("#replyContent").val('');
                        reloadComponent("/coWork/bulletinViewReply", "#divBulletinReply", `bulletinId=${bulletinId}`);
                    }
                }
                , function (xhr) {
                    showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
                });
        },

        setPostBulletinReplyData: function() {
            return JSON.stringify({
                "bulletinId": bulletinId,
                "contents": $("#replyContent").val()
            });
        },

        postBulletinLike: function(enabled) {

            reqPostJSON('/coWorkApi/postBulletinLike'
                , o.setPostBulletinLikeData(enabled)
                , function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        reloadComponent("/coWork/bulletinViewLikesButton", "#divBulletinViewLikesButton", `bulletinId=${bulletinId}`);
                        reloadComponent("/coWork/bulletinViewLikesUserList", "#divBulletinViewLikesUserList", `bulletinId=${bulletinId}`);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
        },

        setPostBulletinLikeData: function(enabled) {
            return JSON.stringify({
                bulletinId,
                enabled
            });
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/coWork/bulletinListCardBody',
        });

        o.init();
    });

})(jQuery);