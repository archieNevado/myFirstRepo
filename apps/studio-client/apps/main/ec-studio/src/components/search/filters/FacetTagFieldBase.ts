import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import InputChipsFieldBase from "@coremedia/studio-client.main.editor-components/sdk/components/ChipsField/InputChipsFieldBase";
import Config from "@jangaroo/runtime/Config";

interface FacetTagFieldBaseConfig extends Config<InputChipsFieldBase>, Partial<Pick<FacetTagFieldBase,
  "facetValueExpression" |
  "selectedFacetValuesExpression"
>> {
}

class FacetTagFieldBase extends InputChipsFieldBase {
  declare Config: FacetTagFieldBaseConfig;

  static readonly LABEL: string = "label";

  static readonly QUERY: string = "query";

  facetValueExpression: ValueExpression = null;

  selectedFacetValuesExpression: ValueExpression = null;

  constructor(config: Config<FacetTagFieldBase> = null) {
    super(config);
  }
}

export default FacetTagFieldBase;
