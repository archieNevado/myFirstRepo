/* --- create own namespace in javascript for own stuff ------------------------------------------------------------- */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  /*global jQuery*/
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

/**
 *
 */
coremedia.blueprint.nodeDecorationService = function (module) {

  var $ = coremedia.blueprint.$;

  /**
   * specifies functionalities to be applied if decorateNode is called
   * @type {Array}
   */
  var nodeDecorators = [];

  /**
   * specifies functionalities to be removed if undecorateNode is called
   * @type {Array}
   */
  var nodeUndecorators = [];

  var getSelectorFunction = function (selector, handler) {
    return function ($target) {
      $target.findAndSelf(selector).each(function () {
        handler($(this));
      });
    };
  };

  var getDataFunction = function (baseConfig, identifier, handler) {
    return function ($target) {
      var selector = "[data-" + identifier + "]";
      $target.findAndSelf(selector).each(function () {
        var $this = $(this);
        var config = $.extend({}, baseConfig, $this.data(identifier));
        var state = $.extend({}, $this.data(identifier + "-state"));
        handler($this, config, state);
        if (!$.isEmptyObject(state)) {
          $this.data(identifier + "-state", state);
        }
      });
    };
  };

  /**
   * Adds a node decorator to list of node decorators
   *
   * @param {nodeDecoratorCallback=} nodeDecorator
   * @param {nodeDecoratorCallback=} nodeUndecorator
   */
  module.addNodeDecorator = function (nodeDecorator, nodeUndecorator) {
    nodeDecorator && nodeDecorators.push(nodeDecorator);
    nodeUndecorator && nodeUndecorators.push(nodeUndecorator);
  };
  /**
   * @callback nodeDecoratorCallback
   * @param {jQuery} $target
   */

  /**
   * Adds a node decorator and already performs selection tasks
   *
   * @param {String} selector
   * @param {nodeDecoratorBySelectorCallback=} decorationHandler
   * @param {nodeDecoratorBySelectorCallback=} undecorationHandler
   */
  module.addNodeDecoratorBySelector = function (selector, decorationHandler, undecorationHandler) {
    decorationHandler && nodeDecorators.push(getSelectorFunction(selector, decorationHandler));
    undecorationHandler && nodeUndecorators.push(getSelectorFunction(selector, undecorationHandler));
  };
  /**
   * @callback nodeDecoratorBySelectorCallback
   * @param {jQuery} $target
   */

  /**
   * Adds a node decorator and already performs selection, configuration and state tasks.
   *
   * @param {object} baseConfig
   * @param {String} identifier
   * @param {nodeDecoratorByDataCallback=} decorationHandler
   * @param {nodeDecoratorByDataCallback=} undecorationHandler
   */
  module.addNodeDecoratorByData = function (baseConfig, identifier, decorationHandler, undecorationHandler) {
    decorationHandler && nodeDecorators.push(getDataFunction(baseConfig, identifier, decorationHandler));
    undecorationHandler && nodeUndecorators.push(getDataFunction(baseConfig, identifier, undecorationHandler));
  };
  /**
   * @callback nodeDecoratorByDataCallback
   * @param {jQuery} $target
   * @param {object} config
   * @param {object} state
   */

  /**
   * Applies node decorators to target node
   *
   * @param {object|jQuery} node can be plain DOM-Node or JQuery Wrapped DOM-Node
   */
  module.decorateNode = function (node) {
    var $target;
    if (node instanceof $) {
      $target = node;
    } else {
      $target = $(node);
    }
    nodeDecorators.forEach(function (functionality) {
      functionality($target);
    });
  };

  /**
   * Applies node undecorators to target node
   *
   * @param {object|jQuery} node can be plain DOM-Node or JQuery Wrapped DOM-Node
   */
  module.undecorateNode = function (node) {
    var $target;
    if (node instanceof $) {
      $target = node;
    } else {
      $target = $(node);
    }
    nodeUndecorators.forEach(function (functionality) {
      functionality($target);
    });
  };

  return module;
}(coremedia.blueprint.nodeDecorationService || {});
