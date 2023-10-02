import ProductAttribute from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductAttribute";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ProductAttributeLabelConfig extends Config<FieldContainer>, Config<HidableMixin>, Partial<Pick<ProductAttributeLabel,
  "productAttribute" |
  "hideText"
>> {
}

class ProductAttributeLabel extends FieldContainer {
  declare Config: ProductAttributeLabelConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productAttributeLabel";

  constructor(config: Config<ProductAttributeLabel> = null) {
    super(ConfigUtils.apply(Config(ProductAttributeLabel, {

      items: [
        Config(DisplayField, {
          fieldLabel: config.productAttribute.displayName,
          labelSeparator: ":",
          labelAlign: "left",
          value: config.productAttribute.value.toLocaleString() + "",
        }),
      ],

    }), config));
  }

  productAttribute: ProductAttribute = null;

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }
}

interface ProductAttributeLabel extends HidableMixin{}

mixin(ProductAttributeLabel, HidableMixin);

export default ProductAttributeLabel;
