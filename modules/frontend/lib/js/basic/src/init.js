import $ from "jquery";
import { debounce } from "@coremedia/js-utils";
import * as logger from "@coremedia/js-logger";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import {
  EVENT_LAYOUT_CHANGED,
  EVENT_NODE_APPENDED,
  ajax,
  renderFragmentHrefs,
} from "./basic";
import * as dropdown from "./basic.dropdown";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as responsive from "./basic.responsive";

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function() {
  const $window = $(window);
  const $document = $(document);

  // append to dom ready (will be executed after all dom ready functions have finished)
  $(function() {
    nodeDecorationService.decorateNode(document);
  });

  // load all dynamic fragments. The special header X-Requested-With is needed by the CAE to identify
  // the request as an Ajax request
  nodeDecorationService.addNodeDecorator(function($target) {
    const identifier = "cm-fragment";
    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function() {
      const $fragment = $(this);
      const url = $(this).data(identifier);
      ajax({
        url: url,
        dataType: "text",
      }).done(function(html) {
        const $html = $(html);
        nodeDecorationService.undecorateNode($fragment);
        $fragment.replaceWith($html);
        nodeDecorationService.decorateNode($html);
        $document.trigger(EVENT_NODE_APPENDED, [$html]);
      });
    });
  });

  // this will substitute all data-hrefs rendered by ESI
  nodeDecorationService.addNodeDecorator(renderFragmentHrefs);

  // initializes the drop down menu
  nodeDecorationService.addNodeDecorator(function($target) {
    const selector = ".cm-dropdown";
    findAndSelf($target, selector).each(function() {
      $(this).on(dropdown.EVENT_DROPDOWN_CHANGED, function() {
        $document.trigger(EVENT_LAYOUT_CHANGED);
      });
      dropdown.init(this);
    });
  });

  // adds removes spinner if an image has finished loading
  nodeDecorationService.addNodeDecoratorBySelector(
    ".cm-image--loading",
    function($target) {
      const callback = function() {
        $target.removeClass("cm-image--loading");
      };
      if (typeof $.fn.imagesLoaded === typeof callback) {
        $target.imagesLoaded(callback);
      } else {
        $target.on("load", callback);
      }
    }
  );

  // handle closing of notification box
  nodeDecorationService.addNodeDecorator(function($target) {
    const selector = ".cm-notification__dismiss";
    findAndSelf($target, selector).click(function() {
      $(this)
        .closest(".cm-notification")
        .fadeOut();
    });
  });

  // add readmore functionality if text is too long
  nodeDecorationService.addNodeDecoratorByData(
    { lines: undefined },
    "cm-readmore",
    function($target, config) {
      const blockReadMore = "cm-readmore";
      // read the line height for the given target
      let lineHeight = $target.css("line-height");
      // only proceed if config is valid and lineHeight could be retrieved
      if (config.lines !== undefined && lineHeight !== undefined) {
        const $wrapper = $target.find("." + blockReadMore + "__wrapper");
        const $buttonbar = $target.find("." + blockReadMore + "__buttonbar");
        const $buttonMore = $buttonbar.find(
          "." + blockReadMore + "__button-more"
        );
        const $buttonLess = $buttonbar.find(
          "." + blockReadMore + "__button-less"
        );

        // calculate line height in px
        if (lineHeight.indexOf("px") > -1) {
          // line height is already in px, just remove the unit
          lineHeight = lineHeight.replace("px", "");
        } else {
          // line height is relative to font-size, calculate line height by multiplying its value with font-size
          lineHeight = lineHeight * $target.css("font-size").replace("px", "");
        }
        const maxHeight = Math.floor(lineHeight * config.lines);
        // enable readmore functionality if text without the readmore button exceeds the maximum height
        // it would make no sense to add a readmore button if it would take more space as rendering the full text
        if ($wrapper.height() - 2 * $buttonbar.height() > maxHeight) {
          $target.addClass(blockReadMore + "--enabled");
          // default without any action by the user ist the non expanded (less) version
          $target.addClass(blockReadMore + "--less");
          $wrapper.css("max-height", maxHeight);
          $buttonMore.on("click", function() {
            $target.removeClass(blockReadMore + "--less");
            $target.addClass(blockReadMore + "--more");
            $wrapper.css("max-height", "");
            $document.trigger(EVENT_LAYOUT_CHANGED);
          });
          $buttonLess.on("click", function() {
            $target.removeClass(blockReadMore + "--more");
            $target.addClass(blockReadMore + "--less");
            $wrapper.css("max-height", maxHeight);
            $document.trigger(EVENT_LAYOUT_CHANGED);
          });
          $buttonLess.on("click", function() {
            $target.removeClass(blockReadMore + "--more");
            $target.addClass(blockReadMore + "--less");
            $wrapper.css("max-height", maxHeight);
            $document.trigger(EVENT_LAYOUT_CHANGED);
          });
          $document.trigger(EVENT_LAYOUT_CHANGED);
        }
      }
    }
  );

  // initially updates new non-adaptive boxes
  nodeDecorationService.addNodeDecorator(function($target) {
    const identifier = "cm-non-adaptive-content";
    const selector = "[data-" + identifier + "]";
    const imageSelector = "img" + selector;
    findAndSelf($target, imageSelector).each(function() {
      const image = this;
      const $image = $(image);
      const callback = function() {
        responsive.updateNonAdaptiveImage(image);
      };
      if (typeof $.fn.imagesLoaded === typeof callback) {
        $image.imagesLoaded(callback);
      } else {
        $image.on("load", callback);
      }
    });
    const videoSelector = "iframe" + selector + ", video" + selector;
    findAndSelf($target, videoSelector).each(function() {
      responsive.updateNonAdaptiveVideo(this);
    });
  });

  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.on(
    "resize",
    {},
    debounce(function() {
      logger.log("Window resized");
      $document.trigger(EVENT_LAYOUT_CHANGED);
    })
  );

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  $document.on(EVENT_LAYOUT_CHANGED, responsive.updateLayout);
});
