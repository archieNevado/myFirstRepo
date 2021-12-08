import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateToolbarButton from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateToolbarButton";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import ValidityColumn from "./columns/ValidityColumn";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMGalleryFormConfig extends Config<DocumentTabPanel> {
}

class CMGalleryForm extends DocumentTabPanel {
  declare Config: CMGalleryFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmGalleryForm";

  /**
   * A constant for the linklist property name
   */
  static readonly ITEMS_PROPERTY_NAME: string = "items";

  /**
   * A constant for the linklist property name
   */
  static readonly PICTURE_PROPERTY_NAME: string = "pictures";

  constructor(config: Config<CMGalleryForm> = null) {
    super(ConfigUtils.apply(Config(CMGalleryForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm),
            Config(TeaserDocumentForm, { collapsed: true }),
            Config(PropertyFieldGroup, {
              itemId: "cmGalleryPicturesForm",
              title: CustomLabels_properties.PropertyGroup_GalleryPictures_label,
              items: [
                Config(LinkListPropertyField, {
                  showThumbnails: true,
                  propertyName: CMGalleryForm.ITEMS_PROPERTY_NAME,
                  itemId: CMGalleryForm.ITEMS_PROPERTY_NAME,

                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateToolbarButton, {
                      contentType: "CMPicture",
                      propertyName: CMGalleryForm.ITEMS_PROPERTY_NAME,
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
                Config(LinkListPropertyField, {
                  linkType: "CMPicture",
                  showThumbnails: true,
                  propertyName: CMGalleryForm.PICTURE_PROPERTY_NAME,
                  itemId: CMGalleryForm.PICTURE_PROPERTY_NAME,
                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateToolbarButton, {
                      contentType: "CMPicture",
                      propertyName: CMGalleryForm.PICTURE_PROPERTY_NAME,
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
            }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMGalleryForm;
