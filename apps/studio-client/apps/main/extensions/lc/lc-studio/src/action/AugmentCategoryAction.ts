import CategoryImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CategoryImpl";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AugmentCatalogObjectActionBase from "./AugmentCatalogObjectActionBase";

interface AugmentCategoryActionConfig extends Config<AugmentCatalogObjectActionBase> {
}

class AugmentCategoryAction extends AugmentCatalogObjectActionBase {
  declare Config: AugmentCategoryActionConfig;

  constructor(config: Config<AugmentCategoryAction> = null) {
    super(ConfigUtils.apply(Config(AugmentCategoryAction, {
      actionName: "augmentCategory",
      contentType: "CMExternalChannel",
      catalogObjectType: CategoryImpl,
      inheritEditors: false,

    }), config));
  }
}

export default AugmentCategoryAction;
