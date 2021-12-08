import ProductPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductPropertyNames";
import RichTextPlainTextTransformer from "@coremedia/studio-client.cap-base-models/content/RichTextPlainTextTransformer";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatefulTextArea from "@coremedia/studio-client.ext.ui-components/components/StatefulTextArea";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ProductDescriptionTextAreaConfig extends Config<StatefulTextArea>, Partial<Pick<ProductDescriptionTextArea,
  "bindTo"
>> {
}

class ProductDescriptionTextArea extends StatefulTextArea {
  declare Config: ProductDescriptionTextAreaConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productDescriptionTextArea";

  constructor(config: Config<ProductDescriptionTextArea> = null) {
    super(ConfigUtils.apply(Config(ProductDescriptionTextArea, {
      labelAlign: "top",
      labelSeparator: "",
      height: 100,
      anchor: "100%",
      readOnly: true,

      plugins: [
        Config(BindPropertyPlugin, {
          bindTo: ValueExpressionFactory.createTransformingValueExpression(
            config.bindTo.extendBy(ProductPropertyNames.SHORT_DESC),
            RichTextPlainTextTransformer.convertToPlainText),
        }),
      ],

    }), config));
  }

  /**
   * The bean value expression pointing to a product teaser.
   */
  bindTo: ValueExpression = null;
}

export default ProductDescriptionTextArea;
