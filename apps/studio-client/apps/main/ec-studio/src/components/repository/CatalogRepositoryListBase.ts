import Marketing from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Marketing";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import HeaderContainer from "@jangaroo/ext-ts/grid/header/Container";
import { bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import CatalogHelper from "../../helper/CatalogHelper";
import AbstractCatalogList from "../AbstractCatalogList";
import CatalogRepositoryList from "./CatalogRepositoryList";

interface CatalogRepositoryListBaseConfig extends Config<AbstractCatalogList>, Partial<Pick<CatalogRepositoryListBase,
  "selectedNodeValueExpression"
>> {
}

class CatalogRepositoryListBase extends AbstractCatalogList {
  declare Config: CatalogRepositoryListBaseConfig;

  /**
   * value expression for the selected node in the library tree
   */
  selectedNodeValueExpression: ValueExpression = null;

  #sortInfo: Record<string, any> = {};

  /**
   * When a user opens a context menu on a TablePanel, an object with the properties rowIndex, columnIndex,
   * record and columnDateIndex about the clicked table cell is written to the value expression.
   */
  #lastClickedCellVE: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryList> = null) {
    super(config);
    this.on("afterrender", bind(this, this.#bindStoreAndView));
    this.selectedNodeValueExpression.addChangeListener(bind(this, this.#selectionChanged));
  }

  protected override beforeDestroy(): void {
    this.selectedNodeValueExpression.removeChangeListener(bind(this, this.#selectionChanged));

    super.beforeDestroy();
  }

  #selectionChanged(): void {
    const value: RemoteBean = this.selectedNodeValueExpression.getValue();
    if (is(value, Marketing)) {
      this.getView().setEmptyText(ECommerceStudioPlugin_properties.CatalogView_spots_selection_empty_text);
    } else {
      this.getView().setEmptyText(ECommerceStudioPlugin_properties.CatalogView_empty_text);
    }
    this.getView().refresh();
  }

  #bindStoreAndView(): void {
    this.on("sortchange", bind(this, this.#sortChanged));
    this.getCatalogItemsValueExpression().addChangeListener(bind(this, this.#catalogItemsChanged));
    // TODO Ext 6 list sorters, see CMS-7895
    // getStore().setDefaultSort('id', 'ASC');
  }

  //noinspection JSUnusedLocalSymbols
  #sortChanged(headerContainer: HeaderContainer, column: Column, direction: string): void {
    this.#sortInfo.field = column.getSortParam();
    this.#sortInfo.direction = direction;
    if (this.#sortInfo.field === "name" || this.#sortInfo.direction === "DESC") {
      this.#loadCurrentBeans();
    }
  }

  #catalogItemsChanged(): void {
    if (this.#sortInfo.field === "name" || this.#sortInfo.direction === "DESC") {
      this.#loadCurrentBeans();
    }
  }

  #loadCurrentBeans(): void {
    // start loading all RemoteBeans in this view...
    // and afterwards sort the Store...
    const beans: Array<any> = this.getCatalogItemsValueExpression().getValue();
    if (beans && beans.length > 0) {
      RemoteBeanUtil.loadAll((): void =>
        EventUtil.invokeLater((): void =>
          this.getStore().sort(this.#sortInfo.field, this.#sortInfo.direction),
        )
      , beans);
    }
  }

  getCatalogItemsValueExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Array<any> =>
      CatalogHelper.getInstance().getChildren(this.selectedNodeValueExpression.getValue()),
    );
  }

  getLastClickedCellVE(): ValueExpression {
    if (!this.#lastClickedCellVE) {
      this.#lastClickedCellVE = ValueExpressionFactory.createFromValue();
    }
    return this.#lastClickedCellVE;
  }
}

export default CatalogRepositoryListBase;
