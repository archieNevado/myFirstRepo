import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogObjectActionBase from "./CatalogObjectActionBase";

interface CatalogObjectActionConfig extends Config<CatalogObjectActionBase>, Partial<Pick<CatalogObjectAction,
  "catalogObjectExpression"
>> {
}

class CatalogObjectAction extends CatalogObjectActionBase {
  declare Config: CatalogObjectActionConfig;

  constructor(config: Config<CatalogObjectAction> = null) {
    super(ConfigUtils.apply(Config(CatalogObjectAction), config));
  }

  /**
   * A value expression evaluating to a catalog object to be processed
   * or to an array of catalog objects to be processed.
   */
  catalogObjectExpression: ValueExpression = null;
}

export default CatalogObjectAction;
