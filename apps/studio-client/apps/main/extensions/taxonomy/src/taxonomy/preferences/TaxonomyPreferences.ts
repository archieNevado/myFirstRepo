import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import BoundListView from "@jangaroo/ext-ts/view/BoundList";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyPreferencesBase from "./TaxonomyPreferencesBase";

interface TaxonomyPreferencesConfig extends Config<TaxonomyPreferencesBase> {
}

class TaxonomyPreferences extends TaxonomyPreferencesBase {
  declare Config: TaxonomyPreferencesConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyPreferences";

  constructor(config: Config<TaxonomyPreferences> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyPreferences, {
      title: TaxonomyStudioPlugin_properties.TaxonomyPreferences_tab_title,

      items: [
        Config(FieldContainer, {
          fieldLabel: TaxonomyStudioPlugin_properties.TaxonomyPreferences_option_name,
          items: [
            Config(LocalComboBox, {
              encodeItems: true,
              valueField: "value",
              hideEmptyLabel: true,
              displayField: "name",
              store: this.getStore(),
              flex: 1,
              helpIconText: TaxonomyStudioPlugin_properties.TaxonomyPreferences_settings_tooltip,
              ...ConfigUtils.append({
                plugins: [
                  Config(BindPropertyPlugin, {
                    componentEvent: "change",
                    componentProperty: "value",
                    bindTo: this.getSuggestionTypesValueExpression(),
                    bidirectional: true,
                  }),
                ],
              }),
              listConfig: Config(BoundListView),
            }),
          ],
          layout: Config(HBoxLayout, { align: "middle" }),
        }),
      ],
      layout: Config(AnchorLayout),
    }), config))());
  }
}

export default TaxonomyPreferences;
