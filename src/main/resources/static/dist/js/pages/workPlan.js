(function ($) {
    var o = {

        init: function () {
            $(document).on("click", "#btnAdd", function () {
                reqGet("/adminModal/addWorkPlan"
                    , function (data) {
                        $('#modalWorkPlanAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "a[id^=\"btnUpdate_\"]", function () {
                $("#tmpWorkPlanId").val($(this).attr("data-id"));

                reqGet("/adminModal/updateWorkPlan?id=" + $("#tmpWorkPlanId").val()
                    , function (data) {
                        $('#modalWorkPlanUpdate').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "#btnDeleteWorkPlan", function () {
                if (confirm(confirmDeleteWorkAmount)) {
                    o.deleteWorkPlan(this);
                }
            });

            $.nfx.click("#btnSearchWorkPlan", function () {
                o.reloadCardBody();
            });

            $.nfx.click("#btnResetSearchCondition", function () {
                o.resetAndSearch();
            });

            $.nfx.click("#btnPagePrevious, a[id^=\"btnPageNo_\"], #btnPageNext", function () {
                $("#pageNo").val($(this).data("page-no"));
                o.reloadCardBody();
            });
        },

        deleteWorkPlan: function(obj) {
            reqDelete(`/adminApi/deleteWorkPlan?id=${$(obj).data('work-plan-id')}`,
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
            listCardBody: '/admin/workPlanCardBody',
        });

        o.init();
    });

})(jQuery);