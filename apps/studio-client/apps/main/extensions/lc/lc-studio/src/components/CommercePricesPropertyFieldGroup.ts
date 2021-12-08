import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductPropertyNames";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CommercePricesPropertyFieldGroupConfig extends Config<PropertyFieldGroup> {
}

class CommercePricesPropertyFieldGroup extends PropertyFieldGroup {
  declare Config: CommercePricesPropertyFieldGroupConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commercePricesPropertyFieldGroup";

  #isPriceEnabled(): boolean {
    const product = as(this.bindTo.getValue(), Product);
    return !!product && !!product.getListPrice();
  }

  constructor(config: Config<CommercePricesPropertyFieldGroup> = null) {
    super((()=> ConfigUtils.apply(Config(CommercePricesPropertyFieldGroup, {
      title: LivecontextStudioPlugin_properties.Commerce_Product_PropertyGroup_prices_title,

      ...ConfigUtils.append({
        plugins: [
          Config(BindVisibilityPlugin, { bindTo: ValueExpressionFactory.createFromFunction(bind(this, this.#isPriceEnabled)) }),
        ],
      }),
      items: [
        Config(DisplayField, {
          itemId: "listPrice",
          labelAlign: "left",
          labelSeparator: ":",
          fieldLabel: LivecontextStudioPlugin_properties.Commerce_Product_listPrice_label,
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "value",
              bindTo: CatalogHelper.getInstance().getPriceWithCurrencyExpression(config.bindTo, ProductPropertyNames.LIST_PRICE),
              ifUndefined: LivecontextStudioPlugin_properties.Commerce_Product_listPrice_emptyText,
            }),
          ],
        }),
        Config(DisplayField, {
          itemId: "offerPrice",
          labelAlign: "left",
          labelSeparator: ":",
          fieldLabel: LivecontextStudioPlugin_properties.Commerce_Product_offerPrice_label,
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "value",
              bindTo: CatalogHelper.getInstance().getPriceWithCurrencyExpression(config.bindTo, ProductPropertyNames.OFFER_PRICE),
              ifUndefined: LivecontextStudioPlugin_properties.Commerce_Product_offerPrice_emptyText,
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default CommercePricesPropertyFieldGroup;
