import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import { cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CommerceObjectsDisplayField from "./CommerceObjectsDisplayField";

interface CommerceObjectsDisplayFieldBaseConfig extends Config<DisplayField> {
}

class CommerceObjectsDisplayFieldBase extends DisplayField {
  declare Config: CommerceObjectsDisplayFieldBaseConfig;

  #storeForContentExpression: ValueExpression = null;

  #personaContentExpression: ValueExpression = null;

  #catalogObjectIdListName: string = null;

  #emptyMessage: string = null;

  #invalidMessage: string = null;

  constructor(config: Config<CommerceObjectsDisplayField> = null) {
    super(config);
    this.#catalogObjectIdListName = config.catalogObjectIdListName;
    this.#emptyMessage = config.emptyMessage;
    this.#invalidMessage = config.invalidMessage;

    this.#getPersonaContentExpression().setValue(config.personaContent);
  }

  getCommerceObjectsExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      if (!this.#getStoreForContentExpression().getValue()) {
        return LivecontextStudioPlugin_properties.p13n_commerce_no_store_configured;
      }

      const catalogObjects = CatalogHelper.getCatalogObjectsExpression(this.#getPersonaContentExpression(),
        this.#catalogObjectIdListName,
        this.#invalidMessage).getValue();

      if (!catalogObjects || catalogObjects.length === 0) {
        return this.#emptyMessage;
      }

      const commerceObjectsAsString = catalogObjects.map((commerceObject): string => {
        if (is(commerceObject, CatalogObject)) {
          return cast(CatalogObject, commerceObject).getName();
        } else {
          //error handling: when the id is invalid then catalogObject is just a bean with a name containing the error description
          return commerceObject.get("name");
        }
      });

      return commerceObjectsAsString.join(", ");

    });
  }

  #getStoreForContentExpression(): ValueExpression {
    if (!this.#storeForContentExpression) {
      this.#storeForContentExpression = CatalogHelper.getInstance().
        getStoreForContentExpression(this.#getPersonaContentExpression());
    }
    return this.#storeForContentExpression;
  }

  #getPersonaContentExpression(): ValueExpression {
    if (!this.#personaContentExpression) {
      this.#personaContentExpression = ValueExpressionFactory.createFromValue();
    }

    return this.#personaContentExpression;
  }

}

export default CommerceObjectsDisplayFieldBase;
