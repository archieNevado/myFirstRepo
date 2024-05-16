import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";

interface CMTaxonomyFormConfig extends Config<DocumentTabPanel> {
}

class CMTaxonomyForm extends DocumentTabPanel {
  declare Config: CMTaxonomyFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmTaxonomyForm";

  constructor(config: Config<CMTaxonomyForm> = null) {
    super(ConfigUtils.apply(Config(CMTaxonomyForm, {

      items: [
        /*Do not rename itemId, because the TaxonomyExplorerPanel uses it to highlight the value field*/
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "CMTaxonomy",
          items: [
            Config(PropertyFieldGroup, {
              itemId: "taxonomy",
              title: CustomLabels_properties.PropertyGroup_tags_label,
              propertyNames: ["value"],
              items: [
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "value",
                  itemId: "value",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              itemId: "externalReference",
              title: BlueprintDocumentTypes_properties.CMTaxonomy_externalReference_text,
              propertyNames: ["externalReference"],
              items: [
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "externalReference",
                  itemId: "externalReference",
                }),
              ],
            }),
          ],
        }),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMTaxonomyForm;
