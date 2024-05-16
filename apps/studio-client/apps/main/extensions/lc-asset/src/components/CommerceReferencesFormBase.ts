import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import CommerceReferencesForm from "./CommerceReferencesForm";

interface CommerceReferencesFormBaseConfig extends Config<PropertyFieldGroup> {
}

class CommerceReferencesFormBase extends PropertyFieldGroup {
  declare Config: CommerceReferencesFormBaseConfig;

  static readonly #PROPERTIES: string = "properties";

  static readonly #LOCAL_SETTINGS_STRUCT_NAME: string = "localSettings";

  static readonly #COMMERCE_STRUCT_NAME: string = "commerce";

  static readonly #PRODUCTS_LIST_NAME: string = CatalogHelper.REFERENCES_LIST_NAME;

  static readonly PROPERTY_NAME: string = CommerceReferencesFormBase.#LOCAL_SETTINGS_STRUCT_NAME + "." + CommerceReferencesFormBase.#COMMERCE_STRUCT_NAME + "." + CommerceReferencesFormBase.#PRODUCTS_LIST_NAME;

  static readonly #INHERIT_PROPERTY_NAME: string = "inherit";

  #inheritedExpression: ValueExpression = null;

  constructor(config: Config<CommerceReferencesForm> = null) {
    super(config);
  }

  protected getShopExpression(config: Config<CommerceReferencesForm>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const store = cast(Store, CatalogHelper.getInstance().getStoreForContentExpression(config.bindTo).getValue());
      return !!store && !!store.getName() && !CatalogHelper.getInstance().isCoreMediaStore(store);
    });
  }

  protected getReadOnlyExpression(config: Config<CommerceReferencesForm>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(this.getReadOnlyFunction(config));
  }

  protected getReadOnlyFunction(config: Config<CommerceReferencesForm>): AnyFunction {
    return (): boolean => {
      //are we forced to set read-only?
      if (config.forceReadOnlyValueExpression.getValue()) {
        return true;
      }
      if (config.bindTo) {
        return ! !this.getInheritedExpression(config).getValue();
      }
      return false;
    };
  }

  protected getInheritedExpression(config: Config<CommerceReferencesForm>): ValueExpression {
    if (!this.#inheritedExpression) {
      this.#inheritedExpression = config.bindTo.extendBy(CommerceReferencesFormBase.#PROPERTIES, CommerceReferencesFormBase.#LOCAL_SETTINGS_STRUCT_NAME, CommerceReferencesFormBase.#COMMERCE_STRUCT_NAME, CommerceReferencesFormBase.#INHERIT_PROPERTY_NAME);
    }
    return this.#inheritedExpression;
  }

  protected createStructs(): void {
    const localSettingsStructExpression = this.bindTo.extendBy(CommerceReferencesFormBase.#PROPERTIES, CommerceReferencesFormBase.#LOCAL_SETTINGS_STRUCT_NAME);
    localSettingsStructExpression.loadValue((): void => {
      const localSettingsStruct: Struct = localSettingsStructExpression.getValue();
      cast(RemoteBean, localSettingsStruct).load((): void => {
        if (!localSettingsStruct.get(CommerceReferencesFormBase.#COMMERCE_STRUCT_NAME)) {
          localSettingsStruct.getType().addStructProperty(CommerceReferencesFormBase.#COMMERCE_STRUCT_NAME);
        }

        const commerceStruct: Struct = localSettingsStruct.get(CommerceReferencesFormBase.#COMMERCE_STRUCT_NAME);
        const productsStruct: Struct = commerceStruct.get(CommerceReferencesFormBase.#PRODUCTS_LIST_NAME);
        if (!productsStruct) {
          commerceStruct.getType().addStringListProperty(CommerceReferencesFormBase.#PRODUCTS_LIST_NAME, 1000000);
        }
      });
    });
  }

}

export default CommerceReferencesFormBase;
