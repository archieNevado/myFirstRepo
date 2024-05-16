import PropertiesWithDefaultsAdapterBase from "@coremedia/studio-client.client-core/data/PropertiesWithDefaultsAdapterBase";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import { bind } from "@jangaroo/runtime";

/**
 * Declares an observable with properties and their default values for the call-to-action feature.
 *
 * The settings will be stored in the bean the given {@link ValueExpression} provides using the following properties:
 *
 * {@link #CallToActionSettings#CALL_TO_ACTION_ENABLED_PROPERTY_NAME} specifies if the cta feature is enabled.
 * {@link #CallToActionSettings#CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME} specifies a custom cta text (optional).
 *
 * Also supports a legacy settings format via the constructor parameter "legacy". If set to true the internal settings
 * format is changed ("callToActionEnabled" is replaced with "callToActionDisabled").
 * The difference is that the legacy format assumes that the cta feature is always enabled while the new format only
 * enables the cta feature when enabled is set to true. The difference is only made internally (when storing and
 * retrieving the settings), always use the new settings format to access the properties, the old format will be
 * transformed accordingly when storing the settings in the provided bean.
 */
class CallToActionSettings extends PropertiesWithDefaultsAdapterBase {

  // legacy property name
  static readonly #CALL_TO_ACTION_DISABLED_PROPERTY_NAME: string = "callToActionDisabled";

  static readonly CALL_TO_ACTION_ENABLED_PROPERTY_NAME: string = "callToActionEnabled";

  static readonly CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME: string = "callToActionCustomText";

  static readonly CALL_TO_ACTION_HASH_PROPERTY_NAME: string = "callToActionHash";

  #legacy: boolean = false;

  constructor(ve: ValueExpression, legacy: boolean = false) {
    super(ve,
      CallToActionSettings.CALL_TO_ACTION_ENABLED_PROPERTY_NAME, false,
      CallToActionSettings.CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, "",
      CallToActionSettings.CALL_TO_ACTION_HASH_PROPERTY_NAME, "",
      // legacy
      CallToActionSettings.#CALL_TO_ACTION_DISABLED_PROPERTY_NAME, false,
    );
    this.#legacy = legacy;

    if (legacy) {
      this.addListener(CallToActionSettings.#CALL_TO_ACTION_DISABLED_PROPERTY_NAME, bind(this, this.#ctaDisabledUpdated));
    }
  }

  #ctaDisabledUpdated(): void {
    this.fireEvent(CallToActionSettings.CALL_TO_ACTION_ENABLED_PROPERTY_NAME, {});
  }

  get callToActionEnabled(): boolean {
    if (this.#legacy) {
      return !this.getProperty(CallToActionSettings.#CALL_TO_ACTION_DISABLED_PROPERTY_NAME);
    }
    return this.getProperty(CallToActionSettings.CALL_TO_ACTION_ENABLED_PROPERTY_NAME);
  }

  set callToActionEnabled(value: boolean) {
    if (this.#legacy) {
      this.setProperty(CallToActionSettings.#CALL_TO_ACTION_DISABLED_PROPERTY_NAME, !value);
    } else {
      this.setProperty(CallToActionSettings.CALL_TO_ACTION_ENABLED_PROPERTY_NAME, value);
    }
  }

  get callToActionCustomText(): string {
    return this.getProperty(CallToActionSettings.CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME);
  }

  set callToActionCustomText(value: string) {
    this.setProperty(CallToActionSettings.CALL_TO_ACTION_CUSTOM_TEXT_PROPERTY_NAME, value);
  }

  get callToActionHash(): string {
    return this.getProperty(CallToActionSettings.CALL_TO_ACTION_HASH_PROPERTY_NAME);
  }

  set callToActionHash(value: string) {
    this.setProperty(CallToActionSettings.CALL_TO_ACTION_HASH_PROPERTY_NAME, value);
  }

  override destroy(): void {
    if (this.#legacy) {
      this.removeListener(CallToActionSettings.#CALL_TO_ACTION_DISABLED_PROPERTY_NAME, bind(this, this.#ctaDisabledUpdated));
    }
    super.destroy();
  }
}

export default CallToActionSettings;
