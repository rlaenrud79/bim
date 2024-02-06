
	/* sideNav */
	function sideNav() {
		var $sideNav = $('.side-nav');
		var $sideNavDep1 = $('.side-nav > ul > li > a');
		var $sideNavDep2 = $('.side-nav .gnb-dep2 li > a');

		var depthCk = function () {
			if ($(this).parent().hasClass('on')) {
				$(this).siblings().slideUp();
				$(this).parent().removeClass('on');
			} else {
				if ($(this).parent().find('.gnb-dep3').length) {
					$('.side-nav .gnb-dep2 li').removeClass('on');
					$('.side-nav .gnb-dep2 li > div').slideUp();
				}
				$(this).parent().siblings().removeClass('on');
				$(this).parent().siblings().find('> div').slideUp();
				$(this).siblings().slideDown();
				$(this).parent().addClass('on');
			}
		};

		$sideNavDep1.on('click', depthCk);
		$sideNavDep2.on('click', depthCk);

		// Set active menu based on current URL
		var currentUrl = window.location.href;
		var newURL = currentUrl.replace('#', '');

		$('.side-nav ul li').each(function () {
			var menuItemUrl = $(this).find('> a').attr('href');
			if (newURL.includes(menuItemUrl)) {
				$(this).addClass('on');
				//$(this).find('> div').show();
				$('.gnb-dep3').hide();
				$(this).parent().parent().show();
				$(this).closest('.gnb-dep2').show();
				$(this).parents('li').addClass('on');

			}

			// if ($(this).find('ul').length) {
			// 	$(this).find('> a').addClass('on');
			// }
		});
	}




	/*common*/
	var windowH = $(window).height();
	function bodyFix() {
		$('body').css('overflow','hidden');
	}
	function bodyAuto() {
		$('body').css('overflow','auto');
	}


	/*top btn*/
	function goTop() {
		var goTopBtn = $('.btn-gotop');
		// if ($(document).scrollTop() > windowH) {
		// 	goTopBtn.css('position','fixed');
		// } else {
		// 	goTopBtn.css('position','absolute');
		// }
		goTopBtn.on('click',function(){
           $('html,body').animate({scrollTop:0},500)
        });
	}

    /*popup*/
    function layerPopup() {
        var $this;
        $(document).on("click",'.pop-open-btn',function(){
            //$('.pop-open-btn').on('click', function () {
            var modal = $(this).data('modal');
            $('body').css('overflow', 'hidden');
            $(modal).attr('tabindex',0).show().focus();
            $this = $(this);

        });
        $(document).on("click",'.popup-wrap .close',function(){
            //$('.popup-wrap .close').on('click', function () {
			$(this).closest('.popup-wrap').hide();
			$(this).closest('.popup-wrap').find('.popup-con').html("");
            $('body').css('overflow', 'auto');
            //$this.focus();
        });
		$(document).on("click",'.popup-wrap .process_close',function(){
			//$('.popup-wrap .close').on('click', function () {
			$(this).closest('.popup-wrap').hide();
			$('body').css('overflow', 'auto');
			//$this.focus();
		});
        $('.popup-wrap .focus-return').on('focus', function () {
            $(this).closest('.popup-wrap').attr('tabindex',0).focus();
        });
    }


	/*tabInpage*/
	function tabInpage() {
		var tabBtn = $('.tabInpage ul li a');
		tabBtn.on('click',function(e){
			e.preventDefault();
			var thisTxt = $(this).text();
			var tabCon = $(this).attr('href');
			$(this).parent().addClass('on');
			$(this).parent().siblings().removeClass('on');
			$(tabCon).closest('.tabInpage-con').find('> div').removeClass('on');
			$(tabCon).addClass('on');
			//$(this).closest('.tabInpage').find('.mo-drop-tit').text(thisTxt);
			//$(this).closest('.tabInpage').find('.mo-drop-tit').removeClass('on');
			//if($(window).width() <= 1000) {
				//$(this).closest('ul').hide();
				//$(this).closest('.tabInpage-con').find('.mo-drop-tit').removeClass('on');
			//}
		});
	}

$(document).ready(function () {

	// $.datepicker.setDefaults({
	// 	dateFormat: 'yy/mm/dd',
	// 	prevText: '이전 달',
	// 	nextText: '다음 달',
	// 	monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	// 	monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	// 	dayNames: ['일', '월', '화', '수', '목', '금', '토'],
	// 	dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
	// 	dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
	// 	showMonthAfterYear: true,
	// 	yearSuffix: '년',
	// 	//beforeShowDay: noBefore
	// 	//minDate: 0
	// });

	/*drop button*/
	const dropNav = $(".drop-nav");
	dropNav.each(function() {
		$(this).find('span').on('click', function () {
			$(this).siblings("ul").toggle();
			$(this).toggleClass('on');
		});
	});
	$(document).mouseup(function (e){
		if(dropNav.has(e.target).length === 0){
			dropNav.find("ul").hide();
			dropNav.find("span").removeClass('on');
		}
	});

	/*init*/
	sideNav();
	layerPopup();
	tabInpage();
	goTop();
	//$("#datepicker").datepicker();

	/*scroll*/
	$('.scroll-wrap').overlayScrollbars({
	});

	$('.drag').draggable({
		cursor: 'move'
	});

	new WOW().init();

	/*sub tab round*/
	$('.tab-nav-round ul li').on('click',function(){
		$(this).addClass('on');
		$(this).siblings().removeClass('on');
	})

	/*사용자 이미지 마움스오버*/
	$('.user-img').each(function(){
		$(this).on('mouseenter',function(){
			if($(this).find('.bubble span').text()) {
				$(this).addClass('on');
			}
		});
		$(this).on('mouseout',function(){
			$(this).removeClass('on');
		});
	});

});












