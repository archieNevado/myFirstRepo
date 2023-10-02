import RichTextPlainTextTransformer from "@coremedia/studio-client.cap-base-models/content/RichTextPlainTextTransformer";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import ContributionAdministrationPropertyNames from "@coremedia/studio-client.main.es-models/ContributionAdministrationPropertyNames";
import ContributionPropertyNames from "@coremedia/studio-client.main.es-models/ContributionPropertyNames";
import ValueExpressionUtil from "@coremedia/studio-client.main.social-studio-plugin/util/ValueExpressionUtil";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ProductInformationContainerBase from "./ProductInformationContainerBase";

interface ProductInformationContainerConfig extends Config<ProductInformationContainerBase> {
}

class ProductInformationContainer extends ProductInformationContainerBase {
  declare Config: ProductInformationContainerConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.elastic.social.studio.config.productInformationContainer";

  constructor(config: Config<ProductInformationContainer> = null) {
    super(ConfigUtils.apply(Config(ProductInformationContainer, {
      ui: ConfigUtils.asString(ContainerSkin.CARD_200),

      layout: Config(VBoxLayout),
      items: [
        Config(Container, {
          layout: Config(HBoxLayout, { align: "stretch" }),
          items: [
            Config(IconDisplayField, { itemId: ProductInformationContainerBase.TARGET_BUTTON_ICON_ITEM_ID }),
            Config(DisplayField, {
              itemId: ProductInformationContainerBase.TARGET_LABEL_ID,
              plugins: [
                Config(BindPropertyPlugin, {
                  componentProperty: "value",
                  bindTo: new ConfigBasedValueExpression({
                    context: config.contributionAdministration,
                    expression: ValueExpressionUtil.createPath([
                      ContributionAdministrationPropertyNames.DISPLAYED,
                      ContributionPropertyNames.TARGET,
                      "name",
                    ]),
                  }),
                }),
              ],
            }),
          ],
        }),
        Config(DisplayField, {
          margin: "0 0 0 20px",
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "value",
              transformer: RichTextPlainTextTransformer.convertToPlainText,
              bindTo: new ConfigBasedValueExpression({
                context: config.contributionAdministration,
                expression: ValueExpressionUtil.createPath([
                  ContributionAdministrationPropertyNames.DISPLAYED,
                  ContributionPropertyNames.TARGET,
                  "shortDescription",
                ]),
              }),
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default ProductInformationContainer;
