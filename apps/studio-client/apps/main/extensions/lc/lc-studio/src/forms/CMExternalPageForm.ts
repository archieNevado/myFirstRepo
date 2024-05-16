import BlueprintDocumentTypes_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CustomLabels_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/CustomLabels_properties";
import HiddenChannelColumn from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/columns/HiddenChannelColumn";
import ValidityColumn from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/columns/ValidityColumn";
import CategoryDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import ChannelMetaDataInformationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ChannelMetaDataInformationForm";
import CollapsibleStringPropertyForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CollapsibleStringPropertyForm";
import MultiLangWithBundlesDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLangWithBundlesDocumentForm";
import SEOForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SEOForm";
import TeaserWithPictureDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TeaserWithPictureDocumentForm";
import ValidityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ValidityDocumentForm";
import VisibilityConfigurationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/VisibilityConfigurationForm";
import VisibilityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/VisibilityDocumentForm";
import ViewtypeRenderer from "@coremedia-blueprint/studio-client.main.blueprint-forms/util/ViewtypeRenderer";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PageGridPropertyField from "@coremedia/studio-client.main.bpbase-pagegrid-studio-plugin/pagegrid/PageGridPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CMExternalPageFormConfig extends Config<DocumentTabPanel> {
}

class CMExternalPageForm extends DocumentTabPanel {
  declare Config: CMExternalPageFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.cmExternalPageForm";

  static readonly EXTERNAL_ID_ITEM_ID: string = "externalIdString";

  static readonly EXTERNAL_URI_PATH_ITEM_ID: string = "externalUriPath";

  static readonly CHILDREN_LIST_ITEM_ID: string = "children";

  static readonly VISIBILITY_ITEM_ID: string = "visibility";

  constructor(config: Config<CMExternalPageForm> = null) {
    super(ConfigUtils.apply(Config(CMExternalPageForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(CollapsibleStringPropertyForm, {
              propertyName: "title",
              title: BlueprintDocumentTypes_properties.CMChannel_title_text,
            }),
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Placements_label,
              itemId: "cmExternalChannelPlacementForm",
              items: [
                Config(PageGridPropertyField, {
                  propertyName: "placement",
                  hideLabel: true,
                  fields: [
                    Config(DataField, {
                      name: ValidityColumn.STATUS_ID,
                      mapping: "",
                      convert: ValidityColumn.convert,
                    }),
                    Config(DataField, {
                      name: "viewtypeStatus",
                      mapping: "",
                      convert: ViewtypeRenderer.convert,
                    }),
                  ],
                  columns: [
                    Config(LinkListThumbnailColumn),
                    Config(TypeIconColumn),
                    Config(NameColumn),
                    Config(ValidityColumn),
                    Config(Column, {
                      stateId: "viewTypeUrl",
                      width: 40,
                      sortable: false,
                      dataIndex: "viewtypeStatus",
                      fixed: true,
                      renderer: ViewtypeRenderer.renderer,
                    }),
                    Config(StatusColumn),
                  ],
                  placementRowWidgetItems: [
                    Config(VisibilityConfigurationForm),
                  ],
                }),
              ],
            }),
            Config(TeaserWithPictureDocumentForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_navigation_title,
          itemId: "navigation",
          items: [
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Navigation_label,
              itemId: "cmExternalPageNavigation",
              items: [
                Config(LinkListPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  propertyName: "children",
                  itemId: CMExternalPageForm.CHILDREN_LIST_ITEM_ID,
                  fields: [
                    Config(DataField, {
                      name: ValidityColumn.STATUS_ID,
                      mapping: "",
                      convert: ValidityColumn.convert,
                    }),
                    Config(DataField, {
                      name: HiddenChannelColumn.STATUS_ID,
                      mapping: "properties.hidden",
                    }),
                  ],
                  columns: [
                    Config(LinkListThumbnailColumn),
                    Config(TypeIconColumn),
                    Config(NameColumn),
                    Config(ValidityColumn),
                    Config(HiddenChannelColumn),
                    Config(StatusColumn),
                  ],
                }),
              ],
            }),
            Config(VisibilityDocumentForm, { itemId: CMExternalPageForm.VISIBILITY_ITEM_ID }),
            Config(PropertyFieldGroup, {
              title: LivecontextStudioPlugin_properties.EnhancedPageGroup_title,
              itemId: "cmExternalPageEnhancedPage",
              items: [
                Config(StringPropertyField, {
                  propertyName: "externalId",
                  changeBuffer: 1000,
                  itemId: CMExternalPageForm.EXTERNAL_ID_ITEM_ID,
                }),
                Config(StringPropertyField, {
                  propertyName: "externalUriPath",
                  changeBuffer: 1000,
                  itemId: CMExternalPageForm.EXTERNAL_URI_PATH_ITEM_ID,
                }),
              ],
            }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(CategoryDocumentForm),
            Config(SEOForm),
          ],
        }),
        Config(MultiLangWithBundlesDocumentForm),
        Config(ChannelMetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMExternalPageForm;
