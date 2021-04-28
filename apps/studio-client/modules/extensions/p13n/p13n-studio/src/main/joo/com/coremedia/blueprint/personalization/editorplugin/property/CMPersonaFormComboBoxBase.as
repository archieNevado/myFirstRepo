package com.coremedia.blueprint.personalization.editorplugin.property {
import com.coremedia.ui.mixins.IHidableMixin;

import ext.Ext;
import ext.data.ArrayStore;
import ext.form.field.ComboBox;

public class CMPersonaFormComboBoxBase extends ComboBox implements IHidableMixin {

  /**
   * @cfg {Object} properties the enumeration of possible properties and their display names. See below
   * @param config
   */
  public function CMPersonaFormComboBoxBase(config:CMPersonaFormComboBox = null) {

    super(ComboBox(Ext.apply(config, {

      store: new ArrayStore(ArrayStore({
        id: 0,
        fields: [
          'myId',
          'displayText'
        ],
        data: config['properties']
      })),
      valueField: 'myId',
      displayField: 'displayText'
    })));
  }

  /** @private */
  [Bindable]
  public function set hideText(newHideText:String):void {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  [Bindable]
  public function get hideText():String {
    return getFieldLabel();
  }

  /** @private */
  [Bindable]
  public native function set hideId(newHideId:String):void;

  /** @inheritDoc */
  [Bindable]
  public native function get hideId():String;

}
}
