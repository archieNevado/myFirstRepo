import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { as, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import sitesService from "@coremedia/studio-client.multi-site-models/global/sitesService";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import StringUtil from "@coremedia/studio-client.client-core/util/StringUtil";
import localesService from "@coremedia/studio-client.cap-base-models/locale/localesService";
import TaxonomyL10nUtil from "./TaxonomyL10nUtil";

interface TaxonomyTranslationFieldsBaseConfig extends Config<PropertyFieldGroup> {
}

class TaxonomyTranslationFieldsBase extends PropertyFieldGroup {
  declare Config: TaxonomyTranslationFieldsBaseConfig;

  #visibilityExpr: ValueExpression<boolean>;

  constructor(config: Config<TaxonomyTranslationFieldsBase> = null) {
    super(config);
  }

  protected override afterRender(): void {
    super.afterRender();
    this.bindTo.addChangeListener(bind(this, this.#contentChanged));
    this.#contentChanged(this.bindTo);
  }

  protected override onDestroy(): void {
    this.bindTo.removeChangeListener(bind(this, this.#contentChanged));
    super.onDestroy();
  }

  #contentChanged(ve: ValueExpression): void {
    ValueExpressionFactory.createFromFunction((): string => {
      const content = ve.getValue();
      const site = sitesService._.getSiteFor(content);
      if (site === undefined) {
        return undefined;
      }

      if (!!site) {
        return null;
      }

      return TaxonomyL10nUtil.getDefaultLanguageExpression().getValue();
    }).loadValue((value): void => {
      if (value) {
        const availableLocalesExpression = localesService.getAvailableLocalesExpression();
        const locales = as(availableLocalesExpression.getValue(), Object);
        if (locales[value]) {
          value = locales[value].getDisplayName();
        } else {
          Logger.warn("Unable to resolve default taxonomy localization label for language tag \"" + value + "\", using nearest locale.");
          for (const l in locales) {
            const locale = locales[l];
            if (locale.getLanguage() === value) {
              value = locale.getDisplayName();
              break;
            }
          }
        }

        const documentFormTab = cast(DocumentForm, this.findParentByType(DocumentForm.xtype));
        const propertyFieldGroup = cast(Panel, documentFormTab.items.get(0));
        const msg = StringUtil.format(TaxonomyStudioPlugin_properties.Taxonomy_l10n_title, value);
        propertyFieldGroup.setTitle(msg);
      }
    });
  }

  protected getVisibilityExpression(config?: TaxonomyTranslationFieldsBaseConfig): ValueExpression {
    if (!this.#visibilityExpr) {
      this.#visibilityExpr = ValueExpressionFactory.createFromFunction(() => {
        const enabled = TaxonomyL10nUtil.isL10NEnabledExpression().getValue();
        if (enabled == undefined) {
          return undefined;
        }

        if (!enabled) {
          return false;
        }

        const content = config.bindTo.getValue();
        const site = sitesService._.getSiteFor(content);
        return !site;
      });
    }
    return this.#visibilityExpr;
  }

  protected getTemplateKey(value: string): string {
    return value;
  }

  protected updateResourceBundle(): void {
    const rm = resourceManager;
    const contentTypesBundle = rm.getResourceBundle(null, ContentTypes_properties);

    TaxonomyL10nUtil.getConfiguredTranslationsExpression().loadValue((translations: Array<any>): void => {
      if (translations) {
        const availableLocalesExpression = as(editorContext._, EditorContextImpl).getLocalesService().getAvailableLocalesExpression();
        const locales = as(availableLocalesExpression.getValue(), Object);

        for (const l in locales) {
          const locale = locales[l];
          contentTypesBundle.content["CMTaxonomy_localSettings.translations." + locale.getLanguageTag() + "_text"] = locale.getDisplayName();
          contentTypesBundle.content["CMTaxonomy_localSettings.translations." + locale.getLanguageTag() + "_emptyText"] = TaxonomyStudioPlugin_properties.TaxonomyTranslationFields_emptyText;

          //always add a resource bundle key for the base language too
          if (locale.getCountry() && locale.getCountry().length > 0) {
            contentTypesBundle.content["CMTaxonomy_localSettings.translations." + locale.getLanguage() + "_text"] = locale.getDisplayName();
            contentTypesBundle.content["CMTaxonomy_localSettings.translations." + locale.getLanguage() + "_emptyText"] = TaxonomyStudioPlugin_properties.TaxonomyTranslationFields_emptyText;
          }
        }
      }
    });
  }
}

export default TaxonomyTranslationFieldsBase;
