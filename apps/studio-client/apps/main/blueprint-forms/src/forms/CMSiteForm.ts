import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AddQuickTipPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddQuickTipPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CMSiteFormBase from "./CMSiteFormBase";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMSiteFormConfig extends Config<CMSiteFormBase> {
}

class CMSiteForm extends CMSiteFormBase {
  declare Config: CMSiteFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmSiteForm";

  constructor(config: Config<CMSiteForm> = null) {
    super(ConfigUtils.apply(Config(CMSiteForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMSite_text,
              itemId: "cmSiteRootForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "root",
                  itemId: "root",
                }),
                Config(StringPropertyField, {
                  propertyName: "id",
                  itemId: "id",
                  disabled: config.forceReadOnlyValueExpression.getValue(),
                  forceReadOnlyValueExpression: ValueExpressionFactory.createFromValue(!CMSiteFormBase.isAdministrator()),
                }),
                Config(StringPropertyField, {
                  propertyName: "name",
                  itemId: "name",
                }),
              ],
            }),
            Config(CollapsibleStringPropertyForm, {
              propertyName: "siteManagerGroup",
              itemId: "cmSiteSiteManagerForm",
              collapsed: true,
              title: BlueprintDocumentTypes_properties.CMSite_siteManagerGroup_text,
              plugins: [
                Config(AddQuickTipPlugin, { text: BlueprintDocumentTypes_properties.CMSite_siteManagerGroup_toolTip }),
              ],
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMSiteForm;
