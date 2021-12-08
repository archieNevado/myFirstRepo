import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";

interface CMJavaScriptFormConfig extends Config<DocumentTabPanel> {
}

class CMJavaScriptForm extends DocumentTabPanel {
  declare Config: CMJavaScriptFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmJavaScriptForm";

  constructor(config: Config<CMJavaScriptForm> = null) {
    super(ConfigUtils.apply(Config(CMJavaScriptForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMJavaScript_text,
              itemId: "cmJavaScriptDataForm",
              items: [
                Config(StringPropertyField, {
                  propertyName: "dataUrl",
                  itemId: "dataUrl",
                }),
                Config(RichTextPropertyField, {
                  itemId: "code",
                  propertyName: "code",
                }),
                Config(BooleanPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "inHead",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMJavaScript_include_text,
              itemId: "cmJavaScriptIncludeForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "include",
                  hideLabel: true,
                }),
              ],
            }),
            /**
             * @deprecated since 2110.1, Old Internet Explorer (IE) is not supported anymore.
             */
            Config(CollapsibleStringPropertyForm, {
              propertyName: "ieExpression",
              collapsed: true,
              title: BlueprintDocumentTypes_properties.CMAbstractCode_ieExpression_text,
            }),
          ],
        }),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMJavaScriptForm;
