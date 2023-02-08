import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LocalizedStringPropertyField from "./LocalizedStringPropertyField";
import TaxonomyTranslationFieldsBase from "./TaxonomyTranslationFieldsBase";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import TaxonomyL10nUtil from "./TaxonomyL10nUtil";

interface TaxonomyTranslationFieldsConfig extends Config<TaxonomyTranslationFieldsBase> {
}

class TaxonomyTranslationFields extends TaxonomyTranslationFieldsBase {
  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyTranslationFields";

  declare Config: TaxonomyTranslationFieldsConfig;

  constructor(config: Config<TaxonomyTranslationFields> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#__initialize__(config);
    super(ConfigUtils.apply(Config(TaxonomyTranslationFields, {
      title: TaxonomyStudioPlugin_properties.TaxonomyTranslationFields_title,
      itemId: "translations",
      plugins: [
        Config(BindComponentsPlugin, {
          configBeanParameterName: "lang",
          clearBeforeUpdate: false,
          reuseComponents: false,
          valueExpression: TaxonomyL10nUtil.getConfiguredTranslationsExpression(),
          getKey: bind(this$, this$.getTemplateKey),
          template: Config(LocalizedStringPropertyField, {}),
        }),
        Config(BindVisibilityPlugin, {
          bindTo: this$.getVisibilityExpression(config),
        }),
      ],
    }), config));
  }

  // called by generated constructor code
  #__initialize__(config: Config<TaxonomyTranslationFields>): void {
    this.updateResourceBundle();
  }
}

export default TaxonomyTranslationFields;
