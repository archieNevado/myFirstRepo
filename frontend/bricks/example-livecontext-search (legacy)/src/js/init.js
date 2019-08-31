import $ from "jquery";

import { EVENT_NODE_APPENDED } from "@coremedia/brick-dynamic-include";
import * as nodeDecorationService from "@coremedia/brick-node-decoration-service";
import { popup } from "@coremedia-examples/brick-livecontext-header";
import { EVENT_LAYOUT_CHANGED, ajax, findAndSelf } from "@coremedia/brick-utils";
import {
  EVENT_DEVICE_CHANGED,
  getLastDevice,
} from "@coremedia/brick-device-detector";

function applySearchAsPopup($target) {
  // find header icon for search
  const $headerSearchIcon = findAndSelf($target, ".cm-header-icon--search");
  // search form is placed below the search icon
  const $headerSearchForm = $headerSearchIcon.find(".cm-search-form");

  // it no longer acts as icon...
  $headerSearchIcon.removeClass("cm-header-icon");
  // submit button in search form has a different icon for desktop
  $headerSearchForm
    .find(".cm-search-form__button > i")
    .removeClass("icon-arrow-right")
    .addClass("icon-search");
  // attach form to search icon without being wrapped into popup
  $headerSearchForm.appendTo(".cm-search");
}

function removeSearchAsPopup($target) {
  // find header icon for search
  const $headerSearchIcon = findAndSelf($target, ".cm-header-icon--search");
  // search form is placed below the search icon
  const $headerSearchForm = $headerSearchIcon.find(".cm-search-form");

  $headerSearchForm.appendTo(".cm-popup--search");
  $headerSearchForm
    .find(".cm-search-form__button > i")
    .removeClass("icon-search")
    .addClass("icon-arrow-right");
  $headerSearchIcon.addClass("cm-header-icon");
}

const $document = $(document);
const DESKTOP_DEVICE_TYPE = "desktop";

// update images on device change
$document.on(EVENT_DEVICE_CHANGED, function(event, newDevice, oldDevice) {
  if (
    oldDevice.type === DESKTOP_DEVICE_TYPE &&
    newDevice.type !== DESKTOP_DEVICE_TYPE
  ) {
    removeSearchAsPopup($document);
  }
  if (
    newDevice.type === DESKTOP_DEVICE_TYPE &&
    oldDevice.type !== DESKTOP_DEVICE_TYPE
  ) {
    applySearchAsPopup($document);
  }
  $document.trigger(EVENT_LAYOUT_CHANGED);
});

// device settings need to be reapplied if DOM changes
$document.on(EVENT_NODE_APPENDED, function(event, $node) {
  const device = getLastDevice();
  if (device.type === DESKTOP_DEVICE_TYPE) {
    applySearchAsPopup($node);
  }
});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function() {
  const $document = $(document);

  // initialize search form
  nodeDecorationService.addNodeDecorator(function($target) {
    const baseConfig = { urlSuggestions: undefined, minLength: undefined };
    findAndSelf($target, ".cm-search--form").each(function() {
      const $search = $(this);
      const config = $.extend(baseConfig, $search.data("cm-search"));
      const $popupSuggestions = $(this).find(".cm-popup--search-suggestions");
      const $listSuggestions = $(this).find(".cm-search-suggestions");
      const $suggestion = $listSuggestions
        .find(".cm-search-suggestions__item")
        .clone();
      const $prototypeSuggestion = $suggestion.clone();
      let lastQuery = undefined;

      // remove the sample suggestion from dom
      $suggestion.remove();
      $search.find(".cm-search__form-input").bind("input", function() {
        const $input = $(this);
        const value = $input.val();
        popup.close($popupSuggestions);
        // only show suggestions if the search text has the minimum length
        if (value.length >= config.minLength) {
          // clear suggestions
          nodeDecorationService.undecorateNode($listSuggestions);
          $listSuggestions.html("");
          // save last query
          lastQuery = value;
          ajax({
            url: config.urlSuggestions,
            dataType: "json",
            data: {
              type: "json",
              query: value,
            },
          }).done(function(data) {
            // in case ajax calls earlier ajax calls receive their result later, only show most recent results
            if (lastQuery === value) {
              const classNonEmpty = "cm-search-suggestions--non-empty";
              $listSuggestions.removeClass(classNonEmpty);
              // transform search suggestions into dom elements
              $.map(data, function(item) {
                $listSuggestions.addClass(classNonEmpty);
                const $suggestion = $prototypeSuggestion.clone();
                $listSuggestions.append($suggestion);
                $suggestion.html(
                  "<b>" + value + "</b>" + item.label.substr(value.length)
                );
                // attribute must exist, otherwise selector will not match
                $suggestion.attr("data-cm-search-suggestion", "");
                // set attribute for jquery (not visible in dom)
                $suggestion.data("cm-search-suggestion", {
                  form: ".cm-search--form",
                  target: ".cm-search__form-input",
                  value: item.value,
                  popup: ".cm-popup--search-suggestions",
                });
                nodeDecorationService.decorateNode($suggestion);
              });
              // show search suggestions
              popup.open($popupSuggestions);
              // set focus back to input element
              $input.focus();
              $document.trigger(EVENT_NODE_APPENDED, [$suggestion]);
            }
          });
        }
      });
    });
  });

  // initializes search suggestions
  nodeDecorationService.addNodeDecorator(function($target) {
    // read configuration
    const baseConfig = {
      form: undefined,
      target: undefined,
      value: undefined,
      popup: undefined,
    };
    const identifier = "cm-search-suggestion";
    const selector = "[data-" + identifier + "]";

    findAndSelf($target, selector).each(function() {
      const $suggestion = $(this);
      const config = $.extend(baseConfig, $suggestion.data(identifier));
      const $popup = $(config.popup);
      // when clicking search suggestions form should be filled with the suggestion and be submitted
      $suggestion.bind("click", function() {
        popup.close($popup);
        $(config.target).val(config.value);
        $(config.form).submit();
      });
    });
  });
});
