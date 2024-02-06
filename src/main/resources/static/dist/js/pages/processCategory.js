(function ($) {
    var o = {
        init: function () {

            $(document).on("click", '.btn-cate1', function () {
                reqGet("/processModal/addProcessItem?cate1=" + $(this).data('cate1')
                    , function (data) {
                        $('#modalProcessAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $.nfx.click('.btn-cate2', function () {
                reqGet("/processModal/addProcessItem?cate1=" + $(this).data('cate1')
                    , function (data) {
                        $('#modalProcessAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });
        }

    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/gisung/gisungListCardBody',
        });

        o.init();
    });

})(jQuery);