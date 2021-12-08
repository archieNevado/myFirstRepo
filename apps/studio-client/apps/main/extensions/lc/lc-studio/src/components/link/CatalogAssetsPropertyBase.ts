import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import Config from "@jangaroo/runtime/Config";
import CatalogAssetsProperty from "./CatalogAssetsProperty";

interface CatalogAssetsPropertyBaseConfig extends Config<SwitchingContainer> {
}

class CatalogAssetsPropertyBase extends SwitchingContainer {
  declare Config: CatalogAssetsPropertyBaseConfig;

  protected static readonly CATALOG_ASSET_PROPERTY_ITEM_ID: string = "catalogAssets";

  protected static readonly CATALOG_EMPTY_LABEL_ITEM_ID: string = "emptyLabelText";

  #selectedExpression: ValueExpression = null;

  #readOnlyVE: ValueExpression = null;

  constructor(config: Config<CatalogAssetsProperty> = null) {
    super(config);
  }

  static getActiveCatalogAssetPropertyValueExpression(config: Config<CatalogAssetsProperty>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(CatalogAssetsPropertyBase.#getActiveCatalogAssetProperty, config);
  }

  protected getReadOnlyVE(): ValueExpression {
    if (!this.#readOnlyVE) {
      this.#readOnlyVE = ValueExpressionFactory.createFromValue(true);
    }
    return this.#readOnlyVE;
  }

  static #getActiveCatalogAssetProperty(config: Config<CatalogAssetsProperty>): string {
    const valueExpression = config.bindTo.extendBy(config.propertyName);
    //noinspection JSMismatchedCollectionQueryUpdate
    const values: Array<any> = valueExpression.getValue();
    if (values && values.length !== 0) {
      return CatalogAssetsPropertyBase.CATALOG_ASSET_PROPERTY_ITEM_ID;
    }
    return CatalogAssetsPropertyBase.CATALOG_EMPTY_LABEL_ITEM_ID;
  }

  protected getSelectedExpression(): ValueExpression {
    if (!this.#selectedExpression) {
      this.#selectedExpression = ValueExpressionFactory.createFromValue([]);
    }
    return this.#selectedExpression;
  }

}

export default CatalogAssetsPropertyBase;
