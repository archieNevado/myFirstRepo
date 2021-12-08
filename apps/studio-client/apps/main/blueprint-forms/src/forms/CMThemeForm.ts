import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import LinkedSettingsForm from "./containers/LinkedSettingsForm";
import LocalSettingsForm from "./containers/LocalSettingsForm";

interface CMThemeFormConfig extends Config<DocumentTabPanel> {
}

class CMThemeForm extends DocumentTabPanel {
  declare Config: CMThemeFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmThemeForm";

  constructor(config: Config<CMThemeForm> = null) {
    super(ConfigUtils.apply(Config(CMThemeForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTheme_javaScriptGroup_text,
              itemId: "cmThemeJavaScriptLibsForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "javaScriptLibs",
                  itemId: "javaScriptLibs",
                }),
                Config(LinkListPropertyField, {
                  propertyName: "javaScripts",
                  itemId: "javaScripts",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTheme_css_text,
              itemId: "cmThemeCssForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "css",
                  itemId: "css",
                  hideLabel: true,
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTheme_templateSets_text,
              itemId: "cmThemeTemplateSetsForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "templateSets",
                  itemId: "templateSets",
                  hideLabel: true,
                }),
              ],
            }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Description_label,
              itemId: "detailsDocumentForm",
              collapsed: false,
              items: [
                Config(StringPropertyField, {
                  propertyName: "description",
                  itemId: "description",
                }),
                Config(RichTextPropertyField, {
                  itemId: "detailText",
                  propertyName: "detailText",
                  initialHeight: 200,
                }),
                Config(Component, { height: 6 }),
                Config(BlobPropertyField, { propertyName: "icon" }),
              ],
            }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_locale_title,
          itemId: "locale",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTheme_resourceBundles_text,
              itemId: "cmThemeResourceBundlesForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "resourceBundles",
                  hideLabel: true,
                }),
              ],
            }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_system_title,
          itemId: "system",
          autoHide: true,
          items: [
            Config(DocumentInfo),
            Config(VersionHistory, { itemId: "cmVersionHistoryText" }),
            Config(ReferrerListPanel),
            Config(DocumentMetaDataFormDispatcher),
            Config(LinkedSettingsForm, { collapsed: true }),
            Config(LocalSettingsForm, { collapsed: true }),
            Config(CollapsibleStringPropertyForm, {
              propertyName: "viewRepositoryName",
              title: BlueprintDocumentTypes_properties.CMTheme_viewRepositoryName_text,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default CMThemeForm;
