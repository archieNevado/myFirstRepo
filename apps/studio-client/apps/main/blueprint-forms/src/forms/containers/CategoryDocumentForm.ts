import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface CategoryDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class CategoryDocumentForm extends PropertyFieldGroup {
  declare Config: CategoryDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.categoryDocumentForm";

  constructor(config: Config<CategoryDocumentForm> = null) {
    super(ConfigUtils.apply(Config(CategoryDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_tags_label,
      expandOnValues: "subjectTaxonomy,locationTaxonomy,keywords",
      itemId: "subjectTaxonomyDocumentForm",
      propertyNames: ["subjectTaxonomy", "locationTaxonomy", "keywords"],
      collapsed: config.collapsed === undefined || config.collapsed === true,

      items: [
        /* Taxonomy Editors will be added here*/
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config));
  }
}

export default CategoryDocumentForm;
