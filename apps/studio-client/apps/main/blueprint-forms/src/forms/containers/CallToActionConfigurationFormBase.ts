import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import IAnnotatedLinkListForm from "@coremedia/studio-client.ext.ui-components/components/IAnnotatedLinkListForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Events from "@jangaroo/ext-ts/Events";
import { bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CallToActionConfigurationForm from "./CallToActionConfigurationForm";
import CallToActionSettings from "./CallToActionSettings";
import CallToActionViewModel from "./CallToActionViewModel";

interface CallToActionConfigurationFormBaseEvents extends Events<PropertyFieldGroup> {

  /**
   * Fires after the configuration has changed.
   */
  CTAConfigurationChanged?(): any;
}

interface CallToActionConfigurationFormBaseConfig extends Config<PropertyFieldGroup>, Partial<Pick<CallToActionConfigurationFormBase,
  "useLegacyCTASettings" |
  "ctaSettings" |
  "ctaViewModel" |
  "settingsVE"
>> {
  listeners?: CallToActionConfigurationFormBaseEvents;
}

// NOSONAR - no type

class CallToActionConfigurationFormBase extends PropertyFieldGroup implements IAnnotatedLinkListForm {
  declare Config: CallToActionConfigurationFormBaseConfig;

  static readonly CTA_CONFIGURATION_CHANGED_EVENT: string = "CTAConfigurationChanged";

  static readonly TEXT_ITEM_ID: string = "customCTAText";

  static readonly HASH_ITEM_ID: string = "CTAHash";

  /**
   * A value expression that leads to a bean storing the {@link CallToActionSettings}.
   */
  #_settingsVE: ValueExpression = null;

  /**
   * If TRUE legacy CTA settings (described in {@link CallToActionSettings}) are used.
   */
  useLegacyCTASettings: boolean = false;

  #_ctaSettings: CallToActionSettings = null;

  #_ctaViewModel: CallToActionViewModel = null;

  constructor(config: Config<CallToActionConfigurationForm> = null) {
    super(config);
    this.ctaSettings.addListener(CallToActionSettings.CALL_TO_ACTION_ENABLED_PROPERTY_NAME, bind(this, this.#ctaConfigurationChangedListener));
    this.ctaSettings.addListener(CallToActionSettings.CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, bind(this, this.#ctaConfigurationChangedListener));
    this.ctaSettings.addListener(CallToActionSettings.CALL_TO_ACTION_HASH_PROPERTY_NAME, bind(this, this.#ctaConfigurationChangedListener));
    if (this.settingsVE.isLoaded()) {
      this.#ctaConfigurationChangedListener();
    }
  }

  get ctaSettings(): CallToActionSettings {
    if (!this.#_ctaSettings) {
      this.#_ctaSettings = new CallToActionSettings(this.settingsVE, this.useLegacyCTASettings);
    }
    return this.#_ctaSettings;
  }

  get ctaViewModel(): CallToActionViewModel {
    if (!this.#_ctaViewModel) {
      this.#_ctaViewModel = new CallToActionViewModel();
    }
    return this.#_ctaViewModel;
  }

  #ctaConfigurationChangedListener(): void {
    this.fireEvent(CallToActionConfigurationFormBase.CTA_CONFIGURATION_CHANGED_EVENT);
  }

  protected override onDestroy(): void {
    this.ctaViewModel.destroy();
    this.ctaSettings.destroy();
    super.onDestroy();
  }

  set settingsVE(settingsVE: ValueExpression) {
    this.#_settingsVE = settingsVE;
  }

  get settingsVE(): ValueExpression {
    return this.#_settingsVE;
  }
}
mixin(CallToActionConfigurationFormBase, IAnnotatedLinkListForm);

export default CallToActionConfigurationFormBase;
