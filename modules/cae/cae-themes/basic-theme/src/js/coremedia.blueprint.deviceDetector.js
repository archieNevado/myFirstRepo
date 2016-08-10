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
 *
 */
coremedia.blueprint.deviceDetector = (function (module) {

  var $ = coremedia.blueprint.$;
  var $window = $(window);
  var $document = $(document);

  // EVENTS
  var EVENT_PREFIX = "coremedia.blueprint.deviceDetector.";
  module.EVENT_DEVICE_CHANGED = EVENT_PREFIX + "deviceChanged";

  /**
   * store the last device whose specific settings were applied
   * @type {{type: String|undefined, orientation: String|undefined, isTouch: boolean|undefined}}
   */
  var lastDevice = {
    type: undefined,
    orientation: undefined,
    isTouch: undefined
  };

  /**
   * reads the current device type from body:after content defined by css media queries
   * @returns {string} "mobile"|"tablet"|"desktop"
   */
  var detectDeviceType = function () {
    return window.getComputedStyle(document.body, ":after").getPropertyValue("content").replace(/\'|\"/g, "");
  };

  /**
   * reads the current device orientation from body:before content defined by css media queries
   * @returns {string} "portrait"|"landscape"
   */
  var detectDeviceOrientation = function () {
    return window.getComputedStyle(document.body, ":before").getPropertyValue("content").replace(/\'|\"/g, "");
  };

  /**
   * checks if the current device is a touch device which means that swiping is possible but hovering is not.
   * @returns {boolean} true if touch device otherwise false
   */
  var isTouchDevice = function () {
    return 'ontouchstart' in window || navigator.msMaxTouchPoints;
  };

  /**
   * returns the current device
   * @returns {{type: String|undefined, orientation: String|undefined, isTouch: boolean|undefined}}
   */
  module.getLastDevice = function () {
    return {
      type: lastDevice.type,
      orientation: lastDevice.orientation,
      isTouch: lastDevice.isTouch
    };
  };

  /**
   * Updates the device detection. If device has changed device specific settings are applied.
   */
  var update = function () {
    var newDevice = {
      type: detectDeviceType(),
      orientation: detectDeviceOrientation(),
      isTouch: isTouchDevice()
    };
    if (lastDevice.type === undefined
            || lastDevice.orientation === undefined
            || lastDevice.isTouch === undefined
            || lastDevice.type !== newDevice.type
            || lastDevice.orientation !== newDevice.orientation
            || lastDevice.isTouch !== newDevice.isTouch ) {
      $document.trigger(coremedia.blueprint.deviceDetector.EVENT_DEVICE_CHANGED, [newDevice, lastDevice]);

      lastDevice.type = newDevice.type;
      lastDevice.orientation = newDevice.orientation;
      lastDevice.isTouch = newDevice.isTouch;
    }
  };

  /**
   * inits the device detector
   */
  module.init = function () {
    $window.smartresize(function () {
      update();
    });
    // delay initial update after all other document ready functions have been called
    setTimeout(function () {
      update();
    }, 1);
  };
  return module;
}(coremedia.blueprint.deviceDetector || {}));
