import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import RichTextPlainTextTransformer from "@coremedia/studio-client.cap-base-models/content/RichTextPlainTextTransformer";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import GridColumns_properties from "@coremedia/studio-client.ext.cap-base-components/columns/GridColumns_properties";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CopyCellContentToSystemClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/actions/CopyCellContentToSystemClipboardAction";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import Item from "@jangaroo/ext-ts/menu/Item";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import AugmentationUtil from "../../helper/AugmentationUtil";
import CatalogHelper from "../../helper/CatalogHelper";
import AbstractCatalogList from "../AbstractCatalogList";
import CatalogSearchContextMenu from "./CatalogSearchContextMenu";

interface CatalogSearchListConfig extends Config<AbstractCatalogList>, Partial<Pick<CatalogSearchList,
  "searchResultHitsValueExpression"
>> {
}

class CatalogSearchList extends AbstractCatalogList {
  declare Config: CatalogSearchListConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchList";

  /**
   * The itemId of the copy to system clipboard menu item.
   */
  static readonly COPY_COLUMN_CONTENT_TO_SYSTEM_CLIPBOARD: string = "copyCellContentToSystemClipboard";

  #catalogHelper: CatalogHelper = null;

  #lastClickedCellVE: ValueExpression = null;

  constructor(config: Config<CatalogSearchList> = null) {
    super((()=>{
      this.#catalogHelper = CatalogHelper.getInstance();
      this.#lastClickedCellVE = ValueExpressionFactory.createFromValue();
      return ConfigUtils.apply(Config(CatalogSearchList, {
        emptyText: Editor_properties.CollectionView_emptySearch_text,
        header: false,
        ddGroup: "ContentDD",

        ...ConfigUtils.prepend({
          plugins: [
            Config(BindListPlugin, {
              lazy: true,
              bindTo: config.searchResultHitsValueExpression,
              initialViewLimit: 50,
              viewLimitIncrement: 100,
              fields: [
                Config(DataField, {
                  name: "type",
                  mapping: "",
                  convert: AugmentationUtil.getTypeLabel,
                }),
                Config(DataField, {
                  name: "typeCls",
                  mapping: "",
                  convert: AugmentationUtil.getTypeCls,
                }),
                Config(DataField, {
                  name: "id",
                  mapping: "externalId",
                }),
                Config(DataField, {
                  name: "name",
                  mapping: "",
                  convert: bind(this.#catalogHelper, this.#catalogHelper.getDecoratedName),
                }),
                Config(DataField, {
                  name: "description",
                  mapping: "shortDescription",
                  convert: (v: string, catalogObject: CatalogObject): string =>
                    RichTextPlainTextTransformer.convertToPlainText(catalogObject.getShortDescription()),
                }),
              ],
            }),
            Config(ContextMenuPlugin, {
              lastClickedCellVE: this.#lastClickedCellVE,
              contextMenu: Config(CatalogSearchContextMenu, {
                selectedSearchItemsValueExpression: config.selectedItemsValueExpression,
                ...ConfigUtils.append({
                  plugins: [
                    Config(AddItemsPlugin, {
                      items: [
                        Config(Item, {
                          itemId: CatalogSearchList.COPY_COLUMN_CONTENT_TO_SYSTEM_CLIPBOARD,
                          baseAction: new CopyCellContentToSystemClipboardAction({
                            lastClickedCellVE: this.#lastClickedCellVE,
                            selectedItemsValueExpression: config.selectedItemsValueExpression,
                          }),
                        }),
                      ],
                    }),
                  ],
                }),
              }),
            }),
          ],
        }),
        columns: [
          Config(TypeIconColumn, {
            showTypeName: true,
            sortable: true,
            ...{ sortField: "type" },
            width: 125,
          }),
          Config(Column, {
            header: ECommerceStudioPlugin_properties.id_header,
            stateId: "id",
            dataIndex: "id",
            sortable: true,
            hideable: false,
            menuDisabled: true,
            width: 125,
          }),
          Config(Column, {
            header: GridColumns_properties.name_header,
            stateId: "name",
            dataIndex: "name",
            sortable: true,
            hideable: false,
            menuDisabled: true,
            width: 200,
          }),
          Config(Column, {
            header: ECommerceStudioPlugin_properties.description_header,
            stateId: "description",
            dataIndex: "description",
            hideable: false,
            menuDisabled: true,
            flex: 1,
          }),
        ],
      }), config);
    })());
  }

  searchResultHitsValueExpression: ValueExpression = null;
}

export default CatalogSearchList;
