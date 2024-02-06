(function ($) {
    var o = {

        init: function () {
            $(document).on("click", "#btnAdd, .fc-add-button", function () {
                reqGet("/adminModal/addSchedule"
                    , function (data) {
                        $('#modalScheduleAdd').find('.popup-con').html(data);

                        var modal = "#modalScheduleAdd";
                        $('body').css('overflow', 'hidden');
                        $(modal).attr('tabindex',0).show().focus();
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
        });

        o.init();
    });

})(jQuery);