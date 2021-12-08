import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import Ext from "@jangaroo/ext-ts";
import ArrayStore from "@jangaroo/ext-ts/data/ArrayStore";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CMPersonaFormComboBox from "./CMPersonaFormComboBox";

interface CMPersonaFormComboBoxBaseConfig extends Config<ComboBox>, Config<HidableMixin>, Partial<Pick<CMPersonaFormComboBoxBase,
  "hideText"
>> {
}

class CMPersonaFormComboBoxBase extends ComboBox {
  declare Config: CMPersonaFormComboBoxBaseConfig;

  /**
   * @cfg {Object} properties the enumeration of possible properties and their display names. See below
   * @param config
   */
  constructor(config: Config<CMPersonaFormComboBox> = null) {

    super(Config(ComboBox, Ext.apply(config, {

      store: new ArrayStore(Config(ArrayStore, {
        fields: [
          "myId",
          "displayText",
        ],
        data: config.properties,
      })),
      valueField: "myId",
      displayField: "displayText",
    })));
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }

}

interface CMPersonaFormComboBoxBase extends HidableMixin{}

mixin(CMPersonaFormComboBoxBase, HidableMixin);

export default CMPersonaFormComboBoxBase;
