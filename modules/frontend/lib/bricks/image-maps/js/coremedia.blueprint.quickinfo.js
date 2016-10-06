/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

/**
 * Quickinfo functionality
 */
coremedia.blueprint.quickInfo = function (module) {

  var $ = coremedia.blueprint.$;

  // identifier/class name definitions
  var prefix = "cm-";
  var prefixcss = "." + prefix;
  var identifier = prefix + "quickinfo";
  var classQuickInfoActive = identifier + "--active";

  // prefix/namespace for events in this module
  var EVENT_PREFIX = "coremedia.blueprint.quickinfo.";

  /**
   * @type {string} name of the event to be triggered if quick info has changed
   */
  module.EVENT_QUICKINFO_CHANGED = EVENT_PREFIX + "quickInfoChanged";

  /**
   * Return the configuration of the given quickinfo
   * @param {jQuery} $quickinfo quickinfo
   * @returns {object} configuration configuration of the quickinfo
   */
  var getConfig = function ($quickinfo) {
    return $.extend({modal: false, group: undefined}, $quickinfo.data(identifier));
  };
  var isActive = function ($quickinfo) {
    return $quickinfo.hasClass(classQuickInfoActive);
  };

  /**
   * Opens a quickinfo
   * @param $quickinfo the quickinfo to be opened
   */
  module.show = function ($quickinfo) {
    if (!isActive($quickinfo)) {
      var config = getConfig($quickinfo);
      if (config.group !== undefined) {
        // notify all other quickinfos in group
        $("[data-" + identifier + "]").not($quickinfo).each(function () {
          module.groupHide($(this), config.group);
        });
      }
      $quickinfo.addClass(classQuickInfoActive);
      if (config.modal) {
        // use magnificPopup to open quickinfo as full screen overlay
        $.magnificPopup.open({
          closeBtnInside: false,
          items: {
            src: $quickinfo,
            type: "inline"
          },
          callbacks: {
            close: function () {
              module.hide($quickinfo);
            }
          }
        });
      }
      $quickinfo.trigger(module.EVENT_QUICKINFO_CHANGED, [true]);
    }
  };

  /**
   * Hides a quickinfo
   * @param $quickinfo the quickinfo to be hidden
   */
  module.hide = function ($quickinfo) {
    if (isActive($quickinfo)) {
      var config = getConfig($quickinfo);
      var buttons = $quickinfo.find(prefixcss + "button");
      var transition = [];

      if (config.modal) {
        // close full screen overlay
        $.magnificPopup.close();
      }

      // Disable transition feature for cm-button
      $.each(buttons, function () {
        transition.push($(this).css("transition"));
        $(this).css("transition", "none");
      });

      // Hide quick in
      $quickinfo.removeClass(classQuickInfoActive);
      $quickinfo.trigger(module.EVENT_QUICKINFO_CHANGED, [false]);

      // Re-enable transition feature for cm-button
      $.each(buttons, function (index, button) {
        transition.push($(this).css("transition"));
        $(button).css("transition", transition[index]);
      });
    }
  };

  /**
   * Opens a quickinfo if it is hidden or hides a quickinfo if it is shown
   * @param $quickinfo the quickinfo to be toggled
   */
  module.toggle = function ($quickinfo) {
    if (isActive($quickinfo)) {
      module.hide($quickinfo);
    } else {
      module.show($quickinfo);
    }
  };

  /**
   * Hides a quickinfo if it is in the given group
   *
   * @param $quickinfo the quickinfo to be hidden if it is in the given group
   * @param group the given group
   */
  module.groupHide = function ($quickinfo, group) {
    if (group === getConfig($quickinfo).group) {
      module.hide($quickinfo);
    }
  };

  module.closeQuickInfo = function ($quickInfo) {
    $quickInfo.find(prefixcss + "quickinfo__close").click(function () {
      module.hide($quickInfo);
    });
  };

  module.toggleQuickInfo = function ($button, $config) {
    $button.click(function () {
      var $quickInfo = $("#" + $config.target);
      module.toggle($quickInfo);
      return false;
    });

  };

  return module;
}(coremedia.blueprint.quickInfo || {});