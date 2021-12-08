import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import FacetComboFieldBase from "./FacetComboFieldBase";

interface FacetComboFieldConfig extends Config<FacetComboFieldBase> {
}

/**
 * @public
 */
class FacetComboField extends FacetComboFieldBase {
  declare Config: FacetComboFieldConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.components.search.filters.facetComboField";

  constructor(config: Config<FacetComboField> = null) {
    super((()=> ConfigUtils.apply(Config(FacetComboField, {
      emptyText: ECommerceStudioPlugin_properties.CollectionView_search_filter_empty_facet_text,
      labelSeparator: "",
      itemId: "facetComboField",
      valueField: FacetComboFieldBase.QUERY,
      displayField: FacetComboFieldBase.LABEL,
      queryMode: "local",
      triggerAction: "all",
      publishes: FacetComboFieldBase.LABEL,
      forceSelection: true,
      anchor: "100%",

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: this.getComboValuesExpression(config),
            ifUndefined: null,
            sortDirection: "ASC",
            sortField: FacetComboFieldBase.LABEL,
            fields: [
              Config(DataField, {
                name: FacetComboFieldBase.LABEL,
                encode: false,
              }),
              Config(DataField, {
                name: FacetComboFieldBase.QUERY,
                encode: false,
              }),
            ],
          }),
        ],
      }),
    }), config))());
  }
}

export default FacetComboField;
