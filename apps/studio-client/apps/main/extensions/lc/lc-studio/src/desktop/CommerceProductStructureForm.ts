import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import ProductPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductPropertyNames";
import ReadOnlyCatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/ReadOnlyCatalogLinkPropertyField";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CommerceProductStructureFormConfig extends Config<DocumentForm> {
}

class CommerceProductStructureForm extends DocumentForm {
  declare Config: CommerceProductStructureFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceProductStructureForm";

  constructor(config: Config<CommerceProductStructureForm> = null) {
    super(ConfigUtils.apply(Config(CommerceProductStructureForm, {
      title: LivecontextStudioPlugin_properties.Commerce_Tab_structure_title,

      items: [

        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_category_title,
          itemId: "category",
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, { propertyName: CatalogObjectPropertyNames.CATEGORY }),
          ],
        }),

        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_parentProduct_title,
          itemId: "parentProductd",
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: CatalogHelper.getInstance().getIsVariantExpression(config.bindTo) }),
            ],
          }),
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, { propertyName: CatalogObjectPropertyNames.PARENT }),
          ],
        }),

        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_productVariants_title,
          itemId: "variants",
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: CatalogHelper.getInstance().getIsNotVariantExpression(config.bindTo) }),
            ],
          }),
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, {
              propertyName: ProductPropertyNames.VARIANTS,
              emptyText: LivecontextStudioPlugin_properties.Commerce_Product_productVariants_emptyText,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default CommerceProductStructureForm;
