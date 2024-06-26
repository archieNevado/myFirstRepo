import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import com_coremedia_ui_store_DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ThumbnailImage from "@coremedia/studio-client.ext.ui-components/util/ThumbnailImage";
import ThumbDataView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/thumbnail/ThumbDataView";
import ThumbDataViewBase
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/thumbnail/ThumbDataViewBase";
import ext_data_field_DataField from "@jangaroo/ext-ts/data/field/Field";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogDragDropVisualFeedback from "../../dragdrop/CatalogDragDropVisualFeedback";
import AugmentationUtil from "../../helper/AugmentationUtil";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogObjectPropertyNames
  from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";

interface CatalogThumbDataViewConfig extends Config<ThumbDataView> {
}

class CatalogThumbDataView extends ThumbDataView {
  declare Config: CatalogThumbDataViewConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogThumbDataView";

  constructor(config: Config<CatalogThumbDataView> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(CatalogThumbDataView, {
      ddHtmlFeedbackFunction: CatalogDragDropVisualFeedback.getHtmlFeedback,
      dragDDGroup: "ContentDD",
      editable: false,
      fields: [
        Config(com_coremedia_ui_store_DataField, {
          name: "type",
          mapping: "name",
          convert: (v: string, catalogObject: CatalogObject): string =>
            AugmentationUtil.getTypeLabel(catalogObject),
        }),
        Config(com_coremedia_ui_store_DataField, {
          name: "docTypeClass",
          mapping: "name",
          convert: (v: string, catalogObject: CatalogObject): string =>
            AugmentationUtil.getTypeCls(catalogObject),
        }),
        Config(com_coremedia_ui_store_DataField, {
          name: "id",
          mapping: "externalId",
        }),
        Config(com_coremedia_ui_store_DataField, { name: "name" }),
        Config(com_coremedia_ui_store_DataField, {
          name: "thumbnailImage",
          mapping: "pictures",
          convert: bind(this$, this$.#computeCatalogThumbnailImage),
          ifUnreadable: null,
          allowNull: true,
        }),
        Config(ext_data_field_DataField, {
          name: "shortName",
          mapping: "name",
          convert: (v: string, catalogObject: CatalogObject): string =>
            CatalogHelper.getInstance().getDecoratedName(catalogObject),
        }),
      ],

    }), config));
  }

  #computeCatalogThumbnailImage(v: string, catalogObject: CatalogObject): ThumbnailImage {
    const pictures = catalogObject.get(CatalogObjectPropertyNames.PICTURES);
    if (pictures && pictures.length > 0) {
      const p = pictures[0];
      if (!p.isLoaded()) {
        p.load();
        return undefined;
      }
      return ThumbDataViewBase.computeThumbnailImage(p.getType().getName(), pictures[0]);
    }

    const commercePicture = ThumbDataViewBase.computeThumbnailImage(CatalogHelper.getInstance().getType(catalogObject), catalogObject);
    if (commercePicture && commercePicture.imageUri) {
      return commercePicture;
    }
    return null;
  }
}

export default CatalogThumbDataView;
