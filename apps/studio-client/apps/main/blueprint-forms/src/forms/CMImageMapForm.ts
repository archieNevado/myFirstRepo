import StructContentLinkListWrapper from "@coremedia/studio-client.content-link-list-models/StructContentLinkListWrapper";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import AnnotatedLinkListWidget from "@coremedia/studio-client.ext.ui-components/components/AnnotatedLinkListWidget";
import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateToolbarButton from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateToolbarButton";
import AnnotatedLinkListHelper from "@coremedia/studio-client.main.editor-components/sdk/util/AnnotatedLinkListHelper";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import ImageMapEditor from "@coremedia/studio-client.main.image-map-editor-components/ImageMapEditor";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import ValidityColumn from "./columns/ValidityColumn";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import CallToActionConfigurationForm from "./containers/CallToActionConfigurationForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import SinglePictureDocumentForm from "./containers/SinglePictureDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMImageMapFormConfig extends Config<DocumentTabPanel>, Partial<Pick<CMImageMapForm,
  "contentType" |
  "folders"
>> {
}

class CMImageMapForm extends DocumentTabPanel {
  declare Config: CMImageMapFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmImageMapForm";

  static readonly TARGET_ANNOTATION_WIDGET_ITEM_ID: string = "target-annotation-widget";

  static readonly OVERLAY_CONFIG_ITEMID: string = "overlayConfig";

  #structContentLinkListWrapper: StructContentLinkListWrapper = null;

  constructor(config: Config<CMImageMapForm> = null) {
    super((()=> ConfigUtils.apply(Config(CMImageMapForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(TeaserDocumentForm, {
              itemId: "detailsForm",
              collapsed: false,
              ...ConfigUtils.append({
                plugins: [
                  Config(RemoveItemsPlugin, {
                    recursive: true,
                    items: [
                      Config(CallToActionConfigurationForm),
                    ],
                  }),
                ],
              }),
            }),
            Config(SinglePictureDocumentForm, { itemId: "cmImageMapPicturesForm" }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMTeaser_targets_text,
              itemId: "cmImageMapTargetForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "targets",
                  hideLabel: true,
                  showThumbnails: true,
                  linkListWrapper: this.#getStructContentLinkListWrapper(config),
                  linkType: "CMTeasable",
                  rowWidgetsAnnotatedPredicates: [CallToActionConfigurationForm.isAnnotated],
                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateToolbarButton, {
                      contentType: "CMPicture",
                      propertyName: "target",
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
                  rowWidget: Config(AnnotatedLinkListWidget, {
                    itemId: CMImageMapForm.TARGET_ANNOTATION_WIDGET_ITEM_ID,
                    items: [
                      Config(CallToActionConfigurationForm, {
                        bindTo: config.bindTo,
                        collapsible: false,
                        ui: PanelSkin.DEFAULT.getSkin(),
                        header: false,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
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
            Config(PropertyFieldGroup, {
              itemId: CMImageMapForm.OVERLAY_CONFIG_ITEMID,
              title: BlueprintDocumentTypes_properties.CMImageMap_overlayConfiguration_title,
              ...ConfigUtils.append({
                plugins: [
                  Config(ShowIssuesPlugin, {
                    bindTo: config.bindTo,
                    propertyName: "localSettings.overlay",
                  }),
                  Config(PropertyFieldPlugin, { propertyName: "localSettings.overlay" }),
                ],
              }),
              items: [
                Config(BooleanPropertyField, {
                  itemId: "displayTitle",
                  propertyName: "localSettings.overlay.displayTitle",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
                Config(BooleanPropertyField, {
                  itemId: "displayShortText",
                  propertyName: "localSettings.overlay.displayShortText",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
                Config(BooleanPropertyField, {
                  itemId: "displayPicture",
                  propertyName: "localSettings.overlay.displayPicture",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
              ],
            }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintDocumentTypes_properties.CMImageMap_title,
          itemId: "hotzones",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMImageMap_title,
              itemId: "cmImageMapEditorForm",
              items: [
                Config(ImageMapEditor, {
                  imageBlobValueExpression: config.bindTo.extendBy("properties.pictures.0.properties.data"),
                  structPropertyName: "localSettings",
                  areaContentType: "CMTeasable",
                  plugins: [
                    Config(VerticalSpacingPlugin),
                  ],
                }),
              ],
            }),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config))());
  }

  #getStructContentLinkListWrapper(config: Config<CMImageMapForm>): ILinkListWrapper {
    if (!this.#structContentLinkListWrapper) {
      this.#structContentLinkListWrapper = AnnotatedLinkListHelper.createStructContentLinkListWrapper(config.bindTo, config.forceReadOnlyValueExpression);
    }
    return this.#structContentLinkListWrapper;
  }

  /**
   * The content type to fill the tree with.
   */
  contentType: string = null;

  /**
   * The comma separated folder values to read the content from.
   */
  folders: string = null;
}

export default CMImageMapForm;
