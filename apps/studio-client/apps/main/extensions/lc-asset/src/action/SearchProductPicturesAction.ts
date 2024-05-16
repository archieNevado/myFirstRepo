import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import SearchProductPicturesActionBase from "./SearchProductPicturesActionBase";

interface SearchProductPicturesActionConfig extends Config<SearchProductPicturesActionBase> {
}

class SearchProductPicturesAction extends SearchProductPicturesActionBase {
  declare Config: SearchProductPicturesActionConfig;

  constructor(config: Config<SearchProductPicturesAction> = null) {
    super(ConfigUtils.apply(Config(SearchProductPicturesAction), config));
  }
}

export default SearchProductPicturesAction;
