/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

/**
 *  CoreMedia Blueprint Javascript Framework
 *  including following functions
 *
 *  - infiniteScroll
 *  - toggle
 *  - dropdown
 *  - accordion
 */
coremedia.blueprint.basic = function (module) {

  var $ = coremedia.blueprint.$;
  var $document = $(document);

  // Events
  var EVENT_PREFIX = "coremedia.blueprint.basic.";
  module.EVENT_LAYOUT_CHANGED = EVENT_PREFIX + "layoutChanged";
  module.EVENT_NODE_APPENDED = EVENT_PREFIX + "nodeAppended";

  /**
   * Redirects the user to given redirectUrl if the redirectUrl is not part of the current
   * url to prevent infinite loops.
   *
   * @param redirectUrl
   */
  module.redirectTo = function(redirectUrl) {
    // prevent infinite loop of redirects
    if (window.location.href.indexOf(redirectUrl) < 0) {
      window.location.href = redirectUrl + "?next=" + encodeURI(window.location.href);
    }
  };

  /**
   * Replace "$nextUrl$" in all data-href and store as href attribute.
   * Assumes that if the page contains a form with a nextUrl hidden input field, the form is already loaded.
   *
   * @param {jQuery} $target
   */
  module.renderFragmentHrefs = function ($target) {
    var nextUrl;
    if (window.location.pathname.match(/^\/dynamic\//) || window.location.pathname.match(/^\/blueprint\/servlet\/dynamic\//)) {
      // we are inside a web flow, try to find "nextUrl" hidden input field value, else leave nextUrl blank
      nextUrl = $('input:hidden[name="nextUrl"]').val() || "";
    } else {
      // for all other pages, take the current page as the next page after login
      nextUrl = window.location.href;
      //remove current scheme in case the scheme is changed before the redirect
      nextUrl = nextUrl.replace(/^(http|https):(.+)/, "$2");
    }

    var selector = "a[data-href]";
    $target.findAndSelf(selector).each(function () {
      var $this = $(this);
      $this.attr("href", $this.data("href").replace(/\$nextUrl\$/g, encodeURIComponent(nextUrl)));
    });
  };

  /**
   * Changes a given target
   * @param $target The target the update is to be applied to
   * @param $update The update to add to DOM
   * @param replaceTarget if TRUE target will be replaced with the given target, otherwise only inner nodes will be removed
   */
  module.updateTarget = function ($target, $update, replaceTarget) {
    if (replaceTarget) {
      coremedia.blueprint.nodeDecorationService.undecorateNode($target);
      $target.replaceWith($update);
    } else {
      $target.children().each(function () {
        coremedia.blueprint.nodeDecorationService.undecorateNode(this);
      });
      $target.empty().append($update);
    }
    coremedia.blueprint.nodeDecorationService.decorateNode($update);
    $document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$update]);
  };

  /**
   * Extend jQuery Ajax Function
   *
   * @param {object} options
   * @returns $.ajax()
   */
  module.ajax = function (options) {
    /* always set xhr headers for CORS */
    var cmOptions = {
      headers: {'X-Requested-With': 'XMLHttpRequest'},
      xhrFields: { withCredentials: true },
      global: false,
      url: undefined
    };

    options = $.extend({}, cmOptions, options);

    // IE9 does not support CORS w/ credentials, so make sure the host matches the current host
    var isIE9 = /MSIE (9.\d+);/.test(navigator.userAgent);
    if (isIE9 && options.url !== undefined) {
      options.url = options.url.replace(/\/\/([^/]+)\/(.+)/, "//" + window.location.host + "/$2");
      // set Origin header if not present and url is absolute
      var isAbsolute = new RegExp("^([a-z]+://|//)");
      if (options.headers["Origin"] === undefined && isAbsolute.test(options.url)) {
        options.headers["Origin"] = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port: "");
      }
    }

    return $.ajax(options);
  };

  /**
   * Updates a given target with the result of the provided url.
   *
   * Only the last triggered update will have an effect if multiple updates are triggered without waiting for
   * the ajax request to be finished.
   *
   * @param $target target to be updated
   * @param requestConfig the request config to be used containing
   *        url: the url to retrieve the new target from
   *        params: additional request params (optional)
   *        method: the request method (optional, defaults to GET)
   * @param replaceTarget (default) true, if false replaces only the child elements of the target
   * @param {updateTargetWithAjaxResponseCallback} callback to be triggered on success
   */
  module.updateTargetWithAjaxResponse = function ($target, requestConfig, replaceTarget, callback) {
    requestConfig = $.extend({ url: undefined, params: {}, method: "GET"}, requestConfig);

    if (typeof replaceTarget === "undefined") {
      replaceTarget = true;
    }
    if (requestConfig.url !== undefined) {
      var FRAGMENT_REQUEST_COUNTER = "cm-fragment-request-counter";
      var FRAGMENT_LOADING_CLASS = "cm-fragment--loading";
      var requestId = ($target.data(FRAGMENT_REQUEST_COUNTER) || 0) + 1;
      $target.data(FRAGMENT_REQUEST_COUNTER, requestId);

      var isOutdated = function () {
        // if $target is no longer in DOM or the request is not the current latest request: ignore update
        return (!$.contains(document.documentElement, $target[0]))
                || (requestId !== $target.data(FRAGMENT_REQUEST_COUNTER));
      };

      $target.addClass(FRAGMENT_LOADING_CLASS);
      coremedia.blueprint.basic.ajax({
        type: requestConfig.method,
        url: requestConfig.url,
        data: requestConfig.params,
        dataType: "text"
      }).done(function (data, _, jqXHR) {
        if (isOutdated()) {
          return;
        }
        var $html = undefined;
        if (jqXHR.status === 200) {
          $html = $(data);
          module.updateTarget($target, $html, replaceTarget);
        }
        if (callback) {
          callback(jqXHR, $html);
        }
      }).fail(function (jqXHR) {
        if (callback) {
          callback(jqXHR);
        }
      }).always(function () {
        if (isOutdated()) {
          return;
        }
        $target.removeClass(FRAGMENT_LOADING_CLASS);
      });
    }
  };
  /**
   * @callback updateTargetWithAjaxResponseCallback
   * @param jqXHR the jQuery XHR object
   * @param {jQuery} $html the new html if the request was successful
   */

  /**
   * Refreshes a refreshable fragment by reading its configuration.
   *
   * @param $fragment the refreshable fragment to refresh
   * @param callback to be triggered on success
   * @param requestParams additional request params
   */
  module.refreshFragment = function ($fragment, callback, requestParams) {
    var config = $.extend({"url": undefined}, $fragment.data("cm-refreshable-fragment"));
    var requestConfig = {
      url: config.url,
      params: requestParams
    };
    module.updateTargetWithAjaxResponse($fragment, requestConfig, true, callback);
  };

  return module;
}(coremedia.blueprint.basic || {});

/**
 * Generic infinite scroll functionality
 */
coremedia.blueprint.basic.infiniteScroll = function (module) {

  var $ = coremedia.blueprint.$;

  /**
   * Setup a new scrollbox.
   *
   * @scrollbox {object} scrollbox to be used (must contain child-element with class "scrollwrapper")
   * @hasNext {function()} callback function called to determine if there are more items to be loaded
   * @addData {function(function())} callback function called if there are items to add. has a callback as param indicating that data is added
   * @additionalSpace {number} defines the additionalSpace to be added to the scrollbox to indicate that there is "more" in pixels
   */
  module.init = function (scrollbox, hasNext, addData, additionalSpace) {
    scrollbox.overflow = "overlay";

    /**
     * Refresh the infinite scroll
     * If hasNext() function returns true infinite scroll functionality is added else removed
     *
     * @param {object} scrollbox
     */
    function refresh(scrollbox) {
      var $scrollbox = $(scrollbox);
      // set dimensions of scrollwrapper(s) inside scrollbox
      $scrollbox.find(".scrollwrapper").each(function () {
        var $this = $(this);
        // save old scrolling position
        var backup = this.scrollTop;
        // by default height is set to "auto" indicating that there is no more content
        $this.height("auto");
        // if callback returns that there is more content
        if (hasNext()) {
          // extend height by additional space configured
          $this.height($this.height() + additionalSpace);
        }
        // restore old scrolling position
        this.scrollTop = backup;
      });
    }

    // by default loading of data by scrolling is not locked
    var loadLock = false;

    // bind trigger to scroll event of scrollbox
    $(scrollbox).on("scroll", function () {
      // only perform checks if loading of data is not locked
      if (!loadLock) {
        // detect if scrollBox is scrolled down to the bottom of the wrapper (only react in that case)
        if (hasNext() && (this.scrollHeight - this.scrollTop) === $(this).height()) {
          // lock loading of data
          loadLock = true;
          // trigger given callback function
          addData(function () {
            // refresh scrollbox
            refresh(scrollbox);
            // release the lock
            loadLock = false;
          });
        }
      }
    });

    // initiate scrollbox by refreshing
    refresh(scrollbox);
  };

  return module;
}(coremedia.blueprint.basic.infiniteScroll || {});

/**
 *
 */
coremedia.blueprint.basic.dropdown = function (module) {

  var $ = coremedia.blueprint.$;

  var classMain = "cm-dropdown";
  var classMenu = "cm-dropdown-menu";
  var classMenuOpened = "cm-dropdown-menu--active";
  var classMenuSubOpened = "cm-dropdown-menu--opened";
  var classItem = "cm-dropdown-item";
  var classItemLeaf = "cm-dropdown-item--leaf"; // new: defines, that the menu item is a leaf (has no submenus)
  var classButton = "cm-dropdown-button";
  var classButtonOpen = "cm-dropdown-button--open";
  var classButtonClose = "cm-dropdown-button--close";
  var classMenuLevel = "cm-dropdown-menu--level";
  var classMenuMinLevel = "cm-dropdown-menu--min-level";
  var classItemLevel = "cm-dropdown-item--level";
  var classItemMinLevel = "cm-dropdown-item--min-level";
  var classButtonLevel = "cm-dropdown-button--level";
  var classButtonMinLevel = "cm-dropdown-button--min-level";

  var EVENT_PREFIX = "coremedia.blueprint.basic.dropdown.";
  module.EVENT_DROPDOWN_CHANGED = EVENT_PREFIX + "dropdownChanged";

  /**
   * Sets the state of an menu or menu item
   *
   * @param {object} item menu or menu item
   * @param {string} state "opened", "sub-opened" or ""
   */
  module.setState = function (item, state) {
    var $item = $(item);
    if (state == "opened" || state === "sub-opened") {
      $item.addClass(classMenuSubOpened);
    }
    if (state == "sub-opened") {
      $item.removeClass(classMenuOpened);
    }
    if (state === "opened") {
      $item.addClass(classMenuOpened);
    }
    if (state === "") {
      $item.removeClass(classMenuOpened);
      $item.removeClass(classMenuSubOpened);
    }
  };
  /**
   * Opens the delivered menu.
   *
   * @param menu The menu to be opened
   */
  module.open = function (menu) {
    var $menu = $(menu);
    var $root = $(menu).closest("." + classMain);

    var additionalClassButtonOpen = $root.data("dropdown-class-button-open");
    if (additionalClassButtonOpen === undefined) {
      additionalClassButtonOpen = "";
    }
    var additionalClassButtonClose = $root.data("dropdown-class-button-close");
    if (additionalClassButtonClose === undefined) {
      additionalClassButtonClose = "";
    }

    // Full reset

    // remove open or sub-open from all menus
    $root.find("." + classMenu).each(function () {
      module.setState(this, "");
    });

    var $items = $root.find("." + classItem);

    // remove open or sub-open from all menu items
    $items.each(function () {
      module.setState(this, "");
    });

    // add is-leaf to all items
    $items.addClass(classItemLeaf);

    // set open for all openclose buttons having submenu (there can be more than one dropdown-menu-openclose per menu)
    $items.has("." + classMenu).find("." + classButton + ":first").each(function () {
      var $item = $(this).parent(":first");

      // indicate that item is no leaf
      $item.removeClass(classItemLeaf);

      $item.children("." + classButton).each(function () {
        var $this = $(this);
        $this.removeClass(additionalClassButtonClose);
        $this.removeClass(classButtonClose);
        $this.addClass(classButtonOpen);
        $this.addClass(additionalClassButtonOpen);
      });
    });

    module.setState(menu, "opened");

    // set sub-opened to all parent menus
    $menu.parents("." + classMenu).each(function () {
      module.setState(this, "sub-opened");
    });

    // set sub-opened to all parent menu items
    // set close to openclose buttons of menu item
    $menu.parents("." + classItem).each(function () {
      module.setState(this, "sub-opened");
      $(this).find("." + classButton + ":first").each(function () {
        $(this).parent(":first").children("." + classButton).each(function () {
          var $this = $(this);
          $this.removeClass(additionalClassButtonOpen);
          $this.removeClass(classButtonOpen);
          $this.addClass(classButtonClose);
          $this.addClass(additionalClassButtonClose);
        });
      });
    });

    // set opened to parent menu item if menu is not the root menu
    if (!$menu.hasClass(classMain)) {
      module.setState($menu.parent(":first"), "opened");
    }

    $root.trigger(module.EVENT_DROPDOWN_CHANGED, [menu]);
  };
  /**
   * Closes the delivered menu.
   *
   * @param {object} menu The menu to be closed
   */
  module.close = function (menu) {
    var parent = menu.parents("." + classMenu + ":first");
    // closing a menu is the same as opening the parent menu
    module.open(parent);
  };
  /**
   * Initializes a dropdown menu
   *
   * @param {object} menu The menu to be initialized
   */
  module.init = function (menu) {
    var $menu = $(menu);

    // the root menu itsself is a dropdown-menu
    $menu.addClass(classMenu);

    // add classes for menu and items if selectors are specified
    var selectorMenus = $menu.data("dropdown-menus");
    if (typeof selectorMenus !== "undefined") {
      $menu.find(selectorMenus).addClass(classMenu);
    }
    var selectorItems = $menu.data("dropdown-items");
    if (typeof selectorItems !== "undefined") {
      $menu.find(selectorItems).addClass(classItem);
    }

    // every menu items get an openclose button (initialized with no action to be performed)
    $menu.find("." + classItem).prepend("<button class=\"" + classButton + "\"></button>");

    // recursively add levels
    var addLevel = function (menu, level) {
      var $menu = $(menu);
      $menu.addClass(classMenuLevel + level);
      for (var i = 1; i <= level; i++) {
        $menu.addClass(classMenuMinLevel + i);
      }
      var $items = $menu.children("." + classItem);
      $items.each(function () {
        var $item = $(this);
        $item.addClass(classItemLevel + level);
        for (var i = 1; i <= level; i++) {
          $item.addClass(classItemMinLevel + i);
        }

        // min 0, max 1
        $item.children("." + classButton).each(function () {
          var $button = $(this);
          $button.addClass(classButtonLevel + level);
          for (var i = 1; i <= level; i++) {
            $button.addClass(classButtonMinLevel + i);
          }
        });
        $item.children("." + classMenu).each(function () {
          addLevel(this, level + 1);
        });
      });
    };
    addLevel(menu, 1);

    // open the menu to be initialized
    module.open(menu);

    // bind click-listener to openclose button
    $menu.find("." + classButton).bind("click", function () {
      var $this = $(this);
      var $parent = $(this).closest("." + classItem).find("." + classMenu + ":first");
      if ($this.hasClass(classButtonOpen)) {
        module.open($parent);
      } else if ($this.hasClass(classButtonClose)) {
        module.close($parent);
      }
      return true;
    });

    // bind delegation from empty link to openclose button
    $menu.find("." + classItem + " > a").each(function () {
      var $this = $(this);
      if (!$this.attr("href")) {
        $this.bind("click", function () {
          $this.closest("." + classItem).find("." + classButton + ":first").trigger("click");
          return false;
        });
      }
    });
  };
  return module;
}(coremedia.blueprint.basic.dropdown || {});

/**
 * Accordion functionality
 */
coremedia.blueprint.basic.accordion = function (module) {

  var $ = coremedia.blueprint.$;
  var $document = $(document);

  // class name definitions
  var classAccordionItem = "cm-accordion-item";
  var classAccordionItemHeader = classAccordionItem + "__header";
  var classAccordionItemContent = classAccordionItem + "__content";
  var classAccordionItemHeaderActive = classAccordionItemHeader + "--active";
  var classAccordionItemContentActive = classAccordionItemContent + "--active";

  // prefix/namespace for events in this module
  var EVENT_PREFIX = "coremedia.blueprint.basic.accordion.";

  /**
   * @type {string} Name for the event to be triggered if accordion has changed
   */
  module.EVENT_ACCORDION_CHANGED = EVENT_PREFIX + "accordionChanged";

  /**
   * Changes the active item of the given accordion to the given item
   * @param {jQuery} $accordion the accordion to change
   * @param {jQuery} $activeItem the item to be active
   */
  module.change = function ($accordion, $activeItem) {
    $accordion.find(".cm-accordion-item").not($activeItem).each(function () {
      var $item = $(this);
      $item.find("." + classAccordionItemHeader).first().removeClass(classAccordionItemHeaderActive);
      $item.find("." + classAccordionItemContent).first().removeClass(classAccordionItemContentActive);
    });
    $activeItem.find("." + classAccordionItemHeader).first().addClass(classAccordionItemHeaderActive);
    $activeItem.find("." + classAccordionItemContent).first().addClass(classAccordionItemContentActive);
    $accordion.trigger(module.EVENT_ACCORDION_CHANGED, [$activeItem]);
    $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
  };

  return module;
}(coremedia.blueprint.basic.accordion || {});

/**
 * Popup functionality
 */
coremedia.blueprint.basic.popup = function (module) {

  // identifier/class name definitions
  var identifier = "cm-popup";
  var classPopupActive = identifier + "--active";

  // prefix/namespace for events in this module
  var EVENT_PREFIX = "coremedia.blueprint.basic.popup.";

  /**
   * @type {string} name of the event to be triggered if popup has changed
   */
  module.EVENT_POPUP_CHANGED = EVENT_PREFIX + "popupChanged";

  /**
   * Opens the given popup
   * @param {jQuery} $popup popup to be opened
   */
  module.open = function ($popup) {
    $popup.addClass(classPopupActive);
    $popup.trigger(module.EVENT_POPUP_CHANGED, [true]);
  };

  /**
   * Closes the given popup
   * @param {jQuery} $popup popup to be closed
   */
  module.close = function ($popup) {
    $popup.removeClass(classPopupActive);
    $popup.trigger(module.EVENT_POPUP_CHANGED, [false]);
  };

  /**
   * Opens the popup if it is closed and closes the popup if it is opened
   * @param $popup popup to be toggled
   */
  module.toggle = function ($popup) {
    if ($popup.hasClass(classPopupActive)) {
      module.close($popup);
    } else {
      module.open($popup);
    }
  };

  return module;
}(coremedia.blueprint.basic.popup || {});

coremedia.blueprint.basic.responsive = function (module) {

  var $ = coremedia.blueprint.$;

  var resetNonAdaptiveContent = function($content) {
    $content.parent().removeClass("cm-non-adaptive-content-wrapper");
    $content.removeClass("cm-non-adaptive-content");
    $content.removeClass("cm-non-adaptive-content--fit-height");
    $content.css("margin-top", "");
    $content.css("margin-left", "");
  };

  /**
   * private function to adjust the size of non-adaptive content
   *
   * @param {jQuery} $content jQuery wrapped dom element to be adjusted
   * @param {number} baseRatio ratio that the content should be adjusted to
   * @param {number} boxRatio ratio the content currently has
   * @param {boolean} allowOverflow defines overflow behaviour of the adjustment:
   *                  if true content will be cut to fit the box after proper resizing
   *                  if false content will not be cut after proper resizing creating horizontal or veritical borders
   */
  var adjustNonAdaptiveContent = function ($content, baseRatio, boxRatio, allowOverflow) {
    // add class cm-non-adaptive-content
    $content.addClass("cm-non-adaptive-content");
    // add class to parent container
    $content.parent().addClass("cm-non-adaptive-content-wrapper");

    var adjustment;

    // detect if a horizontal repositioning is needed
    if ((allowOverflow && baseRatio > boxRatio) || (!allowOverflow && baseRatio <= boxRatio)) {
      // horizontal repositioning is needed
      adjustment = (1 - baseRatio / boxRatio) / 2;
      $content.addClass("cm-non-adaptive-content--fit-height");
      // adjust positioning to the left to match the expected result using percentage (for responsive layout)
      $content.css("margin-top", "");
      $content.css("margin-left", (adjustment * 100) + "%");
    } else {
      // vertical repositioning is needed
      adjustment = ((1 / boxRatio - 1 / baseRatio) / 2);
      $content.removeClass("cm-non-adaptive-content--fit-height");
      // adjust positioning to the top to match the expected result using percentage (for responsive layout)
      $content.css("margin-top", (adjustment * 100) + "%");
      $content.css("margin-left", "");
    }
  };


  /**
   * update a single given non-adaptive image
   * @param {Image} image dom node
   */
  module.updateNonAdaptiveImage = function (image) {
    var $image = $(image);

    resetNonAdaptiveContent($image);

    var config = $.extend({overflow: false}, $image.data("cm-non-adaptive-content"));
    var $box = $image.parent();

    var baseImage = new Image();
    baseImage.src = image.src;

    var baseRatio = baseImage.width / baseImage.height;
    var boxRatio = $box.width() / $box.height();

    adjustNonAdaptiveContent($image, baseRatio, boxRatio, config.overflow);
  };

  /**
   * update a single given non-adaptive video
   * @param {HTMLVideoElement} video
   */
  module.updateNonAdaptiveVideo = function (video) {
    var $video = $(video);

    resetNonAdaptiveContent($video);

    var config = $.extend({overflow: false}, $video.data("cm-non-adaptive-content"));
    var $box = $video.parent();

    var baseRatio = $video.width() / $video.height();
    if ($video.is("video")) {
      baseRatio = video.videoWidth / video.videoHeight;
    }
    var boxRatio = $box.width() / $box.height();

    adjustNonAdaptiveContent($video, baseRatio, boxRatio, config.overflow);
  };

  /**
   * updates non adaptive images and videos for the whole page
   */
  module.updateNonAdaptiveContents = function () {
    $(document.body).find("img[data-cm-non-adaptive-content]").each(function () {
      module.updateNonAdaptiveImage(this);
    });
    $(document.body).find("video[data-cm-non-adaptive-content]").each(function () {
      module.updateNonAdaptiveVideo(this);
    });
  };

  /**
   * Updates the layout by recalculating responsive images, hotzones and adaptive contents.
   */
  module.updateLayout = function () {
    // recalculate responsive images if layout changes
    $(".cm-image--responsive").responsiveImages();
    // recalculate hotzones if layout changes
    $(".cm-imagemap").each(function () {
      coremedia.blueprint.imagemap.update($(this));
    });
    // recalculate non adaptive contents
    coremedia.blueprint.basic.responsive.updateNonAdaptiveContents();
  };

  return module;
}(coremedia.blueprint.basic.responsive || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;
  var $window = $(window);
  var $document = $(document);

  // append to dom ready (will be executed after all dom ready functions have finished)
  $(function () {
    coremedia.blueprint.nodeDecorationService.decorateNode(document);
  });

  // load all dynamic fragments. The special header X-Requested-With is needed by the CAE to identify
  // the request as an Ajax request
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-fragment";
    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $fragment = $(this);
      var url = $(this).data(identifier);
      coremedia.blueprint.basic.ajax({
        url: url,
        dataType: "text"
      }).done(function (html) {
        var $html = $(html);
        coremedia.blueprint.nodeDecorationService.undecorateNode($fragment);
        $fragment.replaceWith($html);
        coremedia.blueprint.nodeDecorationService.decorateNode($html);
        $document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$html]);
      });
    });
  });

  // this will substitute all data-hrefs rendered by ESI
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(coremedia.blueprint.basic.renderFragmentHrefs);

  // initializes the drop down menu
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var selector = ".cm-dropdown";
    $target.findAndSelf(selector).each(function () {
      $(this).on(coremedia.blueprint.basic.dropdown.EVENT_DROPDOWN_CHANGED, function () {
        $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
      });
      coremedia.blueprint.basic.dropdown.init(this);
    });
  });

  // adds removes spinner if an image has finished loading
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-image--loading", function ($target) {
    var callback = function () {
      $target.removeClass("cm-image--loading");
    };
    if (typeof $.fn.imagesLoaded === typeof callback) {
      $target.imagesLoaded(callback);
    } else {
      $target.on("load", callback);
    }
  });

  // initializes responsive images
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-image--responsive", function ($target) {
    $target.responsiveImages();
  });

  // handle closing of notification box
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var selector = ".cm-notification__dismiss";
    $target.findAndSelf(selector).click(function () {
      $(this).closest(".cm-notification").fadeOut();
    });
  });

  // add readmore functionality if text is too long
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({lines: undefined}, "cm-readmore", function ($target, config) {
    var blockReadMore = "cm-readmore";
    // read the line height for the given target
    var lineHeight = $target.css("line-height");
    // only proceed if config is valid and lineHeight could be retrieved
    if (config.lines !== undefined && lineHeight !== undefined) {
      var $wrapper = $target.find("." + blockReadMore + "__wrapper");
      var $buttonbar = $target.find("." + blockReadMore + "__buttonbar");
      var $buttonMore = $buttonbar.find("." + blockReadMore + "__button-more");
      var $buttonLess = $buttonbar.find("." + blockReadMore + "__button-less");

      // calculate line height in px
      if (lineHeight.indexOf("px") > -1) {
        // line height is already in px, just remove the unit
        lineHeight = lineHeight.replace("px", "");
      } else {
        // line height is relative to font-size, calculate line height by multiplying its value with font-size
        lineHeight = lineHeight * $target.css("font-size").replace("px", "");
      }
      var maxHeight = Math.floor(lineHeight * config.lines);
      // enable readmore functionality if text without the readmore button exceeds the maximum height
      // it would make no sense to add a readmore button if it would take more space as rendering the full text
      if ($wrapper.height() - 2 * $buttonbar.height() > maxHeight) {
        $target.addClass(blockReadMore + "--enabled");
        // default without any action by the user ist the non expanded (less) version
        $target.addClass(blockReadMore + "--less");
        $wrapper.css("max-height", maxHeight);
        $buttonMore.on("click", function () {
          $target.removeClass(blockReadMore + "--less");
          $target.addClass(blockReadMore + "--more");
          $wrapper.css("max-height", "");
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        $buttonLess.on("click", function () {
          $target.removeClass(blockReadMore + "--more");
          $target.addClass(blockReadMore + "--less");
          $wrapper.css("max-height", maxHeight);
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        $buttonLess.on("click", function () {
          $target.removeClass(blockReadMore + "--more");
          $target.addClass(blockReadMore + "--less");
          $wrapper.css("max-height", maxHeight);
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
      }
    }
  });

  // initially updates new non-adaptive boxes
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-non-adaptive-content";
    var selector = "[data-" + identifier + "]";
    var imageSelector = "img" + selector;
    $target.findAndSelf(imageSelector).each(function () {
      var image = this;
      var $image = $(image);
      var callback = function () {
        coremedia.blueprint.basic.responsive.updateNonAdaptiveImage(image);
      };
      if (typeof $.fn.imagesLoaded === typeof callback) {
        $image.imagesLoaded(callback);
      } else {
        $image.on("load", callback);
      }
    });
    var videoSelector = "iframe" + selector + ", video" + selector;
    $target.findAndSelf(videoSelector).each(function () {
      coremedia.blueprint.basic.responsive.updateNonAdaptiveVideo(this);
    });
  });

  // initially load 360 spinner
  // initializes responsive images
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-spinner__canvas", function ($target) {
    $target.threeSixtySpinner();
  });


  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.smartresize(function () {
    coremedia.blueprint.logger.log("Window resized");
    $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
  });

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  $document.on(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED, coremedia.blueprint.basic.responsive.updateLayout);
});
