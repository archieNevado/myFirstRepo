package com.coremedia.blueprint.studio.upload {
import ext.tip.QuickTipManager;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.blueprint.studio.UploadStudioPlugin')]
public class UploadHelper {
  public static function isHTML5():Boolean {
    return window.File && window.FileReader && window.FileList && window.Blob;
  }

  public static function resolveTooltip():String {
    if (!isHTML5()) {
      return ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadFileAction_tooltip_disabled');
    }
    return ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadFileAction_tooltip');
  }

  public static function resolveMenuItemTooltip(thisMenuItem:*):void {
    if (!UploadHelper.isHTML5()) {
      QuickTipManager.register({target:thisMenuItem.getEl().getAttribute('id'), text:UploadHelper.resolveTooltip()});
    }
  }
}
}