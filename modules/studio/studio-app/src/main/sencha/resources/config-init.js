// Set current time... Enables remote control to find out when studio was started
window.document.openTimestamp = (new Date()).getTime();
// Set window name if necessary. Makes window accessible via remote control.
if (window.name != "CoreMediaStudio") {
  window.name = "CoreMediaStudio";
}

joo = {
  localization: {
    localeCookieName: "com.coremedia.cms.editor.locale"
  }
};

(function () {
  // http://stackoverflow.com/questions/984510/what-is-my-script-src-url
  var configLocationPath = (function() {
    var scripts = document.getElementsByTagName('script'),
      script = scripts[scripts.length - 1];
    var scriptSrc;
    if (script.getAttribute.length !== undefined) {
      scriptSrc = script.getAttribute('src')
    } else {
      scriptSrc = script.getAttribute('src', 2);
    }
    return scriptSrc + "/..";
  }());

  var pathname = window.location.pathname;
  var isDevelopmentMode = pathname.indexOf("/target/app/") >= 0;
  window.coremediaRemoteServiceUri = isDevelopmentMode ? "/api/" : "api/";

  // create and append script elements
  function configureScript(attributes) {
    var script = document.createElement('script');
    script.async = false;
    script.type = 'text/javascript';
    Object.keys(attributes).forEach(function(a) {
      script[a] = attributes[a];
    });

    return script;
  }

  function appendScriptToBody(script) {
    document.body.appendChild(script);
  }

  var attributeList = [
    {src: window.coremediaRemoteServiceUri + 'supported-locales.js'},
    {src: window.coremediaRemoteServiceUri + 'accept-language-header.js'},
    {src: 'resources/before-ext-load.js'},
    {id: 'microloader', 'data-app': '906bf4bf-9a7d-42cc-b7a5-6ef30df325e9', src: 'bootstrap.js'}
  ];

  attributeList
    .map(configureScript)
    .forEach(appendScriptToBody);
})();
