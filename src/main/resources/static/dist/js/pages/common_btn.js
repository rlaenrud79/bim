$(document).ready(function() {
    var commonBtn = (
        {
            init: function () {
                commonBtn.addClickEventListener('.popup-button', function () {
                    commonBtn.popOpen($(this).data("modal"), $( this ).data( "link" ));
                });

                commonBtn.addClickEventListener('#itemCheck', function () {
                    $(".list-item-checkbox").prop("checked",($(this).prop("checked")));
                });

                commonBtn.addClickEventListener('.btn-checkbox', function () {
                    if($(this).hasClass('active')) return;
                    var groupName = $(this).data('name');
                    $(".btn-"+groupName).removeClass('active');
                    $(this).addClass('active');
                    $('input[name="'+groupName+'"]').val($(this).data('code'));
                });

                $('textarea.text-limit').keyup(function() {
                    // 텍스트영역의 길이를 체크
                    var textLength = $(this).val().length;
                    var textCountLimit = $(this).data('length');

                    // 입력된 텍스트 길이를 #textCount 에 업데이트 해줌
                    $('.text-limit-count').text(textLength);

                    // 제한된 길이보다 입력된 길이가 큰 경우 제한 길이만큼만 자르고 텍스트영역에 넣음
                    if (textLength > textCountLimit) {
                        $(this).val($(this).val().substr(0, textCountLimit));
                    }
                });

                commonBtn.addEventListener('keypress', 'input.txt-search-keyword', function () {
                    if (event.keyCode == 13) {
                        search();
                    }
                });
            },

            popOpen : function (id, link){
                if($.trim(link) !== ''){
                    $(id + ' .popup-content').empty();
                    $.get(link, function(data) {
                        $(id + ' .popup-content').html(data);
                    });
                }
                $(id).show();
            },

            popupSearch : function (obj){
                if($.trim(obj.data('link')) !== ''){
                    var popupContentLayer = obj.closest('.popup-wrap').find('.popup-content');
                    popupContentLayer.empty();
                    $.get(obj.data('link'), function(data) {
                        popupContentLayer.html(data);
                    });
                }
            },

            addClickEventListener: function (id, func) {
                if ($(id).length) {
                    $(id).off();
                    $(id).click(func);
                }
            },

            addEventListener: function (event_name, id, func) {
                if($(id).length) {
                    $(id).off();
                    $(id).on(event_name, func);
                }
            },

            getData: function(url, params, dataType, callback){
                $.ajax({
                    url: url,
                    dataType: dataType,
                    data: params,
                    type: "post",
                    processData: false,
                    contentType: false,
                    success: function(data){
                        callback(data);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert("["+xhr.status+"] 다시 확인해 주십시오.");
                    }
                });
            }
        }
    );

    commonBtn.init();
});
