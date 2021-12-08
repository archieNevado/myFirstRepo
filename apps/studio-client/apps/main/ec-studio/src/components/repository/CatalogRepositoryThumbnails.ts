import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import PanelHeader from "@jangaroo/ext-ts/panel/Header";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import CatalogThumbDataView from "../thumbnail/CatalogThumbDataView";
import CatalogRepositoryContextMenu from "./CatalogRepositoryContextMenu";
import CatalogRepositoryThumbnailsBase from "./CatalogRepositoryThumbnailsBase";

interface CatalogRepositoryThumbnailsConfig extends Config<CatalogRepositoryThumbnailsBase>, Partial<Pick<CatalogRepositoryThumbnails,
  "createdContentValueExpression" |
  "newContentDisabledValueExpression"
>> {
}

class CatalogRepositoryThumbnails extends CatalogRepositoryThumbnailsBase {
  declare Config: CatalogRepositoryThumbnailsConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryThumbnails";

  static readonly THUMB_DATA_VIEW_ITEM_ID: string = "thumbdataview";

  /**
   * The itemId of the thumb data view panel.
   */
  static readonly THUMB_DATA_VIEW_PANEL_ITEM_ID: string = "thumbdataviewscroller";

  /**
   * value expression that acts as a model for informing a view of a newly created content object.
   */
  createdContentValueExpression: ValueExpression = null;

  /**
   * Value expression that indicates if a new content can be created based on the current selection in the collection view.
   */
  newContentDisabledValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryThumbnails> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogRepositoryThumbnails, {
      layout: "border",

      items: [
        Config(Panel, {
          itemId: CatalogRepositoryThumbnails.THUMB_DATA_VIEW_PANEL_ITEM_ID,
          bodyBorder: false,
          scrollable: true,
          region: "center",
          ui: PanelSkin.EMBEDDED.getSkin(),
          layout: "anchor",
          listeners: { afterrender: bind(this, this.disableBrowserContextMenu) },
          header: Config(PanelHeader, { height: "24px" }),
          items: [
            Config(CatalogThumbDataView, {
              itemId: CatalogRepositoryThumbnails.THUMB_DATA_VIEW_ITEM_ID,
              bindTo: this.getCatalogItemsValueExpression(),
              emptyText: ECommerceStudioPlugin_properties.CatalogView_empty_text,
              selectedItemsValueExpression: config.selectedItemsValueExpression,
              initialViewLimit: 50,
              viewLimitIncrement: 100,
              ...ConfigUtils.append({
                plugins: [
                  Config(ContextMenuPlugin, {
                    contextMenu: Config(CatalogRepositoryContextMenu, {
                      selectedItemsValueExpression: config.selectedItemsValueExpression,
                      selectedFolderValueExpression: config.selectedFolderValueExpression,
                      newContentDisabledValueExpression: config.newContentDisabledValueExpression,
                      createdContentValueExpression: config.createdContentValueExpression,
                    }),
                  }),
                ],
              }),
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default CatalogRepositoryThumbnails;
