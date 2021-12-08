import LinkListViewDragZone from "@coremedia/studio-client.ext.link-list-components/LinkListViewDragZone";
import AbstractCustomGrid from "@coremedia/studio-client.ext.ui-components/components/AbstractCustomGrid";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogDragDropVisualFeedback from "../dragdrop/CatalogDragDropVisualFeedback";

interface AbstractCatalogListConfig extends Config<AbstractCustomGrid> {
}

class AbstractCatalogList extends AbstractCustomGrid {
  declare Config: AbstractCatalogListConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.abstractCatalogList";

  constructor(config: Config<AbstractCatalogList> = null) {
    super(ConfigUtils.apply(Config(AbstractCatalogList, {

      dragZoneCfg: Config(LinkListViewDragZone, {
        htmlFeedback: CatalogDragDropVisualFeedback.getHtmlFeedback,
        ddGroups: ["ContentLinkDD"],
      }),
    }), config));
  }
}

export default AbstractCatalogList;
