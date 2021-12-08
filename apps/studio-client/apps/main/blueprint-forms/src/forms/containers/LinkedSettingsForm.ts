import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface LinkedSettingsFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<LinkedSettingsForm,
  "folders" |
  "contentType"
>> {
}

class LinkedSettingsForm extends PropertyFieldGroup {
  declare Config: LinkedSettingsFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.linkedSettingsForm";

  constructor(config: Config<LinkedSettingsForm> = null) {
    super(ConfigUtils.apply(Config(LinkedSettingsForm, {
      title: CustomLabels_properties.PropertyGroup_LinkedSettings_label,
      itemId: "linkedSettings",

      items: [
        Config(LinkListPropertyField, {
          propertyName: "linkedSettings",
          hideLabel: true,
        }),
      ],

    }), config));
  }

  folders: string = null;

  contentType: string = null;
}

export default LinkedSettingsForm;
