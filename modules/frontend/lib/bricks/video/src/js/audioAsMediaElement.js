import $ from "jquery";
import { log } from "@coremedia/js-logger";
import * as basic from "@coremedia/js-basic";

import "mediaelement/full";

/**
 * Generates a mediaElement for the given audio
 * See renderers for supported external audios
 *
 * @param audioElement
 */
function audioAsMediaElement(audioElement) {
  const $audio = $(audioElement);
  const $document = $(document);

  // mediaElement object of the audio
  const me = new MediaElement(audioElement, {
    fakeNodeName: "cm-mediaelementwrapper",
    useDefaultControls: true,

    // events of audios
    success: function(mediaElement) {
      const $mediaElement = $(mediaElement);
      // attach css class
      $mediaElement.addClass("cm-mediaelementwrapper");
      // audio loaded
      mediaElement.addEventListener(
        "loadedmetadata",
        function() {
          log("Audio " + mediaElement.src + " loaded.", $audio);
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        },
        false
      );

      // audio started
      mediaElement.addEventListener(
        "playing",
        function() {
          log("Audio started with duration of " + me.duration + "ms.");
        },
        false
      );

      // audio ended
      mediaElement.addEventListener(
        "ended",
        function() {
          log("Audio playback ended.");
        },
        false
      );
    },
  });
}

/**
 * default wrapper function to handle dom elements or jQuery selectors
 * @param domElementOrJQueryResult
 */
export default function(domElementOrJQueryResult) {
  if (domElementOrJQueryResult instanceof $) {
    $.each(domElementOrJQueryResult, function(index, item) {
      audioAsMediaElement(item);
    });
  } else {
    audioAsMediaElement(domElementOrJQueryResult);
  }
}
