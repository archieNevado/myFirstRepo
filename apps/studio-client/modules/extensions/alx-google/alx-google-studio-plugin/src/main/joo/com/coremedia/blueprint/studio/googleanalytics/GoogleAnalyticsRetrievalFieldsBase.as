package com.coremedia.blueprint.studio.googleanalytics {
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

public class GoogleAnalyticsRetrievalFieldsBase extends PropertyFieldGroup {

  private static const GOOGLE_ANALYTICS:String = 'googleAnalytics';
  private static const AUTH_FILE:String = 'authFile';
  private static const LOCAL_SETTINGS:String = 'localSettings';
  private static const CM_DOWNLOAD:String = "CMDownload";

  private var authFileVE:ValueExpression;
  private var localSettings:RemoteBean;

  public function GoogleAnalyticsRetrievalFieldsBase(config:GoogleAnalyticsRetrievalFields = null) {
    super(config);
    updateAuthFileFromStruct();
    getAuthFileVE().addChangeListener(updateStruct);
    bindTo.addChangeListener(updateAuthFileFromStruct);
  }

  private function updateStruct():void {
    var value:Array = getAuthFileVE().getValue();
    if (value && value.length > 0) {
      applyToStruct(bindTo.getValue(), CM_DOWNLOAD, AUTH_FILE, value[0]);
    } else {
      removeLinkFromStruct(bindTo.getValue(), AUTH_FILE);
    }
  }

  private static function removeLinkFromStruct(content:Content, structPropertyName:String):void {
    var struct:Struct = content.getProperties().get(LOCAL_SETTINGS);
    if (struct) {
      var googleAnalytics:Struct = getStruct(struct, GOOGLE_ANALYTICS);
      if (googleAnalytics) {
        googleAnalytics.getType().removeProperty(structPropertyName);
      }
    }
  }

  private static function getStruct(struct:Struct, key:String):Struct {
    return struct.get(key);
  }


  protected function getAuthFileVE():ValueExpression {
    if (!authFileVE) {
      authFileVE = ValueExpressionFactory.createFromValue([]);
    }
    return authFileVE;
  }

  private function updateAuthFileFromStruct():void {
    var c:Content = bindTo.getValue();
    c.load(function ():void {
      var props:ContentProperties = c.getProperties();
      var init:Boolean = false;
      if (!localSettings) {
        init = true;
      }
      localSettings = props.get(LOCAL_SETTINGS) as RemoteBean;
      if (init) {
        localSettings.addPropertyChangeListener(GOOGLE_ANALYTICS, updateAuthFileFromLocalSettings);
      }
      localSettings.load(function ():void {
        updateAuthFileFromLocalSettings();
      });
    });
  }

  private function updateAuthFileFromLocalSettings():void {
    var googleAnalytics:Struct = getStruct(localSettings as Struct, GOOGLE_ANALYTICS);
    if (googleAnalytics) {
      var authFile:Struct = googleAnalytics.get(AUTH_FILE);
      if (!authFile) {
        getAuthFileVE().setValue([]);
      } else {
        getAuthFileVE().setValue([authFile]);
      }
    }
  }

  private function applyToStruct(content:Content, contentType:String, structPropertyName:String, link:Content):void {
    var struct:Struct = content.getProperties().get(LOCAL_SETTINGS);

    //the substruct can be created on the fly but isn't loaded, so we trigger an invalidate in this case
    var googleAnalytics:Struct = getStruct(struct, GOOGLE_ANALYTICS);
    if(!googleAnalytics) {
      struct.getType().addStructProperty(GOOGLE_ANALYTICS);
      content.invalidate(function():void {
        applyToStruct(content, contentType, structPropertyName, link);
      });
      return;
    }

    var capType:CapType = SESSION.getConnection().getContentRepository().getContentType(contentType);
    googleAnalytics.getType().addLinkProperty(structPropertyName, capType, link);

    // apply the link again: in case the substruct had to be created previously,
    // we need to notify the component about the missed initialization
    getAuthFileVE().setValue([link]);
  }

  override protected function onDestroy():void {
    super.onDestroy();
    localSettings.removePropertyChangeListener(GOOGLE_ANALYTICS, updateAuthFileFromLocalSettings);
    getAuthFileVE().removeChangeListener(updateStruct);
    bindTo.removeChangeListener(updateAuthFileFromStruct);
  }
}
}
