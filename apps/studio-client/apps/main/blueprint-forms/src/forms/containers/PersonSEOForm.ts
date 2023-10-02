import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextAreaStringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextAreaStringPropertyField";
import SEOStringPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SEOStringPropertyFieldDelegatePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface PersonSEOFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<PersonSEOForm,
  "delegatePropertyNames"
>> {
}

class PersonSEOForm extends PropertyFieldGroup {
  declare Config: PersonSEOFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.personSeoForm";

  constructor(config: Config<PersonSEOForm> = null) {
    super(ConfigUtils.apply(Config(PersonSEOForm, {
      title: CustomLabels_properties.PropertyGroup_SearchEngineOptimization_label,
      collapsed: false,
      itemId: "personSeoForm",

      items: [
        Config(StringPropertyField, {
          bindTo: config.bindTo,
          propertyName: "segment",
          itemId: "segment",
          ...ConfigUtils.append({
            plugins: [
              Config(SEOStringPropertyFieldDelegatePlugin, { delegatePropertyNames: config.delegatePropertyNames }),
            ],
          }),
        }),
        Config(StringPropertyField, {
          propertyName: "htmlTitle",
          itemId: "htmlTitle",
          ...ConfigUtils.append({
            plugins: [
              Config(SEOStringPropertyFieldDelegatePlugin, { delegatePropertyNames: config.delegatePropertyNames }),
            ],
          }),
        }),
        Config(TextAreaStringPropertyField, {
          changeBuffer: 1000,
          minHeight: 100,
          bindTo: config.bindTo,
          propertyName: "htmlDescription",
          itemId: "htmlDescription",
        }),
      ],

    }), config));
  }

  /**
   * The name of the first fallback property
   */
  delegatePropertyNames: Array<any> = null;
}

export default PersonSEOForm;
