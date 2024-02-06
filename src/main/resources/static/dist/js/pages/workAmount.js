(function ($) {
    var o = {

        init: function () {
            $(document).on("click", "#btnAdd", function () {
                reqGet("/adminModal/addWorkAmount"
                    , function (data) {
                        $('#modalWorkAmountAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "a[id^=\"btnUpdate_\"]", function () {
                $("#tmpWorkAmountId").val($(this).attr("data-id"));

                reqGet("/adminModal/updateWorkAmount?id=" + $("#tmpWorkAmountId").val()
                    , function (data) {
                        $('#modalWorkAmountUpdate').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "#btnDeleteWorkAmount", function () {
                if (confirm(confirmDeleteWorkAmount)) {
                    o.deleteWorkAmount($(this).attr("data-work-amount-id"));
                }
            });

            $.nfx.click("#btnSearchWorkAmount", function () {
                o.reloadCardBody();
            });

            $.nfx.click("#btnResetSearchCondition", function () {
                o.resetAndSearch();
            });

            $(document).on("click", "#btnPagePrevious, a[id^=\"btnPageNo_\"], #btnPageNext", function () {
                $("#pageNo").val($(this).data("page-no"));
                o.reloadCardBody();
            });
        },

        deleteWorkAmount: function(id) {
            reqDelete(`/adminApi/deleteWorkAmount?id=${id}`,
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
            param += `&searchWorkId=${parseInt($("#searchWorkId").val())}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },

        resetSearchCondition: function() {
            $("#searchWorkId").val(0);
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
        }
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/admin/workAmountCardBody',
        });

        o.init();
    });

})(jQuery);