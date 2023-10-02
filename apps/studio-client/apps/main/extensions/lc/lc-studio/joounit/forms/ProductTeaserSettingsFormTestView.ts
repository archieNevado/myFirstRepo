import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ViewSettingsRadioGroup from "../../src/components/product/ViewSettingsRadioGroup";

interface ProductTeaserSettingsFormTestViewConfig extends Config<Viewport>, Partial<Pick<ProductTeaserSettingsFormTestView,
  "bindTo"
>> {
}

class ProductTeaserSettingsFormTestView extends Viewport {
  declare Config: ProductTeaserSettingsFormTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productTeaserSettingsFormTestView";

  constructor(config: Config<ProductTeaserSettingsFormTestView> = null) {
    super(ConfigUtils.apply(Config(ProductTeaserSettingsFormTestView, {

      items: [
        Config(ViewSettingsRadioGroup, {
          bindTo: config.bindTo,
          propertyName: "localSettings.shopNow",
        }),
      ],

    }), config));
  }

  bindTo: ValueExpression = null;
}

export default ProductTeaserSettingsFormTestView;
