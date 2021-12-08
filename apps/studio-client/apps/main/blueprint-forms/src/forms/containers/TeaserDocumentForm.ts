import TeaserOverlayPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/TeaserOverlayPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import StringPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/StringPropertyFieldDelegatePlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import TeaserOverlayConstants from "../../TeaserOverlayConstants";
import TeaserDocumentFormBase from "./TeaserDocumentFormBase";
import TeaserSettingsPropertyFieldGroup from "./TeaserSettingsPropertyFieldGroup";

interface TeaserDocumentFormConfig extends Config<TeaserDocumentFormBase> {
}

class TeaserDocumentForm extends TeaserDocumentFormBase {
  declare Config: TeaserDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.teaserDocumentForm";

  constructor(config: Config<TeaserDocumentForm> = null) {
    super(ConfigUtils.apply(Config(TeaserDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Teaser_label,
      expandOnValues: "teaserTitle",
      itemId: "teaserDocumentForm",
      propertyNames: ["teaserTitle", "teaserText"],

      items: [

        Config(StringPropertyField, {
          itemId: "teaserTitle",
          propertyName: "teaserTitle",
          ...ConfigUtils.append({
            plugins: [
              Config(StringPropertyFieldDelegatePlugin, { delegatePropertyName: "title" }),
            ],
          }),
        }),
        Config(TeaserOverlayPropertyField, {
          propertyName: "teaserText",
          delegatePropertyName: "detailText",
          initialHeight: 100,
          itemId: "teaserText",
          settingsPath: TeaserOverlayConstants.DEFAULT_SETTINGS_PATH,
          styleDescriptorFolderPaths: TeaserOverlayConstants.DEFAULT_STYLE_DESCRIPTOR_FOLDER_PATHS,
          ...ConfigUtils.append({
            plugins: [
              Config(ShowIssuesPlugin, {
                bindTo: config.bindTo,
                propertyName: "teaserText",
                statefulSubComponentsFunction: (): Array<any> =>
                  [this.queryById(TeaserOverlayPropertyField.TEASER_OVERLAY_RICHTEXT_ITEM_ID)],
              }),
            ],
          }),
        }),
        Config(TeaserSettingsPropertyFieldGroup),
      ],

    }), config));
  }
}

export default TeaserDocumentForm;
