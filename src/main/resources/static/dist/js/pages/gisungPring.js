(function ($) {
    var o = {
        processItem: [],
        processItemTotal: [],
        processSearch: false,
        searchProcessPopup: false,

        init: function () {

            // 기성 인쇄 탭
            $.nfx.click('.btn-tab', function () {
                var documentNo = $(this).data("document-no");
                var id = $(this).data("gisung-id");
                location.href = "/gisung/gisungPrintForm?id=" + id + "&no=" + documentNo;
            });

            // 사용자 검색 모달창 열기
            $.nfx.click('#searchUserDisplayName, #btnSearchUser', function () {
                let param = "formElementIdForUserId=searchUserId";
                param += "&formElementIdForUserName=searchUserDisplayName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                const params = {};

                const callback = function (data) {
                    $('#modalSearchSingleUser').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
            });

            $.nfx.on('change', '#pageSize', function () {
                $("#pageNo").val(0);
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $(document).on("click", '#btnPagePrevious, a[id^="btnPageNo_"], #btnPageNext', function () {
                $("#pageNo").val($(this).data("page-no"));
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            // search
            $.nfx.click('#btnSearchGisung', function () {
                o.reloadCardBody();
            });

            // reset
            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetAndSearch();
            });
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
            return param;
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
        },

        resetAndSearch: function() {
            o.resetSearchCondition();
            o.reloadCardBody();
        },

        reloadCardBody: function () {
            reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
        }
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/gisung/gisungPrintListCardBody',
        });

        o.init();
        o.searchProcess.init();
    });

})(jQuery);