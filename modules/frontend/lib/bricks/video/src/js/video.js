/**
 * @event "videoStart" triggered to start the video
 * @deprecated use {@link start} instead, event will be private API soon.
 */
// todo: make private API in the next release as start function should be used.
export const EVENT_VIDEO_START = "start";

/**
 * @event "videoEnded" triggered when a video has ended.
 */
export const EVENT_VIDEO_ENDED = "videoEnded";

/**
 * @event "videoTimeUpdated" triggered when the current playtime has been updated.
 * @type {Object}
 * @property {int} position - indicates the current video position in milliseconds
 */
export const EVENT_VIDEO_TIME_UPDATED = "videoTimeUpdated";

/**
 * Starts a given cm-video element
 * @param {jQuery} $video the cm-video element as jquery wrapped object
 */
export function start($video) {
  // for now trigger the old event
  $video.trigger(EVENT_VIDEO_START);
}
