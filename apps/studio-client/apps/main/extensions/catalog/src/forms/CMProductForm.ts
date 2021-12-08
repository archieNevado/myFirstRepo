import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CustomLabels_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/CustomLabels_properties";
import ValidityColumn from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/columns/ValidityColumn";
import CategoryDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import MediaDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import RelatedDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/RelatedDocumentForm";
import SEOForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SEOForm";
import TeaserSettingsPropertyFieldGroup from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TeaserSettingsPropertyFieldGroup";
import ValidityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ValidityDocumentForm";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import TextAreaPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextAreaPropertyField";
import TextAreaPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/TextAreaPropertyFieldDelegatePlugin";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogStudioPluginBase from "../CatalogStudioPluginBase";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";

interface CMProductFormConfig extends Config<DocumentTabPanel> {
}

class CMProductForm extends DocumentTabPanel {
  declare Config: CMProductFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.cmProductForm";

  constructor(config: Config<CMProductForm> = null) {
    super(ConfigUtils.apply(Config(CMProductForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Details_label,
              itemId: "productDetailsForm",
              items: [
                Config(StringPropertyField, { propertyName: "productName" }),
                Config(StringPropertyField, { propertyName: "productCode" }),
                Config(RichTextPropertyField, {
                  bindTo: config.bindTo,
                  propertyName: "detailText",
                  initialHeight: 200,
                }),
                Config(PropertyFieldGroup, {
                  title: CatalogStudioPlugin_properties.CMProduct_shortDescription_text,
                  itemId: "productShortDescriptionForm",
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

            Config(MediaDocumentForm, { bindTo: config.bindTo }),

            Config(PropertyFieldGroup, {
              title: CatalogStudioPlugin_properties.ProductAssets_Downloads_label,
              itemId: "productDownloadsForm",
              collapsed: true,
              items: [
                Config(LinkListPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  propertyName: "downloads",
                  additionalToolbarItems: [
                    Config(Separator),
                    Config(QuickCreateLinklistMenu, {
                      bindTo: config.bindTo,
                      contentTypes: "CMDownload",
                      propertyName: "downloads",
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

            Config(RelatedDocumentForm, {
              bindTo: config.bindTo,
              title: CatalogStudioPlugin_properties.PropertyGroup_RelatedProducts_label,
            }),
            Config(TeaserSettingsPropertyFieldGroup),
            Config(ValidityDocumentForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(DocumentForm, {
          title: CatalogStudioPlugin_properties.Tab_catalog_structure_title,
          itemId: "structureTab",
          items: [
            Config(PropertyFieldGroup, {
              itemId: "contextsPropertyField",
              title: CatalogStudioPlugin_properties.CMProduct_parentChannel_text,
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "contexts",
                  showThumbnails: true,
                  hideLabel: true,
                  itemId: "contextsPropertyField",
                  openCollectionViewHandler: CatalogStudioPluginBase.openCatalogSearch,
                  bindTo: config.bindTo,
                }),
              ],
            }),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(CategoryDocumentForm, { bindTo: config.bindTo }),
            Config(SEOForm, {
              bindTo: config.bindTo,
              delegatePropertyName: "productName",
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMProductForm;
