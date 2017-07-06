package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.PropertiesWithDefaultsAdapterBase;

/**
 * Declares an observable with properties and their default values for the call-to-action feature.
 */
public class CallToActionSettings extends PropertiesWithDefaultsAdapterBase {

  public static const CALL_TO_ACTION_DISABLED_PROPERTY_NAME:String = "callToActionDisabled";
  public static const CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME:String = "callToActionCustomText";

  public function CallToActionSettings(ve:ValueExpression) {
    super(ve,
            CALL_TO_ACTION_DISABLED_PROPERTY_NAME, false,
            CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, ""
    );
  }

  public function get callToActionDisabled():Boolean {
    return getProperty(CALL_TO_ACTION_DISABLED_PROPERTY_NAME);
  }

  public function set callToActionDisabled(value:Boolean):void {
    setProperty(CALL_TO_ACTION_DISABLED_PROPERTY_NAME, value);
  }

  public function get callToActionCustomText():String {
    return getProperty(CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME);
  }

  public function set callToActionCustomText(value:String):void {
    setProperty(CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, value);
  }
}
}
