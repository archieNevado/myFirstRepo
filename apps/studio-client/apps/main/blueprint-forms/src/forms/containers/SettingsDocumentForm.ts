import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface SettingsDocumentFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<SettingsDocumentForm,
  "folders" |
  "contentType"
>> {
}

class SettingsDocumentForm extends PropertyFieldGroup {
  declare Config: SettingsDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.settingsDocumentForm";

  constructor(config: Config<SettingsDocumentForm> = null) {
    super(ConfigUtils.apply(Config(SettingsDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Settings_label,

      items: [
        Config(PropertyFieldGroup, {
          bindTo: config.bindTo,
          itemId: "settingsDocumentFieldGroup",
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          header: false,
          items: [
            Config(LinkListPropertyField, { propertyName: "linkedSettings" }),
            Config(StructPropertyField, {
              propertyName: "localSettings",
              itemId: "localSettings",
            }),
          ],
        }),
      ],

    }), config));
  }

  folders: string = null;

  contentType: string = null;
}

export default SettingsDocumentForm;
