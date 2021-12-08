import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogHelper from "../helper/CatalogHelper";
import CommerceObjectSelector from "./CommerceObjectSelector";

interface CommerceObjectSelectorBaseConfig extends Config<LocalComboBox> {
}

/**
 * The base class of the commerce objects selector combobox
 * It contains mainly the model logic to retrieve the catalog objects from the commerce system and
 * the string conversion acrobatic to ensure that the catalog object id (which looks like a number) is stored as String.
 */
class CommerceObjectSelectorBase extends LocalComboBox {
  declare Config: CommerceObjectSelectorBaseConfig;

  #quote: boolean = false;

  constructor(config: Config<CommerceObjectSelector> = null) {
    super((()=>{
      this.#quote = config.quote;
      return config;
    })());

    // reset the current selection if the store has been modified
    this.getStore().addListener("add", bind(this, this.#resetSelection));
    this.getStore().addListener("update", bind(this, this.#resetSelection));
    this.getStore().addListener("datachanged", bind(this, this.#resetSelection));
  }

  #resetSelection(): void {
    const v = this.getValue();
    if (v && this.getStore().findExact(this.valueField, this.#unquote(v)) >= 0) {
      this.setValue(v);
    }
  }

  getSelectableCatalogObjectsExpression(config: Config<CommerceObjectSelector>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Array<any> => {
      const store: Store = CatalogHelper.getInstance().getStoreForContentExpression(config.contentValueExpression).getValue();
      if (!store) {
        return undefined;
      } else {
        this.clearInvalid();
      }

      let catalogObjectsArray = as(config.getCommerceObjectsFunction.call(null, store), Array);
      if (catalogObjectsArray && config.selectedCatalogObjectsExpression) {
        const selectedCatalogObjects = config.selectedCatalogObjectsExpression.getValue();
        if (selectedCatalogObjects) {
          catalogObjectsArray = catalogObjectsArray.filter((catalogObject: CatalogObject): boolean =>
            selectedCatalogObjects.indexOf(catalogObject) < 0,
          );
        }
      }

      if (!catalogObjectsArray) {
        return [];
      }
      return RemoteBeanUtil.filterAccessible(catalogObjectsArray);
    });
  }

  override setValue(value: any): this {
    let valueString = as(value, String);
    valueString = this.#unquote(valueString);
    return super.setValue(valueString);
  }

  #unquote(valueString: string): string {
    if (!this.#quote) return valueString;

    if (valueString) {
      if (valueString.indexOf("\"") === 0) {
        valueString = valueString.substr(1);
      }
      if (valueString.lastIndexOf("\"") === valueString.length - 1) {
        valueString = valueString.substr(0, valueString.length - 1);
      }
    }
    return valueString;
  }

  getUnquotedValue(): string {
    return this.#unquote(this.getValue());
  }

  override getValue(): any {
    const value = super.getValue();
    if (!this.#quote) return value;
    return value ? "\"" + value + "\"" : value;
  }

}

export default CommerceObjectSelectorBase;
