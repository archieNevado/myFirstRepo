import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import TeaserOverlayPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/TeaserOverlayPropertyField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import StringPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/StringPropertyFieldDelegatePlugin";
import QuickCreateToolbarButton from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateToolbarButton";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import TeaserOverlayConstants from "../../TeaserOverlayConstants";
import ValidityColumn from "../columns/ValidityColumn";
import TeaserSettingsPropertyFieldGroup from "./TeaserSettingsPropertyFieldGroup";

interface TeaserWithPictureDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class TeaserWithPictureDocumentForm extends PropertyFieldGroup {
  declare Config: TeaserWithPictureDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.teaserWithPictureDocumentForm";

  /**
   * A constant for the linklist property name
   */
  static readonly PICTURE_PROPERTY_NAME: string = "pictures";

  constructor(config: Config<TeaserWithPictureDocumentForm> = null) {
    super(ConfigUtils.apply(Config(TeaserWithPictureDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Teaser_label,
      itemId: "teaserDocumentForm",
      collapsed: true,
      propertyNames: ["teaserTitle", "teaserText", "pictures"],
      expandOnValues: "teaserTitle,teaserText.data,pictures",

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
        }),
        Config(TeaserSettingsPropertyFieldGroup),
        Config(LinkListPropertyField, {
          propertyName: TeaserWithPictureDocumentForm.PICTURE_PROPERTY_NAME,
          itemId: TeaserWithPictureDocumentForm.PICTURE_PROPERTY_NAME,
          showThumbnails: true,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateToolbarButton, {
              contentType: "CMPicture",
              propertyName: TeaserWithPictureDocumentForm.PICTURE_PROPERTY_NAME,
              bindTo: config.bindTo,
            }),
          ],
          fields: [
            Config(DataField, {
              name: ValidityColumn.STATUS_ID,
              mapping: "",
              convert: ValidityColumn.convert,
            }),
          ],
          columns: [
            Config(LinkListThumbnailColumn),
            Config(TypeIconColumn),
            Config(NameColumn, { flex: 1 }),
            Config(ValidityColumn),
            Config(StatusColumn),
          ],
        }),
      ],

    }), config));
  }
}

export default TeaserWithPictureDocumentForm;
