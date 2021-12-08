import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPages_properties from "../TopicPages_properties";
import TaxonomyComboBase from "./TaxonomyComboBase";

interface TaxonomyComboConfig extends Config<TaxonomyComboBase> {
}

class TaxonomyCombo extends TaxonomyComboBase {
  declare Config: TaxonomyComboConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.taxonomyCombo";

  constructor(config: Config<TaxonomyCombo> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyCombo, {
      fieldLabel: TopicPages_properties.TopicPages_taxonomy_combo_title,
      anchor: "100%",
      valueField: "id",
      emptyText: TopicPages_properties.TopicPages_taxonomy_combo_emptyText,
      displayField: "path",
      encodeItems: true,

      ...ConfigUtils.append({
        plugins: [
          Config(BindListPlugin, {
            bindTo: this.getTaxonomiesExpression(),
            fields: [
              Config(DataField, { name: "id" }),
              Config(DataField, {
                name: "path",
                encode: false,
              }),
            ],
          }),
        ],
      }),

    }), config))());
  }
}

export default TaxonomyCombo;
