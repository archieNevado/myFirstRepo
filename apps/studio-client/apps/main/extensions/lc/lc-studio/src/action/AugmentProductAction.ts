import ProductImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductImpl";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AugmentCatalogObjectActionBase from "./AugmentCatalogObjectActionBase";

interface AugmentProductActionConfig extends Config<AugmentCatalogObjectActionBase> {
}

class AugmentProductAction extends AugmentCatalogObjectActionBase {
  declare Config: AugmentProductActionConfig;

  constructor(config: Config<AugmentProductAction> = null) {
    super(ConfigUtils.apply(Config(AugmentProductAction, {
      actionName: "augmentProduct",
      contentType: "CMExternalProduct",
      catalogObjectType: ProductImpl,
      inheritEditors: false,

    }), config));
  }
}

export default AugmentProductAction;
