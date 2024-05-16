// Set current time... Enables remote control to find out when studio was started
window.document.openTimestamp = (new Date()).getTime();
// Set window name if necessary. Makes window accessible via remote control.
if (window.name !== "CoreMediaStudio") {
  window.name = "CoreMediaStudio";
}

joo = {
  localization: {
    localeCookieName: "com.coremedia.cms.editor.locale"
  }
};

(function () {
  // This disables touch events as soon as possible (prevents Ext.supports.TouchEvents=true later on).
  // CMS-11086
  delete window.ontouchend;

  Object.defineProperty(navigator, "msMaxTouchPoints", {get: function () {return 0}, set: function (v) {}});
  Object.defineProperty(navigator, "maxTouchPoints", {get: function () {return 0}, set: function (v) {}});

  // Fix for Chrome 62 / chromedriver 2.32, to be removed as soon as possible
  if (window.location.href.indexOf("testMode=true")) {
    delete window.PointerEvent;
  }

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

  function probeRestApiThenLoadScripts(coremediaRemoteServiceUri, failure) {
    var xhr = new XMLHttpRequest();

    xhr.onload = function() {
      if (xhr.status === 200) {
        xhr = new XMLHttpRequest();
        xhr.onload = function () {
          if (xhr.status === 200) {
            var csp = xhr.getResponseHeader("Content-Security-Policy");
            if (csp) {
              // strip off frame-ancestors, this cannot be achieved with CSP in <meta> tags
              csp = csp.split(";").filter(function (directive) {
                var directiveParts = directive.trim().split(/\s+/g);
                return directiveParts[0].trim().toLowerCase() !== "frame-ancestors";
              }).join(";");
              var metaCsp = document.createElement("meta");
              metaCsp.httpEquiv = "Content-Security-Policy";
              metaCsp.content = csp;
              document.head.appendChild(metaCsp);
            }
            window.coremediaRemoteServiceUri = coremediaRemoteServiceUri;
            loadScripts()
          } else {
            failure();
          }
        };
        xhr.open('GET', coremediaRemoteServiceUri + '../cspInfo.html' + (document.location.search || ""));
        xhr.send();
      } else {
        failure();
      }
    };

    xhr.open('HEAD', coremediaRemoteServiceUri + 'supported-locales.js');
    xhr.send();
  }

  function loadScripts() {
    // create and append script elements
    function loadScript(attributes) {
      var script = document.createElement('script');
      script.async = false;
      script.type = 'text/javascript';
      Object.keys(attributes).forEach(function(a) {
        script[a] = attributes[a];
      });

      document.body.appendChild(script);
    }

    var attributeList = [
      {src: window.coremediaRemoteServiceUri + 'supported-locales.js'},
      {src: window.coremediaRemoteServiceUri + 'accept-language-header.js'},
      {src: 'resources/before-ext-load.js'},
      {id: 'microloader', 'data-app': '906bf4bf-9a7d-42cc-b7a5-6ef30df325e9', src: 'bootstrap.js'}
    ];

    attributeList.forEach(loadScript);
  }

  var pathname = window.location.pathname;
  var isDevelopmentMode = pathname.indexOf("/target/app/") >= 0 || pathname.indexOf("/target/apps/") >= 0;
  var uriPrefix = isDevelopmentMode ? '/' : '';

  probeRestApiThenLoadScripts(uriPrefix + 'rest/api/',
      function () {
        // fallback: try old "api/" URL prefix
        probeRestApiThenLoadScripts(uriPrefix + 'api/',
            function () {
              console.error("No response from Studio Server API at " + uriPrefix + 'rest/api/ or ' + uriPrefix + 'api/');
              document.body.innerHTML = '<style>' +
                '.cm-dolly-body {position: absolute; top:50%; left: 50%; transform: translate(-50% , -50%); font-size: 16px; color: #fff; padding: 40px; border: 1px solid #FFFFFF; border-radius: 10px; font-weight: 400; font-family: "Roboto", "Segoe UI", "Trebuchet MS", "Lucida Grande", "Helvetica", sans-serif;}' +
                '.cm-dolly-head {animation: bobble 1.8s ease-in-out infinite;}' +
                '.cm-dolly-eyes {text-align: center}' +
                '.cm-dolly-mouth {display: flex;justify-content: center}' +
                '.cm-dolly-wrapper {position: relative; width: 84px; padding-top: 16px}' +
                '.cm-dolly-lips:nth-child(odd) {transform: rotate(54deg);}' +
                '.cm-dolly-lips {position: absolute; left: 12px;content: " "; height: 15px; width: 1px; background-color: #FFFFFF; transform: rotate(-54deg);}' +
                '.cm-dolly-lips:nth-child(n+2) {margin-left: 12px;}' +
                '.cm-dolly-lips:nth-child(n+3) {margin-left: 24px;}' +
                '.cm-dolly-lips:nth-child(n+4) {margin-left: 36px;}' +
                '.cm-dolly-lips:nth-child(n+5) {margin-left: 48px;}' +
                '.cm-dolly-lips:nth-child(n+6) {margin-left: 60px;}' +
                '@keyframes bobble { 0%, 100% { margin-top: 20px; margin-bottom: 48px; } 50% { margin-top: 34px; margin-bottom: 34px; }}' +
                '</style>' +
                '<div class="cm-dolly-body">' +
                '<div class="cm-dolly-head">' +
                  '<div class="cm-dolly-eyes">&#10005;&nbsp;&nbsp;&nbsp;&nbsp;&#10005;</div>' +
                  '<div class="cm-dolly-mouth">' +
                    '<div class="cm-dolly-wrapper">' +
                      '<div class="cm-dolly-lips"></div>' +
                      '<div class="cm-dolly-lips"></div>' +
                      '<div class="cm-dolly-lips"></div>' +
                      '<div class="cm-dolly-lips"></div>' +
                      '<div class="cm-dolly-lips"></div>' +
                      '<div class="cm-dolly-lips"></div>' +
                    '</div>' +
                  '</div>' +
                '</div>' +
                  '<h2>Unfortunately, something has gone wrong.</h2>' +
                  '<p style="margin-left: auto; margin-right: auto">Please contact your system administrator if this problem happens again.</p>' +
                '</div>';
            }
        );
      }
  );

})();
