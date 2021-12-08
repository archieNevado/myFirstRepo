import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateToolbarButton from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateToolbarButton";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import CustomLabels_properties from "../../CustomLabels_properties";
import ValidityColumn from "../columns/ValidityColumn";
import PictureDocumentFormBase from "./PictureDocumentFormBase";

interface PictureDocumentFormConfig extends Config<PictureDocumentFormBase>, Partial<Pick<PictureDocumentForm,
  "maxCardinality" |
  "openCollectionViewHandler" |
  "picturePropertyName" |
  "contentType"
>> {
}

class PictureDocumentForm extends PictureDocumentFormBase {
  declare Config: PictureDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.pictureDocumentForm";

  constructor(config: Config<PictureDocumentForm> = null) {
    config = ConfigUtils.apply({
      picturePropertyName: "pictures",
      contentType: "CMPicture",
    }, config);
    super((()=> ConfigUtils.apply(Config(PictureDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Picture_label,
      itemId: "pictureDocumentForm",

      items: [
        Config(LinkListPropertyField, {
          itemId: config.picturePropertyName,
          propertyName: config.picturePropertyName,
          linkType: config.contentType,
          hideLabel: true,
          showThumbnails: true,
          maxCardinality: config.maxCardinality,
          openCollectionViewHandler: config.openCollectionViewHandler,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateToolbarButton, {
              contentType: config.contentType,
              propertyName: config.picturePropertyName,
              forceReadOnlyValueExpression: this.getGridEmptyValueExpression(config),
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

    }), config))());
  }

  /** Maximum amount of items in the list */
  maxCardinality: int = 0;

  /**
   * An optional handler, to open the collection view in a different state. If not set, the collection view
   * opens per default in search mode for the corresponding content type.
   * Signature: function(linkListTargetType:ContentType, sourceContent:Content):void
   */
  openCollectionViewHandler: AnyFunction = null;

  /**
   * The content property name of the list to bind the newly created content to.
   * Defaults to pictures.
   */
  picturePropertyName: string = null;

  /**
   * The content type to create, optional if the content type value expression is set. Defaults to CMPicture.
   */
  contentType: string = null;
}

export default PictureDocumentForm;
