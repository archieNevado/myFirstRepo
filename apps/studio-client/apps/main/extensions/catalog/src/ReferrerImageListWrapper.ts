import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogStudioPluginBase from "./CatalogStudioPluginBase";
import CatalogStudioPlugin_properties from "./CatalogStudioPlugin_properties";
import CatalogTreeRelation from "./library/CatalogTreeRelation";

interface ReferrerImageListWrapperConfig extends Config<ReferrerListPanel> {
}

class ReferrerImageListWrapper extends ReferrerListPanel {
  declare Config: ReferrerImageListWrapperConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.referrerImageListWrapper";

  constructor(config: Config<ReferrerImageListWrapper> = null) {
    super(ConfigUtils.apply(Config(ReferrerImageListWrapper, {
      contentType: CatalogTreeRelation.CONTENT_TYPE_PRODUCT,
      showThumbnail: true,
      propertyName: "pictures",
      emptyText: CatalogStudioPlugin_properties.CMCategory_no_products_for_picture,
      itemId: "productsReferrer",
      title: CatalogStudioPlugin_properties.CMCategory_products_text,

      ...ConfigUtils.append({
        plugins: [
          Config(BindVisibilityPlugin, { bindTo: CatalogStudioPluginBase.getShopExpression(config) }),
        ],
      }),

    }), config));
  }
}

export default ReferrerImageListWrapper;
