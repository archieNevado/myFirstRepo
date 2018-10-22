import $ from "jquery";
import { error, log } from "@coremedia/js-logger";
import { EVENT_LAYOUT_CHANGED } from "@coremedia/js-basic";
import { updateTarget } from "@coremedia/brick-dynamic-include";

/**
 * Loads more search results (next page) via ajax below the search results.
 *
 * @param {string} button
 * @param {string} searchResultsContainerId
 */
export function loadSearchResults(
  button,
  searchResultsContainerId = "cm-search-results"
) {
  let $button = $(button);
  let $searchResultsContainer = $("#" + searchResultsContainerId);
  log("Load more search results via ajax.", $button, $searchResultsContainer);
  //hide button
  $button.hide();
  //show spinner
  $button.next().show();
  // load more results
  log("ajaxUrl", $button.data(searchResultsContainerId));
  $.ajax({
    url: $button.data(searchResultsContainerId),
  })
    .done(function(nextSearchResults) {
      log("Loaded search results successfully.");
      // append the new results to the the search result page
      $searchResultsContainer.append(nextSearchResults);
      // trigger event for layout changes to load responsive images
      $(document).trigger(EVENT_LAYOUT_CHANGED);
      //hide spinner
      $button.next().hide();
    })
    .fail(function() {
      error("Could not load more search results.");
      // show button again for retry
      $button.next().hide();
      $button.show();
    });
}

/**
 * Loads search results via ajax with the given URL.
 *
 * @param link string
 * @param searchResultPageId {string}
 * @param enableBrowserHistory {boolean}, default is true
 */
export function loadSearchResultPage(
  link,
  searchResultPageId = "cm-search-results-page",
  enableBrowserHistory = true
) {
  if (link) {
    let $searchResultPageId = $("#" + searchResultPageId);
    log("Load search result page via ajax.", link, $searchResultPageId);
    let $searchResultsContainer = $("#cm-search-results");
    $searchResultsContainer.addClass("cm-search__results--loading");
    // load results page
    $.ajax({
      url: link,
    })
      .done(function(nextSearchResults) {
        log("Loaded search result page successfully.");
        // append the new results to the the search result page
        updateTarget($searchResultPageId, $(nextSearchResults), true);
        $(document).trigger(EVENT_LAYOUT_CHANGED);
        //set new page url to browser history
        addToBrowserHistory(link, enableBrowserHistory);
      })
      .fail(function() {
        $searchResultsContainer.removeClass("cm-search__results--loading");
        error("Could not load search result page.");
      });
  }
}

/**
 * Default wrapper function to handle dom elements or jQuery selectors
 *
 * @param domElementOrJQueryResult
 * @param searchResultsContainerId
 */
export default function(domElementOrJQueryResult, searchResultsContainerId) {
  if (domElementOrJQueryResult instanceof $) {
    $.each(domElementOrJQueryResult, function(index, item) {
      loadSearchResults(item, searchResultsContainerId);
    });
  } else {
    loadSearchResults(domElementOrJQueryResult, searchResultsContainerId);
  }
}

/**
 * Helper function to remove parameter from URL
 *
 * @param {string} url
 * @param {string} parameter
 * @returns {string} url without parameter
 */
function removeURLParameter(url, parameter) {
  let urlParts = url.split("?");
  if (urlParts.length >= 2) {
    let prefix = encodeURIComponent(parameter) + "=";
    let parameters = urlParts[1].split(/[&]/g);
    for (let i = parameters.length; i-- > 0; ) {
      if (parameters[i].lastIndexOf(prefix, 0) !== -1) {
        parameters.splice(i, 1);
      }
    }
    url = urlParts[0] + "?" + parameters.join("&");
  }
  return url;
}

/**
 * Helper function to add a state to the browser history, if enabled
 *
 * @param link {string} the CAE URL of the search
 * @param enableBrowserHistory {boolean}
 */
function addToBrowserHistory(link, enableBrowserHistory) {
  if (enableBrowserHistory) {
    log("Add search to browser history");
    window.history.pushState(
      { id: "search", link: link },
      "",
      removeURLParameter(link, "view")
    );
  }
}
