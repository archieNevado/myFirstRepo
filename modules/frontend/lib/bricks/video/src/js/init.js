import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import {
  addNodeDecorator,
  addNodeDecoratorByData,
  addNodeDecoratorBySelector,
} from "@coremedia/js-node-decoration-service";
import * as basic from "@coremedia/js-basic";
import { EVENT_VIDEO_ENDED, EVENT_VIDEO_START } from "./videoAsMediaElement";
import audioAsMediaElement from "./audioAsMediaElement";
import videoAsMediaElement from "./videoAsMediaElement";
import videoAsPopup from "./videoAsPopup";

/* --- inititialize videos --- */

// all elements with the attribute "data-cm-video" will be used
const videoIdentifier = "cm-video";
// and finally add it to the nodeDecorator (which handles dom ready )
addNodeDecoratorByData({}, videoIdentifier, videoAsMediaElement);

// handle video in teasers, like pdp and shoppable video
addNodeDecorator(function($target) {
  const baseConfig = {
    preview: undefined,
    play: undefined,
    player: undefined,
    backlightTimeout: 200,
    features: ["backlight"],
  };
  const identifier = "cm-teasable--video";
  const selector = "[data-" + identifier + "]";
  findAndSelf($target, selector).each(function() {
    const $videoTeaser = $(this);
    const config = $.extend(baseConfig, $videoTeaser.data(identifier));
    const $preview = $videoTeaser.find(config.preview);
    const $play = $videoTeaser.find(config.play);
    const $player = $videoTeaser.find(config.player);

    // all teaser with videos show an image and change on click
    // hiding the image and showing the video. when video has finished
    // show image again
    $play.bind("click", function() {
      $preview.addClass("cm-hidden");
      $play.addClass("cm-hidden");
      $player.removeClass("cm-hidden");
      const selector = "[data-cm-video]";
      findAndSelf($player, selector).each(function() {
        const $video = $(this);

        function replacePlayerWithStillImage() {
          $player.addClass("cm-hidden");
          $play.removeClass("cm-hidden");
          $preview.removeClass("cm-hidden");
          // window might have changed while video player was active, e.g. portrait->landscape
          $(document).trigger(basic.EVENT_LAYOUT_CHANGED);
        }

        // start video
        $video.trigger(EVENT_VIDEO_START);
        // show image again when video ended
        $video.on(EVENT_VIDEO_ENDED, replacePlayerWithStillImage);
      });

      return false;
    });

    // when fullscreen playback is canceled on mobiles, videoEnded is not triggered
    $videoTeaser.on("webkitendfullscreen", function() {
      $videoTeaser.trigger(EVENT_VIDEO_ENDED);
    });
  });
});

addNodeDecoratorByData({ url: undefined }, "cm-video-popup", videoAsPopup);
addNodeDecoratorBySelector("cm-audio", audioAsMediaElement);
