import $ from "jquery";
import "./jquery.coremedia.equalheight";
import { EVENT_LAYOUT_CHANGED } from "@coremedia/brick-utils";

$(document).on(EVENT_LAYOUT_CHANGED, function() {
  $(".cm-product-list-grid .cm-category-item__title").equalHeights();
});
