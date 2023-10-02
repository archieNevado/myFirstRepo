import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface LocalSettingsFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<LocalSettingsForm,
  "contentType"
>> {
}

class LocalSettingsForm extends PropertyFieldGroup {
  declare Config: LocalSettingsFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.localSettingsForm";

  constructor(config: Config<LocalSettingsForm> = null) {
    super(ConfigUtils.apply(Config(LocalSettingsForm, {
      title: CustomLabels_properties.PropertyGroup_Settings_label,
      itemId: "localSettingsForm",

      items: [
        Config(StructPropertyField, {
          propertyName: "localSettings",
          hideLabel: true,
          itemId: "localSettings",
        }),
      ],

    }), config));
  }

  contentType: string = null;
}

export default LocalSettingsForm;
