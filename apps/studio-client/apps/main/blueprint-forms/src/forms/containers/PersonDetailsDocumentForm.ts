import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextAreaPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextAreaPropertyField";
import StringPropertyFieldMultipleDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/StringPropertyFieldMultipleDelegatePlugin";
import TextAreaPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/TextAreaPropertyFieldDelegatePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import CustomLabels_properties from "../../CustomLabels_properties";

interface PersonDetailsDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class PersonDetailsDocumentForm extends PropertyFieldGroup {
  declare Config: PersonDetailsDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.personDetailsDocumentForm";

  constructor(config: Config<PersonDetailsDocumentForm> = null) {
    super(ConfigUtils.apply(Config(PersonDetailsDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Details_label,
      itemId: "personDetailsDocumentForm",
      propertyNames: ["firstName", "lastName", "displayName", "organization", "jobTitle", "email", "detailText", "teaserText"],
      expandOnValues: "firstName,lastName,displayName,organization,jobTitle,email,detailText,teaserText",
      manageHeight: false,

      items: [
        Config(StringPropertyField, {
          propertyName: "firstName",
          itemId: "firstName",
        }),
        Config(StringPropertyField, {
          propertyName: "lastName",
          itemId: "lastName",
        }),
        Config(StringPropertyField, {
          bindTo: config.bindTo,
          propertyName: "displayName",
          itemId: "displayName",
          ...ConfigUtils.append({
            plugins: [
              Config(StringPropertyFieldMultipleDelegatePlugin, { delegatePropertyNames: ["firstName", "lastName"] }),
            ],
          }),
        }),
        Config(StringPropertyField, {
          propertyName: "organization",
          itemId: "organization",
        }),
        Config(StringPropertyField, {
          propertyName: "jobTitle",
          itemId: "jobTitle",
        }),
        Config(StringPropertyField, {
          propertyName: "eMail",
          itemId: "eMail",
        }),

        Config(RichTextPropertyField, {
          bindTo: config.bindTo,
          itemId: "detailText",
          propertyName: "detailText",
          initialHeight: 200,
        }),
        Config(TextAreaPropertyField, {
          bindTo: config.bindTo,
          fieldLabel: BlueprintDocumentTypes_properties.CMPerson_teaserText_text,
          propertyName: "teaserText",
          itemId: "teaserText",
          changeBuffer: 1000,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          ...ConfigUtils.append({
            plugins: [
              Config(TextAreaPropertyFieldDelegatePlugin, { delegatePropertyName: "detailText" }),
            ],
          }),
        }),
      ],
    }), config));
  }
}

export default PersonDetailsDocumentForm;
