import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import MetaDataInformationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CMMarketingSpotFormConfig extends Config<DocumentTabPanel> {
}

class CMMarketingSpotForm extends DocumentTabPanel {
  declare Config: CMMarketingSpotFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.cmMarketingSpotForm";

  constructor(config: Config<CMMarketingSpotForm> = null) {
    super(ConfigUtils.apply(Config(CMMarketingSpotForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: LivecontextStudioPlugin_properties.CMMarketingSpot_text,
              itemId: "cmMarketingSpotExternalIdForm",
              items: [
                Config(CatalogLinkPropertyField, {
                  itemId: "externalId",
                  propertyName: "externalId",
                  maxCardinality: 1,
                  replaceOnDrop: true,
                  linkTypeNames: [CatalogModel.TYPE_MARKETING_SPOT],
                  dropAreaHandler: CatalogHelper.getInstance().openMarketingSpots,
                  dropAreaText: LivecontextStudioPlugin_properties.MarketingSpot_Link_empty_text,
                }),
              ],
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMMarketingSpotForm;
