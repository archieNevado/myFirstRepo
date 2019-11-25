import $ from "jquery";

/**
 * Add pseudo for selection by data attribute managed by jQuery (not equal to search for [data-...])
 */
$.extend($.expr[":"], {
  data: $.expr.createPseudo
    ? $.expr.createPseudo(function(dataName) {
        return function(elem) {
          return !!$.data(elem, dataName);
        };
      })
    : // support: jQuery <1.8
      function(elem, i, match) {
        return !!$.data(elem, match[3]);
      },
});
