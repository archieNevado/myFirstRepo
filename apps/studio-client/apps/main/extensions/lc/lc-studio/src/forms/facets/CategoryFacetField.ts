import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CategoryFacetFieldBase from "./CategoryFacetFieldBase";

interface CategoryFacetFieldConfig extends Config<CategoryFacetFieldBase> {
}

class CategoryFacetField extends CategoryFacetFieldBase {
  declare Config: CategoryFacetFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.categoryFacetField";

  constructor(config: Config<CategoryFacetField> = null) {
    super(ConfigUtils.apply(Config(CategoryFacetField, {
      layout: "anchor",

      items: [
        /* Single or multi facet editor will be added here  */
      ],

    }), config));
  }
}

export default CategoryFacetField;
