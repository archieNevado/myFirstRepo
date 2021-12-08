import ProductAttribute from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductAttribute";
import ProductPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductPropertyNames";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ArrayUtils from "@coremedia/studio-client.client-core/util/ArrayUtils";
import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Label from "@jangaroo/ext-ts/form/Label";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import ProductAttributeLabel from "../components/ProductAttributeLabel";

interface CommerceAttributesFormConfig extends Config<DocumentForm> {
}

class CommerceAttributesForm extends DocumentForm {
  declare Config: CommerceAttributesFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceAttributesForm";

  constructor(config: Config<CommerceAttributesForm> = null) {
    super(ConfigUtils.apply(Config(CommerceAttributesForm, {
      title: LivecontextStudioPlugin_properties.Commerce_Tab_attributes_title,
      itemId: "attributes",

      items: [
        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_PropertyGroup_definingAttributes_title,
          itemId: "definingAttributes",
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: CatalogHelper.getInstance().getIsVariantExpression(config.bindTo) }),
              Config(BindComponentsPlugin, {
                valueExpression: config.bindTo.extendBy(ProductPropertyNames.DEFINING_ATTRIBUTES),
                configBeanParameterName: "productAttribute",
                getKey: (productAttribute: ProductAttribute): string => productAttribute.name,
                template: Config(ProductAttributeLabel),
              }),
            ],
          }),
          items: [
            Config(Label, {
              text: LivecontextStudioPlugin_properties.Commerce_definingAttributes_emptyText,
              plugins: [
                Config(BindVisibilityPlugin, {
                  bindTo: ValueExpressionFactory.createTransformingValueExpression(
                    config.bindTo.extendBy(ProductPropertyNames.DEFINING_ATTRIBUTES),
                    ArrayUtils.isEmpty),
                }),
              ],
            }),
          ],
        }),
        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_PropertyGroup_describingAttributes_title,
          itemId: "describingAttributes",
          ...ConfigUtils.append({
            plugins: [
              Config(BindComponentsPlugin, {
                valueExpression: config.bindTo.extendBy(ProductPropertyNames.DESCRIBING_ATTRIBUTES),
                configBeanParameterName: "productAttribute",
                getKey: (productAttribute: ProductAttribute): string => productAttribute.name,
                template: Config(ProductAttributeLabel),
              }),
            ],
          }),
          items: [
            Config(Label, {
              text: LivecontextStudioPlugin_properties.Commerce_describingAttributes_emptyText,
              plugins: [
                Config(BindVisibilityPlugin, {
                  bindTo: ValueExpressionFactory.createTransformingValueExpression(
                    config.bindTo.extendBy(ProductPropertyNames.DESCRIBING_ATTRIBUTES),
                    ArrayUtils.isEmpty),
                }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default CommerceAttributesForm;
