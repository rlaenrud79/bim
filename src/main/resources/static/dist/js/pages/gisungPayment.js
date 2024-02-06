(function ($) {
    var o = {
        init: function () {

            // add
            $.nfx.click('#btnAdd', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalAddGisungPayment').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/addGisungPayment`);
            });

            $.nfx.click('.download-attach-file', function (e) {
                e.preventDefault();
                executeFileDownloadModal($(this).data("file-id"), "GISUNG_PAYMENT_FILE");
            });

            // delete
            $(document).on("click", '#btnDeleteGisungPayment', function () {
                if (confirm(confirmDeleteGisungPayment)) {
                    o.deleteGisungPayment();
                }
            });

            // search
            $.nfx.click('#btnSearchGisungPayment', function () {
                o.reloadCardBody();
            });

            // reset
            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetAndSearch();
            });

            // 수정 모달
            $(document).on("click", '.openUpdateModal', function (e) {
                const url = `/gisung/updateGisungPayment?id=${$(this).data('gisung-payment-id')}`;
                reqGet(url, function (data) {
                    $('#modalUpdateGisungPayment').find('.popup-con').html(data);
                }, function (xhr) {
                    alert(xhr.responseJSON.message);
                });
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
        },

        modalShowAndDraggable: function (obj) {
            $(obj).modal('show');
            $(obj).draggable({'cancel': '.modal-body'});
        },

        deleteGisungPayment: function() {
            reqDelete(`/gisungApi/deleteGisungPayment?id=${$(this).data('gisung-payment-id')}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        o.resetAndSearch();
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        getSearchCondition: function() {
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

        resetAndSearch: function() {
            o.resetSearchCondition();
            o.reloadCardBody();
        },

        reloadCardBody: function() {
            reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/gisung/gisungPaymentListCardBody',
        });

        o.init();
    });

})(jQuery);