import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenTaxonomyChooserActionBase from "./OpenTaxonomyChooserActionBase";

interface OpenTaxonomyChooserActionConfig extends Config<OpenTaxonomyChooserActionBase>, Partial<Pick<OpenTaxonomyChooserAction,
  "propertyValueExpression" |
  "singleSelection"
>> {
}

class OpenTaxonomyChooserAction extends OpenTaxonomyChooserActionBase {
  declare Config: OpenTaxonomyChooserActionConfig;

  static readonly ACTION_ID: string = "openTaxonomyChooserAction";

  constructor(config: Config<OpenTaxonomyChooserAction> = null) {
    super(ConfigUtils.apply(Config(OpenTaxonomyChooserAction), config));
  }

  propertyValueExpression: ValueExpression = null;

  singleSelection: boolean = false;
}

export default OpenTaxonomyChooserAction;
