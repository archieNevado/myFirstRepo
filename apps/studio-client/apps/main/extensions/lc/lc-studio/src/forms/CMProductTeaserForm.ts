import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import CategoryDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import MediaDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MediaDocumentForm";
import MetaDataInformationForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import ValidityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/ViewTypeSelectorForm";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import ViewSettingsRadioGroup from "../components/product/ViewSettingsRadioGroup";
import CommerceDetailsForm from "../desktop/CommerceDetailsForm";
import CMProductTeaserFormBase from "./CMProductTeaserFormBase";
import ProductTeaserDocumentForm from "./ProductTeaserDocumentForm";

interface CMProductTeaserFormConfig extends Config<CMProductTeaserFormBase> {
}

class CMProductTeaserForm extends CMProductTeaserFormBase {
  declare Config: CMProductTeaserFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.cmProductTeaserForm";

  constructor(config: Config<CMProductTeaserForm> = null) {
    super((()=>{
      this.#catalogObjectExpression = CatalogHelper.getInstance().getCatalogExpression(config.bindTo);
      return ConfigUtils.apply(Config(CMProductTeaserForm, {

        items: [
          Config(DocumentForm, {
            title: BlueprintTabs_properties.Tab_content_title,
            itemId: "contentTab",
            items: [
              Config(PropertyFieldGroup, {
                title: ECommerceStudioPlugin_properties.Product_label,
                itemId: "cmProductLinkForm",
                items: [
                  Config(CatalogLinkPropertyField, {
                    bindTo: config.bindTo,
                    maxCardinality: 1,
                    replaceOnDrop: true,
                    itemId: "externalId",
                    propertyName: "externalId",
                    linkTypeNames: [CatalogModel.TYPE_PRODUCT],
                    dropAreaText: LivecontextStudioPlugin_properties.Product_Link_empty_text,
                  }),
                ],
              }),
              Config(CommerceDetailsForm, {
                itemId: "productDetailsDocumentForm",
                bindTo: this.#catalogObjectExpression,
                contentBindTo: config.bindTo,
                collapsed: true,
              }),
              Config(ProductTeaserDocumentForm),
              Config(PropertyFieldGroup, {
                itemId: "shopNowSettings",
                collapsed: true,
                title: LivecontextStudioPlugin_properties["CMProductTeaser_localSettings.shopNow_text"],
                items: [
                  Config(ViewSettingsRadioGroup, {
                    propertyName: "localSettings.view.settings",
                    itemId: "viewSettings",
                    hideLabel: true,
                  }),
                ],
              }),
              Config(MediaDocumentForm),
              Config(ViewTypeSelectorForm),
              Config(ValidityDocumentForm),
            ],
          }),
          Config(DocumentForm, {
            title: BlueprintTabs_properties.Tab_extras_title,
            itemId: "metadata",
            items: [
              Config(CategoryDocumentForm),
            ],
          }),
          Config(MultiLanguageDocumentForm),
          Config(MetaDataInformationForm),
        ],

      }), config);
    })());
  }

  #catalogObjectExpression: ValueExpression = null;
}

export default CMProductTeaserForm;
