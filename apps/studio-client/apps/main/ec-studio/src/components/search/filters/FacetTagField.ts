import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import FacetTagFieldBase from "./FacetTagFieldBase";

interface FacetTagFieldConfig extends Config<FacetTagFieldBase> {
}

/**
 * @public
 */
class FacetTagField extends FacetTagFieldBase {
  declare Config: FacetTagFieldConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.components.search.filters.facetTagField";

  constructor(config: Config<FacetTagField> = null) {
    super(ConfigUtils.apply(Config(FacetTagField, {
      labelSeparator: "",
      filterPickList: true,
      valueField: FacetTagFieldBase.QUERY,
      displayField: FacetTagFieldBase.LABEL,
      queryMode: "local",
      triggerAction: "all",
      publishes: FacetTagFieldBase.LABEL,
      itemId: "facetTagField",
      emptyText: ECommerceStudioPlugin_properties.CollectionView_search_filter_empty_facet_text,
      multiSelect: config.facetValueExpression.getValue().isMultiSelect(),
      ariaRole: "textbox",
      forceSelection: true,
      anchor: "100%",

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: ValueExpressionFactory.createFromValue(config.facetValueExpression.getValue().getValues()),
            ifUndefined: [],
            sortDirection: "ASC",
            sortField: FacetTagFieldBase.LABEL,
            fields: [
              Config(DataField, {
                name: FacetTagFieldBase.LABEL,
                encode: false,
              }),
              Config(DataField, {
                name: FacetTagFieldBase.QUERY,
                encode: false,
              }),
            ],
          }),
          Config(BindPropertyPlugin, {
            componentProperty: "value",
            bidirectional: true,
            bindTo: config.selectedFacetValuesExpression,
          }),
        ],
      }),
    }), config));
  }
}

export default FacetTagField;
