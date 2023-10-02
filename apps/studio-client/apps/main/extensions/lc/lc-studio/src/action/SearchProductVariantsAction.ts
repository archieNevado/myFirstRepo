import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import SearchProductVariantsActionBase from "./SearchProductVariantsActionBase";

interface SearchProductVariantsActionConfig extends Config<SearchProductVariantsActionBase> {
}

class SearchProductVariantsAction extends SearchProductVariantsActionBase {
  declare Config: SearchProductVariantsActionConfig;

  constructor(config: Config<SearchProductVariantsAction> = null) {
    super(ConfigUtils.apply(Config(SearchProductVariantsAction), config));
  }
}

export default SearchProductVariantsAction;
