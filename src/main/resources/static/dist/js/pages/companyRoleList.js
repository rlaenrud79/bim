(function ($) {
    var o = {
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

            // 등록 열기
            $(document).on("click", '#btnAdd', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalCompanyRoleAdd').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/adminModal/addCompanyRole`);
            });

            $(document).on("click", 'button[id^=\'btnForwardSortNo_\']', function () {
                $("#tmpCompanyRoleId").val($(this).attr("data-id"));
                $("#tmpTrIndex").val($(this).attr("data-index"));
                o.updateCompanyRoleSortNoASC();
            });

            $(document).on("click", 'button[id^=\'btnBackwardSortNo_\']', function () {
                $("#tmpCompanyRoleId").val($(this).attr("data-id"));
                $("#tmpTrIndex").val($(this).attr("data-index"));
                o.updateCompanyRoleSortNoDESC();
            });

            $(document).on("click", 'a[id^=\'btnUpdate_\']', function () {
                $("#tmpCompanyRoleId").val($(this).attr("data-id"));
                reqGet("/adminModal/updateCompanyRole?mCompanyRoleId=" + $("#tmpCompanyRoleId").val()
                    , function (data) {
                        $('#modalCompanyRoleUpdate').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseJSON).error);
                    }, "html");
            });
        },

        updateCompanyRoleSortNoASC: function() {
            reqPost('/adminApi/putCompanyRoleSortNoASC'
                , {"companyRoleId": $("#tmpCompanyRoleId").val()}
                , function (data) {
                    if (data.result) {
                        reloadComponent("/admin/companyRoleListCardBody", "#divCardBody", "");
                        //toastr.success(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
        },

        updateCompanyRoleSortNoDESC: function() {
            reqPost('/adminApi/putCompanyRoleSortNoDESC'
                , {"companyRoleId": $("#tmpCompanyRoleId").val()}
                , function (data) {
                    if (data.result) {
                        reloadComponent("/admin/companyRoleListCardBody", "#divCardBody", "");
                        //toastr.success(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
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