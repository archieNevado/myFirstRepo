import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PageGridPropertyField from "@coremedia/studio-client.main.bpbase-pagegrid-studio-plugin/pagegrid/PageGridPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import ViewtypeRenderer from "../util/ViewtypeRenderer";
import CMChannelFormBase from "./CMChannelFormBase";
import HiddenChannelColumn from "./columns/HiddenChannelColumn";
import ValidityColumn from "./columns/ValidityColumn";
import CategoryDocumentForm from "./containers/CategoryDocumentForm";
import ChannelMetaDataInformationForm from "./containers/ChannelMetaDataInformationForm";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MultiLangWithBundlesDocumentForm from "./containers/MultiLangWithBundlesDocumentForm";
import SEOForm from "./containers/SEOForm";
import TeaserWithPictureDocumentForm from "./containers/TeaserWithPictureDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import VisibilityConfigurationForm from "./containers/VisibilityConfigurationForm";
import VisibilityDocumentForm from "./containers/VisibilityDocumentForm";

interface CMChannelFormConfig extends Config<CMChannelFormBase> {
}

class CMChannelForm extends CMChannelFormBase {
  declare Config: CMChannelFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmChannelForm";

  static readonly PAGE_GRID_TAB_ITEM_ID: string = "pageGridTab";

  constructor(config: Config<CMChannelForm> = null) {
    super(ConfigUtils.apply(Config(CMChannelForm, {

      items: [
        Config(DocumentForm, {
          itemId: CMChannelForm.PAGE_GRID_TAB_ITEM_ID,
          title: BlueprintTabs_properties.Tab_content_title,
          items: [
            Config(CollapsibleStringPropertyForm, {
              propertyName: "title",
              title: BlueprintDocumentTypes_properties.CMChannel_title_text,
            }),
            Config(PropertyFieldGroup, {
              itemId: "cmChannelPageGridForm",
              title: CustomLabels_properties.PropertyGroup_Placements_label,
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
          itemId: "navigationTab",
          items: [
            Config(PropertyFieldGroup, {
              itemId: "cmChannelChildrenForm",
              title: CustomLabels_properties.PropertyGroup_Navigation_label,
              items: [
                Config(LinkListPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  propertyName: "children",
                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateLinklistMenu, {
                      bindTo: config.bindTo,
                      contentTypes: "CMChannel",
                      propertyName: "children",
                    }),
                  ],
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
                    Config(TypeIconColumn),
                    Config(NameColumn),
                    Config(ValidityColumn),
                    Config(HiddenChannelColumn),
                    Config(StatusColumn),
                  ],
                }),
              ],
            }),
            Config(VisibilityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(CategoryDocumentForm, { bindTo: config.bindTo }),
            Config(SEOForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(MultiLangWithBundlesDocumentForm),
        Config(ChannelMetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMChannelForm;
