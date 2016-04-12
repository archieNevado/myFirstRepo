package com.coremedia.blueprint.assets.studio.forms {

import com.coremedia.blueprint.assets.studio.config.assetDetailsBlobPropertyField;
import com.coremedia.cms.editor.sdk.premular.fields.BlobPropertyField;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Component;
import ext.Container;
import ext.form.Checkbox;

public class AssetDetailsBlobPropertyFieldBase  extends BlobPropertyField {

  private var blobPropertyVE:ValueExpression;

  private var checkboxVisibleVE:ValueExpression;

  protected const CHECKBOX_ITEM_ID:String = "checkBoxItemId";

  public function AssetDetailsBlobPropertyFieldBase(config:assetDetailsBlobPropertyField = null) {
    super(config);

    getBlobPropertyVE(config.bindTo, config.propertyName).addChangeListener(updateCheckbox);
  }

  protected function getBlobPropertyVE(bindTo:ValueExpression, propertyName:String):ValueExpression {
    if (!blobPropertyVE) {
      blobPropertyVE = bindTo.extendBy('properties', propertyName);
    }
    return blobPropertyVE;
  }

  private function updateCheckbox():void {
    if (!blobPropertyVE.getValue()) {
      var checkbox:Checkbox = findCheckBox();
      if (checkbox) {
        checkbox.setValue(false);
      }
    }
  }

  protected function getCheckboxVisibleVE(visiblePropertyName:String, bindTo:ValueExpression, propertyName:String):ValueExpression {
    if (!checkboxVisibleVE) {
      checkboxVisibleVE = ValueExpressionFactory.createFromFunction(visible, visiblePropertyName, getBlobPropertyVE(bindTo, propertyName));
    }

    return checkboxVisibleVE;
  }

  /**
   * If no property or no blob exist, hide component.
   *
   * @param property
   * @param blobVE
   * @return false if no property or blob exist.
   */
  protected static function visible(property:String, blobVE:ValueExpression):Boolean {
    if (!property) {
      return false;
    }

    return blobVE && blobVE.getValue() && blobVE.getValue().getSize() !== undefined;
  }

  protected static function findBlobDetailsContainer(container:Container):Component {
    return container.find('itemId', BlobPropertyField.BLOB_DETAILS_ITEM_ID)[0];
  }

  private function findCheckBox():Checkbox {
    var checkbox:Checkbox = find("itemId", CHECKBOX_ITEM_ID)[0] as Checkbox;
    return (checkbox) ? checkbox : null;
  }

  override protected function onDestroy():void {
    blobPropertyVE.removeChangeListener(updateCheckbox);

    super.onDestroy();
  }

}
}
