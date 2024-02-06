(function ($) {
    var o = {

        _this: $(this),

        init: function () {
            $(document).on("click", "#btnAdd", function () {
                reqGet("/contentsModal/addDocumentCategory", function (data) {
                        $('#modalDocumentCategoryAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "a[id^=\"btnUpdate_\"]", function () {
                $("#tmpDocumentCategoryId").val($(this).attr("data-id"));
                console.log("tmpDocumentCategoryId : " + $("#tmpDocumentCategoryId").val());

                reqGet("/contentsModal/updateDocumentCategory?mDocumentCategoryId=" + $("#tmpDocumentCategoryId").val(), function (data) {
                        $('#modalDocumentCategoryAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "#btnDelete", function () {
                if (confirm(confirmDeleteCompanyCategory)) {
                    const id = $(this).attr("data-document-category-id");
                    o.deleteDocumentCategory(id);
                }
            });
        },

        deleteDocumentCategory: function(id) {
            reqDelete(`/contentsApi/deleteDocumentCategory?id=${id}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        reloadComponent($.nfx.listCardBody, "#divCardBody", "");
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            )
        }
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/contents/documentCategoryCardBody',
        });

        o.init();
    });

})(jQuery);