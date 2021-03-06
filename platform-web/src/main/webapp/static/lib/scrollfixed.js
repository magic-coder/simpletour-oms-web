/**
 * Created by L on 15/3/15.
 */
;
(function ($) {
    $.ScrollFixed = function (el, options) {
        var base = this;
        base.$el = $(el);
        base.el = el;
        var target = base.$el;
        var original_left = parseInt(target.css('left'));
        var original_right = parseInt(target.css('right'));

        var _fix_position = function () {
            if (base.options.fixed == 'right') {
                target.css('right', ($(window).scrollLeft() + $(window).width() - $(document).width() + original_right) + 'px');
            } else if (base.options.fixed == 'left') {
                target.css('left', (original_left - $(window).scrollLeft()) + 'px');
            }
        };

        var windowResize = function () {
            _fix_position();
        };
        var windowScroll = function () {
            _fix_position();
        };

        base.init = function () {
            base.options = $.extend({}, $.ScrollFixed.defaultOptions, options);
            $(window).resize(windowResize);
            $(window).scroll(windowScroll);
            _fix_position();
        };

        base.init();
    };

    $.ScrollFixed.defaultOptions = {
        fixed: 'left'
    };
    $.fn.scrollFixed = function (options) {
        return this.each(function () {
            (new $.ScrollFixed(this, options));
        });
    };
})(jQuery);