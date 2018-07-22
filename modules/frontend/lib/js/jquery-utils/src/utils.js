import * as bem from "./utils.bem";
import $ from "jquery";

export { bem };

/*! Utils Plugin | Copyright (c) CoreMedia AG */

/**
 * Executes a jQuery find on the given $self but also takes $self into account (in addition to its children).
 *
 * @param $self {jQuery} the jQuery result to search on
 * @param selector {string} the selector to search for
 * @return {jQuery} the search result as jQuery result
 */
export function findAndSelf($self, selector) {
  return $self.filter(selector).add($self.find(selector));
}

/**
 * Finds relative to the given $self or in whole DOM based on existence of ">" in selector.
 *
 * @param $self {jQuery} the element to perform the relative search from
 * @param selector the selector
 * @returns {jQuery} the search result of the given selector
 */
export function findRelativeOrAbsolute($self, selector) {
  if (typeof selector === typeof "string" && /^\s*[>|+~]/.test(selector)) {
    return $self.find(selector);
  }
  return $self.constructor(selector);
}

/**
 * Extend jQuery Ajax Function
 *
 * @param {Object} options
 * @returns $.ajax()
 */
export function ajax(options) {
  /* always set xhr headers for CORS */
  const cmOptions = {
    headers: { "X-Requested-With": "XMLHttpRequest" },
    xhrFields: { withCredentials: true },
    global: false,
    url: undefined,
  };

  options = $.extend({}, cmOptions, options);

  // IE9 does not support CORS w/ credentials, so make sure the host matches the current host
  const isIE9 = /MSIE (9.\d+);/.test(navigator.userAgent);
  if (isIE9 && options.url !== undefined) {
    options.url = options.url.replace(
      /\/\/([^/]+)\/(.+)/,
      "//" + window.location.host + "/$2"
    );
    // set Origin header if not present and url is absolute
    const isAbsolute = new RegExp("^([a-z]+://|//)");
    if (
      options.headers["Origin"] === undefined &&
      isAbsolute.test(options.url)
    ) {
      options.headers["Origin"] =
        window.location.protocol +
        "//" +
        window.location.hostname +
        (window.location.port ? ":" + window.location.port : "");
    }
  }

  return $.ajax(options);
}
