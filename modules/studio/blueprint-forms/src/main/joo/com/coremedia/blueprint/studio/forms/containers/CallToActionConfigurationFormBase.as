package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ui.data.ValueExpression;

/**
 * Fires after the configuration has changed.
 */
[Event(name = "CTAConfigurationChanged")] // NOSONAR - no type

public class CallToActionConfigurationFormBase extends PropertyFieldGroup {

  public static const CTA_CONFIGURATION_CHANGED_EVENT:String = "CTAConfigurationChanged";

  [Bindable]
  public var settingsVE:ValueExpression;

  private var _ctaSettings:CallToActionSettings;

  private var _ctaViewModel:CallToActionViewModel;

  public function CallToActionConfigurationFormBase(config:CallToActionConfigurationForm = null) {
    super(config);
    ctaSettings.addListener(CallToActionSettings.CALL_TO_ACTION_DISABLED_PROPERTY_NAME, settingsDisabledListener);
    ctaSettings.addListener(CallToActionSettings.CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, settingsTextListener);
    if (settingsVE.isLoaded()) {
      settingsDisabledListener();
      settingsTextListener();
    }
  }

  [Bindable]
  internal function get ctaSettings():CallToActionSettings {
    if (!_ctaSettings) {
      _ctaSettings = new CallToActionSettings(settingsVE);
    }
    return _ctaSettings;
  }

  [Bindable]
  internal function get ctaViewModel():CallToActionViewModel {
    if (!_ctaViewModel) {
      _ctaViewModel = new CallToActionViewModel();
    }
    return _ctaViewModel;
  }

  private function settingsDisabledListener():void {
    fireEvent(CTA_CONFIGURATION_CHANGED_EVENT);
  }

  private function settingsTextListener():void {
    fireEvent(CTA_CONFIGURATION_CHANGED_EVENT);
  }

  override protected function onDestroy():void {
    ctaViewModel.destroy();
    ctaSettings.destroy();
    super.onDestroy();
  }
}
}
