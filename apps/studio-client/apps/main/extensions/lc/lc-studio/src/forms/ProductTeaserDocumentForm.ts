import CustomLabels_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/CustomLabels_properties";
import TeaserOverlayConstants from "@coremedia-blueprint/studio-client.main.blueprint-forms/TeaserOverlayConstants";
import TeaserSettingsPropertyFieldGroup from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TeaserSettingsPropertyFieldGroup";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import ProductPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductPropertyNames";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import TeaserOverlayPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/TeaserOverlayPropertyField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import StringPropertyFieldDelegatePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/StringPropertyFieldDelegatePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ProductTeaserDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class ProductTeaserDocumentForm extends PropertyFieldGroup {
  declare Config: ProductTeaserDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productTeaserDocumentForm";

  constructor(config: Config<ProductTeaserDocumentForm> = null) {
    super(ConfigUtils.apply(Config(ProductTeaserDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Teaser_label,
      itemId: "productTeaserDocumentForm",
      expandOnValues: "teaserTitle,teaserText.data",

      items: [
        Config(StringPropertyField, {
          propertyName: "teaserTitle",
          itemId: "teaserTitle",
          ...ConfigUtils.append({
            plugins: [
              Config(StringPropertyFieldDelegatePlugin, { delegateExpression: CatalogHelper.getInstance().getProductPropertyExpression(config.bindTo, CatalogObjectPropertyNames.NAME) }),
            ],
          }),
        }),
        Config(TeaserOverlayPropertyField, {
          propertyName: "teaserText",
          delegateExpression: CatalogHelper.getInstance().getProductPropertyExpression(config.bindTo, ProductPropertyNames.SHORT_DESC),
          initialHeight: 100,
          itemId: "teaserText",
          settingsPath: TeaserOverlayConstants.DEFAULT_SETTINGS_PATH,
          styleDescriptorFolderPaths: TeaserOverlayConstants.DEFAULT_STYLE_DESCRIPTOR_FOLDER_PATHS,
        }),
        Config(TeaserSettingsPropertyFieldGroup),
      ],

    }), config));
  }
}

export default ProductTeaserDocumentForm;
