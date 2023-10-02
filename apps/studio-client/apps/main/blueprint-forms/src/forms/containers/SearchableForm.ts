import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface SearchableFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<SearchableForm,
  "contentType"
>> {
}

class SearchableForm extends PropertyFieldGroup {
  declare Config: SearchableFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.searchableForm";

  constructor(config: Config<SearchableForm> = null) {
    super(ConfigUtils.apply(Config(SearchableForm, {
      title: CustomLabels_properties.PropertyGroup_Searchable_label,
      expandOnValues: "notSearchable",
      itemId: "searchableForm",

      items: [
        Config(BooleanPropertyField, {
          bindTo: config.bindTo,
          hideLabel: true,
          propertyName: "notSearchable",
        }),
      ],

    }), config));
  }

  contentType: string = null;
}

export default SearchableForm;
