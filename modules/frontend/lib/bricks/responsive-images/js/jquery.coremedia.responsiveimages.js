/*! Responsive Image Resizer Plugin | Copyright (c) CoreMedia AG */
/**
 * CoreMedia namespace
 * @namespace coremedia
 * @ignore
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));

/**
 * CoreMedia Blueprint namespace
 * @namespace "coremedia.blueprint"
 * @ignore
 */
coremedia.blueprint = (function (module) {
  /*global jQuery*/
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

(function ($) {
  "use strict";

  /**
   * Picks a suitable image from a given set of images regarding given dimensions and the maximum size needed.
   *
   * @function "coremedia.blueprint.$.fn.responsiveImages"
   * @version 1.6
   * @copyright CoreMedia AG
   * @summary Responsive Image Resizer jQuery Plugin
   * @example
   * ###### Usage
   * ```javascript
   * $(".cm-image--responsive").responsiveImage();
   * ```
   *
   * ###### HTML
   * ```html
   * <img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="[
   *  {
   *    "name" : "3x1",
   *    "ratioWidth" : 3,
   *    "ratioHeight" : 1,
   *    "linksForWidth" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"}
   *  },
   *  {
   *    "name" : "2x1",
   *    "ratioWidth" : 2,
   *    "ratioHeight" : 1,
   *    "linksForWidth" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}
   *  }]" />
   * ```
   *
   * Deprecated legacy format:
   * ```html
   * <img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="{
   *    "3x1" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"},
   *    "2x1" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}}" />
   * ```
   */
  $.fn.responsiveImages = function () {
    return $.each(this, function (index, item) {

      var $image = $(item);
      var logger = coremedia.blueprint.logger || { log: function () {} };

      var triggerSrcChanged = function () {
        $image.trigger({
          type: "srcChanged",
          src: $image.attr("src"),
          maxWidth: $image.data("lastMaxWidth"),
          ratio: $image.data("lastRatio")
        });
      };

      var imagesLoadedPluginExists = typeof $.fn.imagesLoaded === typeof triggerSrcChanged;

      if ($image.data("cm-responsive-image-state") === undefined) {
        $image.data("cm-responsive-image-state", "initialized");
        // check if imagesLoaded plugin exists
        if (imagesLoadedPluginExists) {
          if ($image.src && $image.src.length > 0) {
            $image.imagesLoaded(triggerSrcChanged);
          }
        } else {
          $image.on("load", triggerSrcChanged);
        }
      }

      var responsiveImages = $image.data("cm-responsive-image");

      // only run if there is at least one aspect ratio defined
      if (typeof responsiveImages !== "undefined") {

        var $imageContainer = $(this).parent();
        var containerWidth = $imageContainer.width();
        var containerHeight = $imageContainer.height();
        if (!containerWidth || !containerHeight) {
          logger.log("Could not load hidden Responsive Image.", $imageContainer);
          return; // image is not visible, do not touch
        }

        // CMS-2905: use retina images, if enabled
        var deviceRatio = window.devicePixelRatio;
        if (deviceRatio > 1 && $image.data("cm-retina-image")) {
          containerHeight *= deviceRatio;
          containerWidth *= deviceRatio;
        }

        // detect best fitting aspect ratio for box
        var containerRatio = containerWidth / containerHeight;
        var fittingRatio = {
          name: undefined,
          difference: undefined,
          linksForWidth: []
        };

        // @since 1.6
        // default method to retrieve ratio from responsive images data
        var getCandidateRatio = function (id, responsiveImages) {
          var format = responsiveImages[id];
          var ratioWidth = format.ratioWidth;
          var ratioHeight = format.ratioHeight;
          return {
            name: format.name,
            difference: Math.abs(containerRatio - (ratioWidth / ratioHeight)),
            linksForWidth: format.linksForWidth
          };
        };

        // continue support of the old format that derived the ratio from the name. will be removed in the next major
        // release
        if (!$.isArray(responsiveImages)) {
          logger.log("Using legacy data structure for responsive image setting. Please consider changing to the new format");
          getCandidateRatio = function (id, responsiveImages) {
            var regexp = /^[a-zA-Z_]*(\d+)x(\d+)$/;
            if (!responsiveImages.hasOwnProperty(id)) {
              return null;
            }

            var match = regexp.exec(id);
            if (match !== null) {
              var ratioWidth = parseInt(match[1]);
              var ratioHeight = parseInt(match[2]);
              return {
                name: id,
                difference: Math.abs(containerRatio - (ratioWidth / ratioHeight)),
                linksForWidth: responsiveImages[id]
              };
            }
            return null;
          };
        }

        // determine the best fit in respect of the defined ratios and the container ratio
        for (var id in responsiveImages) {
          var candidateRatio = getCandidateRatio(id, responsiveImages);
          if (candidateRatio !== null
                  /* jshint ignore:start */
                  && typeof fittingRatio.name === "undefined"
                  || typeof fittingRatio.difference === "undefined"
                  || (fittingRatio.difference > candidateRatio.difference)
                  /* jshint ignore:end */
          ) {
            fittingRatio = candidateRatio;
          }
        }

        // only run if a valid ratio is defined
        if (typeof fittingRatio.name !== "undefined") {

          // find fitting link
          var fittingImage = {
            maxWidth: undefined,
            link: undefined
          };
          for (var maxWidth in fittingRatio.linksForWidth) {

            if (!fittingRatio.linksForWidth.hasOwnProperty(maxWidth)) {
              continue;
            }

            var candidateImage = {
              maxWidth: parseInt(maxWidth),
              link: fittingRatio.linksForWidth[maxWidth]
            };

            // calculate fitting image, allow no quality loss
            /* jshint ignore:start */
            if (// case: no fitting image is set
            // -> take the candidate image
            typeof fittingImage.maxWidth === "undefined"
            || typeof fittingImage.link === "undefined"
              // case: fittingImage and candidate are smaller than the container
              // -> take candidate if the image is bigger (lesser quality loss)
            || (fittingImage.maxWidth < containerWidth
            && candidateImage.maxWidth < containerWidth
            && candidateImage.maxWidth > fittingImage.maxWidth)
              // case: fittingImage is smaller and candidate is bigger than the container
              // -> take candidate image (no quality loss is better than any quality loss)
            || (fittingImage.maxWidth < containerWidth
            && candidateImage.maxWidth >= containerWidth)
              // case: fittingImage and candidate are bigger than the container
              // -> take candidate if the image is smaller (no quality loss and smaller size)
            || (fittingImage.maxWidth >= containerWidth
            && candidateImage.maxWidth >= containerWidth
            && candidateImage.maxWidth < fittingImage.maxWidth)) {
              fittingImage = candidateImage;
            }
            /* jshint ignore:end */
          }

          // @since 1.3
          // image can be an <img> tag
          if ($image.is("img")) {
            // replace link if not the same
            if (fittingImage.link !== $image.attr("src")) {
              logger.log("Change Responsive Image to aspect ratio: '" + fittingRatio.name + "' and maxWidth: '" + fittingImage.maxWidth + "'", $imageContainer);
              $image.trigger({
                type: "srcChanging",
                src: $image.attr("src"),
                maxWidth: fittingImage.maxWidth,
                ratio: fittingRatio.name
              });
              $image.data("lastMaxWidth", fittingImage.maxWidth);
              $image.data("lastRatio", fittingRatio.name);
              $image.attr("src", fittingImage.link);
              if (imagesLoadedPluginExists) {
                $image.imagesLoaded(triggerSrcChanged);
              }
            }
            // or a background image via style attribute
          } else {
            // replace link if not the same
            if ("background-image: url('" + fittingImage.link + "');" !== $image.attr("style")) {
              logger.log("Change Responsive Background Image to aspect ratio: '" + fittingRatio.name + "' and maxWidth: '" + fittingImage.maxWidth + "'", $imageContainer);
              $image.data("lastMaxWidth", fittingImage.maxWidth);
              $image.data("lastRatio", fittingRatio.name);
              $image.attr("style", "background-image: url('" + fittingImage.link + "');");
            }
          }
        }
      }
    });
  };
})(jQuery || coremedia.blueprint.$);
