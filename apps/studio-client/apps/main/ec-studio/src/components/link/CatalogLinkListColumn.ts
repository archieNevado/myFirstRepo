import GridColumns_properties from "@coremedia/studio-client.ext.cap-base-components/columns/GridColumns_properties";
import Ext from "@jangaroo/ext-ts";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import CatalogLinkListColumnBase from "./CatalogLinkListColumnBase";

interface CatalogLinkListColumnConfig extends Config<CatalogLinkListColumnBase> {
}

class CatalogLinkListColumn extends CatalogLinkListColumnBase {
  declare Config: CatalogLinkListColumnConfig;

  constructor(config: Config<CatalogLinkListColumn> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogLinkListColumn, {
      header: GridColumns_properties.type_header,
      catalogToolTipText: ECommerceStudioPlugin_properties.catalog_header,
      catalogIconText: ECommerceStudioPlugin_properties.catalog_header,
      catalogIconCls: ECommerceStudioPlugin_properties.Store_icon,
      dataIndex: "typeCls",
      stateId: "catalog",
      fixed: false,
      iconOnly: false,
      renderer: bind(this, this.getRenderer),
      tpl: CatalogLinkListColumnBase.getXTemplate(),

    }), config))());
  }

  /** @private */
  protected override calculateIconCls(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    return as(value, String);
  }

  /** @private */
  protected override calculateIconText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    const data: any = Ext.apply({}, record.data, record.getAssociatedData());
    return as(data.type, String);
  }

  /** @private */
  protected override calculateToolTipText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    return this.calculateIconText(value, metadata, record, rowIndex, colIndex, store);
  }
}

export default CatalogLinkListColumn;
