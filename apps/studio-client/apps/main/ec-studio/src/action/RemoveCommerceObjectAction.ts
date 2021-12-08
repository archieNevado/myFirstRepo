import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import RemoveCommerceObjectActionBase from "./RemoveCommerceObjectActionBase";

interface RemoveCommerceObjectActionConfig extends Config<RemoveCommerceObjectActionBase>, Partial<Pick<RemoveCommerceObjectAction,
  "commerceObject" |
  "catalogObjectIdsExpression" |
  "catalogObjectIdListName" |
  "actionName"
>> {
}

class RemoveCommerceObjectAction extends RemoveCommerceObjectActionBase {
  declare Config: RemoveCommerceObjectActionConfig;

  constructor(config: Config<RemoveCommerceObjectAction> = null) {
    super(ConfigUtils.apply(Config(RemoveCommerceObjectAction), config));
  }

  commerceObject: Bean | CatalogObject = null;

  catalogObjectIdsExpression: ValueExpression = null;

  catalogObjectIdListName: string = null;

  actionName: string = null;
}

export default RemoveCommerceObjectAction;
