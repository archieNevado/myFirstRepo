import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";
import LinkedSettingsForm from "./LinkedSettingsForm";
import LocalSettingsForm from "./LocalSettingsForm";
import ThemeSelectorForm from "./ThemeSelectorForm";

interface ChannelMetaDataInformationFormConfig extends Config<DocumentForm> {
}

class ChannelMetaDataInformationForm extends DocumentForm {
  declare Config: ChannelMetaDataInformationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.channelMetaDataInformationForm";

  constructor(config: Config<ChannelMetaDataInformationForm> = null) {
    super(ConfigUtils.apply(Config(ChannelMetaDataInformationForm, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      items: [
        Config(DocumentInfo),
        Config(VersionHistory),
        Config(ReferrerListPanel),
        Config(DocumentMetaDataFormDispatcher, { bindTo: config.bindTo }),
        Config(LinkedSettingsForm, {
          bindTo: config.bindTo,
          collapsed: true,
        }),
        Config(LocalSettingsForm, {
          bindTo: config.bindTo,
          collapsed: true,
        }),
        Config(PropertyFieldGroup, {
          title: BlueprintDocumentTypes_properties.CMNavigation_theme_text,
          itemId: "channelMetaDataThemeForm",
          collapsed: true,
          items: [
            Config(ThemeSelectorForm, { propertyName: "theme" }),
          ],
        }),
        Config(PropertyFieldGroup, {
          title: BlueprintDocumentTypes_properties.CMNavigation_javaScript_text,
          itemId: "channelMetaDataJavaScriptForm",
          collapsed: true,
          items: [
            Config(LinkListPropertyField, {
              bindTo: config.bindTo,
              hideLabel: true,
              propertyName: "javaScript",
            }),
          ],
        }),
        Config(PropertyFieldGroup, {
          title: BlueprintDocumentTypes_properties.CMNavigation_css_text,
          itemId: "channelMetaDataCssForm",
          collapsed: true,
          items: [
            Config(LinkListPropertyField, {
              bindTo: config.bindTo,
              hideLabel: true,
              propertyName: "css",
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default ChannelMetaDataInformationForm;
