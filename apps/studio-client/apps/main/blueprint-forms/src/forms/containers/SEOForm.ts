import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextAreaStringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextAreaStringPropertyField";
import StringPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/StringPropertyFieldDelegatePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface SEOFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<SEOForm,
  "delegatePropertyName"
>> {
}

class SEOForm extends PropertyFieldGroup {
  declare Config: SEOFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.seoForm";

  static readonly SEO_FORM_ITEM_ID: string = "seoForm";

  constructor(config: Config<SEOForm> = null) {
    config = ConfigUtils.apply({ delegatePropertyName: "title" }, config);
    super(ConfigUtils.apply(Config(SEOForm, {
      title: CustomLabels_properties.PropertyGroup_SearchEngineOptimization_label,
      collapsed: false,
      itemId: SEOForm.SEO_FORM_ITEM_ID,

      items: [
        Config(StringPropertyField, {
          bindTo: config.bindTo,
          itemId: "segment",
          propertyName: "segment",
          ...ConfigUtils.append({
            plugins: [
              Config(StringPropertyFieldDelegatePlugin, { delegatePropertyName: config.delegatePropertyName }),
            ],
          }),
        }),
        Config(StringPropertyField, {
          itemId: "htmlTitle",
          propertyName: "htmlTitle",
          ...ConfigUtils.append({
            plugins: [
              Config(StringPropertyFieldDelegatePlugin, { delegatePropertyName: config.delegatePropertyName }),
            ],
          }),
        }),
        Config(TextAreaStringPropertyField, {
          changeBuffer: 1000,
          minHeight: 100,
          bindTo: config.bindTo,
          itemId: "htmlDescription",
          propertyName: "htmlDescription",
        }),
      ],

    }), config));
  }

  /**
   * The name of the first fallback property
   */
  delegatePropertyName: string = null;
}

export default SEOForm;
