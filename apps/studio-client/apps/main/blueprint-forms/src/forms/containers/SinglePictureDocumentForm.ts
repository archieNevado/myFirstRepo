import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import PictureDocumentForm from "./PictureDocumentForm";

interface SinglePictureDocumentFormConfig extends Config<PictureDocumentForm> {
}

class SinglePictureDocumentForm extends PictureDocumentForm {
  declare Config: SinglePictureDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.singlePictureDocumentForm";

  /**
   * A constant for the linklist property name
   */
  static readonly PICTURE_PROPERTY_NAME: string = "pictures";

  constructor(config: Config<SinglePictureDocumentForm> = null) {
    super(ConfigUtils.apply(Config(SinglePictureDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Single_Picture_label,
      itemId: "pictureDocumentForm",
      maxCardinality: 1,
      picturePropertyName: SinglePictureDocumentForm.PICTURE_PROPERTY_NAME,
      contentType: "CMPicture",
      openCollectionViewHandler: config.openCollectionViewHandler,

    }), config));
  }
}

export default SinglePictureDocumentForm;
