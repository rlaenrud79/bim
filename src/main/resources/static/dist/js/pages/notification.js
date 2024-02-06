(function ($) {
    var o = {

        pageNo: 0,

        init: function () {

            $.nfx.click('.btn-del-sel', function () {
                if ($(".list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm('선택한 공사일지를 삭제 하시겠습니까?')) {
                        o.del($.nfx.form, 'del_sel', null);
                    }
                } else {
                    alert('삭제할 공사일지를 선택하십시오.');
                }
            });

            // 사용자 검색 모달창 열기
            $.nfx.click('#writerName, #btnSearchUser', function () {
                let param = "formElementIdForUserId=writerId";
                param += "&formElementIdForUserName=writerName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                const params = {};

                const callback = function (data) {
                    $('#modalSearchSingleUser').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
            });

            $.nfx.click('#btnSearch', function () {
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.setSendData(0, $("#pageSize").val()));
            });

            $.nfx.click('#btnInit', function () {
                o.initSearchCondition();
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.setSendData(0, $("#pageSize").val()));
            });

            $.nfx.on('change', '#pageSize', function () {
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.setSendData(0, $("#pageSize").val()));
            });

            $(document).on("click", ".sort", function () {
                $("#sortProp").val($(this).data('sort'));
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.setSendData(0, $("#pageSize").val()));
            });


            $(document).on("click", '#btnPagePrevious, a[id^="btnPageNo_"], #btnPageNext', function () {
                o.pageNo = $(this).data("page-no");
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.setSendData(o.pageNo, $("#pageSize").val()));
            });



            // 작업일지 삭제 버튼
            $.nfx.click('#listBtnDelete', function () {
                if (confirm(confirmDeleteJobSheet)) {
                    o.deleteJobSheetList($(this));
                }
            });

            $.nfx.click('#btnDelete', function () {
                if (confirm(confirmDeleteJobSheet)) {
                    o.deleteJobSheet($(this));
                }
            });

            $(document).on("click", 'a[id^=\'aTitle_\']', function () {
                let noticeId = $(this).data("id");
                location.href = "/coWork/notificationView?noticeId=" + noticeId + "&" + o.setSendData(o.pageNo, $("#pageSize").val());
            });
        },

        setSendData: function(page, pageSize) {
            let param = "";

            param += "page=" + page;
            param += "&size=" + pageSize;
            param += "&searchType=" + $("#searchType").val();
            param += "&searchText=" + $("#searchText").val();
            param += "&writerName=" + $("#writerName").val();
            param += "&writerId=" + $("#writerId").val();
            param += "&searchDateFrom=" + $("#searchDateFrom").val();
            param += "&searchDateEnd=" + $("#searchDateEnd").val();
            param += "&sortProp=" + $("#sortProp").val();

            return param;
        },

        initSearchCondition: function () {
            $("#searchType").val("");
            $("#searchText").val("");
            $("#writerName").val("");
            $("#writerId").val("0");
            $("#searchDateFrom").val("");
            $("#searchDateEnd").val("");
            $("#sortProp").val("");
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/coWork/notificationListCardBody',
        });

        o.init();
    });

})(jQuery);