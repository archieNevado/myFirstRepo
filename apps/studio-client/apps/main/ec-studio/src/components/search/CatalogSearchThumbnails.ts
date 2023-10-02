import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import Container from "@jangaroo/ext-ts/container/Container";
import PanelHeader from "@jangaroo/ext-ts/panel/Header";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogThumbDataView from "../thumbnail/CatalogThumbDataView";
import CatalogSearchContextMenu from "./CatalogSearchContextMenu";

interface CatalogSearchThumbnailsConfig extends Config<Container>, Partial<Pick<CatalogSearchThumbnails,
  "selectedItemsValueExpression" |
  "searchResultHitsValueExpression"
>> {
}

class CatalogSearchThumbnails extends Container {
  declare Config: CatalogSearchThumbnailsConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchThumbnails";

  static readonly THUMB_DATA_VIEW_ITEM_ID: string = "thumbdataview";

  /**
   * The itemId of the thumb data view panel.
   */
  static readonly THUMB_DATA_VIEW_PANEL_ITEM_ID: string = "thumbdataviewscroller";

  constructor(config: Config<CatalogSearchThumbnails> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchThumbnails, {
      layout: "border",

      items: [
        Config(Panel, {
          itemId: CatalogSearchThumbnails.THUMB_DATA_VIEW_PANEL_ITEM_ID,
          bodyBorder: false,
          scrollable: true,
          region: "center",
          ui: PanelSkin.EMBEDDED.getSkin(),
          layout: "anchor",
          header: Config(PanelHeader, { height: "24px" }),
          items: [
            Config(CatalogThumbDataView, {
              itemId: CatalogSearchThumbnails.THUMB_DATA_VIEW_ITEM_ID,
              bindTo: config.searchResultHitsValueExpression,
              emptyText: Editor_properties.CollectionView_emptySearch_text,
              selectedItemsValueExpression: config.selectedItemsValueExpression,
              initialViewLimit: 50,
              viewLimitIncrement: 100,
              ...ConfigUtils.append({
                plugins: [
                  Config(ContextMenuPlugin, { contextMenu: Config(CatalogSearchContextMenu, { selectedSearchItemsValueExpression: config.selectedItemsValueExpression }) }),
                ],
              }),
            }),
          ],
        }),
      ],

    }), config));
  }

  selectedItemsValueExpression: ValueExpression = null;

  searchResultHitsValueExpression: ValueExpression = null;
}

export default CatalogSearchThumbnails;
