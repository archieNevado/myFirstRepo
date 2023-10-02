import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import MetaDataInformationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataInformationForm";
import SinglePictureDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SinglePictureDocumentForm";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMDocumentTypes_properties from "../AMDocumentTypes_properties";
import AMStudioPluginBase from "../AMStudioPluginBase";

interface AMTaxonomyFormConfig extends Config<DocumentTabPanel> {
}

class AMTaxonomyForm extends DocumentTabPanel {
  declare Config: AMTaxonomyFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amTaxonomyForm";

  static readonly AM_TAXONOMY_THUMBNAIL_FORM_ITEM_ID: string = "amTaxonomyThumbnailFormItemId";

  constructor(config: Config<AMTaxonomyForm> = null) {
    super(ConfigUtils.apply(Config(AMTaxonomyForm, {

      items: [
        /*Do not rename itemId, because the TaxonomyExplorerPanel uses it to highlight the value field*/
        Config(DocumentForm, {
          itemId: "AMTaxonomy",
          title: BlueprintTabs_properties.Tab_content_title,
          items: [
            Config(PropertyFieldGroup, {
              itemId: "taxonomy",
              title: AMDocumentTypes_properties.AMTaxonomy_value_text,
              propertyNames: ["value"],
              items: [
                Config(StringPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  propertyName: "value",
                }),
              ],
            }),
            /* Asset preview */
            /* quick create uses AMPictureAsset and not AMDocumentAsset for now */
            Config(SinglePictureDocumentForm, {
              itemId: AMTaxonomyForm.AM_TAXONOMY_THUMBNAIL_FORM_ITEM_ID,
              title: AMDocumentTypes_properties.AMTaxonomy_assetThumbnail_label,
              contentType: AMDocumentTypes_properties.AMPictureAsset_doctype,
              picturePropertyName: AMDocumentTypes_properties.AMTaxonomy_assetThumbnail_name,
              openCollectionViewHandler: AMStudioPluginBase.openAssetSearch,
            }),
          ],
        }),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default AMTaxonomyForm;
