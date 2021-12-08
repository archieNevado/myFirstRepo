import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMSitemapFormConfig extends Config<DocumentTabPanel> {
}

class CMSitemapForm extends DocumentTabPanel {
  declare Config: CMSitemapFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmSitemapForm";

  constructor(config: Config<CMSitemapForm> = null) {
    super(ConfigUtils.apply(Config(CMSitemapForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMSitemap_text,
              itemId: "cmSitemapTitleForm",
              items: [
                Config(StringPropertyField, {
                  propertyName: "title",
                  itemId: "title",
                }),
                Config(LinkListPropertyField, {
                  propertyName: "root",
                  itemId: "root",
                }),
                Config(IntegerPropertyField, {
                  propertyName: "localSettings.sitemap_depth",
                  itemId: "cmSitemapDepthForm",
                  fieldLabel: BlueprintDocumentTypes_properties.CMSitemap_localSettings_sitemap_depth_text,
                }),
              ],
            }),
            Config(CollapsibleStringPropertyForm, {
              title: BlueprintDocumentTypes_properties.CMTeasable_teaserText_text,
              propertyName: "teaserTitle",
            }),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMSitemapForm;
