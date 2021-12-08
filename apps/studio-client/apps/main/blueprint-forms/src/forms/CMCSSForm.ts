import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";

interface CMCSSFormConfig extends Config<DocumentTabPanel> {
}

class CMCSSForm extends DocumentTabPanel {
  declare Config: CMCSSFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmcssForm";

  constructor(config: Config<CMCSSForm> = null) {
    super(ConfigUtils.apply(Config(CMCSSForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              itemId: "cmCssCodeForm",
              expandOnValues: "dataUrl,code",
              title: BlueprintDocumentTypes_properties.CMCSS_code_text,
              items: [
                Config(StringPropertyField, {
                  propertyName: "dataUrl",
                  itemId: "dataUrl",
                }),
                Config(RichTextPropertyField, {
                  propertyName: "code",
                  itemId: "code",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMCSS_include_text,
              expandOnValues: "include",
              itemId: "cmCssIncludeForm",
              collapsed: true,
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
              itemId: "cmCssIeExpressionForm",
              expandOnValues: "ieExpression",
              title: BlueprintDocumentTypes_properties.CMAbstractCode_ieExpression_text,
              collapsed: true,
              propertyName: "ieExpression",
            }),
          ],
        }),
        Config(MetaDataWithoutSettingsForm),
      ],

    }), config));
  }
}

export default CMCSSForm;
