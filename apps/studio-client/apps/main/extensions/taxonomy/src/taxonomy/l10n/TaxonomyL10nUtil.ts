import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StudioConfigurationUtil
  from "@coremedia/studio-client.ext.cap-base-components/util/config/StudioConfigurationUtil";

export default class TaxonomyL10nUtil {
  static readonly #TAXONOMY_SETTINGS: string = "TaxonomySettings";

  static readonly #TRANSLATIONS: string = "translations";

  static readonly #DEFAULT_LOCALE: string = "defaultLocale";

  static #l10nEnabledExpr: ValueExpression<boolean>;
  static #defaultLanguageExpr: ValueExpression<string>;
  static #configuredTranslationsExpr: ValueExpression<string>;

  public static isL10NEnabledExpression(): ValueExpression {
    if (!this.#l10nEnabledExpr) {
      this.#l10nEnabledExpr = ValueExpressionFactory.createFromFunction(() => {
        const languages = this.getConfiguredTranslationsExpression().getValue();
        if (languages == undefined) {
          return undefined;
        }

        if (languages.length === 0) {
          return false;
        }

        return this.getDefaultLanguageExpression().getValue();
      });
    }
    return this.#l10nEnabledExpr;
  }

  public static getConfiguredTranslationsExpression(): ValueExpression {
    if (!this.#configuredTranslationsExpr) {
      this.#configuredTranslationsExpr = ValueExpressionFactory.createFromFunction(() => {
        const translations = StudioConfigurationUtil.getConfiguration(TaxonomyL10nUtil.#TAXONOMY_SETTINGS, TaxonomyL10nUtil.#TRANSLATIONS);
        if (translations === undefined) {
          return undefined;
        }

        const defaultTranslation = this.getDefaultLanguageExpression().getValue();
        if (!defaultTranslation) {
          return [];
        }

        return translations.filter((language) => {
          return language !== defaultTranslation;
        });
      });
    }
    return this.#configuredTranslationsExpr;
  }

  public static getDefaultLanguageExpression(): ValueExpression {
    if (!this.#defaultLanguageExpr) {
      this.#defaultLanguageExpr = ValueExpressionFactory.createFromFunction(
        StudioConfigurationUtil.getConfiguration,
        TaxonomyL10nUtil.#TAXONOMY_SETTINGS,
        TaxonomyL10nUtil.#DEFAULT_LOCALE);
    }
    return this.#defaultLanguageExpr;
  }
}
