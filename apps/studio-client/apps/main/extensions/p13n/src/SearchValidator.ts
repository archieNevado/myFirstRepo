import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import SearchValidatorBase from "./SearchValidatorBase";

interface SearchValidatorConfig extends Config<SearchValidatorBase> {
}

class SearchValidator extends SearchValidatorBase {
  declare Config: SearchValidatorConfig;

  constructor(config: Config<SearchValidator> = null) {
    super(ConfigUtils.apply(Config(SearchValidator), config));
  }
}

export default SearchValidator;
