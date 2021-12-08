import BlueprintDocumentTypes_properties
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CategoryDocumentForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import ChannelMetaDataInformationForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ChannelMetaDataInformationForm";
import CollapsibleStringPropertyForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CollapsibleStringPropertyForm";
import MultiLanguageDocumentForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import SEOForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SEOForm";
import TeaserWithPictureDocumentForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TeaserWithPictureDocumentForm";
import ValidityDocumentForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ValidityDocumentForm";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObjectPropertyNames
  from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import CatalogLinkPropertyField
  from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CatalogPreferencesBase
  from "@coremedia-blueprint/studio-client.main.ec-studio/components/preferences/CatalogPreferencesBase";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ImageComponent from "@coremedia/studio-client.ext.ui-components/components/ImageComponent";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentPath from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentPath";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Component from "@jangaroo/ext-ts/Component";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogThumbnailResolver from "../CatalogThumbnailResolver";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CatalogAssetsProperty from "../components/link/CatalogAssetsProperty";
import CommerceAugmentedPageGridForm from "../desktop/CommerceAugmentedPageGridForm";
import CommerceDetailsForm from "../desktop/CommerceDetailsForm";
import CommerceAugmentedCategoryStructureForm from "./CommerceAugmentedCategoryStructureForm";

interface CMExternalChannelFormConfig extends Config<DocumentTabPanel> {
}

class CMExternalChannelForm extends DocumentTabPanel {
  declare Config: CMExternalChannelFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.cmExternalChannelForm";

  static readonly CONTENT_TAB_ITEM_ID: string = "contentTab";

  static readonly CATALOG_STRUCTURE_TAB_ITEM_ID: string = "catalogStructureTab";

  static readonly PDP_PAGEGRID_TAB_ITEM_ID: string = "pdpPageGridTab";

  static readonly CATALOG_LINK_ITEM_ID: string = "catalogLink";

  static readonly EXTERNAL_ID_PROPERTY: string = "externalId";

  #catalogObjectExpression: ValueExpression = null;

  constructor(config: Config<CMExternalChannelForm> = null) {
    super((()=>{
      this.#catalogObjectExpression = AugmentationUtil.getCatalogObjectExpression(config.bindTo);
      return ConfigUtils.apply(Config(CMExternalChannelForm, {

        items: [
          Config(DocumentForm, {
            itemId: CMExternalChannelForm.CONTENT_TAB_ITEM_ID,
            title: BlueprintTabs_properties.Tab_content_title,
            items: [
              Config(CollapsibleStringPropertyForm, {
                propertyName: "title",
                title: BlueprintDocumentTypes_properties.CMChannel_title_text,
              }),
              Config(CommerceDetailsForm, {
                itemId: "commerceDetails",
                bindTo: this.#catalogObjectExpression,
                contentBindTo: config.bindTo,
                collapsed: true,
              }),
              Config(CommerceAugmentedPageGridForm, {
                itemId: "placement",
                pageGridPropertyName: "placement",
              }),
              Config(PropertyFieldGroup, {
                title: LivecontextStudioPlugin_properties.Commerce_PropertyGroup_thumbnail_title,
                itemId: "picture",
                items: [
                  Config(ImageComponent, {
                    width: 120,
                    plugins: [
                      Config(BindPropertyPlugin, {
                        componentProperty: "src",
                        bindTo: CatalogThumbnailResolver.imageValueExpression(config.bindTo),
                      }),
                    ],
                  }),
                ],
                layout: Config(VBoxLayout),
              }),
              Config(PropertyFieldGroup, {
                title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_richMedia_title,
                itemId: "richMedia",
                bindTo: this.#catalogObjectExpression,
                items: [
                  Config(CatalogAssetsProperty, {
                    propertyName: CatalogObjectPropertyNames.VISUALS,
                    assetContentTypes: ["CMPicture", "CMVideo", "CMSpinner"],
                    emptyText: LivecontextStudioPlugin_properties.Commerce_Product_richMedia_emptyText,
                  }),
                ],
              }),
              Config(PropertyFieldGroup, {
                title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_downloads_title,
                itemId: "downloads",
                bindTo: this.#catalogObjectExpression,
                items: [
                  Config(CatalogAssetsProperty, {
                    propertyName: CatalogObjectPropertyNames.DOWNLOADS,
                    assetContentTypes: ["CMDownload"],
                    emptyText: LivecontextStudioPlugin_properties.Commerce_Product_downloads_emptyText,
                  }),
                ],
              }),
              Config(TeaserWithPictureDocumentForm),
              Config(ValidityDocumentForm),
            ],
          }),
          Config(CommerceAugmentedCategoryStructureForm, {
            bindTo: config.bindTo,
            itemId: CMExternalChannelForm.CATALOG_STRUCTURE_TAB_ITEM_ID,
          }),
          Config(DocumentForm, {
            itemId: CMExternalChannelForm.PDP_PAGEGRID_TAB_ITEM_ID,
            title: LivecontextStudioPlugin_properties.CMExternalChannel_tab_PDP_pagegrid_title,
            items: [
              Config(CommerceAugmentedPageGridForm, {
                itemId: "pdpPagegrid",
                pageGridPropertyName: "pdpPagegrid",
                fallbackPageGridPropertyName: "placement",
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
          Config(MultiLanguageDocumentForm),
          Config(ChannelMetaDataInformationForm, {
            ...ConfigUtils.append({
              plugins: [
                Config(NestedRulesPlugin, {
                  rules: [
                    Config(DocumentPath, {
                      plugins: [
                        Config(BindPropertyPlugin, {
                          bindTo: ValueExpressionFactory.create<boolean>(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext._.getPreferences()),
                          ifUndefined: true,
                          componentProperty: "hidden",
                          transformer: (enabled: boolean) => !enabled,
                        }),
                      ],
                    }),
                  ],
                }),
                Config(AddItemsPlugin, {
                  items: [
                    Config(PropertyFieldGroup, {
                      title: LivecontextStudioPlugin_properties.CMExternalChannel_externalId_text,
                      itemId: "externalId",
                      /* We show the catalog category link field even for the root category.
               TODO: But there should be instead a label like 'this is root category.'
                              <plugins mode="append">
                                <ui:bindVisibilityPlugin bindTo="{getVisibilityExpression(config.bindTo)}"/>
                              </plugins>
               */
                      items: [
                        Config(CatalogLinkPropertyField, {
                          itemId: CMExternalChannelForm.CATALOG_LINK_ITEM_ID,
                          maxCardinality: 1,
                          propertyName: CMExternalChannelForm.EXTERNAL_ID_PROPERTY,
                          linkTypeNames: [CatalogModel.TYPE_CATEGORY],
                          dropAreaText: LivecontextStudioPlugin_properties.Category_Link_empty_text,
                          showChangeReferenceButton: true,
                          readOnlyValueExpression: ValueExpressionFactory.createFromValue(false),
                          ...ConfigUtils.append({
                            plugins: [
                              Config(ShowIssuesPlugin, {
                                propertyName: "externalId",
                                bindTo: config.bindTo,
                              }),
                            ],
                          }),
                        }),
                      ],
                    }),
                  ],
                  after: [
                    Config(Component, { itemId: VersionHistory.ITEM_ID_VERSION_HISTORY }),
                  ],
                }),
              ],
            }),
          }),
        ],

      }), config);
    })());
  }
}

export default CMExternalChannelForm;
