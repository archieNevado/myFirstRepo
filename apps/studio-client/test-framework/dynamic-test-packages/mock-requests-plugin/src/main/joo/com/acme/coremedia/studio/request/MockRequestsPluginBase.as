package com.acme.coremedia.studio.request {
import com.acme.coremedia.studio.request.support.MockRequestResponse;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.ui.logging.Logger;

import ext.Ajax;
import ext.data.request.AjaxRequest;

/**
 * This plugin is meant to mock requests to Studio REST backend and actually prevent sending
 * them and provide a mocked response instead. It may be used to simulate server outages
 * or to provoke error responses like 403 from the server.
 */
public class MockRequestsPluginBase extends StudioPlugin {
  public function MockRequestsPluginBase() {
  }

  override public function init(editorContext:IEditorContext):void {
    MOCK_REQUEST_REGISTRY = new MockRequestRegistry();

    Logger.info('Initialized com.acme.coremedia.studio.remoteerror.MockRequestRegistry.');

    var originalCreateRequest:Function = Ajax['createRequest'];
    Ajax['createRequest'] = function (options:Object, requestOptions:Object):AjaxRequest {
      var originalRequest:AjaxRequest = originalCreateRequest.call(Ajax, options, requestOptions);
      var applicableMock:MockRequestResponse = MOCK_REQUEST_REGISTRY.findApplicableMock(requestOptions);
      if (applicableMock) {
        originalRequest['openRequest'] = function (options:Object, requestOptions:Object, async:Boolean, username:String, password:String):Object {
          return applicableMock.createResponse(requestOptions);
        };
      }
      return originalRequest;
    };
    super.init(editorContext);
  }
}
}
