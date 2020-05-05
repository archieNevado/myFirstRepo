package com.acme.coremedia.studio.request.support {
import com.acme.coremedia.studio.request.util.HTTP_STATUS_CODES;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.JSON;

import js.XMLHttpRequest;

/**
 * Mocks a <code>XMLHttpRequest</code> as required by existing tests. Note, that it is
 * not possible to extend <code>XMLHttpRequest</code> - instead this mock contains
 * "comparable" methods which should behave similar as in <code>XMLHttpRequest</code>.
 * <p>
 * Note also, that the mock does not guarantee to be complete for all use-cases. It
 * may need to be extended for different requests which are yet untested.
 */
public class MockXMLHttpRequest {
  /**
   * Observer who will be informed, when this request got sent.
   */
  private var _observer:RequestSentObserver;
  /**
   * The status code to return.
   */
  private var _status:uint;
  /**
   * The response object (typically from JSON).
   */
  private var _response:Object;
  /**
   * Custom header, to override/extend default one.
   */
  private var _header:Object = {};
  /**
   * Method set in async scenarios, to be called, when the state changes.
   * As we only simulate a state change, _onreadystatechange is just scheduled
   * to be invoked later. Note, that current Ext code only sets this method
   * if we are in async-mode. Otherwise Ext directly calls Connection.onComplete
   * after sending the request.
   */
  private var _onreadystatechange:Function = function ():void {
  };

  /**
   * <p>
   * Mock for <code>XMLHttpRequest</code> which prevents send but instead sets the corresponding
   * response directly.
   * </p>
   * <p>
   * <b>Example Response Object, similar to REST tests:</b>
   * <pre>
   * {
   *   "$Type":"com.coremedia.cap.content.IssuesDetectedError",
   *   "errorCode":"CAP-REST-01008",
   *   "errorName":"NOT_VALID",
   *   "issues": {
   *     "entity" : { "$Ref": "content/12"},
   *     "byProperty" : {
   *       "title": [
   *         {
   *           "$Type": "com.coremedia.ui.data.validation.Issue",
   *           "entity": {
   *             "$Ref" : "content/12"
   *           },
   *           "severity": "ERROR",
   *           "property": "title",
   *           "code": "ISSUE-BLACKLISTED",
   *           "arguments": []
   *         }
   *       ]
   *     }
   *   }
   * }
   * </pre>
   *
   * @param status the HTTP status code to respond
   * @param response the response object
   */
  public function MockXMLHttpRequest(observer:RequestSentObserver, status:uint, response:Object) {
    _observer = observer;
    _status = status;
    _response = response || {};
  }

  //noinspection JSUnusedGlobalSymbols
  public function get header():Object {
    return _header;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Sets the header fields to be contained in the response. Defaults to entries for date (current date) and
   * content-type (application/json).
   *
   * @param value additional fields for header or overrides for default fields
   */
  public function set header(value:Object):void {
    _header = value || {};
  }

  //noinspection JSUnusedGlobalSymbols
  public function get onreadystatechange():Function {
    return _onreadystatechange;
  }

  /**
   * Sets the callback to be invoked, when the state changes. This method is expected to be
   * set only in async mode by Ext's Connection.
   *
   * @param value function to be called when state changes, currently most likely Connection.onComplete
   */
  //noinspection JSUnusedGlobalSymbols
  public function set onreadystatechange(value:Function):void {
    _onreadystatechange = value || function ():void {
    };
  }

  /**
   * Send the request. As this is a mock request, nothing is sent - we only invoke the
   * <code>onreadystatechange</code> callback. In sync-mode the callback <code>onreadystatechange</code>
   * is expected to be a dummy (the default value) which does nothing.
   *
   * @param body ignored body, as we don't need to send anything
   */
  //noinspection JSUnusedGlobalSymbols
  public function send(body:* = undefined):void {
    // Mostly harmless in non-async context: It simply will do nothing.
    EventUtil.invokeLater(_onreadystatechange);
  }

  //noinspection JSMethodCanBeStatic
  /**
   * As we are a simulated request, we are always DONE!
   */
  public function get readyState():int {
    return XMLHttpRequest.DONE;
  }

  /**
   * The mocked status code.
   */
  public function get status():uint {
    return _status;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Mocked status text, derived from status code via {@link com.acme.coremedia.studio.request.util.HTTP_STATUS_CODES}.
   */
  public function get statusText():String {
    return HTTP_STATUS_CODES[this.status];
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * The response text, which is the JSON-encoded response object.
   */
  public function get responseText():String {
    EventUtil.invokeLater(function ():void {
      _observer.requestSent()
    });
    return JSON.encode(_response);
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Response headers parsed by Ext-JS. By default contains the fields <code>date</code> and
   * <code>content-type</code>. The latter one is set as <code>application/json</code> but may
   * be overridden via {@link #header}.
   *
   * @return response headers, separated by newlines (as expected by Ext)
   */
  public function getAllResponseHeaders():String {
    return toResponseHeader(_header);
  }

  private static function toResponseHeader(header:Object):String {
    var defaultHeader:Object = {
      'date': new Date().toGMTString(),
      'content-type': 'application/json'
    };
    var actualHeader:Object = Ext.apply(defaultHeader, header);
    var stringHeaders:Array = [];

    for (var key:String in actualHeader) {
      if (actualHeader.hasOwnProperty(key)) {
        stringHeaders.push(key + ":" + actualHeader[key]);
      }
    }
    return stringHeaders.join("\n");
  }

}
}
