import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import int from "@jangaroo/runtime/int";
import CustomLabels_properties from "../../CustomLabels_properties";
import ValidityColumn from "../columns/ValidityColumn";

interface MediaDocumentFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<MediaDocumentForm,
  "maxCardinality" |
  "mediaPropertyName" |
  "quickCreateTypes"
>> {
}

class MediaDocumentForm extends PropertyFieldGroup {
  declare Config: MediaDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.mediaDocumentForm";

  constructor(config: Config<MediaDocumentForm> = null) {
    config = ConfigUtils.apply({
      mediaPropertyName: "pictures",
      quickCreateTypes: "CMPicture,CMVideo,CMSpinner",
    }, config);
    super(ConfigUtils.apply(Config(MediaDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Media_label,
      itemId: "mediaDocumentForm",

      items: [
        Config(LinkListPropertyField, {
          itemId: config.mediaPropertyName,
          propertyName: config.mediaPropertyName,
          showThumbnails: true,
          hideLabel: true,
          maxCardinality: config.maxCardinality,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateLinklistMenu, {
              bindTo: config.bindTo,
              propertyName: config.mediaPropertyName,
              contentTypes: config.quickCreateTypes,
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

  /** Maximum amount of items in the list */
  maxCardinality: int = 0;

  /**
   * The content property name of the list to bind the newly created content to.
   * Defaults to pictures.
   */
  mediaPropertyName: string = null;

  /**
   * The content types for the QuickCreate menu.
   * Default is CMPicture,CMVideo,CMSpinner.
   */
  quickCreateTypes: string = null;
}

export default MediaDocumentForm;
