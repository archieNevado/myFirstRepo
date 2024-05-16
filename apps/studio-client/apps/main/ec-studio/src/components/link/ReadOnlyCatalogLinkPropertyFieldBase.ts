import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ReadOnlyCatalogLinkPropertyField from "./ReadOnlyCatalogLinkPropertyField";

interface ReadOnlyCatalogLinkPropertyFieldBaseConfig extends Config<SwitchingContainer> {
}

class ReadOnlyCatalogLinkPropertyFieldBase extends SwitchingContainer {
  declare Config: ReadOnlyCatalogLinkPropertyFieldBaseConfig;

  protected static readonly READ_ONLY_CATALOG_LINK_ITEM_ID: string = "readOnlyCatalogLink";

  protected static readonly READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID: string = "readOnlyCatalogLinkEmptyDisplayField";

  constructor(config: Config<ReadOnlyCatalogLinkPropertyField> = null) {
    super(config);
  }

  getActiveCatalogLinkPropertyValueExpression(config: Config<ReadOnlyCatalogLinkPropertyField>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(bind(this, this.#getActiveCatalogLinkProperty), config);
  }

  #getActiveCatalogLinkProperty(config: Config<ReadOnlyCatalogLinkPropertyField>): string {
    const valueExpression = config.bindTo.extendBy(config.propertyName);
    const values: Array<any> = valueExpression.getValue();
    if (values && values.length != 0) {
      return ReadOnlyCatalogLinkPropertyFieldBase.READ_ONLY_CATALOG_LINK_ITEM_ID;
    }
    return ReadOnlyCatalogLinkPropertyFieldBase.READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID;
  }

}

export default ReadOnlyCatalogLinkPropertyFieldBase;
