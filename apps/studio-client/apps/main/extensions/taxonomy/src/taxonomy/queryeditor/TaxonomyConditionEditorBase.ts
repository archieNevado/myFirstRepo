import ConditionEditor from "@coremedia/studio-client.main.bpbase-studio-dynamic-query-list/conditions/ConditionEditor";
import ConditionEditorBase from "@coremedia/studio-client.main.bpbase-studio-dynamic-query-list/conditions/ConditionEditorBase";
import Config from "@jangaroo/runtime/Config";
import TaxonomyConditionEditor from "./TaxonomyConditionEditor";

interface TaxonomyConditionEditorBaseConfig extends Config<ConditionEditor> {
}

class TaxonomyConditionEditorBase extends ConditionEditor {
  declare Config: TaxonomyConditionEditorBaseConfig;

  constructor(config: Config<TaxonomyConditionEditor> = null) {
    super(config);

    // Ensures that the substruct the condition editor writes into is created as this acts as an indicator if the
    // form is shown (even if no context is specified) or not.
    const structPropertyName = this.propertyName.substring(this.propertyName.lastIndexOf(".") + 1, this.propertyName.length);
    ConditionEditorBase.applyBaseStruct(this.bindTo, this.contentType, structPropertyName);

    if (!config.taxonomyId) {
      throw new Error("TaxonomyCondition for property \"" + this.propertyName + "\" must set config parameter \"taxonomyId\"");
    }
  }
}

export default TaxonomyConditionEditorBase;
