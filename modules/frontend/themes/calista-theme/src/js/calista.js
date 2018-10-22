import $ from "jquery";
import { ajax } from "@coremedia/js-jquery-utils";
import * as logger from "@coremedia/js-logger";
import * as deviceDetector from "@coremedia/js-device-detector";
import { EVENT_LAYOUT_CHANGED } from "@coremedia/js-basic";

const EVENT_PREFIX = "coremedia.blueprint.calista.";
const EVENT_CHECK_LOGIN_STATUS = EVENT_PREFIX + "loginStatusChecked";
const LINK_PLACEHOLDER = "NEXT_URL_PLACEHOLDER";

/**
 * Set url parameter in login/logout links for WCS navigation
 * @param $btn
 */
function urlParams($btn) {
  let btnUrl = $btn.attr("href");
  if (!btnUrl) {
    return;
  }

  let nexturl = encodeUrlForWcsAndSpring(window.location.href);

  btnUrl = btnUrl.replace(LINK_PLACEHOLDER, nexturl);
  $btn.attr("href", btnUrl);
}

/**
 * <p>
 * Due to using the URL query parameter to hand over to WCS we have to adapt the value to be correctly encoded to
 * keep the original query parameters otherwise WCS will truncate them and we will lose necessary query parameters.
 * </p>
 *
 * <p>
 * "correctly encoded" means:
 * </p>
 * <ul>
 *   <li> Once encoded, that the WCS can decode it and place it in the hidden input field </li>
 *   <li> second time encoded, that all query parameters are still encoded after WCS decoding </li>
 *   <li> after those complete encodings we must encode the `/` character a third time to still have a double encoded `/` to
 *    make spring find our handler.  </li>
 * </ul>
 * @param $url
 * @returns {string}
 */
function encodeUrlForWcsAndSpring($url) {
  let $urlencodedurl = encodeURIComponent($url);
  let $doubleencodedurl = encodeURIComponent($urlencodedurl);

  let doubleEncodedSlash = encodeURIComponent(encodeURIComponent("/"));
  let tripleEncodedSlash = encodeURIComponent(doubleEncodedSlash);

  return $doubleencodedurl.split(doubleEncodedSlash).join(tripleEncodedSlash);
}

/**
 * Handle Login State
 */
function handleLogin() {
  const $document = $(document);
  const loginStatusURL = $(".cm-header__login").data("cm-loginstatus");
  const $loginBtn = $("#cm-login");
  const $logoutBtn = $("#cm-logout");

  if (loginStatusURL) {
    ajax({
      url: loginStatusURL,
      dataType: "json",
    })
      .done(function(data) {
        if (data.loggedIn) {
          $logoutBtn.css("display", "inline-block");
          urlParams($logoutBtn);
        } else {
          $loginBtn.css("display", "inline-block");
          urlParams($loginBtn);
        }
        $document.trigger(EVENT_CHECK_LOGIN_STATUS);
      })
      .fail(function() {
        logger.error(`Login error!`);
      });
  }
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------

$(function() {
  "use strict";

  logger.log("Welcome to CoreMedia Calista Integration");

  // init device detection
  deviceDetector.init();

  const $navbar = $("#navbar");
  $navbar.on("show.bs.collapse", function() {
    $("body").addClass("fixed");
  });
  $navbar.on("hidden.bs.collapse", function() {
    $("body").removeClass("fixed");
  });

  /* --- Mobile Search --- */
  const $search = $("#cmSearchWrapper");
  const $searchInput = $(".search_input");
  $(".cm-search__open-mobile-search-button, .cm-search-form__close").on(
    "click",
    function() {
      $search.toggleClass("open");
      if ($search.hasClass("open")) {
        $searchInput.focus();
      }
    }
  );
  $("#cm-search-form__button").on("click", function(e) {
    if ($searchInput.val().length === 0) {
      e.preventDefault();
      $searchInput.focus();
    }
  });

  /* --- Login --- */
  handleLogin();

  /* --- Search --- */
  $(".cm-wcs-tabs__tab a").on("click touch", function() {
    // trigger layout changed for responsive images, if search results are hidden
    if ($("#cmsSearchResultTab").is(":hidden")) {
      logger.log("open content search");
      setTimeout(function() {
        $(document).trigger(EVENT_LAYOUT_CHANGED);
      }, 100);
    }
    // disable browser history in search
    $("body").data("cm-search-disable-browser-history", "true");
  });
});
