import CommerceCatalogObjectsSelectForm from "@coremedia-blueprint/studio-client.main.ec-studio/forms/CommerceCatalogObjectsSelectForm";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextP13NStudioPluginBase from "../src/LivecontextP13NStudioPluginBase";

interface CommerceCatalogObjectsSelectFormTestViewConfig extends Config<Viewport>, Partial<Pick<CommerceCatalogObjectsSelectFormTestView,
  "content" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "catalogObjectIdsExpression"
>> {
}

class CommerceCatalogObjectsSelectFormTestView extends Viewport {
  declare Config: CommerceCatalogObjectsSelectFormTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.p13n.studio.config.commerceCatalogObjectsSelectFormTestView";

  static readonly TEST_VIEW_ID: string = "viewport";

  static readonly SELECT_FORM_ITEM_ID: string = "testSelectForm";

  constructor(config: Config<CommerceCatalogObjectsSelectFormTestView> = null) {
    super(ConfigUtils.apply(Config(CommerceCatalogObjectsSelectFormTestView, {
      id: CommerceCatalogObjectsSelectFormTestView.TEST_VIEW_ID,

      items: [
        Config(CommerceCatalogObjectsSelectForm, {
          itemId: CommerceCatalogObjectsSelectFormTestView.SELECT_FORM_ITEM_ID,
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          catalogObjectIdsExpression: config.catalogObjectIdsExpression,
          invalidMessage: "Invalid e-Commerce user contract ID: {0}",
          getCommerceObjectsFunction: LivecontextP13NStudioPluginBase.getContracts,
          height: 400,
        }),
      ],

    }), config));
  }

  content: Content = null;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  catalogObjectIdsExpression: ValueExpression = null;
}

export default CommerceCatalogObjectsSelectFormTestView;
