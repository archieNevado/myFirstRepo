import $ from "jquery";
import magnificPopup from "@coremedia/js-magnific-popup";
import { log, error } from "@coremedia/js-logger";
import "mediaelement/full";
import "mediaelement/build/renderers/dailymotion";
import "mediaelement/build/renderers/twitch";
import "mediaelement/build/renderers/vimeo";
import videoAsMediaElement from "./videoAsMediaElement";

export default function($popupElements, { url, parentSelector }) {
  $popupElements.each((index, popupElement) => {
    const $popupElement = $(popupElement);
    log("Video popup found.", $popupElement);

    let $links = $popupElement;
    // if a parentSelector is specified include more links for replacement
    if (parentSelector) {
      const linkToReplace = $popupElement.attr("href");
      // search for all affected links in the given parent
      // links need to point to the same href as the initiator...
      $links = $popupElement
        .closest(parentSelector)
        .find("a")
        .filter((index, element) => $(element).attr("href") === linkToReplace);
    }
    magnificPopup($links, {
      type: "inline",
      midClick: true,

      callbacks: {
        elementParse: function(item) {
          //generate popup on the fly (for lazy loading)
          item.src = `<div class="cm-popup">
          <video data-cm-video class="cm-popup__video cm-video" src="${url}" autoplay controls></video>
          </div>`;
        },
        open: function() {
          log("Video popup opened.");
          // find video inside popup
          const $video = $(".mfp-content [data-cm-video]");
          // and initialize mediaElement
          if ($video.length > 0) {
            videoAsMediaElement($video);
          } else {
            error("Error: No video found in popup");
          }
        },
        close: function() {
          log("Video popup closed.");
        },
      },
    });
  });
}
