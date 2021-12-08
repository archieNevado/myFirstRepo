import IconWithTextBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/IconWithTextBEMEntities";
import IconColumnBase from "@coremedia/studio-client.ext.ui-components/grid/column/IconColumnBase";
import BEMModifier from "@coremedia/studio-client.ext.ui-components/models/bem/BEMModifier";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import DataView from "@jangaroo/ext-ts/view/View";
import Config from "@jangaroo/runtime/Config";

interface CatalogLinkListColumnBaseConfig extends Config<IconColumnBase>, Partial<Pick<CatalogLinkListColumnBase,
  "catalogIconCls" |
  "catalogIconText" |
  "catalogToolTipText" |
  "hideCatalog" |
  "catalogObjectIdDataIndex" |
  "catalogObjectNameDataIndex" |
  "catalogNameDataIndex" |
  "multiCatalogDataIndex"
>> {
}

class CatalogLinkListColumnBase extends IconColumnBase {
  declare Config: CatalogLinkListColumnBaseConfig;

  /**
   * The icon css class to use for the catalog icon.
   */
  catalogIconCls: string = null;

  /**
   * An additional text describing the catalog icon.
   */
  catalogIconText: string = null;

  /**
   * A tooltip to display when hoving the catalog block of the column.
   */
  catalogToolTipText: string = null;

  /**
   * If true the catalog info will be hidden. Default is false.
   */
  hideCatalog: boolean = false;

  constructor(config: Config<CatalogLinkListColumnBase> = null) {
    super(config);
  }

  /**
   * The dataIndex of the catalog object id.
   *
   * @see ext.grid.column.Column.dataIndex
   */
  catalogObjectIdDataIndex: string = null;

  /**
   * The dataIndex of the catalog object name.
   *
   * @see ext.grid.column.Column.dataIndex
   */
  catalogObjectNameDataIndex: string = null;

  /**
   * The dataIndex of the catalog name.
   *
   * @see ext.grid.column.Column.dataIndex
   */
  catalogNameDataIndex: string = null;

  multiCatalogDataIndex: string = null;

  static readonly #MODIFIER_SECOND_ITEM: BEMModifier = IconWithTextBEMEntities.BLOCK.createModifier("second-item");

  protected override getRenderer(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store, view: DataView): string {
    return this.tpl.apply({
      modifiers: this.getModifierCls(this.calculateModifier(value, metadata, record, rowIndex, colIndex, store)),
      iconCls: this.calculateIconCls(value, metadata, record, rowIndex, colIndex, store) || "",
      iconText: this.calculateIconText(value, metadata, record, rowIndex, colIndex, store) || "",
      toolTipText: this.calculateToolTipText(value, metadata, record, rowIndex, colIndex, store) || "",
      catalogIconCls: this.catalogIconCls || "",
      catalogIconText: this.catalogIconText || "",
      catalogToolTipText: this.catalogToolTipText || "",
      catalogObjectId: record.get(this.catalogObjectIdDataIndex),
      catalogObjectName: record.get(this.catalogObjectNameDataIndex),
      catalog: !this.hideCatalog && record.get(this.catalogNameDataIndex),
      multiCatalog: record.get(this.multiCatalogDataIndex),
    });
  }

  protected static override getXTemplate(): XTemplate {
    const xTemplate = new XTemplate([
      "<div aria-label=\"{iconText:escape}\" class=\"" + IconWithTextBEMEntities.BLOCK + " {modifiers:escape}\" {toolTipText:unsafeQtip}>",
      "  <span class=\"" + IconWithTextBEMEntities.ELEMENT_ICON + " {iconCls:escape}\"></span>",
      "  <span style=\"width: 0px;position:absolute;overflow:hidden;\">{iconText:escape}</span>",
      "  <span class=\"" + IconWithTextBEMEntities.ELEMENT_TEXT + "\">{catalogObjectId} <tpl if=\"catalogObjectId !== catalogObjectName\">({catalogObjectName})</tpl></span>",
      "</div>",
      "<tpl if=\"catalog && multiCatalog\"><div aria-label=\"{catalogIconText:escape}\" class=\"" + IconWithTextBEMEntities.BLOCK + " " + CatalogLinkListColumnBase.#MODIFIER_SECOND_ITEM + "\" {catalogToolTipText:unsafeQtip}>",
      "  <span class=\"" + IconWithTextBEMEntities.ELEMENT_ICON + " {catalogIconCls:escape}\"></span>",
      "  <span style=\"width: 0px;position:absolute;overflow:hidden;\">{catalogIconText:escape}</span>",
      "  <span class=\"" + IconWithTextBEMEntities.ELEMENT_TEXT + "\">{catalog}</span>",
      "</div></tpl>",
    ]);
    return xTemplate;
  }
}

export default CatalogLinkListColumnBase;
