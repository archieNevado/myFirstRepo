import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyPropertyField from "./TaxonomyPropertyField";

interface TaxonomyPropertyFieldBaseConfig extends Config<FieldContainer>, Config<HidableMixin>, Partial<Pick<TaxonomyPropertyFieldBase,
  "hideText"
>> {
}

/**
 * Base class for the taxonomy property editor.
 * The class is used to disable the suggestion panel if they are not required.
 */
class TaxonomyPropertyFieldBase extends FieldContainer {
  declare Config: TaxonomyPropertyFieldBaseConfig;

  #disableSuggestions: boolean = false;

  constructor(config: Config<TaxonomyPropertyField> = null) {
    super((()=>{
      this.#disableSuggestions = config.disableSuggestions;
      return config;
    })());
  }

  protected override initComponent(): void {
    super.initComponent();
    if (this.#disableSuggestions) {
      this.queryById("suggestionsPanel").hide();
    }
  }

  protected getTaxonomyIdExpr(config: Config<TaxonomyPropertyField>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      if (config.taxonomyId) {
        return config.taxonomyId;
      }

      return config.taxonomyIdExpression.getValue();
    });
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }
}

interface TaxonomyPropertyFieldBase extends HidableMixin{}

mixin(TaxonomyPropertyFieldBase, HidableMixin);

export default TaxonomyPropertyFieldBase;
