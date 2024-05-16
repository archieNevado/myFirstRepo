import PreferencesUtil from "@coremedia/studio-client.cap-base-models/preferences/PreferencesUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import PreferencePanel from "@coremedia/studio-client.ext.frame-components/preferences/PreferencePanel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogPreferences from "./CatalogPreferences";

interface CatalogPreferencesBaseConfig extends Config<Panel> {
}

class CatalogPreferencesBase extends Panel implements PreferencePanel {
  declare Config: CatalogPreferencesBaseConfig;

  static PREFERENCE_SHOW_CATALOG_KEY: string = "showCatalogContent";

  static SORT_CATEGORIES_BY_NAME_KEY: string = "sortCategoriesByName";

  showCatalogValueExpression: ValueExpression<boolean> = null;

  sortCategoriesByNameExpression: ValueExpression<string> = null;

  constructor(config: Config<CatalogPreferences> = null) {
    super(config);
  }

  protected getShowCatalogValueExpression(): ValueExpression {
    if (!this.showCatalogValueExpression) {
      const enabled: boolean = ValueExpressionFactory.create<boolean>(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext._.getPreferences()).getValue();
      this.showCatalogValueExpression = ValueExpressionFactory.createFromValue(enabled);
    }
    return this.showCatalogValueExpression;
  }

  protected getSortCategoriesByNameExpression(): ValueExpression {
    if (!this.sortCategoriesByNameExpression) {
      const enabled: string = ValueExpressionFactory.create<string>(CatalogPreferencesBase.SORT_CATEGORIES_BY_NAME_KEY, editorContext._.getPreferences()).getValue();
      this.sortCategoriesByNameExpression = ValueExpressionFactory.createFromValue(enabled);
    }
    return this.sortCategoriesByNameExpression;
  }

  updatePreferences(): void {
    const showCatalogValue: boolean = this.getShowCatalogValueExpression().getValue();
    const sortCategoriesByNameValue: boolean = this.getSortCategoriesByNameExpression().getValue();
    PreferencesUtil.updatePreferencesJSONProperty(showCatalogValue, CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY);
    PreferencesUtil.updatePreferencesJSONProperty(sortCategoriesByNameValue, CatalogPreferencesBase.SORT_CATEGORIES_BY_NAME_KEY);
  }
}
mixin(CatalogPreferencesBase, PreferencePanel);

export default CatalogPreferencesBase;
