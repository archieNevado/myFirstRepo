import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CMPersonaFormComboBoxBase from "./CMPersonaFormComboBoxBase";

interface CMPersonaFormComboBoxConfig extends Config<CMPersonaFormComboBoxBase>, Partial<Pick<CMPersonaFormComboBox,
  "properties"
>> {
}

class CMPersonaFormComboBox extends CMPersonaFormComboBoxBase {
  declare Config: CMPersonaFormComboBoxConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.cmPersonaFormComboBox";

  constructor(config: Config<CMPersonaFormComboBox> = null) {
    super(ConfigUtils.apply(Config(CMPersonaFormComboBox), config));
  }

  /**
   * the enumeration of possible properties and their display names. See below
   */
  properties: any = null;
}

export default CMPersonaFormComboBox;
