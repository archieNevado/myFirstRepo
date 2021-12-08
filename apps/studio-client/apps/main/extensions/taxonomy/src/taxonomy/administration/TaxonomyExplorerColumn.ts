import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import TableViewSkin from "@coremedia/studio-client.ext.ui-components/skins/TableViewSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import GridViewDragDropPlugin from "@jangaroo/ext-ts/grid/plugin/DragDrop";
import Item from "@jangaroo/ext-ts/menu/Item";
import Menu from "@jangaroo/ext-ts/menu/Menu";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import TableView from "@jangaroo/ext-ts/view/Table";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import CutKeywordAction from "../action/CutKeywordAction";
import DeleteKeywordAction from "../action/DeleteKeywordAction";
import PasteKeywordAction from "../action/PasteKeywordAction";
import TaxonomyExplorerColumnBase from "./TaxonomyExplorerColumnBase";

interface TaxonomyExplorerColumnConfig extends Config<TaxonomyExplorerColumnBase>, Partial<Pick<TaxonomyExplorerColumn,
  "siteSelectionExpression" |
  "clipboardValueExpression" |
  "selectedNodeExpression" |
  "parentNode"
>> {
}

class TaxonomyExplorerColumn extends TaxonomyExplorerColumnBase {
  declare Config: TaxonomyExplorerColumnConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyExplorerColumn";

  constructor(config: Config<TaxonomyExplorerColumn> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyExplorerColumn, {
      hideHeaders: true,
      forceFit: true,

      plugins: [
        Config(BindListPlugin, {
          bindTo: this.getEntriesValueExpression(),
          fields: [
            Config(DataField, { name: "name" }),
            Config(DataField, { name: "siteId" }),
            Config(DataField, { name: "type" }),
            Config(DataField, { name: "ref" }),
            Config(DataField, { name: "level" }),
            Config(DataField, { name: "root" }),
            Config(DataField, { name: "leaf" }),
            Config(DataField, { name: "taxonomyId" }),
            Config(DataField, { name: "selectable" }),
            Config(DataField, { name: "extendable" }),
          ],
        }),
        Config(ContextMenuPlugin, {
          disableDoubleClick: true,
          contextMenu: Config(Menu, {
            items: [
              Config(Item, {
                itemId: "cutNode",
                iconCls: CoreIcons_properties.cut,
                baseAction: new CutKeywordAction({
                  clipboardValueExpression: config.clipboardValueExpression,
                  selectionExpression: config.selectedNodeExpression,
                }),
              }),
              Config(Item, {
                itemId: "pasteNode",
                iconCls: CoreIcons_properties.paste,
                baseAction: new PasteKeywordAction({
                  clipboardValueExpression: config.clipboardValueExpression,
                  selectionExpression: config.selectedNodeExpression,
                }),
              }),
              Config(Separator),
              Config(Item, {
                itemId: "deleteNode",
                iconCls: CoreIcons_properties.remove,
                baseAction: new DeleteKeywordAction({ selectionExpression: config.selectedNodeExpression }),
              }),
            ],
          }),
        }),
      ],
      columns: [
        Config(Column, {
          stateId: "name",
          flex: 1,
          sortable: false,
          fixed: true,
          dataIndex: "name",
          renderer: bind(this, this.nameColRenderer),
        }),
        Config(Column, {
          stateId: "leaf",
          width: 21,
          sortable: false,
          fixed: true,
          dataIndex: "leaf",
          renderer: bind(this, this.pointerColRenderer),
        }),
      ],
      selModel: new RowSelectionModel({ mode: "MULTI" }),
      viewConfig: Config(TableView, {
        deferEmptyText: false,
        emptyText: TaxonomyStudioPlugin_properties.TaxonomyExplorerColumn_emptyText_loading,
        ui: TableViewSkin.LIGHT.getSkin(),
        plugins: [
          Config(GridViewDragDropPlugin, {
            ddGroup: "taxonomies",
            enableDrag: config.parentNode !== undefined,
            enableDrop: config.parentNode !== undefined,
            pluginId: TaxonomyExplorerColumnBase.DRAG_DROP_PLUGIN_ID,
          }),
        ],
      }),

    }), config))());
  }

  /**
   * Contains the active selected site, selected by site chooser component.
   */
  siteSelectionExpression: ValueExpression = null;

  /**
   * The clipboard for cut and paste
   */
  clipboardValueExpression: ValueExpression = null;

  /**
   * Contains the active selected node.
   */
  selectedNodeExpression: ValueExpression = null;

  /**
   * Contains the parent node.
   */
  parentNode: TaxonomyNode = null;
}

export default TaxonomyExplorerColumn;
