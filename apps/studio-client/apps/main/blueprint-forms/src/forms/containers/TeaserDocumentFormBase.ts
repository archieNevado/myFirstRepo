import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import TeaserDocumentForm from "./TeaserDocumentForm";

interface TeaserDocumentFormBaseConfig extends Config<PropertyFieldGroup> {
}

/**
 * The base class for the TeaserDocumentForm
 */
class TeaserDocumentFormBase extends PropertyFieldGroup {
  declare Config: TeaserDocumentFormBaseConfig;

  /**
   * Create a new instance of this class.
   *
   * @param config the config object
   */
  constructor(config: Config<TeaserDocumentForm> = null) {
    super(config);
  }
}

export default TeaserDocumentFormBase;
