/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
/**
 *  CoreMedia Blueprint Namespace
 */
coremedia.blueprint = (function (module) {
  return module;
}(coremedia.blueprint || {}));
/**
 *  CoreMedia Blueprint Imagemap Namespace
 */
coremedia.blueprint.imagemap = (function (module) {
  return module;
}(coremedia.blueprint.imagemap || {}));
/**
 *  CoreMedia Blueprint Imagemap Initialization Namespace
 */
coremedia.blueprint.imagemap = (function (module) {
  return module;
}(coremedia.blueprint.imagemap || {}));

/**
 * Imagemap initialization
 */
coremedia.blueprint.imagemap.init = function (module) {

  module.initialize = function ($, $target) {
    var prefix = "cm-";
    var cssprefix = "." + prefix;
    var identifier = prefix + "imagemap";
    var selector = "." + identifier;
    $target.findAndSelf(selector).each(function () {

      var $imagemap = $(this);

      // get configuration of imagemap
      var config = $.extend({coordsBaseWidth: 1}, $imagemap.data(prefix + "imagemap"));
      var $areas = $imagemap.find(cssprefix + "imagemap__areas");
      var $image = $imagemap.find(cssprefix + "imagemap__image");
      var $wrapper = $imagemap.find(cssprefix + "imagemap__wrapper");

      /**
       * recalculates all areas of the imagemap for the actual dimensions the imagemap has
       *
       * @param newRatio (optional) if there was an aspect ratio switch this is the new ratio to be used
       */
      function recalculateAreas(newRatio) {
        newRatio = newRatio || undefined;

        var width = $image.width();
        var height = $image.height();
        // width for relative positioning (wrapper is assumed to be the last dom element with position != static)
        var rWidth = $wrapper.width();
        var rHeight = $wrapper.height();

        // calculate the needed transformation base
        var fraction = width / config.coordsBaseWidth;

        // iterate over all areas having shape and data-coords set
        $areas.find("area[data-coords][shape]").each(function () {
          var $area = $(this);
          var coords = $area.data("current-coords");
          var shape = $area.attr("shape");
          if (newRatio !== undefined) {
            coords = $area.data("coords")[newRatio];
          }
          $area.data("current-coords", coords);
          if (!coords) {
            // there are no coordinates to recompute
            return;
          }

          // transform the coordinates given as String into an array of Point
          var coordsAsPoints = [];
          if (typeof coremedia.blueprint.imagemap.coordsConverter.coordsTo[shape] === "function") {
            coordsAsPoints = coremedia.blueprint.imagemap.coordsConverter.coordsTo[shape](coords);
          }

          // There have to be at least 3 points, otherwise no shape can be drawn
          if (coordsAsPoints.length >= 3) {

            var i;
            // transform and normalize coordinates
            // smooth normalization needed taking left and right coordinate into account (for polygons)
            for (i = 0; i < coordsAsPoints.length; i++) {
              coordsAsPoints[i].x = Math.min(Math.max(coordsAsPoints[i].x * fraction, 0), width);
              coordsAsPoints[i].y = Math.min(Math.max(coordsAsPoints[i].y * fraction, 0), height);
            }

            var hotzoneBox = coremedia.blueprint.imagemap.calculateBoundingBox(coordsAsPoints);

            // check visibility of hotzone:
            // surface area of bounding box must be greater than zero
            var visible = (Math.abs(hotzoneBox.x1 - hotzoneBox.x2) * Math.abs(hotzoneBox.y1 - hotzoneBox.y2)) > 0;

            // hotzone indicator must fit into image
            var hotzoneCenter = {
              x: (hotzoneBox.x1 + hotzoneBox.x2) / 2,
              y: (hotzoneBox.y1 + hotzoneBox.y2) / 2
            };
            var $hotzoneIndicator = $area.next(cssprefix + "imagemap__hotzone");
            var hotzoneIndicatorWidth = Math.abs($hotzoneIndicator.width());
            var hotzoneIndicatorHeight = Math.abs($hotzoneIndicator.height());
            var hotzoneIndicatorBox = {
              x1: hotzoneCenter.x - (hotzoneIndicatorWidth / 2),
              x2: hotzoneCenter.x + (hotzoneIndicatorWidth / 2),
              y1: hotzoneCenter.y - (hotzoneIndicatorHeight / 2),
              y2: hotzoneCenter.y + (hotzoneIndicatorHeight / 2)
            };

            // short formular, assuming x1 <= x2, y1 <= y2
            visible = visible && hotzoneIndicatorBox.x1 >= 0
                    && hotzoneIndicatorBox.x2 < width
                    && hotzoneIndicatorBox.y1 >= 0
                    && hotzoneIndicatorBox.y2 < height;

            if (visible) {
              // set new hot zone coordinates
              var strCoords = coremedia.blueprint.imagemap.coordsConverter.toCoords[shape](coordsAsPoints);
              if (strCoords != "") {
                $area.attr("coords", strCoords);
              } else {
                $area.removeAttr("coords");
              }

              $hotzoneIndicator.each(function () {
                var $hotzoneIndicator = $(this);
                // the area's marker div must be repositioned if ratio has changed
                if (newRatio !== undefined || $hotzoneIndicator.data(prefix + "hotzone-indicator-disabled")) {
                  $hotzoneIndicator.data(prefix + "hotzone-indicator-disabled", false);
                  $hotzoneIndicator.css({
                    "top": hotzoneCenter.y * 100 / $wrapper.height() + "%",
                    "left": hotzoneCenter.x * 100 / $wrapper.width() + "%",
                    //"display": "",
                    "transform": ""
                  });
                }
              });
            } else {
              // move everything out of viewport of wrapper
              $area.attr("coords", [rWidth, rHeight, rWidth, rHeight].join(","));
              $hotzoneIndicator.data(prefix + "hotzone-indicator-disabled", true);
              $hotzoneIndicator.css({
                "transform": "none",
                //"display": "none"
                "top": "100%",
                "left": "100%"
              });
            }
          }
        });
      }

      // TODO: update functionality should be moved to library part, this is just a workaround
      $imagemap.data(prefix + "imagemap-update", recalculateAreas);

      // Handle responsive and non-responsive images
      if ($image.data(prefix + "responsive-image-state") !== undefined) {
        $image.on("srcChanging", function () {
          // hide hotzones if src is changing
          $imagemap.find(cssprefix + "imagemap__hotzone").css("display", "none");
        });
        $image.on("srcChanged", function (event) {
          // display hotzones if src has changed (and is fully loaded)
          $imagemap.find(cssprefix + "imagemap__hotzone").css("display", "");
          recalculateAreas(event.ratio);
        });
      } else {
        // determine image ratio (if attached) and set "uncropped" as fallback
        var ratio = $image.data(prefix + "image-ratio") || "uncropped";
        recalculateAreas(ratio);
      }

      // imagemap plugin doesn't rely on quickinfos being elements of the imagemap
      var areasConfig = $.extend({quickInfoMainId: undefined}, $areas.data(prefix + "areas"));
      var openQuickInfoMain = function () {
        if (areasConfig.quickInfoMainId !== undefined) {
          $("#" + areasConfig.quickInfoMainId).each(function () {
            coremedia.blueprint.quickInfo.show($(this));
          });
        }
      };

      $image.on("click", function () {
        openQuickInfoMain();
      });

      openQuickInfoMain();

      var mouseenter = function () {
        var $this = $(this);
        var $button = $this.is(cssprefix + "imagemap__hotzone") ? $this : $this.next(cssprefix + "imagemap__hotzone");
        $button.addClass(prefix + "imagemap__hotzone--hover");
      };
      var mouseleave = function () {
        var $this = $(this);
        var $button = $this.is(cssprefix + "imagemap__hotzone") ? $this : $this.next(cssprefix + "imagemap__hotzone");
        $button.removeClass(prefix + "imagemap__hotzone--hover");
      };

      // delegate click to button
      $imagemap.find(cssprefix + "imagemap__area").click(function () {
        $(this).next(cssprefix + "imagemap__hotzone").trigger("click");
        return false;
      });

      $imagemap.find(cssprefix + "imagemap__area, " + cssprefix + "-imagemap__hotzone").hover(mouseenter, mouseleave);

      // listen to quickinfo changed event and adjust hotzone state accordingly
      $areas.find(cssprefix + "imagemap__area").each(function () {
        var $area = $(this);
        var quickInfoId = $area.data("quickinfo");
        var $button = $area.next(cssprefix + "imagemap__hotzone");
        $("#" + quickInfoId).on(coremedia.blueprint.quickInfo.EVENT_QUICKINFO_CHANGED, function (event, active) {
          if (active) {
            $button.addClass(prefix + "imagemap__hotzone--active");
          } else {
            $button.removeClass(prefix + "imagemap__hotzone--active");
            openQuickInfoMain();
          }
        });
      });
    });
  };

  return module;
}(coremedia.blueprint.imagemap.init || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;

// initializes imagemaps
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    coremedia.blueprint.imagemap.init.initialize($, $target);
  });
});