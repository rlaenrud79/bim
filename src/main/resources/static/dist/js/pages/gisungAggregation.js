(function ($) {
    var o = {
        init: function () {

            // add
            $.nfx.click('#btnAdd', function () {
                var year = $(this).data("gisung-year");
                location.href = "/gisung/gisungAggregationForm?year="+year;
            });

            // modify
            $.nfx.click('.btnModify', function () {
                var year = $(this).data("gisung-year");
                var documentNo = $(this).data("document-no");
                location.href = "/gisung/gisungAggregationForm?year="+year+"&no="+documentNo;
            });

            // delete
            $.nfx.click('#btnDeleteGisungAggregation', function () {
                if (confirm(confirmDeleteDocument)) {
                    o.deleteGisungAggregation();
                }
            });

            // 등록 모달 테스트
            $.nfx.click('#btnAddModal', function () {
                console.log("test");
                reqGet("/adminModal/addSchedule"
                    , function (data) {
                        $('#modalTestAdd').find('.popup-con').html(data);
                        $('#modalTestAdd').show();
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $.nfx.click('.btn-aggregation-add', function () {
                var tbId = $(this).data('tbid');
                var html = "";
                var gtype = 1;
                var netCheck = 1;
                if (tbId == "tbTab1") {
                    gtype = 1;
                    netCheck = "Y";
                } else if (tbId == "tbTab2") {
                    gtype = 2;
                    netCheck = "Y";
                } else if (tbId == "tbTab3") {
                    gtype = 3;
                } else if (tbId == "tbTab4") {
                    gtype = 4;
                }
                html += '<tr>\n' +
                    '    <td>' +
                    '         <input type="hidden" name="netCheck[]" value="'+netCheck+'">' +
                    '         <input type="hidden" name="gtype[]" value="'+gtype+'">' +
                    '         <input type="text" name="title[]" id="title">' +
                    '    </td>\n' +
                    '    <td><input type="text" name="cost[]" id="cost"></td>\n' +
                    '    <td><input type="text" name="percent[]" id="percent"></td>\n' +
                    '    <td>\n' +
                    '        <div class="btn-control">\n' +
                    '            <a href="#none" class="btn-add btn-aggregation-add" data-tbid="'+tbId+'" title="추가"></a>\n' +
                    '            <a href="#none" class="btn-remove btn-aggregation-del" data-tbid="'+tbId+'" title="삭제"></a>\n' +
                    '        </div>\n' +
                    '    </td>\n' +
                    '</tr>';
                $("#"+tbId+" tbody").append(html);
            });

            $.nfx.click('.btn-aggregation-del', function () {
                $(this).closest('tr').remove();
            });

            $.nfx.click('#btnMoveListPage', function () {
                window.location.href = '/gisung/gisungAggregationList';
            });

            $.nfx.click('#mBtnAdd', function () {
                if ($("#year").val() === "") {
                    alert(errorNoYear);
                    return;
                }
                /**
                 if ($("#title").val() === "") {
                showErrorAlert("ALERT", [[#{admin.modal.add_gisung_aggregation.error_no_title}]]);
                return;
            }
                 if ($("#cost").val() === "") {
                showErrorAlert("ALERT", [[#{admin.modal.add_gisung_aggregation.error_no_cost}]]);
                return;
            }
                 **/
                if (confirm(confirmAddGisungAggregation)) {
                    o.addGisungAggregation();
                }
            });

            $.nfx.click('#mBtnModify', function () {;
                if ($("#year").val() === "") {
                    alert(errorNoYear);
                    return;
                }

                if (confirm(confirmAddGisungAggregation)) {
                    o.updateGisungAggregation();
                }
            });

            $.nfx.click('.btn-tab', function () {
                documentNo = $(this).data("document-no");
                var year = $(this).data("year");
                if (documentNo == 1) {
                    $(".tab-box .btn-tab").removeClass("on");
                    $(".tab-box .btn-tab").eq(0).addClass("on");
                } else {
                    $(".tab-box .btn-tab").removeClass("on");
                    $(".tab-box .btn-tab").eq(1).addClass("on");
                }
                location.href = "/gisung/gisungAggregationForm?year="+year+"&no="+documentNo;
            });
        },

        deleteGisungAggregation: function() {
            reqDelete(`/gisungApi/deleteGisungAggregation?year=${$(this).data('gisung-year')}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        //resetAndSearch();
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

        updateGisungAggregation: function() {
            reqPostJSON('/adminApi/putGisungAggregation'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        //$("#modalWorkPlanUpdate").modal("hide");
                        //reloadComponent("/admin/workPlanCardBody", "#divCardBody", "");
                        window.location.href = '/gisung/gisungAggregationList';
                        toastr.success(data.message);
                    } else {
                        showErrorAlert("ALERT", data.message);
                    }
                }
                , function (xhr) {
                    showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
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
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            //listCardBody: '/gisung/gisungListCardBody',
        });

        o.init();
    });

})(jQuery);