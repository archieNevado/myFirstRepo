/**
 * @callback readyCallback
 */
/**
 * Executes the callback function, if the DOM is ready for JavaScript code to execute.
 * @param {readyCallback} f - Callback function to be executed.
 * @function
 */
export const domReady = f => {
  // DOM is ready
  if (document.readyState !== "loading") {
    f();
  } else if (document.addEventListener) {
    // modern Browsers (Firefox, Chrome, Safari, Opera, IE 9-up
    const handler = () => {
      document.removeEventListener("DOMContentLoaded", handler, false);
      f();
    };
    document.addEventListener("DOMContentLoaded", handler, false);
  } else {
    // IE <9
    const handler = () => {
      if (document.readyState !== "complete") {
        document.detachEvent("onreadystatechange", handler);
        f();
      }
    };
    document.attachEvent("onreadystatechange", handler);
  }
};
