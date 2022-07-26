import Config from "@jangaroo/runtime/Config";
import CommonCatalogContextMenu from "../CommonCatalogContextMenu";

interface CatalogSearchContextMenuConfig extends Config<CommonCatalogContextMenu> {
}

/**
 * The context menu for the list or thumbnail view in the catalog search view.
 */
class CatalogSearchContextMenu extends CommonCatalogContextMenu {
  declare Config: CatalogSearchContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchContextMenu";

  constructor(config: Config<CatalogSearchContextMenu> = null) {
    super(config);
  }
}

export default CatalogSearchContextMenu;
