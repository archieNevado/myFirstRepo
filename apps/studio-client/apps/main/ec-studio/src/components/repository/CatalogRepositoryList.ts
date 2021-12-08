import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import RichTextPlainTextTransformer from "@coremedia/studio-client.cap-base-models/content/RichTextPlainTextTransformer";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import GridColumns_properties from "@coremedia/studio-client.ext.cap-base-components/columns/GridColumns_properties";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import CopyCellContentToSystemClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/actions/CopyCellContentToSystemClipboardAction";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import Item from "@jangaroo/ext-ts/menu/Item";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import AugmentationUtil from "../../helper/AugmentationUtil";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogRepositoryContextMenu from "./CatalogRepositoryContextMenu";
import CatalogRepositoryListBase from "./CatalogRepositoryListBase";

interface CatalogRepositoryListConfig extends Config<CatalogRepositoryListBase>, Partial<Pick<CatalogRepositoryList,
  "mySelectedItemsValueExpression" |
  "createdContentValueExpression" |
  "newContentDisabledValueExpression"
>> {
}

class CatalogRepositoryList extends CatalogRepositoryListBase {
  declare Config: CatalogRepositoryListConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryList";

  /**
   * The itemId of the copy to system clipboard menu item.
   */
  static readonly COPY_COLUMN_CONTENT_TO_SYSTEM_CLIPBOARD: string = "copyCellContentToSystemClipboard";

  /**
   * value expression for the selected items, either in the list view, or - if the selection there is empty - the
   * selected folder in the tree view.
   */
  mySelectedItemsValueExpression: ValueExpression = null;

  #catalogHelper: CatalogHelper = null;

  /**
   * value expression that acts as a model for informing a view of a newly created content object.
   */
  createdContentValueExpression: ValueExpression = null;

  /**
   * Value expression that indicates if a new content can be created based on the current selection in the collection view.
   */
  newContentDisabledValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryList> = null) {
    super((()=>{
      this.#catalogHelper = CatalogHelper.getInstance();
      return ConfigUtils.apply(Config(CatalogRepositoryList, {
        emptyText: ECommerceStudioPlugin_properties.CatalogView_empty_text,
        header: false,
        ddGroup: "ContentDD",

        ...ConfigUtils.prepend({
          plugins: [
            Config(BindListPlugin, {
              bindTo: this.getCatalogItemsValueExpression(),
              lazy: true,
              fields: [
                Config(DataField, {
                  name: "type",
                  mapping: "",
                  convert: AugmentationUtil.getTypeLabel,
                }),
                Config(DataField, {
                  name: "typeCls",
                  mapping: "",
                  convert: AugmentationUtil.getIconFunctionWithLink(config.selectedNodeValueExpression),
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
              lastClickedCellVE: this.getLastClickedCellVE(),
              contextMenu: Config(CatalogRepositoryContextMenu, {
                selectedItemsValueExpression: config.mySelectedItemsValueExpression,
                selectedFolderValueExpression: config.selectedNodeValueExpression,
                newContentDisabledValueExpression: config.newContentDisabledValueExpression,
                createdContentValueExpression: config.createdContentValueExpression,
                ...ConfigUtils.append({
                  plugins: [
                    Config(AddItemsPlugin, {
                      items: [
                        Config(Item, {
                          itemId: CatalogRepositoryList.COPY_COLUMN_CONTENT_TO_SYSTEM_CLIPBOARD,
                          baseAction: new CopyCellContentToSystemClipboardAction({
                            lastClickedCellVE: this.getLastClickedCellVE(),
                            selectedItemsValueExpression: config.mySelectedItemsValueExpression,
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
}

export default CatalogRepositoryList;
