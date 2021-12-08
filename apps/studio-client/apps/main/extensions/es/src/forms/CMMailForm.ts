import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CollapsibleStringPropertyForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CollapsibleStringPropertyForm";
import MetaDataWithoutSettingsForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import OpenReferenceWindowAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenReferenceWindowAction";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextBlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextBlobPropertyField";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ElasticSocialStudioPlugin_properties from "../ElasticSocialStudioPlugin_properties";
import CMMailHelpWindow from "./CMMailHelpWindow";

interface CMMailFormConfig extends Config<DocumentTabPanel> {
}

class CMMailForm extends DocumentTabPanel {
  declare Config: CMMailFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.elastic.social.studio.config.cmMailForm";

  constructor(config: Config<CMMailForm> = null) {
    super(ConfigUtils.apply(Config(CMMailForm, {
      itemId: "CMMail",

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(CollapsibleStringPropertyForm, {
              title: ElasticSocialStudioPlugin_properties.CMMail_from_text,
              expandOnValues: "from",
              propertyName: "from",
            }),
            Config(PropertyFieldGroup, {
              title: ElasticSocialStudioPlugin_properties.CMMail_text,
              itemId: "cmMailTextForm",
              items: [
                Config(StringPropertyField, {
                  propertyName: "subject",
                  itemId: "subject",
                }),
                Config(TextBlobPropertyField, {
                  propertyName: "text",
                  height: 300,
                }),
                Config(Container, {
                  layout: "hbox",
                  items: [
                    Config(Container, { flex: 1 }),
                    Config(Button, {
                      itemId: "cmmail-help-button",
                      ui: ButtonSkin.SIMPLE.getSkin(),
                      iconCls: CoreIcons_properties.help,
                      text: ElasticSocialStudioPlugin_properties.cmmail_help_text,
                      baseAction: new OpenReferenceWindowAction({ dialog: Config(CMMailHelpWindow) }),
                    }),
                  ],
                }),
              ],
            }),
            Config(CollapsibleStringPropertyForm, {
              collapsed: true,
              expandOnValues: "contentType",
              title: ElasticSocialStudioPlugin_properties.CMMail_contentType_text,
              propertyName: "contentType",
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSettingsForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default CMMailForm;
