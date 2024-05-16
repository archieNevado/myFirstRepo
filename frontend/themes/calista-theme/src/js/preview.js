/*! Theme calista: Preview JS */
window.coremedia.blueprint.$(function () {
  /**
   * remove newPreviewSession parameter from next URL in preview to avoid login problems
   *
   * Static Application Security Testing (SAST) tools like Checkmarx may complain
   * about this function, if they assume that it embeds untrusted data without
   * proper sanitization or encoding. Such reports are false positives.
   */
  function replaceLoginPreviewURL() {
    const $loginBtn = window.coremedia.blueprint.$("#cm-login");
    if ($loginBtn.length > 0) {
      $loginBtn.attr(
        "href",
        $loginBtn.attr("href").replace("newPreviewSession%3Dtrue%26", "")
      );
    }
  }

  replaceLoginPreviewURL();
  window.coremedia.blueprint
    .$(document)
    .on("coremedia.blueprint.base.loginStatusChecked", replaceLoginPreviewURL);
});
