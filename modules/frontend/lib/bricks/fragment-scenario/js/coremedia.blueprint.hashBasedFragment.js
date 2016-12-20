/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  /*global jQuery*/
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

coremedia.blueprint.hashBasedFragment = (function (module) {

  var $ = coremedia.blueprint.$;

  module.requestParamsToString = function (requestParams) {
    var result = "";
    for (var name in requestParams) {
      if (!requestParams.hasOwnProperty(name)) {
        continue;
      }
      if (result.length > 1) {
        result += "&";
      }
      result += encodeURIComponent(name);
      if (typeof requestParams[name] !== typeof undefined) {
        result += "=" + encodeURIComponent(requestParams[name]);
      }
    }
    return result;
  };

  module.stringToRequestParams = function (string, validParameters) {
    validParameters = validParameters || [];
    var requestParams = {};
    var hashParams = string.split("&");
    $.each(hashParams, function (_, parameter) {
      var keyValue = parameter.split("=", 2);
      var key = keyValue[0];
      var value = keyValue[1];
      if (key && validParameters.indexOf(key) > -1) {
        var decodedKey = decodeURIComponent(key);
        if (typeof value !== typeof undefined) {
          requestParams[decodedKey] = decodeURIComponent(value);
        } else {
          requestParams[decodedKey] = "";
        }
      }
    });

    return requestParams;
  };

  return module;
}(coremedia.blueprint.hashBasedFragment || {}));
coremedia.blueprint.hashBasedFragment.Handler = (function (module) {
  "use strict";

  // PRIVATE STATIC
  var $ = coremedia.blueprint.$;
  var $window = $(window);

  var STATE_DATA_ATTRIBUTE_NAME = "hash-based-fragment-handler-state";

  /**
   * baseUrl must always be given
   * if global is set to true a selector must be specified
   * @param handlerConfig
   * @returns {boolean}
   */
  var validateHandlerConfig = function (handlerConfig) {
    return handlerConfig.baseUrl
            && (!handlerConfig.fragmentContainer
            || handlerConfig.fragmentContainer.global === false
            || handlerConfig.fragmentContainer.global === true && handlerConfig.fragmentContainer.selector);
  };

  module = function (element, config) {
    this._element = element;
    this._config = config;
    this._lastRequestParams = undefined;
    this._windowListener = undefined;
    this._disabled = false;

    this._init();
  };

  // PUBLIC
  module.prototype.disable = function () {
    this._disabled = true;
  };

  module.prototype.enable = function () {
    this._disabled = false;
  };

  module.prototype._getFragmentContainer = function () {
    if (!this._config.fragmentContainer.selector) {
      return this._element;
    }
    if (this._config.fragmentContainer.global) {
      return $.find(this._config.fragmentContainer.selector);
    } else {
      return this._handler.find(this._config.fragmentContainer.selector);
    }
  };

  // private
  module.prototype._requestParamsChanged = function (newRequestParams) {
    if (!this._lastRequestParams && newRequestParams || this._lastRequestParams && !newRequestParams) {
      return true;
    }
    var name;
    for (name in this._lastRequestParams) {
      if (!this._lastRequestParams.hasOwnProperty(name)) {
        continue;
      }
      if (this._lastRequestParams[name] !== newRequestParams[name]) {
        return true;
      }
    }
    for (name in newRequestParams) {
      if (!newRequestParams.hasOwnProperty(name)) {
        continue;
      }
      if (this._lastRequestParams[name] !== newRequestParams[name]) {
        return true;
      }
    }
    return false;
  };

  // private
  module.prototype._changeRef = function (requestParams) {
    if (this._disabled) {
      return;
    }
    var $fragmentContainer = this._getFragmentContainer();
    // disable underlying hashBasedFragmentHandlers so no unnecessary requests are triggered
    var $subHandlers = $fragmentContainer.find(":data(" + STATE_DATA_ATTRIBUTE_NAME + ")");
    $subHandlers.each(function () {
      $(this).data(STATE_DATA_ATTRIBUTE_NAME).instance.disable();
    });
    var requestConfig = {
      url: this._config.baseUrl,
      params: requestParams
    };
    var that = this;
    coremedia.blueprint.basic.updateTargetWithAjaxResponse($fragmentContainer, requestConfig, false, function (jqXHR) {
      if (jqXHR.status === 200) {
        // only handle modified parameters if header prefix is given
        if (that._config.modifiedParametersHeaderPrefix) {
          var requestChanged = false;
          $.each(that._config.validParameters, function (_, validParameter) {
            var modifierParameter = jqXHR.getResponseHeader(that._config.modifiedParametersHeaderPrefix + validParameter);
            if (modifierParameter) {
              requestChanged = true;
              requestParams[validParameter] = modifierParameter;
            }
          });

          if (requestChanged) {
            // adjust state so no reload is triggered
            that._lastRequestParams = requestParams;
            var newHash = "#" + coremedia.blueprint.hashBasedFragment.requestParamsToString(requestParams);
            if (history.replaceState) {
              history.replaceState({}, "", newHash);
            }
          }
        }
      } else {
        $subHandlers.each(function () {
          $(this).data(STATE_DATA_ATTRIBUTE_NAME).instance.enable();
        });
      }
    });
  };

  // private
  module.prototype._handleHashChange = function (newHash) {
    var requestParams = coremedia.blueprint.hashBasedFragment.stringToRequestParams(newHash.replace(/^#/, "") || "", this._config.validParameters);
    if (this._requestParamsChanged(requestParams)) {
      this._lastRequestParams = requestParams;
      this._changeRef(requestParams);
    }
  };

  // PUBLIC

  module.BASE_CONFIG = {
    baseUrl: undefined,
    validParameters: [],
    modifiedParametersHeaderPrefix: undefined,
    fragmentContainer: {
      selector: undefined,
      global: false
    }
  };

  module.prototype._init = function () {

    // validate configuration
    if (!validateHandlerConfig(this._config)) {
      throw("Invalid handler configuration");
    }

    var hash = window.location.hash;
    this._handleHashChange(hash);

    var that = this;
    this._windowListener = function () {
      that._handleHashChange(window.location.hash);
    };
    $window.on("hashchange", this._windowListener);
  };

  module.prototype.destroy = function () {

    // validate configuration
    if (!validateHandlerConfig(this._config)) {
      coremedia.blueprint.logger.log("Invalid configuration:", this._config);
      return;
    }

    if (this._windowListener) {
      $window.off("hashchange", this._windowListener);
      this._windowListener = undefined;
    }
  };
  return module;
}(coremedia.blueprint.hashBasedFragment.Handler || function () {}));

coremedia.blueprint.hashBasedFragment.Link = (function (module) {

  module = function($link, linkConfig) {
    $link.attr("href", "#" + coremedia.blueprint.hashBasedFragment.requestParamsToString(linkConfig.requestParams));
  };

  module.BASE_CONFIG = {
    requestParams: []
  };

  return module;
}(coremedia.blueprint.hashBasedFragment.Link || function () {}));

coremedia.blueprint.hashBasedFragment.Form = (function (module) {

  var $ = coremedia.blueprint.$;

  module = function($form) {
    $form.on("submit", function(e) {
      e.preventDefault();
      var requestParams = {};
      var fields = $form.serializeArray();
      $.each(fields, function(i, field) {
        requestParams[field.name] = field.value;
      });
      window.location.hash = "#" + coremedia.blueprint.hashBasedFragment.requestParamsToString(requestParams);
    });
  };

  module.BASE_CONFIG = {
  };

  return module;
}(coremedia.blueprint.hashBasedFragment.Form || function () {}));


// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  // "Imports"
  var Handler = coremedia.blueprint.hashBasedFragment.Handler;
  var Link = coremedia.blueprint.hashBasedFragment.Link;
  var Form = coremedia.blueprint.hashBasedFragment.Form;

  // handle hashBasedFragmentHandler
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          Handler.BASE_CONFIG,
          "hash-based-fragment-handler",
          // decorate
          function ($handler, handlerConfig, state) {
            try {
              state.instance = new Handler($handler, handlerConfig);
            } catch (error) {
              coremedia.blueprint.logger.log(error);
            }
          },
          // undecorate
          function ($handler, handlerConfig, state) {
            state.instance && state.instance.destroy();
          }
  );

  // handle hashBasedFragmentLinks
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          Link.BASE_CONFIG,
          "hash-based-fragment-link",
          function ($link, linkConfig) {
            new Link($link, linkConfig);
          }
  );

  // handle hashBasedFragmentForms
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          Form.BASE_CONFIG,
          "hash-based-fragment-form",
          function ($form, formConfig) {
            new Form($form, formConfig);
          }
  );

});
