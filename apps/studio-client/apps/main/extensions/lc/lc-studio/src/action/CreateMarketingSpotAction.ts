import MarketingSpotImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpotImpl";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreateCatalogObjectDocumentAction from "./CreateCatalogObjectDocumentAction";

interface CreateMarketingSpotActionConfig extends Config<CreateCatalogObjectDocumentAction> {
}

class CreateMarketingSpotAction extends CreateCatalogObjectDocumentAction {
  declare Config: CreateMarketingSpotActionConfig;

  constructor(config: Config<CreateMarketingSpotAction> = null) {
    super(ConfigUtils.apply(Config(CreateMarketingSpotAction, {
      actionName: "createMarketingSpot",
      contentType: "CMMarketingSpot",
      catalogObjectType: MarketingSpotImpl,

    }), config));
  }
}

export default CreateMarketingSpotAction;
