import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ProductTeaserDocumentForm from "../../src/forms/ProductTeaserDocumentForm";

interface ProductTeaserDocumentFormTestViewConfig extends Config<Viewport>, Partial<Pick<ProductTeaserDocumentFormTestView,
  "bindTo"
>> {
}

class ProductTeaserDocumentFormTestView extends Viewport {
  declare Config: ProductTeaserDocumentFormTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productTeaserDocumentFormTestView";

  constructor(config: Config<ProductTeaserDocumentFormTestView> = null) {
    super(ConfigUtils.apply(Config(ProductTeaserDocumentFormTestView, {

      items: [
        Config(ProductTeaserDocumentForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }

  bindTo: ValueExpression = null;
}

export default ProductTeaserDocumentFormTestView;
