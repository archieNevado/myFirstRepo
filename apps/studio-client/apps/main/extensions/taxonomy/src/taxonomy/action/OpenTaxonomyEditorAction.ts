import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenTaxonomyEditorActionBase from "./OpenTaxonomyEditorActionBase";

interface OpenTaxonomyEditorActionConfig extends Config<OpenTaxonomyEditorActionBase>, Partial<Pick<OpenTaxonomyEditorAction,
  "taxonomyId"
>> {
}

class OpenTaxonomyEditorAction extends OpenTaxonomyEditorActionBase {
  declare Config: OpenTaxonomyEditorActionConfig;

  constructor(config: Config<OpenTaxonomyEditorAction> = null) {
    super(ConfigUtils.apply(Config(OpenTaxonomyEditorAction), config));
  }

  taxonomyId: string = null;
}

export default OpenTaxonomyEditorAction;
