import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import CheckboxGroup from "@jangaroo/ext-ts/form/CheckboxGroup";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import CatalogPreferencesBase from "./CatalogPreferencesBase";

interface CatalogPreferencesConfig extends Config<CatalogPreferencesBase> {
}

class CatalogPreferences extends CatalogPreferencesBase {
  declare Config: CatalogPreferencesConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogPreferences";

  static readonly CATALOG_PREFERENCES_ITEM_ID: string = "contentCatalogPreferences";

  constructor(config: Config<CatalogPreferences> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogPreferences, {
      title: ECommerceStudioPlugin_properties.EcCatalogPreferences_tab_title,
      itemId: CatalogPreferences.CATALOG_PREFERENCES_ITEM_ID,

      items: [
        Config(CheckboxGroup, {
          columns: 1,
          fieldLabel: ECommerceStudioPlugin_properties.EcCatalogPreferences_catalog_title,
          items: [
            Config(Checkbox, {
              itemId: "catalogSettingsGroup",
              boxLabel: ECommerceStudioPlugin_properties.EcCatalogPreferences_show_catalog_items,
              plugins: [
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  ifUndefined: "false",
                  bindTo: this.getShowCatalogValueExpression(),
                }),
              ],
            }),
            Config(Checkbox, {
              itemId: "sortChildrenAndDisableLazyLoading",
              boxLabel: ECommerceStudioPlugin_properties.EcCatalogPreferences_sort_children,
              plugins: [
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  ifUndefined: "false",
                  bindTo: this.getSortCategoriesByNameExpression(),
                }),
              ],
            }),
          ],
        }),
      ],
      layout: Config(AnchorLayout),
    }), config))());
  }
}

export default CatalogPreferences;
