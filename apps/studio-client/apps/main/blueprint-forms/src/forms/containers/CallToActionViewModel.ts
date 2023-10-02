import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";

/**
 * Declares an observable with properties and their default values for the call-to-action feature.
 */
class CallToActionViewModel extends BeanImpl {

  static readonly CTA_ENABLED_PROPERTY_NAME: string = "CTAEnabled";

  static readonly CTA_TEXT_PROPERTY_NAME: string = "CTAText";

  static readonly CTA_HASH_PROPERTY_NAME: string = "CTAHash";

  constructor() {
    super();
  }

  CTAEnabled: boolean;

  CTAText: string;

  CTAHash: string;
}

export default CallToActionViewModel;
