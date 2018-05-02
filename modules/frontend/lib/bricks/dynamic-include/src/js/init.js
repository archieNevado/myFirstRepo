import * as logger from "@coremedia/js-logger";
import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";

import {
  default as Handler,
  BASE_CONFIG as HANDLER_BASE_CONFIG,
} from "./hashBasedFragment.Handler";
import {
  default as Link,
  BASE_CONFIG as LINK_BASE_CONFIG,
} from "./hashBasedFragment.Link";
import {
  default as Form,
  BASE_CONFIG as FORM_BASE_CONFIG,
} from "./hashBasedFragment.Form";

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function() {
  // handle hashBasedFragmentHandler
  nodeDecorationService.addNodeDecoratorByData(
    HANDLER_BASE_CONFIG,
    "hash-based-fragment-handler",
    // decorate
    function($handler, handlerConfig, state) {
      try {
        state.instance = new Handler($handler, handlerConfig);
      } catch (error) {
        logger.log(error);
      }
    },
    // undecorate
    function($handler, handlerConfig, state) {
      state.instance && state.instance.destroy();
    }
  );

  // handle hashBasedFragmentLinks
  nodeDecorationService.addNodeDecoratorByData(
    LINK_BASE_CONFIG,
    "hash-based-fragment-link",
    function($link, linkConfig) {
      new Link($link, linkConfig);
    }
  );

  // handle hashBasedFragmentForms
  nodeDecorationService.addNodeDecoratorByData(
    FORM_BASE_CONFIG,
    "hash-based-fragment-form",
    function($form, formConfig) {
      new Form($form, formConfig);
    }
  );
});
