import FacetUtil from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/filters/FacetUtil";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import CategoryFacetComboFieldBase from "./CategoryFacetComboFieldBase";
import CategoryFacetsPropertyFieldBase from "./CategoryFacetsPropertyFieldBase";

interface CategoryFacetComboFieldConfig extends Config<CategoryFacetComboFieldBase> {
}

class CategoryFacetComboField extends CategoryFacetComboFieldBase {
  declare Config: CategoryFacetComboFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.categoryFacetComboField";

  constructor(config: Config<CategoryFacetComboField> = null) {
    super((()=> ConfigUtils.apply(Config(CategoryFacetComboField, {
      fieldLabel: FacetUtil.localizeFacetLabel(config.facet.getLabel()),
      emptyText: LivecontextStudioPlugin_properties["CMProductList_localSettings.productList.facetValue.emptyText"],
      labelAlign: "top",
      labelSeparator: "",
      valueField: CategoryFacetComboFieldBase.QUERY,
      displayField: CategoryFacetComboFieldBase.LABEL,
      anchor: "100%",

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: this.getComboValuesExpression(config),
            ifUndefined: [],
            sortDirection: "ASC",
            sortField: CategoryFacetComboFieldBase.LABEL,
            fields: [
              Config(DataField, {
                name: CategoryFacetComboFieldBase.LABEL,
                encode: false,
              }),
              Config(DataField, {
                name: CategoryFacetComboFieldBase.QUERY,
                encode: false,
              }),
            ],
          }),
          Config(ShowIssuesPlugin, {
            bindTo: config.bindTo,
            ifUndefined: "",
            propertyName: config.structPropertyName + "." + CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME + "." + CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME + "." + config.facet.getKey(),
          }),
          Config(BindReadOnlyPlugin, {
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            bindTo: config.bindTo,
          }),
          Config(PropertyFieldPlugin, { propertyName: config.structPropertyName + "." + CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME + "." + CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME + "." + config.facet.getKey() }),
        ],
      }),
    }), config))());
  }
}

export default CategoryFacetComboField;
