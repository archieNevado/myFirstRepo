import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";

interface CMLocTaxonomyFormConfig extends Config<DocumentTabPanel> {
}

class CMLocTaxonomyForm extends DocumentTabPanel {
  declare Config: CMLocTaxonomyFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmLocTaxonomyForm";

  constructor(config: Config<CMLocTaxonomyForm> = null) {
    super(ConfigUtils.apply(Config(CMLocTaxonomyForm, {

      items: [
        /*Do not rename itemId, because the TaxonomyExplorerPanel uses it to highlight the value field*/
        /*Do not rename itemId, because the OSM extension replaces the location property editor this way*/
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "CMLocTaxonomy",
          items: [
            Config(PropertyFieldGroup, {
              itemId: "locTaxonomy",
              title: CustomLabels_properties.PropertyGroup_Location_label,
              propertyNames: ["value", "externalReference", "postcode"],
              items: [
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "value",
                  itemId: "value",
                }),
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "externalReference",
                  itemId: "externalReference",
                }),
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "postcode",
                  itemId: "postcode",
                }),
              ],
            }),
            Config(CollapsibleStringPropertyForm, {
              propertyName: "latitudeLongitude",
              collapsed: true,
              title: BlueprintDocumentTypes_properties.CMLocTaxonomy_latitudeLongitude_text,
              itemId: "latitudeLongitude",
            }),
          ],
        }),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMLocTaxonomyForm;
