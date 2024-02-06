(function ($) {
    var o = {

        _this: $(this),

        init: function () {
            $(document).on("click", "#btnAdd", function () {
                reqGet("/adminModal/addWork", function (data) {
                        $('#modalWorkAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            $(document).on("click", "a[id^=\"btnUpdate_\"]", function () {
                $("#tmpWorkId").val($(this).attr("data-id"));
                console.log("tmpWorkId : " + $("#tmpWorkId").val());

                reqGet("/adminModal/updateWork?mWorkId=" + $("#tmpWorkId").val(), function (data) {
                        $('#modalWorkAdd').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });
        },

    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/work/workListCardBody',
        });

        o.init();
    });

})(jQuery);