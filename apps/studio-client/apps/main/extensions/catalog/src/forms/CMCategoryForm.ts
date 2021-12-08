import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CustomLabels_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/CustomLabels_properties";
import CMChannelForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMChannelForm";
import ValidityColumn from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/columns/ValidityColumn";
import CategoryDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import ChannelMetaDataInformationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ChannelMetaDataInformationForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import SEOForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SEOForm";
import SinglePictureDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SinglePictureDocumentForm";
import TeaserSettingsPropertyFieldGroup from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TeaserSettingsPropertyFieldGroup";
import ValidityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ValidityDocumentForm";
import VisibilityConfigurationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/VisibilityConfigurationForm";
import ViewtypeRenderer from "@coremedia-blueprint/studio-client.main.blueprint-forms/util/ViewtypeRenderer";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PageGridPropertyField from "@coremedia/studio-client.main.bpbase-pagegrid-studio-plugin/pagegrid/PageGridPropertyField";
import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextAreaPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextAreaPropertyField";
import TextAreaPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/TextAreaPropertyFieldDelegatePlugin";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogStudioPluginBase from "../CatalogStudioPluginBase";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";
import CatalogTreeRelation from "../library/CatalogTreeRelation";

interface CMCategoryFormConfig extends Config<CMChannelForm> {
}

class CMCategoryForm extends CMChannelForm {
  declare Config: CMCategoryFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.cmCategoryForm";

  static override readonly PAGE_GRID_TAB_ITEM_ID: string = "pageGridTab";

  constructor(config: Config<CMCategoryForm> = null) {
    super(ConfigUtils.apply(Config(CMCategoryForm, {

      items: [
        Config(DocumentForm, {
          itemId: CMCategoryForm.PAGE_GRID_TAB_ITEM_ID,
          title: BlueprintTabs_properties.Tab_content_title,
          items: [
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Details_label,
              itemId: "categoryDetailsForm",
              items: [
                Config(StringPropertyField, {
                  itemId: "title",
                  propertyName: "title",
                }),
                Config(RichTextPropertyField, {
                  bindTo: config.bindTo,
                  itemId: "detailText",
                  propertyName: "detailText",
                  initialHeight: 200,
                }),
                Config(PropertyFieldGroup, {
                  title: CatalogStudioPlugin_properties.CMCategory_teaserText_text,
                  itemId: "categoryTeaserTextForm",
                  items: [
                    Config(TextAreaPropertyField, {
                      propertyName: "teaserText",
                      hideLabel: true,
                      ...ConfigUtils.append({
                        plugins: [
                          Config(TextAreaPropertyFieldDelegatePlugin, { delegatePropertyName: "detailText" }),
                        ],
                      }),
                    }),
                  ],
                }),
              ],
            }),
            Config(SinglePictureDocumentForm),
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
            Config(TeaserSettingsPropertyFieldGroup),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: CatalogStudioPlugin_properties.Tab_catalog_structure_title,
          itemId: "navigationTab",
          items: [
            Config(ReferrerListPanel, {
              contentType: CatalogTreeRelation.CONTENT_TYPE_CATEGORY,
              showThumbnail: true,
              propertyName: "children",
              hideHeaders: true,
              itemId: "parentCategoryReferrer",
              addBorder: true,
              emptyText: CatalogStudioPlugin_properties.CMCategory_no_parent,
              title: CatalogStudioPlugin_properties.CMCategory_parentChannel_text,
              hideDeletedItemsCheckbox: true,
            }),
            Config(PropertyFieldGroup, {
              itemId: "cmChannelChildrenForm",
              title: CatalogStudioPlugin_properties.PropertyGroup_SubCategories_label,
              items: [
                Config(LinkListPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  showThumbnails: true,
                  itemId: "categoryChildrenLinkList",
                  openCollectionViewHandler: CatalogStudioPluginBase.openCatalogSearch,
                  propertyName: "children",
                }),
              ],
            }),
            Config(ReferrerListPanel, {
              contentType: CatalogTreeRelation.CONTENT_TYPE_PRODUCT,
              showThumbnail: true,
              propertyName: "contexts",
              hideHeaders: true,
              itemId: "productsReferrer",
              addBorder: true,
              emptyText: CatalogStudioPlugin_properties.CMCategory_no_products,
              title: CatalogStudioPlugin_properties.CMCategory_products_text,
              hideDeletedItemsCheckbox: true,
            }),
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
        Config(MultiLanguageDocumentForm),
        Config(ChannelMetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMCategoryForm;
