import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import FacetUtil from "./FacetUtil";
import FacetsChooserBase from "./FacetsChooserBase";

interface FacetsChooserConfig extends Config<FacetsChooserBase> {
}

class FacetsChooser extends FacetsChooserBase {
  declare Config: FacetsChooserConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.components.search.filters.facetsChooser";

  constructor(config: Config<FacetsChooser> = null) {
    super((()=> ConfigUtils.apply(Config(FacetsChooser, {
      emptyText: ECommerceStudioPlugin_properties.CollectionView_search_filter_combo_emptyText,
      allowBlank: true,
      valueField: "key",
      displayField: "label",
      encodeItems: true,
      forceSelection: true,
      stateful: true,
      editable: true,
      ariaAttributes: { "aria-required": "false" },
      stateEvents: ["select"],

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: this.getFacetListExpression(config),
            fields: [
              Config(DataField, {
                name: "key",
                encode: false,
              }),
              Config(DataField, {
                name: "label",
                encode: false,
                convert: FacetUtil.localizeFacetLabel,
              }),
            ],
          }),
          Config(BindPropertyPlugin, {
            componentProperty: "disabled",
            bindTo: this.getDisabledExpression(config),
          }),
          Config(BindPropertyPlugin, {
            componentProperty: "emptyText",
            bindTo: this.getEmptyTextExpression(config),
          }),
        ],
      }),

    }), config))());
  }
}

export default FacetsChooser;
