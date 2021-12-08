import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import AddLazyItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddLazyItemsPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Contribution from "@coremedia/studio-client.main.es-models/Contribution";
import CommentMetaDataPanel from "@coremedia/studio-client.main.social-studio-plugin/moderation/shared/details/comments/CommentMetaDataPanel";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import { is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ProductInformationContainer from "./ProductInformationContainer";

interface LcElasticSocialStudioPluginConfig extends Config<StudioPlugin> {
}

class LcElasticSocialStudioPlugin extends StudioPlugin {
  declare Config: LcElasticSocialStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.elastic.social.studio.config.lcElasticSocialStudioPlugin";

  static readonly #PRODUCT_INFORMATION_CONTAINER_ITEM_ID: string = "com-coremedia-ecommerce-studio-model-ProductImpl";

  constructor(config: Config<LcElasticSocialStudioPlugin> = null) {
    super((()=>{
      this.#__initialize__(config);
      return ConfigUtils.apply(Config(LcElasticSocialStudioPlugin, {

        rules: [
          Config(CommentMetaDataPanel, {
            plugins: [
              Config(AddLazyItemsPlugin, {
                applyTo: (container: Container): Component => container.queryById(CommentMetaDataPanel.TARGET_INFORMATION_CONTAINER_ITEM_ID),
                items: [
                  Config(ProductInformationContainer, { itemId: LcElasticSocialStudioPlugin.#PRODUCT_INFORMATION_CONTAINER_ITEM_ID }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }

  #__initialize__(config: Config<LcElasticSocialStudioPlugin>): void {
    CommentMetaDataPanel.registerContributionToItemIdPredicate(LcElasticSocialStudioPlugin.#PRODUCT_INFORMATION_CONTAINER_ITEM_ID, (contribution: Contribution): boolean =>
      is(contribution, Product),
    );
  }
}

export default LcElasticSocialStudioPlugin;
