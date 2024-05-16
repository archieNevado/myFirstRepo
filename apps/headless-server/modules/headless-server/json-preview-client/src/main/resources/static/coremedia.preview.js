!function(){function e(o){return e="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},e(o)
/**
 * CoreMedia preview module
 *
 * Initializes the communication between Studio and CAE to provide the PBE feature.
 *
 * The script is robust against multiple loading.
 *
 * Static Application Security Testing (SAST) tools like Checkmarx may complain
 * about this script, if they assume that it embeds untrusted data without
 * proper sanitization or encoding. Such reports are false positives.
 *
 * @license CoreMedia Open Source License
 */}var o=window.studioUrlWhitelist||[];function t(e){var i=e.data,r=e.origin,d=void 0;try{d=JSON.parse(i)}catch(e){}if(d&&"initConfirm"===d.type){var a=document.createElement("a");a.href=r;var w=document.createElement("a");if(o.length>0)for(var s=0;s<o.length;s++){w.href=o[s];var c=w.protocol,m=w.hostname,l=w.port;if(c===a.protocol&&m===a.hostname&&l===a.port){window.com_coremedia_pbe_studioUrl=r;break}}else window.com_coremedia_pbe_studioUrl="*";if(window.com_coremedia_pbe_studioUrl&&d.body.url){var p=document.createElement("script");p.type="text/javascript",p.src=d.body.url;var u=document.getElementsByTagName("script")[0];u.parentNode.insertBefore(p,u)}else n("Preview received initConfirm message from origin "+r+". This does not match any of the given whitelist URLs (see 'cae.preview.pbe.studio-url-whitelist')");window.removeEventListener("message",t)}}function n(e){window.console&&window.console.warn&&window.console.warn(e)}var i,r="undefined"!==e(window.PDE_INITIALIZED);r&&n("Preview webresources are attached to DOM multiple times. Consider removing duplicates."),window.JSON||n("Cannot initialize preview: JSON not supported"),window.JSON&&!r&&(window.PDE_INITIALIZED=!1,i=function(){if(!window.PDE_INITIALIZED){if(window.parent&&window.parent!==window){window.addEventListener("message",t);var e=JSON.stringify({type:"init",body:{windowType:"preview"}});window.parent.postMessage(e,"*")}window.PDE_INITIALIZED=!0}},"loading"!==document.readyState?i():document.addEventListener("DOMContentLoaded",i))}();
//# sourceMappingURL=coremedia.preview.js.map