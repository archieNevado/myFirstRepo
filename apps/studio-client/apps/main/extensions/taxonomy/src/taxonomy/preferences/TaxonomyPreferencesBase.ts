import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import PreferencePanel from "@coremedia/studio-client.ext.frame-components/preferences/PreferencePanel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ArrayStore from "@jangaroo/ext-ts/data/ArrayStore";
import Store from "@jangaroo/ext-ts/data/Store";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyStudioPlugin from "../../TaxonomyStudioPlugin";
import TaxonomyPreferences from "./TaxonomyPreferences";
import PreferenceWindowBase from "@coremedia/studio-client.ext.frame-components/preferences/PreferenceWindowBase";

interface TaxonomyPreferencesBaseConfig extends Config<Panel> {
}

class TaxonomyPreferencesBase extends Panel implements PreferencePanel {
  declare Config: TaxonomyPreferencesBaseConfig;

  static PREFERENCE_SEMANTIC_SETTINGS_KEY: string = "semanticSettings";

  #previewOptionValueExpression: ValueExpression<string>;

  static #comboStore: Array<any> = [];

  constructor(config: Config<TaxonomyPreferences> = null) {
    super(config);
  }

  static addTaggingStrategy(serviceId: string, label: string): void {
    TaxonomyPreferencesBase.#comboStore.push([label, serviceId]);
  }

  getStore(): Store {
    const arrayStore: Store = new ArrayStore(Config(ArrayStore, {
      data: this.getTaxonomyOptions(),
      fields: ["name", "value"],
    }));
    return arrayStore;
  }

  protected getSuggestionTypesValueExpression(): ValueExpression {
    if (!this.#previewOptionValueExpression) {
      this.#previewOptionValueExpression = ValueExpressionFactory.create("taxonomyOption", editorContext._.getBeanFactory().createLocalBean());
      let valueString: string = editorContext._.getPreferences().get(TaxonomyPreferencesBase.PREFERENCE_SEMANTIC_SETTINGS_KEY);
      if (!valueString) {
        valueString = TaxonomyStudioPlugin.DEFAULT_SUGGESTION_KEY;
      }
      this.#previewOptionValueExpression.setValue(valueString);
      this.#previewOptionValueExpression.addChangeListener(bind(this, this.#forceReload));
    }
    return this.#previewOptionValueExpression;
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#previewOptionValueExpression.removeChangeListener(bind(this, this.#forceReload));
  }

  getTaxonomyOptions(): Array<any> {
    return TaxonomyPreferencesBase.#comboStore;
  }

  #forceReload(): void {
    PreferenceWindowBase.getForceReloadExpression().setValue(true);
  }

  #persistOptionSelection(ve: ValueExpression): void {
    const previewOption: string = ve.getValue();
    editorContext._.getPreferences().set(TaxonomyPreferencesBase.PREFERENCE_SEMANTIC_SETTINGS_KEY, previewOption);
    editorContext._.getApplicationContext().set(TaxonomyPreferencesBase.PREFERENCE_SEMANTIC_SETTINGS_KEY, previewOption);
  }

  updatePreferences(): void {
    this.#persistOptionSelection(this.#previewOptionValueExpression);
  }
}

mixin(TaxonomyPreferencesBase, PreferencePanel);

export default TaxonomyPreferencesBase;
