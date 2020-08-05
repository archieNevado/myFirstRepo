import $ from "jquery";
import * as utils from "@coremedia/brick-utils";
import "./cartPopup";
import { addNodeDecoratorBySelector } from "@coremedia/brick-node-decoration-service";

const EVENT_PREFIX = "coremedia.blueprint.base.";
const EVENT_CHECK_LOGIN_STATUS = EVENT_PREFIX + "loginStatusChecked";

const $document = $(document);

/**
 * Set url parameter in login/logout links for WCS navigation
 * @param $btn
 */
function urlParams($btn) {
  let btnUrl = $btn.attr("href");
  if (!btnUrl) {
    return;
  }
  $btn.attr("href", btnUrl + "&URL=" + window.location.href);
}

/**
 * Handle Login State
 */
function handleLogin() {
  const loginStatusURL = $("[data-cm-loginstatus]").data("cm-loginstatus");
  const $loginBtn = $("#cm-login");
  const $logoutBtn = $("#cm-logout");

  if (loginStatusURL) {
    utils
      .ajax({
        url: loginStatusURL,
        dataType: "json",
      })
      .done(function (data) {
        if (data.loggedIn) {
          $logoutBtn.css("display", "inline-block");
          urlParams($logoutBtn);
        } else {
          $loginBtn.css("display", "inline-block");
          urlParams($loginBtn);
        }
        $document.trigger(EVENT_CHECK_LOGIN_STATUS);
      })
      .fail(function (e) {
        utils.error(
          `Error in AJAX login status check.`,
          e.status,
          loginStatusURL
        );
      });
  }
}

addNodeDecoratorBySelector(".cm-wcs-tabs", () => {
  const $tabs = $(".cm-wcs-tabs__tab a");
  /* --- Search --- */
  $tabs.on("click touch", function (event) {
    event.preventDefault();

    const $clickedLink = $(this);
    $tabs.each(function () {
      const $link = $(this);
      const $tab = $link.closest(".cm-wcs-tabs__tab");
      const $tabPanel = $($link.attr("href"));

      if ($link.is($clickedLink)) {
        $tab.addClass("active");
        $tabPanel.addClass("active");
      } else {
        $tabPanel.removeClass("active");
        $tab.removeClass("active");
      }
    });

    $document.trigger(utils.EVENT_LAYOUT_CHANGED);

    // disable browser history in search
    $("body").data("cm-search-disable-browser-history", "true");
  });
});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------

$(function () {
  "use strict";

  utils.log("Welcome to CoreMedia Calista Integration");

  /* --- Login --- */
  handleLogin();

  // update tabs in wcs (e.g. pdp)
  $(".tab_container").on("click", function () {
    $document.trigger(utils.EVENT_LAYOUT_CHANGED);
  });
});
