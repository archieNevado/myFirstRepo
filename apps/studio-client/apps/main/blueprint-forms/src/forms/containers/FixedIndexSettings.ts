import PropertiesWithDefaultsAdapterBase from "@coremedia/studio-client.client-core/data/PropertiesWithDefaultsAdapterBase";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";

/**
 * Declares an observable with properties and their default values for the fixed index feature.
 */
class FixedIndexSettings extends PropertiesWithDefaultsAdapterBase {

  static readonly INDEX_PROPERTY_NAME: string = "index";

  constructor(ve: ValueExpression) {
    super(ve,
      FixedIndexSettings.INDEX_PROPERTY_NAME, null,
    );
  }

  get index(): number {
    return this.getProperty(FixedIndexSettings.INDEX_PROPERTY_NAME);
  }

  set index(value: number) {
    this.setProperty(FixedIndexSettings.INDEX_PROPERTY_NAME, value);
  }
}

export default FixedIndexSettings;
