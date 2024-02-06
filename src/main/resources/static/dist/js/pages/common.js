(function ($) {
	function nfx() {
	}

	var interval;

	var paging = {
		action: '',
		isLoading: false,
		pages: 0,
		page: 1,
		prefix: 'scrolling-list',

		init: function () {
			if (paging.page == paging.pages) {
				if ($('.box-' + paging.prefix + '-more').length) {
					$('.box-' + paging.prefix + '-more').hide();
				}
			}

			if ($('.btn-' + paging.prefix + '-more-paging').length) {
				$.nfx.click('.btn-' + paging.prefix + '-more-paging', function () {
					paging.list('');
				});
			}
		},

		list: function (queryString) {
			var tqueryString = (queryString.length) ? queryString + '&' : '',
				params = tqueryString + 'ptype=list&page=' + paging.page,
				callback = function (html) {
					if($.trim(html) != '') {
						$('.box-' + paging.prefix).append(html);
					}

					$('.box-' + paging.prefix).show().scrollTop();

					if (paging.page > paging.pages) {
						if ($('.box-' + paging.prefix + '-more').length) {
							$('.box-' + paging.prefix + '-more').hide();
						}
					} else {
						paging.page = paging.page + 1;
					}

					paging.isLoading = false;
					paging.init();

					var event = $.Event('NFX_PAGE_LOADED');
					$(document).trigger(event);
				};

			$.nfx.ajax(params, 'html', callback, paging.action);
		},

		scrolling: function (queryString) {
			var isLastPage = (paging.page <= paging.pages) ? false : true;

			if (!isLastPage && !paging.isLoading) {
				paging.isLoading = true;
				paging.list(queryString);
			}
		},

		/**
		 * paging.start(window, queryString, 'proc.php');
		 * paging.start('.box-scrolling-list-content', queryString, 'proc.php');
		 */
		start: function (obj, queryString, action) {
			if ($('.box-' + paging.prefix).length) {
				var page = ($('.box-' + paging.prefix).data('page')) ? paging.getNo('page') : 2;

				if(page <= 1){
					page = 2;
				}

				paging.action = action;
				paging.pages = paging.getNo('pages');
				paging.page = (paging.pages > 0) ? page : 0;

				$(obj).scroll(function () {
					$('.box-footer').fadeOut();

					if (paging.isScrollEnd(obj)) {
						paging.scrolling(queryString);
					}

					clearTimeout($.data(this, 'scrollTimer'));
					$.data(this, 'scrollTimer', setTimeout(function() {
						$('.box-footer').fadeIn();
					}, 250));
				});
			}
		},

		getNo: function(name){
			return parseInt($('.box-' + paging.prefix).data(name), 10);
		},

		isScrollEnd: function (obj) {
			var isEnd,
				addHeight = 68; //하단 제거 높이

			//biz.js의 headerH에서 높이 조절
			addHeight = 0;

			if ($.isWindow(obj)) {
				isEnd = ($(window).scrollTop() == $(document).height() - $(window).height()) ? true : false;
			} else {
				var elem = $(obj);
				isEnd = (elem[0].scrollHeight - (elem.scrollTop() + addHeight) == elem.outerHeight()) ? true : false;
			}

			return isEnd;
		}
	};

	$.extend(nfx.prototype, {
		form: null,

		data: {},

		url: '',

		paging: paging,

		change: function (id, func) {
			$.nfx.on('change', id, func);
		},

		click: function (id, func) {
			$.nfx.on('click', id, func);
		},

		on: function (event_name, id, func) {
			if ($(id).length) {
				$(id).off(event_name);
				$(id).on(event_name, func);
			}
		},

		init: function () {
			$.nfx.click('.zip-daum-find-popup', function () {
				$.nfx.findZipPop($(this).attr('data-zip'), $(this).attr('data-addr'));
			});

			$.nfx.click('.zip-daum-find', function () {
				$.nfx.findZip($(this).attr('data-zip'), $(this).attr('data-addr'));
			});

			$.nfx.on('keyup', 'input.txt-verify-pass', function () {
				$.nfx.verifyPass($(this));
			});

			$.nfx.on('keyup', 'input.txt-only-number', function () {
				$.nfx.checkNumber($(this));
			});

			$.nfx.on('keyup', 'input.txt-price', function () {
				var price = $.nfx.comma($(this).val());
				$(this).val(price);
			});

			$.nfx.click( '.chk-check-all', function () {
				var useName = ($(this).is('[data-name]')) ? true : false,
					a = (useName) ? 'name' : 'class',
					o = $(this).attr('data-'+a);


				$.nfx.checkbox.checkAll(this, o, a);
			});

			$.nfx.click('.btn-com-del', function () {
				var formName = 'FRM',
					p = $.nfx.getDataObject($('form[name='+formName+']')),
					a = $.nfx.getDataObject($(this));
				p['action'] = $.nfx.getDataValue($('form[name='+formName+']'), 'action', 'cp_proc');
				p[p.key] = $(this).attr('data-key');
				p['checkbox'] = $.nfx.getDataValue($(this), 'checkbox', '');
				$.nfx.checkbox.remove(p, a);
			});


		},

		checkNumber: function (obj) {
			$(obj).val($(obj).val().replace(/[^0-9]/g, ""));
		},

		comma: function (str) {
			str = $.nfx.uncomma(str);
			str = String(str);
			return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
		},

		numberWithCommas: function(x) {
			return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		},

		calculateMultipleZ: function (val1, val2) {
			if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseInt(val1 * val2);
			return "";
		},

		calculateAdd: function (val1, val2) {
			if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseFloat(val1) + parseFloat(val2);
			return "";
		},

		calculateMinus: function (val1, val2) {
			if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseFloat(val1) - parseFloat(val2);
			return "";
		},

		numberWithCommasPoint: function (number) {
			const formattedNumber = number.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',').replace(/\.00$/, '');
			return formattedNumber;
		},

		uncomma: function (str) {
			str = String(str);
			return str.replace(/[^\d]+/g, '');
		},

		verifyPassValue: function (val) {
			var msg = '';
			if (val.length < 6) {
				msg = '비밀번호는 6자리 이상으로 사용해주세요';
			} else {
				//var passwordRules = /^(?=.*[a-z])(?=.*[!@#$%^*])(?=.*[0-9]).{6,12}$/;

				if (!/^[A-Za-z0-9!@#$%^*]{6,12}$/.test(val)) {
					msg = '영문 소문자, 숫자, 특수문자(!,@,#,$,%,^,*)를 사용할 수 있습니다';
				}
			}

			return msg;
		},

		verifyPass: function(obj){
			var passwordRules = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*])(?=.*[0-9]).{6,12}$/;
			$(obj).val($(obj).val().replace(passwordRules, ""));
			//return passwordRules .test(str);
		},

		getForm: function (obj) {
			var formName = $.nfx.getFormName(obj);
			return document.getElementsByName(formName)[0];
		},

		getDataValues: function(selector, dataName) {
			return $(selector).map(function () {
				return $(this).attr('data-' + dataName);
			}).toArray().join(',');
		},

		isFrmEmpty: function (selecter, msg) {
			if ($(selecter).val().length == 0) {
				alert(msg);
				$(selecter).focus();
				return true;
			}

			return false;
		},

		isEmail: function (form_element_name) {
			var email = form_element_name.value;
			var regExp = /[0-9a-zA-Z][_0-9a-zA-Z-]*@[_0-9a-zA-Z-]+(\.[_0-9a-zA-Z-]+){1,2}$/;

			if (isFrmEmpty(form_element_name, "이메일 주소를 입력해 주십시오.")) return false;

			if (!email.match(regExp)) {
				alert('올바른 이메일 주소를 입력해 주십시오.');
				form_element_name.focus();
				return false;
			}

			return true;
		},

		getFormName: function (obj) {
			return $(obj).closest('form').attr('name');
		},

		checkForm: function (formName, checkBase, callback, url) {
			var form = document.getElementsByName(formName)[0];
			if (checkBase(form)) {
				$.nfx.form = form;
				$.nfx.ajaxProc(formName, callback, url);
			}
		},

		biz: {
			displayButtonForLen: function(selector, obj, len){
				var isShow = ($(obj).val().length >= len) ? true : false;
				$.nfx.biz.displayButton(selector, isShow);
			},

			displayButton: function (selector, isShow){
				if(isShow){
					$(selector).removeClass('disable');
				}else{
					$(selector).addClass('disable');
				}
			},

			isButtonActive: function(selector){
				return ($(selector).hasClass('disable')) ? false : true;
			}
		},

		str: {
			init: function () {
				$.nfx.on('keyup', '.txt-str-length', function () {
					var p = $.nfx.getDataObject($(this));

					if(p.type == 'str'){
						$.nfx.str.checkStr(this, $('.' + p.caption), p);
					}else {
						$.nfx.str.checkByte(this, $('.' + p.caption), p);
					}
				});
			},

			checkStr: function (obj, objMsg, p) {
				var max_len = parseInt(p.len, 10),
					text = $(obj).val(),
					leng = text.length,
					check_msg = false,
					strLen = 0,
					title = '';
				while (getTextStrLength(text) > max_len) {
					check_msg = true;
					leng--;
					text = text.substring(0, leng);
				}

				if (check_msg) {
					$(obj).val(text);
				}

				strLen = getTextStrLength(text);

				title = (typeof p.format !== 'undefined') ? p.format.replace('#', strLen) : strLen + '자';
				$(objMsg).text(title);

				var event = jQuery.Event("NFX_STR_LENGTH");
				event.length = strLen;
				$(document).trigger(event);

				function getTextStrLength(str) {
					return str.length;
				}
			},

			checkByte: function (obj, objMsg, p) {
				var max_bytes = parseInt(p.len, 10),
					text = $(obj).val(),
					leng = text.length,
					check_msg = false,
					strLen = 0,
					title = '';
				while (getTextByteLength(text) > max_bytes) {
					check_msg = true;
					leng--;
					text = text.substring(0, leng);
				}

				if (check_msg) {
					$(obj).val(text);
				}

				strLen = getTextByteLength(text);

				title = (typeof p.format !== 'undefined') ? p.format.replace('#', strLen) : strLen + 'Byte';
				$(objMsg).text(title);

				function getTextByteLength(str) {
					var len = 0;
					for (var i = 0; i < str.length; i++) {
						if (escape(str.charAt(i)).length == 6) {
							len++;
						}
						len++;
					}
					return len;
				}
			}
		},

		timer: {
			init: function(){
				$.nfx.on('NFX_TIMER_STOP', document, function () {
					$.nfx.timer.end();
				});
			},

			start: function(selector, duration, showHour) {
				var timer = duration * 60,
					showHour = (typeof showHour !== 'undefined') ? showHour : false,
					hours, minutes, seconds;

				interval = setInterval(function(){
					hours	= parseInt(timer / 3600, 10);
					minutes = parseInt(timer / 60 % 60, 10);
					seconds = parseInt(timer % 60, 10);

					hours 	= hours < 10 ? "0" + hours : hours;
					minutes = minutes < 10 ? "0" + minutes : minutes;
					seconds = seconds < 10 ? "0" + seconds : seconds;

					if(showHour) {
						$(selector).text(hours + ':' + minutes + ':' + seconds);
					}else{
						$(selector).text(minutes + ':' + seconds);
					}

					if (--timer < 0) {
						timer = 0;
						$.nfx.timer.end();

						var event = jQuery.Event("NFX_TIMER_END");
						$(document).trigger(event);
					}
				}, 1000);
			},

			end: function () {
				clearInterval(interval);
			}
		},

		checkbox: {
			checkedCount: function (name) {
				return $('input:checkbox[name="'+name+'"]:checked').length;
			},

			checkAll: function(o, c, a){
				var s, check,
					c = (a=='name') ? 'input[name="'+c+'"]:checkbox' : 'input.'+c+':checkbox';

				if(!$(o).is('[type]')){
					if(!$(o).is('[data-check]')){
						$(o).attr('data-check','');
					}

					check = ($(o).attr('data-check')=='Y') ? 'N' : 'Y';
					$(o).attr('data-check',check);

					s = (check=='Y') ? true : false;
				}else{
					s = ($(o).is(':checked')) ? true : false;
				}

				$(c).prop('checked',s);
			},

			checkedRemove: function (name, className) {
				$('input:checkbox[name="'+name+'"]:checked').each(function () {
					$('.'+className+'-'+$(this).val()).remove();
				});
			},

			getCheckedArray: function (name) {
				return $('input:checkbox[name="' + name + '"]:checked').map(function () {
					var v = ($(this).val().length) ? $(this).val() : '';
					if(v) {
						return v;
					}
				}).toArray();
			},

			remove: function(p, a){
				var params = {'ptype':'del'},
					c = function (d) {
						switch(d.result){
							case 'OK':
								window.location.reload();
								break;

							default:
								alert('다시 클릭해 주세요.');
								break;
						}
					};

				if(checked(p)) {
					if(typeof a !== 'undefined'){
						if(Object.keys( a ).length) {
							delete a['checkbox'];

							for (var key in a) {
								params[key] = a[key];
							}
						}
					}

					params[p.key] = (p.checkbox.length) ? $.nfx.checkbox.getCheckedArray(p.checkbox) : p[p.key];
					$.nfx.ajax(params, 'json', c, getHomePath() + 'module/' + p['svc-name'] + '/' + p['action'] + '.php');
				}

				function checked(p) {
					var r = true;

					if(p.checkbox.length) {
						var cnt = $.nfx.checkbox.checkedCount(p.checkbox);

						if (cnt < 1) {
							alert("삭제할 " + p['msg'] +" 선택해 주십시오.");
							r = false;
						}
					}

					if(r) {
						r = (confirm("선택한 " + p.msg + " 삭제 하시겠습니까?"));
					}

					return r;
				}
			}
		},

		ajaxProc: function (formName, callback, url) {
			var fl = 'form[name=' + formName + ']',
				params;

			if ($(fl).is('[enctype]')) {
				params = $.nfx.getFileParam(formName);
				$.nfx.ajaxFile(params, 'json', callback, url);
			} else {
				$.nfx.ajax($(fl).serialize(), 'json', callback, url);
			}
		},

		getFileParam: function (formName) {
			var form = $('form[name=' + formName + ']')[0];
			var formData = new FormData(form);

			$('form[name=' + formName + '] input[type=file]').each(function () {
				formData.append($(this).attr('name'), $('input[name=' + $(this).attr('name') + ']')[0].files[0]);
			});

			return formData;
		},

		ajaxFile: function (params, dataType, callback, url) {
			$.ajax({
				url: (typeof url == 'undefined') ? $.nfx.url : url,
				dataType: dataType,
				processData: false,
				contentType: false,
				data: params,
				type: "post",
				success: function (data) {
					callback(data, $.nfx.form);
				},
				error: function (xhr, ajaxOptions, thrownError) {
					alert("[" + xhr.status + "] 다시 확인해 주십시오.");
				}
			});
		},

		ajax: function (params, dataType, callback, url) {
			$.ajax({
				url: (typeof url == 'undefined') ? $.nfx.url : url,
				dataType: dataType,
				data: params,
				type: "post",
				//contentType: 'application/json; charset=utf-8',
				success: function (data) {
					callback(data, $.nfx.form);
				},
				error: function (xhr, ajaxOptions, thrownError) {
					alert("[" + xhr.status + "] 다시 확인해 주십시오.");
				}
			});
		},

		ajaxGet: function (params, dataType, callback, url) {
			$.ajax({
				url: (typeof url == 'undefined') ? $.nfx.url : url,
				dataType: dataType,
				data: params,
				type: "get",
				success: function (data) {
					callback(data, $.nfx.form);
				},
				error: function (xhr, ajaxOptions, thrownError) {
					alert("[" + xhr.status + "] 다시 확인해 주십시오.");
				}
			});
		},

		serializeObject: function (ele) {
			var arr = $(ele).serializeArray();
			if (arr) {
				obj = {};
				$.each(arr, function () {
					obj[this.name] = this.value;
				});
			}

			return obj;
		},

		getDataValue: function(o, name, basic){
			return ($(o).is('[data-'+name+']')) ? $(o).attr('data-'+name) : basic;
		},

		getDataObject: function (o, d) {
			var data = (typeof d !== 'undefined') ? d : {};

			if($(o).length) {
				var attributes = $(o)[0].attributes;
				if (typeof attributes === 'object') {
					$.each(attributes, function (i, attrib) {
						if (attrib.name.indexOf('data-') == 0) {
							data[attrib.name.replace(/data-/g, '')] = attrib.value;
						}
					});
				}
			}

			return data;
		},

		setDataObject: function (attributes) {
			if (typeof attributes === 'object') {
				$.each(attributes, function (i, attrib) {
					if (attrib.name.indexOf('data-') == 0) {
						$.nfx.data[attrib.name.replace(/data-/g, '')] = attrib.value;
					}
				});
			}
		},

		printOptions: function (select_name, data, start_pos) {
			var opt_len = $(select_name + ' > option').length - 1;

			start_pos =(typeof start_pos === 'undefined') ? 1 : start_pos;

			for (var i = opt_len; i >= start_pos; i--) {
				$(select_name + ' option:eq('+i+')').remove();
			}

			if(data.result == 'CANCEL') {

			} else {
				for (i = 0; i < data.length; i++) {
					$(select_name).append($('<option>', {
						value: data[i].key,
						text: data[i].value
					}));
				}
			}
		},

		findZipPop: function(zip, addr){
			var daum_id = 'daum_postcode_wrap';

			if(!$('#'+daum_id).length){
				$('body').append('<div id="daum_postcode_wrap" style="display:none;border:1px solid;width:92%;height:50%;margin:5px 0;position:absolute; top:100px; left:20px;">\n' +
					'    <img src="//t1.daumcdn.net/postcode/resource/images/close.png" class="btn-daum-zip-close" style="cursor:pointer;position:absolute;right:0px;top:-21px;z-index:1">\n' +
					'</div>');

				$.nfx.click('.btn-daum-zip-close', function () {
					$('#'+daum_id).hide();
				});
			}

			var element_wrap = document.getElementById(daum_id);
			// 현재 scroll 위치를 저장해놓는다.
			var currentScroll = Math.max(document.body.scrollTop, document.documentElement.scrollTop);

			new daum.Postcode({
				oncomplete: function(data) {
					$('input[name=' + zip + ']').val(data.zonecode);
					$('input[name=' + addr + ']').val($.nfx.getDaumZipAddress(data));
					//$("#address_detail").focus();

					// iframe을 넣은 element를 안보이게 한다.
					// (autoClose:false 기능을 이용한다면, 아래 코드를 제거해야 화면에서 사라지지 않는다.)
					element_wrap.style.display = 'none';

					// 우편번호 찾기 화면이 보이기 이전으로 scroll 위치를 되돌린다.
					document.body.scrollTop = currentScroll;
				},
				// 우편번호 찾기 화면 크기가 조정되었을때 실행할 코드를 작성하는 부분. iframe을 넣은 element의 높이값을 조정한다.
				onresize : function(size) {
					element_wrap.style.height = size.height+'px';
				},
				width : '100%',
				height : '100%'
			}).embed(element_wrap);

			element_wrap.style.display = 'block';
		},

		//<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
		findZip: function (zip, addr) {
			var daum_id = 'daum_postcode_wrap';

			if (!$('#' + daum_id).length) {
				$('body').append('<div id="' + daum_id + '" data-state="N" style="position:absolute;display:none;border:1px solid #dddddd;width:500px;margin-top:0px;"></div>');
			}

			var element_wrap = document.getElementById("daum_postcode_wrap"),
				eaddr = $("input[name=" + addr + "]");

			$('#' + daum_id).offset({top: eaddr.offset().top, left: eaddr.offset().left});

			if ($('#' + daum_id).length) {

				if ($('#' + daum_id).attr('data-state') == 'N') {
					new daum.Postcode({
						oncomplete: function (data) {
							$('input[name=' + zip + ']').val(data.zonecode);
							$('input[name=' + addr + ']').val($.nfx.getDaumZipAddress(data));
							//$("#address_detail").focus();

							$('#' + daum_id).remove();
						}
						, onclose: function (state) {
							if (state === "COMPLETE_CLOSE") {
								closeLayer();
							}
						}
					}).embed(element_wrap);

					$('#' + daum_id).attr('data-state', 'Y');
					$('#' + daum_id).show();
				} else {
					closeLayer();
				}
			} else {
				closeLayer();
			}

			function closeLayer() {
				$('#' + daum_id).remove();
			}
		},

		getDaumZipAddress: function (data) {
			// 도로명 주소의 노출 규칙에 따라 주소를 조합한다.
			// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
			var fullRoadAddr = data.roadAddress; // 도로명 주소 변수
			var extraRoadAddr = ''; // 도로명 조합형 주소 변수

			// 법정동명이 있을 경우 추가한다. (법정리는 제외)
			// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
			if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
				extraRoadAddr += data.bname;
			}
			// 건물명이 있고, 공동주택일 경우 추가한다.
			if (data.buildingName !== '' && data.apartment === 'Y') {
				extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
			}

			// 도로명, 지번 조합형 주소가 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
			if (extraRoadAddr !== '') {
				extraRoadAddr = ' (' + extraRoadAddr + ')';
			}

			// 도로명, 지번 주소의 유무에 따라 해당 조합형 주소를 추가한다.
			if (fullRoadAddr !== '') {
				fullRoadAddr += extraRoadAddr;
			}

			return fullRoadAddr;
		},

		viewLayer: function (html) {
			var layer = '.box-public-layer',
				contentLayer = '.box-public-layer-content';

			$(contentLayer).html(html);
			$(layer).bPopup({
				closeClass:'btn-public-layer-close',
				follow: [false, false] //x, y
			});
		},

		viewLayerOld: function (html) {
			var layer = '.box-public-layer',
				contentLayer = '.box-public-layer-content';

			$(contentLayer).html(html);
			$(layer).show();

			$(layer).css({
				'position': 'fixed',
				'left': '50%',
				'top': '50%'
			});

			$(layer).css({
				'margin-left': -$(layer).outerWidth() / 2 + 'px',
				'margin-top': -$(layer).outerHeight() / 2 + 'px'
			});

			$.nfx.click('.btn-public-layer-close', function () {
				$(layer).hide();
			});
		},

		closeLayer: function () {
			$($.nfx.layer).hide();
		},

		show: function(o, sw){
			if(sw) {
				$(o).show();
			}else{
				$(o).hide();
			}
		},

		showHide: function (showObj, hideObj) {
			if($(showObj).length){
				$(showObj).show();

				if(typeof hideObj != "undefined"){
					$(hideObj).hide();
				}
			}
		}
	});

	$.nfx = new nfx();
})(jQuery);

//주민등록번호 유효성 체크
function checkJumin(form_name1, form_name2) {
	var formvalue = form_name1.value + form_name2.value;
	var result;
	var sum = 0;
	var temp = 2;
	for (var i = 0; i <= 11; i++) {
		sum += parseInt(formvalue.substr(i, 1)) * temp;
		temp++;
		if (temp == 10) {
			temp = 2;
		}
	}
	result = parseInt(sum) % 11;
	result = (11 - result) % 10;
	if (result != formvalue.substr(12, 1)) {
		return false;
	} else {
		return true;
	}

	return true;
}

//사업자등록번호 유효성 체크(숫자10자리)
function checkBiz(vencod) {
	var sum = 0;
	var getlist = new Array(10);
	var chkvalue = new Array("1", "3", "7", "1", "3", "7", "1", "3", "5");

	try {
		for (var i = 0; i < 10; i++) {
			getlist[i] = vencod.substring(i, i + 1);
		}

		for (var i = 0; i < 9; i++) {
			sum += getlist[i] * chkvalue[i];
		}

		sum = sum + parseInt((getlist[8] * 5) / 10);
		sidliy = sum % 10;
		sidchk = 0;

		if (sidliy != 0) {
			sidchk = 10 - sidliy;
		} else {
			sidchk = 0;
		}

		if (sidchk != getlist[9]) {
			return false;
		}

		return true;
	} catch (e) {
		return false;
	}
}

//HTML제거 함수
function trimHTML(strHtml) {
	var objRegExp = new RegExp("<html(.*|)<body([^>]*)>", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	var objRegExp = new RegExp("</body(.*)</html>(.*)", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	var objRegExp = new RegExp("<[/]*(div|layer|body|html|head|meta|form|input|select|textarea|base|font|br|p|b|img|embed|object|span|table|tbody|tr|td|embed|u|a|strong|li|em|col|bgsound|script|center|h1|hr|o:p)[^>]*>", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	var objRegExp = new RegExp("<(style|script|title|link)(.*)</(style|script|title)>", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	var objRegExp = new RegExp("<[/]*(scrit|style|title|xmp)>", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	var objRegExp = new RegExp("&nbsp;", "gi");
	strHtml = strHtml.replace(objRegExp, "");

	return strHtml;
}

/* 새창을 화면 가운데 띠움 */
function openCenterWin(w_url, w_title, w_width, w_height, w_resizable, w_scrollbars) {
	var option = "alwaysRaised,toolbar=0,status=0,menubar=0";

	var w_left = (screen.width) ? (screen.width - w_width) / 2 : 100;
	var w_top = (screen.height) ? (screen.height - w_height) / 2 : 100;

	//width 와 height 가 있을때만 화면의 가운데에 표시한다
	if (w_width) option = option + ",width=" + w_width + ",left=" + w_left;
	if (w_height) option = option + ",height=" + w_height + ",top=" + w_top;
	//창크기 조절가능과 스크롤바 표시는 기본적으로 보이지 않으며, 지정할때만 보인다
	option += (w_resizable == true) ? ",resizable=yes" :  ",resizable=no";
	option += (w_scrollbars == true) ? ",scrollbars=yes" :  ",scrollbars=no";

	var new_instance = window.open(w_url, w_title, option, "");
	new_instance.focus();
}

function isObject(form_element_name) {
	val = typeof (form_element_name);

	if (val == "undefined")
		return false;
	else
		return true;
}

//알파벳 체크 함수
function isAlpha(form_element_name, msg) {
	var alpha = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
	var num = '0123456789';
	var alphanum = alpha + num;
	var t = form_element_name.value;

	for (i = 0; i < t.length; i++) {
		if (alphanum.indexOf(t.substring(i, i + 1)) < 0) {
			alert(msg);
			form_element_name.focus();
			return false;
		}
	}

	return true;
}

function moveNextBox(str_size, form_element_name, next_form_element_name) {
	if (form_element_name.value.length >= str_size)
		next_form_element_name.focus();
}

function isEmail(form_element_name) {
	var email = form_element_name.value;
	var regExp = /[0-9a-zA-Z][_0-9a-zA-Z-]*@[_0-9a-zA-Z-]+(\.[_0-9a-zA-Z-]+){1,2}$/;

	if (isFrmEmpty(form_element_name, "이메일 주소를 입력해 주십시오.")) return false;

	if (!email.match(regExp)) {
		alert('올바른 이메일 주소를 입력해 주십시오.');
		form_element_name.focus();
		return false;
	}

	return true;
}

function isValidPassword(form_element) {
	var regex = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,15}/;
	var result = regex.test(form_element.value);

	if (!result) {
		alert("영어,숫자 및 특수 문자 조합으로 6 ~ 15자리 사이의 비밀번호를 입력해 주십시오.");
		form_element.focus();
	}

	return result;
}

//basic function :
function isFrmEmpty(form_element_name, msg) {
	return isFrmEmptyBase(form_element_name, msg, true);
}

function isFrmEmptyBase(form_element_name, msg, focus) {
	if (isFormEement(form_element_name, msg)) {

		if (form_element_name.value == "") {
			alert(msg);
			if (focus) {
				form_element_name.focus();
			}
			return true;
		} else
			return false;
	}
}

//basic function :
function isNumber(form_element_name, msg) {
	if (isFrmEmpty(form_element_name, msg))
		return false;
	else {
		if (!isNaN(form_element_name.value))
			return true;
		else {
			alert(msg);
			form_element_name.focus();
			return false;
		}
	}
}

//basic function :
function isSame(form_element_name1, form_element_name2, msg) {
	if (form_element_name1.value == form_element_name2.value)
		return true;
	else {
		alert(msg);
		form_element_name1.focus();
		return false;
	}
}

function isCheck(form_element, msg) {
	if (isFormEement(form_element, msg)) {
		var is_checked = false;

		if (typeof (form_element.length) == "undefined") {
			if (form_element.checked) {
				is_checked = true;
			}
		} else {
			for (i = 0; i < form_element.length; i++) {
				if (form_element[i].checked) {
					is_checked = true;
					break;
				}
			}
		}

		if (is_checked) {
			return true;
		} else {
			alert(msg);
			return false;
		}
	}
}

function isFormEement(el, msg) {
	if (typeof (el) == 'undefined') {
		alert('[form element check] ' + msg);
		return false;
	}

	return true;
}

function isCheckValue(form_element) {
	var val = '';
	for (i = 0; i < form_element.length; i++) {
		if (form_element[i].checked) {
			val = form_element[i].value;
			break;
		}
	}

	return val;
}

//extend function :
function isNumber3(form_element_num1, form_element_num2, form_element_num3, msg) {
	if (!isNumber(form_element_num1, msg)) return false;
	if (!isNumber(form_element_num2, msg)) return false;
	if (!isNumber(form_element_num3, msg)) return false;

	return true;
}

function isMultiCheck(form_name, form_element, msg) {
	var box = eval("document." + form_name + ".elements['" + form_element + "']");
	if (box.length > 1) {
		is_checked = false;
		for (i = 0; i < box.length; i++) {
			if (box[i].checked == true) {
				is_checked = true;
				break;
			}
		}

		if (!is_checked) {
			alert(msg);
			return false;
		}
	}

	return true;
}

function isArray(form_name, form_element) {
	var form_element2 = eval("document." + form_name + ".elements['" + form_element + "']");
	if (form_element2.length > 1)
		return true;
	else
		return false;
}

//extend function :
function isJumin(form_element_jumin1, form_element_jumin2) {
	if (!isNumber(form_element_jumin1, "주민등록 번호를 숫자로 입력해 주세요")) return false;
	if (!isNumber(form_element_jumin2, "주민등록 번호를 숫자로 입력해 주세요")) return false;

	if (!checkJumin(form_element_jumin1, form_element_jumin2)) {
		alert("주민등록번호가 일치하지 않습니다.");
		form_element_jumin1.focus();
		return false;
	}

	return true;
}

function getCheckedValues(name) {
	var values = '';
	var len = document.getElementsByName(name).length;

	for (var i = 0; i < len; i++) {
		if (document.getElementsByName(name)[i].checked) {
			values += document.getElementsByName(name)[i].value;
			values += (i < len - 1) ? '|' : '';
		}
	}

	return values;
}

function search() {
	var form = document.FRM;
	/*
        if(isObject(form.scolumn)){
            if(isFrmEmpty(form.scolumn, "검색할 분류를 선택해 주십시오"))	return;
        }
    */
	form.submit();
}

function getCookie(name) {
	var nameOfCookie = name + "=";
	var x = 0;
	while (x <= document.cookie.length) {
		var y = (x + nameOfCookie.length);
		if (document.cookie.substring(x, y) == nameOfCookie) {
			if ((endOfCookie = document.cookie.indexOf(";", y)) == -1)
				endOfCookie = document.cookie.length;
			return unescape(document.cookie.substring(y, endOfCookie));
		}
		x = document.cookie.indexOf(" ", x) + 1;
		if (x == 0)
			break;
	}
	return "";
}

function setCookie(name, value, expiredays) {
	var todayDate = new Date();
	todayDate.setDate(todayDate.getDate() + expiredays);
	document.cookie = name + "=" + escape(value) + "; path=/; expires=" + todayDate.toGMTString() + ";"
}

function getCheckedValue(name) {
	var val = '';
	var obj = document.getElementsByName(name);

	if (obj.length >= 1) {
		for (var i = 0; i < obj.length; i++) {
			if (obj[i].checked) {
				val = obj[i].value;
				break;
			}
		}
	}

	return val;
}

function adddate(add_day) {
	var today = new Date();
	var date1 = today.getYear() + "-" + (today.getMonth() + 1 + 100 + "").substring(1, 3) + "-" + (today.getDate() + 100 + "").substring(1, 3);
	var dateinfo = date1.split("-");

	var src = new Date(dateinfo[0], dateinfo[1] - 1, dateinfo[2]);
	src.setDate(src.getDate() + parseFloat(add_day));
	var date2 = src.getYear() + "-" + (src.getMonth() + 1 + 100 + "").substring(1, 3) + "-" + (src.getDate() + 100 + "").substring(1, 3);

	return date2;
}

function zoomZ(img) {
	window.open(getHomePath() + "module/common/zoom.php?img=" + img, "img", "left=100,top=100");
}

function downSvc(t, c, k) {
	location.href = getHomePath() + "module/common/download.php?t=" + t + "&c=" + c + "&k=" + k;
}

function downFile(file_dir, file_name, logical_name) {
	if (typeof (logical_name) == "undefined") {
		logical_name = '';
	}
	location.href = getHomePath() + "module/common/download.php?file_dir=" + file_dir + "&file_name=" + file_name + "&logical_name=" + logical_name;
}

function downBoardFile(bid, bf_idx) {
	location.href = getHomePath() + "module/board/download.php?bid=" + bid + "&bf_idx=" + bf_idx;
}

//즐겨찾기
function add_favorites() {
	var bookmarkURL = window.location.href;
	var bookmarkTitle = document.title;
	var triggerDefault = false;
	if (window.sidebar && window.sidebar.addPanel) { // Firefox version < 23
		window.sidebar.addPanel(bookmarkTitle, bookmarkURL, '');
	} else if ((window.sidebar && (navigator.userAgent.toLowerCase().indexOf('firefox') > -1)) || (window.opera && window.print)) {
		// Firefox version >= 23 and Opera Hotlist var $this = $(this);
		$this.attr('href', bookmarkURL);
		$this.attr('title', bookmarkTitle);
		$this.attr('rel', 'sidebar');
		$this.off(e);
		triggerDefault = true;
	} else if (window.external && ('AddFavorite' in window.external)) {
		// IE Favorite
		window.external.AddFavorite(bookmarkURL, bookmarkTitle);
	} else { // WebKit - Safari/Chrome
		alert((navigator.userAgent.toLowerCase().indexOf('mac') != -1 ? 'Cmd' : 'Ctrl') + '+D 키를 눌러 즐겨찾기에 등록하실 수 있습니다.');
	}
}

function testAutoInput(email) {
	var inputElements = document.getElementsByTagName("input");

	for (var i = 0; i < inputElements.length; i++) {
		switch (inputElements[i].type) {
			case 'checkbox':
			case 'radio':
				inputElements[i].checked = true;
				break;

			case 'text':
				if (inputElements[i].name.indexOf('email') >= 0) {
					if (email != '') {
						var emails = email.split('@');
						if (inputElements[i].name.indexOf('email1') >= 0) {
							inputElements[i].value = emails[0];
						} else if (inputElements[i].name.indexOf('email2') >= 0) {
							inputElements[i].value = emails[1];
						} else {
							inputElements[i].value = '1';
						}
					}
				} else {
					if (inputElements[i].value == '') {
						inputElements[i].value = '1';
					}
				}
				break;

			case 'password':
				if (inputElements[i].value == '') {
					inputElements[i].value = '0000';
				}
				break;
		}
	}

	var inputElements = document.getElementsByTagName("select");

	for (var i = 0; i < inputElements.length; i++) {
		if (typeof inputElements[i].options[1] != 'undefined') {
			inputElements[i].options[1].selected = true;
		}
	}

	var inputElements = document.getElementsByTagName("textarea");

	for (var i = 0; i < inputElements.length; i++) {
		inputElements[i].value = '1';
	}
}

function getHomePath() {
	var page_home = Cookies.get('page_home');
	return (typeof (page_home) === "undefined") ? '/' : page_home;
}

function getQueryString(form, params) {
	var queryString = '';

	$.each(params, function (i, name) {
		queryString += '&' + name + '=' + $('[name=' + name + ']').val();
	});

	return queryString;
}

function get_extension(filename) {
	var parts = filename.split('.');
	return parts[parts.length - 1].toLowerCase();
}

function getWorkSecondList(workId, selectId, selectDiv) {
	const params = {};
	const callback = function (data) {
		if (data.result) {
			let model = JSON.parse(data.model);

			if (model.length === 0) {
				$('#'+selectDiv).html('');
			} else {
				let selectBox = '<select id="'+selectId+'" name="'+selectId+'">';
				$.each(model, function (index, item) {
					selectBox += '<option value="' + item.id + '">' + item.workNameLocale + '</option>';
				});
				selectBox += '</select>';
				$('#'+selectDiv).html(selectBox);
			}
		}
	};
	$.nfx.ajaxGet(params, 'json', callback, "/contentsApi/getWork/"+workId);
}

function selectPopupUser(param) {
	const params = {};

	const callback = function (data) {
		$('#modalSearchSingleUser').find('.popup-con').html(data);
	};

	$.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
}

// 옵션 그룹화 함수
function workCreateOptionGroup(options, parentId) {
	const optgroup = document.createElement('optgroup');
	options.forEach(option => {
		if (option.upId === parentId) {
			const optionElement = document.createElement('option');
			optionElement.value = option.id;
			optionElement.text = option.workNameLocale;
			optgroup.appendChild(optionElement);
			workCreateOptionGroup(options, option.id);
		}
	});
	return optgroup;
}

function hasUpIdGreaterThanZero(data) {
	for (let i = 0; i < data.length; i++) {
		if (data[i].upId > 0) {
			return true;
		}
	}
	return false;
}

function removeAllOptions(id) {
	const selectElement = document.getElementById(id);
	while (selectElement.options.length > 0) {
		selectElement.remove(0);
	}
}

function handleKeyPress(event, id) {
	if (event.key === "Enter") {
		event.preventDefault(); // 엔터 키 기본 동작 방지
		$(id).click(); // btnSearchUser 버튼 클릭 이벤트 발생
	}
}