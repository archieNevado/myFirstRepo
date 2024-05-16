import FacetUtil from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/filters/FacetUtil";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import CategoryFacetTagFieldBase from "./CategoryFacetTagFieldBase";
import CategoryFacetsPropertyFieldBase from "./CategoryFacetsPropertyFieldBase";

interface CategoryFacetTagFieldConfig extends Config<CategoryFacetTagFieldBase> {
}

class CategoryFacetTagField extends CategoryFacetTagFieldBase {
  declare Config: CategoryFacetTagFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.categoryFacetTagField";

  constructor(config: Config<CategoryFacetTagField> = null) {
    super(ConfigUtils.apply(Config(CategoryFacetTagField, {
      fieldLabel: FacetUtil.localizeFacetLabel(config.facet.getLabel()),
      emptyText: LivecontextStudioPlugin_properties.CategoryFacetTagField_emptyText,
      labelAlign: "top",
      labelSeparator: "",
      filterPickList: true,
      valueField: CategoryFacetTagFieldBase.QUERY,
      displayField: CategoryFacetTagFieldBase.LABEL,
      queryMode: "local",
      triggerAction: "all",
      publishes: CategoryFacetTagFieldBase.LABEL,
      multiSelect: config.facet.isMultiSelect(),
      ariaRole: "textbox",
      forceSelection: true,
      anchor: "100%",

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: ValueExpressionFactory.createFromValue(config.facet.getValues()),
            ifUndefined: [],
            sortDirection: "ASC",
            sortField: CategoryFacetTagFieldBase.LABEL,
            fields: [
              Config(DataField, {
                name: CategoryFacetTagFieldBase.LABEL,
                encode: false,
              }),
              Config(DataField, {
                name: CategoryFacetTagFieldBase.QUERY,
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

    }), config));
  }
}

export default CategoryFacetTagField;
