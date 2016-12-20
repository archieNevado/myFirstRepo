package com.coremedia.blueprint.studio.taxonomy.queryeditor {

import com.coremedia.blueprint.base.queryeditor.conditions.ConditionEditor;

public class TaxonomyConditionEditorBase extends ConditionEditor {

  private var structPropertyName:String;

  public function TaxonomyConditionEditorBase(config:TaxonomyConditionEditor = null) {
    super(config);
    structPropertyName = propertyName.substring(propertyName.lastIndexOf(".")+1, propertyName.length);
  }

  /**
   * Ensures that the substruct the condition editor
   * writes into is created.
   */
  override protected function afterRender():void {
    super.afterRender();
    super.applyBaseStruct(bindTo, contentType, structPropertyName);
  }
}
}
