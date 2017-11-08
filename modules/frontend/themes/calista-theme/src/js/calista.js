import $ from "jquery";
import * as logger from "@coremedia/js-logger";

const EVENT_PREFIX = "coremedia.blueprint.calista.";
const EVENT_CHECK_LOGIN_STATUS = EVENT_PREFIX + "loginStatusChecked";
const LINK_PLACEHOLDER = "NEXT_URL_PLACEHOLDER";

/**
 * Set url parameter in login/logout links for WCS navigation
 * @param $btn
 */
function urlParams($btn) {
  let btnUrl = $btn.attr('href');
  if (!btnUrl) {
    return;
  }

  let nexturl = encodeUrlForWcsAndSpring(window.location.href);

  btnUrl = btnUrl.replace(LINK_PLACEHOLDER, nexturl);
  $btn.attr('href', btnUrl);
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
  const loginStatusURL = $('.cm-header__login').data('cm-loginstatus');
  const $loginBtn = $('#cm-login');
  const $logoutBtn = $('#cm-logout');

  if (loginStatusURL) {
    $.ajax({
      url: loginStatusURL,
      dataType: 'json',
      headers: {'X-Requested-With': 'XMLHttpRequest'},
      xhrFields: {withCredentials: true},
      global: false
    }).done(function (data) {
      if (data.loggedIn) {
        $logoutBtn.css('display', 'inline-block');
        urlParams($logoutBtn);
      } else {
        $loginBtn.css('display', 'inline-block');
        urlParams($loginBtn);
      }
      $document.trigger(EVENT_CHECK_LOGIN_STATUS);
    }).fail(function () {
      logger.error(`Login error!`);
    });
  }
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------

$(function () {
  "use strict";

  logger.log("Welcome to CoreMedia Calista Integration");

  /* --- Touch detection --- */
  const deviceAgent = navigator.userAgent.toLowerCase();
  let isTouchDevice = (deviceAgent.match(/(iphone|ipod|ipad)/) || deviceAgent.match(/(android)/) || deviceAgent.match(/(iemobile)/) || deviceAgent.match(/iphone/i) || deviceAgent.match(/ipad/i) || deviceAgent.match(/ipod/i) || deviceAgent.match(/blackberry/i) || deviceAgent.match(/bada/i));

  /* --- Navigation --- */
  const $navbar = $('#navbar');
  const $navigationEntry = $('.cm-navigation > .cm-navigation-item__list > .cm-navigation-item');
  const $navigationRoot = $(".cm-navigation > ul.cm-navigation-item__list");

  $navbar.on('show.bs.collapse', function () {
    $('body').addClass('fixed');
  });
  $navbar.on('hidden.bs.collapse', function () {
    $('body').removeClass('fixed');
  });
  $navigationEntry.mouseover(function () {
    $navigationRoot.addClass("cm-navigation--hovered");
  });
  $navigationEntry.mouseout(function () {
    $navigationRoot.removeClass("cm-navigation--hovered");
  });
  $navigationEntry.on('click', function (e) {
    if (isTouchDevice) {
      e.preventDefault();
    }
  });

  // Previously hovered menus could still be visible since they won't disappear until the end of their transition.
  // To make sure that only one menu is visible, we need to set the opacity of all other menus to 0.
  $navigationEntry.mouseover(function () {
    $navigationEntry.not(this).each(function() {
      const $this = $(this);
      $this.find("ul.cm-navigation-item__list").css("opacity",0);
      $this.css("border-bottom-width", 0);
    });
    $navigationEntry.find("ul.cm-navigation-item__list").css("opacity",1);
    $(this).css("border-bottom-width", 4);
  });

  /* --- Mobile Search --- */
  const $search = $('#cmSearchWrapper');
  const $searchInput = $('.search_input');
  $('.mobile-search, .cm-search-form__close').on('click', function () {
    $search.toggleClass('open');
    if ($search.hasClass('open')) {
      $searchInput.focus();
    }
  });
  $('#cm-search-form__button').on('click', function (e) {
    if($searchInput.val().length === 0){
      e.preventDefault();
      $searchInput.focus();
    }
  });

  /* --- Login --- */
  handleLogin();

});
