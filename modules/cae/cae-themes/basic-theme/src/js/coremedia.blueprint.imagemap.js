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
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));
/**
 *  CoreMedia Blueprint Imagemap Library Namespace
 */
coremedia.blueprint.imagemap = (function (module) {
  return module;
}(coremedia.blueprint.imagemap || {}));
/**
 *  CoreMedia Blueprint Imagemap Library Init Namespace
 */
coremedia.blueprint.imagemap.init = (function (module) {
  return module;
}(coremedia.blueprint.imagemap.init || {}));


/**
 * Imagemap functionality
 */
coremedia.blueprint.imagemap = function (module) {

  /**
   * Transforms a comma-separated String of coords into an array of points.
   * Points are objects with 2 properties: x and y representing the point.
   *
   * @param {string} coords
   * @return {Array}
   */
  var coordsToRect = function (coords) {
    // coords = left,top,right,bottom
    // browsers also support flipped rects (so right < left and bottom < top are valid)
    var coordsAsInts = coords.split(",").map(function (i) {
      return Math.floor(parseInt(i))
    });

    var result = [];
    if (coordsAsInts.length == 4) {
      result = [
        {x: coordsAsInts[0], y: coordsAsInts[1]},
        {x: coordsAsInts[2], y: coordsAsInts[1]},
        {x: coordsAsInts[2], y: coordsAsInts[3]},
        {x: coordsAsInts[0], y: coordsAsInts[3]}
      ];
    }
    return result;
  };

  /**
   * Transforms an array of points into a comma-separated String.
   * Points are objects with 2 properties: x and y representing the point.
   *
   * @param {Array} points
   * @returns {string}
   */
  var rectToCoords = function (points) {
    // rect is a polygon with 4 edges, so first and third edge will define the rect
    var result = "";
    if (points.length == 4) {
      result = [points[0].x, points[0].y, points[2].x, points[2].y].join(",");
    }
    return result;
  };

  /**
   * Placeholder - not implemented yet
   *
   * @returns {Array}
   */
  var coordsToCircle = function () {
    return [];
  };

  /**
   * Placeholder - not implemented yet
   *
   * @returns {string}
   */
  var circleToCoords = function () {
    return "";
  };

  /**
   * Placeholder - not implemented yet
   *
   * @returns {Array}
   */
  var coordsToPoly = function () {
    return [];
  };

  /**
   * Transforms an array of points into a comma-separated String.
   * Points are objects with 2 properties: x and y representing the point.
   *
   * @param {Array} points
   * @returns {string}
   */
  var polyToCoords = function (points) {
    var result = [];
    for (var j = 0; j < points.length; j++) {
      result.push(points[j].x);
      result.push(points[j].y);
    }
    return result.join(",");
  };

  /**
   * Transforms any object passed into an array representing the default coords.
   * Introduced for consistency. The given param is ignored, function will always return an empty array.
   *
   * @returns {Array}
   */
  var coordsToDefault = function () {
    // shape default has no coords
    return [];
  };

  /**
   * Transforms any object into a String representing the default coords.
   * Introduced for consistency. The given param is ignored, function will always return an empty String.
   *
   * @returns {string}
   */
  var defaultToCoords = function () {
    // shape default has no coords
    return "";
  };

  // maps possible values for the attribute shape of the HTML map element to converter functions (both directions)
  module.coordsConverter = {
    coordsTo: {
      // W3C
      "rect": coordsToRect,
      "circle": coordsToCircle,
      "poly": coordsToPoly,
      // supported in many browsers
      "rectangle": coordsToRect,
      "circ": coordsToCircle,
      "polygon": coordsToPoly,
      // default is ignored (no transformation needed and no hotzone indicator)
      "default": coordsToDefault
    },
    toCoords: {
      // W3C
      "rect": rectToCoords,
      "circle": circleToCoords,
      "poly": polyToCoords,
      // supported in many browsers
      "rectangle": rectToCoords,
      "circ": circleToCoords,
      "polygon": polyToCoords,
      "default": defaultToCoords
    }
  };

  /**
   * calculates a bounding box for given points
   * Points are objects with 2 properties: x and y representing the point.
   *
   * @param {Array} coordsAsPoints
   */
  module.calculateBoundingBox = function (coordsAsPoints) {
    var result = {
      x1: undefined,
      y1: undefined,
      x2: undefined,
      y2: undefined
    };
    for (var i = 0; i < coordsAsPoints.length; i++) {
      var point = coordsAsPoints[i];
      result = {
        x1: Math.min(result.x1 !== undefined ? result.x1 : point.x, point.x),
        x2: Math.max(result.x2 !== undefined ? result.x2 : point.x, point.x),
        y1: Math.min(result.y1 !== undefined ? result.y1 : point.y, point.y),
        y2: Math.max(result.y2 !== undefined ? result.y2 : point.y, point.y)
      }
    }
    return result;
  };

  module.update = function ($imagemap) {
    // TODO: update functionality should be moved from document ready part, this is just a workaround
    var update = $imagemap.data("cm-imagemap-update");
    if (update !== undefined) {
      update();
    }
  };

  return module;
}(coremedia.blueprint.imagemap || {});