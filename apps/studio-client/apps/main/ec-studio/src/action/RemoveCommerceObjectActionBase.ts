import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import ContentUpdateAction from "@coremedia/studio-client.main.editor-components/sdk/actions/ContentUpdateAction";
import { bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import CatalogHelper from "../helper/CatalogHelper";
import RemoveCommerceObjectAction from "./RemoveCommerceObjectAction";

interface RemoveCommerceObjectActionBaseConfig extends Config<ContentUpdateAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 */
class RemoveCommerceObjectActionBase extends ContentUpdateAction {
  declare Config: RemoveCommerceObjectActionBaseConfig;

  #commerceObject: Bean | CatalogObject = null;

  #catalogObjectIdListName: string = null;

  #catalogObjectIdsExpression: ValueExpression = null;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<RemoveCommerceObjectAction> = null) {
    super((()=>ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties).content,
      config, config.actionName,
      { handler: bind(this, this.#removeCommerceObject) }))());

    this.#commerceObject = config.commerceObject;
    this.#catalogObjectIdListName = config.catalogObjectIdListName;
    this.#catalogObjectIdsExpression = config.catalogObjectIdsExpression;
  }

  #removeCommerceObject(): void {
    let id: string;
    //error handling
    if (is(this.#commerceObject, CatalogObject)) {
      id = this.#commerceObject.getId();
    } else {
      //error handling: when the id is invalid then the catalog object is just a bean with the id containing the invalid id
      id = this.#commerceObject.get("id");
    }
    CatalogHelper.removeCatalogObject(ValueExpressionFactory.createFromValue(this.getContent()),
      this.#catalogObjectIdListName, id, this.#catalogObjectIdsExpression);

  }
}

export default RemoveCommerceObjectActionBase;
