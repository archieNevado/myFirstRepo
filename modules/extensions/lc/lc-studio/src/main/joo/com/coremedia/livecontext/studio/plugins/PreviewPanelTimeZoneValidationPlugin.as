package com.coremedia.livecontext.studio.plugins {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtilInternal;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.mixins.ValidationState;
import com.coremedia.ui.mixins.ValidationStateMixin;

import ext.Component;
import ext.StringUtil;
import ext.plugin.AbstractPlugin;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.livecontext.studio.LivecontextStudioPlugin')]
public class PreviewPanelTimeZoneValidationPlugin extends AbstractPlugin {

  /**
   * The model for the date, time and timezone
   */
  [Bindable]
  public var model:Bean;

  [Bindable]
  public var previewPanel:PreviewPanel;

  private var timeZoneIdValueExpression:ValueExpression;

  public function PreviewPanelTimeZoneValidationPlugin(config:PreviewPanelTimeZoneValidationPlugin = null) {
    super(config);
  }

  override public function init(host:Component):void {
    super.init(host);
    var validationStateMixin:ValidationStateMixin = host as ValidationStateMixin;
    if (validationStateMixin) {
      var warningValueExpression:ValueExpression = getWarningValueExpression(this);
      warningValueExpression.addChangeListener(function (validationStateValueExpression:ValueExpression):void {
        applyWarning(validationStateMixin, warningValueExpression);
      });
      applyWarning(validationStateMixin, warningValueExpression);
    }
  }

  private function getTimeZoneIdValueExpression(config:PreviewPanelTimeZoneValidationPlugin):ValueExpression {
    if (!timeZoneIdValueExpression) {
      timeZoneIdValueExpression = ValueExpressionFactory.createFromFunction(function ():String {
        var text:String;
        var entityExpression:ValueExpression = config.previewPanel.getCurrentPreviewContentValueExpression();
        var storeExpression:ValueExpression;
        if (entityExpression.getValue() is Content) {
          storeExpression = CatalogHelper.getInstance().getStoreForContentExpression(WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION);
        } else if (entityExpression.getValue() is CatalogObject) {
          storeExpression = entityExpression.extendBy(CatalogObjectPropertyNames.STORE);
        }

        if (storeExpression && storeExpression.getValue()) {
          var timeZoneId:String = Store(storeExpression.getValue()).getTimeZoneId();
          if (timeZoneId) {
            text = timeZoneId;
          }
        }

        return text;
      });
    }
    return timeZoneIdValueExpression;
  }

  internal function getWarningValueExpression(config:PreviewPanelTimeZoneValidationPlugin):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var commerceTimeZoneId:String = getTimeZoneIdValueExpression(config).getValue();
      if (!commerceTimeZoneId) {
        return undefined;
      }
      var dateTimeModel:Bean = config.model;
      var timeZoneId:String = dateTimeModel.get("timeZone");
      if (timeZoneId !== commerceTimeZoneId) {
        return StringUtil.format(ResourceManager.getInstance().getString('com.coremedia.livecontext.studio.LivecontextStudioPlugin', 'Preview_Wcs_Timezone_Divergation_Warning_Message'),
                ContentLocalizationUtilInternal.localizeTimeZoneID(commerceTimeZoneId));
      }
      return null;
    });
  }

  private static function applyWarning(validationStateMixin:ValidationStateMixin, valueExpression:ValueExpression):void {
    var warningMessage:String = valueExpression.getValue();
    if (warningMessage) {
      validationStateMixin.validationState = ValidationState.WARNING;
      validationStateMixin.validationMessage = warningMessage;
    } else {
      validationStateMixin.validationMessage = null;
      validationStateMixin.validationState = null;
    }

  }
}
}