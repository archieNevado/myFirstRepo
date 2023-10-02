import Column from "@jangaroo/ext-ts/grid/column/Column";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogStudioPlugin_properties from "./CatalogStudioPlugin_properties";

interface ProductCodeColumnConfig extends Config<Column> {
}

/**
 *
 * A column object that displays the name of a content.
 * This column expects that a corresponding <code>name</code> field is defined.
 * If a <code>nameClass</code> field is defined, it is added as CSS class to the td element of the grid cell.
 *
 */

/* annoying miscalculate width bug in ExtJS */
class ProductCodeColumn extends Column {
  declare Config: ProductCodeColumnConfig;

  static readonly DATA_INDEX: string = "productCode";

  constructor(config: Config<ProductCodeColumn> = null) {
    super(ConfigUtils.apply(Config(ProductCodeColumn, {
      header: CatalogStudioPlugin_properties.catalog_lists_product_code_column,
      stateId: "productCode",
      dataIndex: ProductCodeColumn.DATA_INDEX,

    }), config));
  }
}

export default ProductCodeColumn;
