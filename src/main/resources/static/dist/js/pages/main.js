(function ($) {
    var o = {

        init: function () {
            // 복합단가 팝업 열기
            $(document).on("click", '.btnDayProcessItem', function () {
                const date = $(this).data("date");
                console.log(date);
                if (date != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#calendarPopup').find('.popup-tit').html(date);
                        $('#calendarPopup').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/mainModal/mainDateProcessList/${date}`);
                }

            });

            $(document).on("click", '.btn-schedule-tab', function () {
                const tab = $(this).data("tab");
                const date = $(this).data("date");
                console.log(tab);

                let params = "tab=" + tab;
                params += "&date=" + date;

                reqGet(`/mainModal/mainScheduleList?${params}`, function (data) {
                    $("#scheduleDiv").html(data);
                });

            });

            $(document).on("click", '.btn-prevnext-date', function () {
                const tab = $('.btn-schedule-tab.on').data('tab');
                const date = $(this).data("date");
                console.log(tab);

                let params = "tab=" + tab;
                params += "&date=" + date;

                reqGet(`/mainModal/mainScheduleList?${params}`, function (data) {
                    $("#scheduleDiv").html(data);
                });

            });
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
        });

        o.init();
    });



    // 숫자 애니메이션 함수
    function animateCounter(targetElement, start, end, duration) {
        let current = start;
        const increment = (end - start) / duration;

        const updateCounter = () => {
            current += increment;
            $(targetElement).text(Math.round(current));

            if (current >= end) {
                clearInterval(interval);
            }
        };

        const interval = setInterval(updateCounter, 10); // 1초마다 업데이트

        updateCounter(); // 초기값 설정
    }
    // 스크롤 이벤트 핸들러
    function handleScroll() {
        $('.counter-num').each(function() {
            const counterElement = $(this);
            const counterPosition = counterElement.offset().top;

            // 화면에 나타날 때 카운트 애니메이션 실행
            if (counterPosition < $(window).height()) {
                animateCounter(this, parseInt(counterElement.data('start')), parseInt(counterElement.data('end')), parseInt(counterElement.data('duration')));
            }
        });

        // 스크롤 이벤트 리스너 제거
        $(window).off('scroll', handleScroll);
    }

    // 스크롤 이벤트 리스너 등록
    $(window).on('scroll', handleScroll);

    // 초기에도 한 번 체크
    handleScroll();


})(jQuery);