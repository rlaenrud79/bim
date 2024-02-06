(function ($) {
    var o = {
        init: function () {

            // add
            $.nfx.click('#btnAdd', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalAddGisungContractManager').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/addGisungContractManager`);
            });

            // delete
            $(document).on("click", '#btnDeleteGisungContractManager', function () {
                if (confirm(confirmDeleteDocument)) {
                    o.deleteGisungContractManager($(this));
                }
            });

            // search
            $.nfx.click('#btnSearchGisung', function () {
                o.reloadCardBody();
            });

            // reset
            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetAndSearch();
            });

            // 수정 모달
            $(document).on("click", '.openUpdateModal', function (e) {
                const status = $(this).data('status');
                const url = `/gisung/updateGisungContractManager?id=${$(this).data('gisung-contract-manager-id')}`;
                reqGet(url, function (data) {
                    $('#modalUpdateGisungContractManager').find('.popup-con').html(data);
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

        deleteGisungContractManager: function(obj) {
            reqDelete(`/gisungApi/deleteGisungContractManager?id=${obj.data('gisung-contract-manager-id')}`,
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

        addGisungAggregation: function() {
            reqPostJSON('/adminApi/postGisungAggregation'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        //$("#modalWorkPlanUpdate").modal("hide");
                        //reloadComponent("/admin/workPlanCardBody", "#divCardBody", "");
                        window.location.href = '/gisung/gisungAggregationList';
                    } else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        setSendData: function() {
            return JSON.stringify({
                "year": $("#year").val(),
                "documentNo": $("#documentNo").val(),
                "data": o.getValues()
            });
        },

        getValues: function() {
            let vals = [];
            $('input[type="text"][name="title[]"]').each(function(index) {
                var cost = 0;
                var percent = 0;
                if ($('input[name="cost[]"]').eq(index).val()) {
                    cost = $('input[name="cost[]"]').eq(index).val();
                }
                if ($('input[name="percent[]"]').eq(index).val()) {
                    percent = $('input[name="percent[]"]').eq(index).val();
                }
                if ($('input[name="title[]"]').eq(index).val() != "") {
                    vals.push({
                        netCheck: $('input[name="netCheck[]"]').eq(index).val() + "",
                        title: $('input[name="title[]"]').eq(index).val() + "",
                        cost: cost + "",
                        gtype: $('input[name="gtype[]"]').eq(index).val() + "",
                        percent: percent + "",
                    });
                }
            });
            return vals;
        },

        getSearchCondition: function() {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchWorkId=${parseInt($("#searchWorkId").val())}`;
            param += `&searchType=${$("#searchType").val()}`;
            param += `&searchValue=${$("#searchValue").val()}`;
            param += `&searchUserId=${parseInt($("#searchUserId").val()) || 0}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },

        resetSearchCondition: function() {
            $("#searchWorkId").val(0);
            $("#searchType").val('none');
            $("#searchValue").val('');
            $("#searchUserId").val(0);
            $("#searchUserDisplayName").val('');
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
            listCardBody: '/gisung/gisungContractManagerListCardBody',
        });

        o.init();
    });

})(jQuery);