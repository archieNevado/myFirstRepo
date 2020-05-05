package com.acme.coremedia.studio.hash {
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.login.AutoLogout;
import com.coremedia.ui.util.UrlUtil;

/**
 * This plugin is meant to add hash params supported during test
 * execution only. It allows for example to configure the auto-logout
 * delay during a test.
 */
public class HashParamsPluginBase extends StudioPlugin {
  public function HashParamsPluginBase() {
  }

  /**
   * Allows to set the auto-logout delay in seconds via hash parameter
   * {@code autoLogout.delay}.
   */
  private static function configureAutoLogoutDelay():void {
    var delayString:String = UrlUtil.getHashParam("autoLogout.delay");
    if (delayString) {
      AutoLogout.setDelay(Number(delayString));
    }
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);
    configureAutoLogoutDelay();
  }
}
}
