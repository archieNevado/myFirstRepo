import Personalization_properties from "@coremedia/studio-client.main.cap-personalization-ui/Personalization_properties";
import AbstractConditionBase from "@coremedia/studio-client.main.cap-personalization-ui/condition/AbstractConditionBase";
import OperatorSelector from "@coremedia/studio-client.main.cap-personalization-ui/condition/OperatorSelector";
import SelectionRuleHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/SelectionRuleHelper";
import Config from "@jangaroo/runtime/Config";
import AbstractTaxonomyCondition from "./AbstractTaxonomyCondition";
import BooleanTaxonomyCondition from "./BooleanTaxonomyCondition";

interface BooleanTaxonomyConditionBaseConfig extends Config<AbstractTaxonomyCondition> {
}

/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with two possible values: 1 (taxonomy active) and
 * !1 (taxonomy not active). The keyword represents the taxonomy name. The value is set implicit by setting the operator
 * (contains =1; contains not !=1)
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.BooleanTaxonomyCondition
 */
class BooleanTaxonomyConditionBase extends AbstractTaxonomyCondition {
  declare Config: BooleanTaxonomyConditionBaseConfig;

  static readonly #NO_VALUE_OPERATORS: Array<any> = [
    SelectionRuleHelper.OP_EQUAL,
    SelectionRuleHelper.OP_NOTEQUAL,
  ];

  protected static readonly NO_VALUE_OPERATOR_DISPLAY_NAMES: Record<string, any> = {};

  static #static = (() =>{
    BooleanTaxonomyConditionBase.NO_VALUE_OPERATOR_DISPLAY_NAMES[SelectionRuleHelper.OP_EQUAL] = Personalization_properties.p13n_op_contains;
    BooleanTaxonomyConditionBase.NO_VALUE_OPERATOR_DISPLAY_NAMES[SelectionRuleHelper.OP_NOTEQUAL] = Personalization_properties.p13n_op_contains_not;
  })();

  constructor(config: Config<BooleanTaxonomyCondition> = null) {
    // create operator combo
    const operators: any = config["operators"];
    const operator: any = config.operator;
    let opSelector: OperatorSelector;

    super((()=>{
      opSelector = this.initOpSelector(null, config.operatorNames, config.operatorEmptyText, operator,
        BooleanTaxonomyConditionBase.#NO_VALUE_OPERATORS, BooleanTaxonomyConditionBase.NO_VALUE_OPERATOR_DISPLAY_NAMES);
      return config;
    })());

    this.#addOpSelector(opSelector, operators, operator);

    // the default value is 100 (which means 100% -> 1)
    config.valueText = "100";

    this.addKeywordField();
    this.addTaxonomyButton();

    // the value field needs to be added, but should be hidden
    this.addValueField(config, false);

  }

  #addOpSelector(opSelector: OperatorSelector, operators: any, operator: any): void {
    this.add(opSelector);

    const internalOperators: Array<any> = operators != null ? AbstractConditionBase.convertToInternalNames(operators) : BooleanTaxonomyConditionBase.#NO_VALUE_OPERATORS;
    this.on("afterrender", (): void => {
      opSelector.addListener("modified", (): void => {
        this.fireEvent("modified");
      });
      // set the initial operator
      opSelector.setValue(operator ? AbstractConditionBase.internalFromExternalOperatorName(operator) : internalOperators[0]);
    });
  }
}

export default BooleanTaxonomyConditionBase;
