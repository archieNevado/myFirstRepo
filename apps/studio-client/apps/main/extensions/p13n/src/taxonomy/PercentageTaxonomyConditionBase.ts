import AbstractConditionBase from "@coremedia/studio-client.main.cap-personalization-ui/condition/AbstractConditionBase";
import OperatorSelector from "@coremedia/studio-client.main.cap-personalization-ui/condition/OperatorSelector";
import SelectionRuleHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/SelectionRuleHelper";
import Config from "@jangaroo/runtime/Config";
import AbstractTaxonomyCondition from "./AbstractTaxonomyCondition";
import PercentageTaxonomyCondition from "./PercentageTaxonomyCondition";

interface PercentageTaxonomyConditionBaseConfig extends Config<AbstractTaxonomyCondition> {
}

/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with values from 0 - 100. The keyword represents the
 * taxonomy name and the value field represents the percentage of that taxonomy.
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.PercentageTaxonomyCondition
 */
class PercentageTaxonomyConditionBase extends AbstractTaxonomyCondition {
  declare Config: PercentageTaxonomyConditionBaseConfig;

  static readonly #OPERATORS: Array<any> = [
    SelectionRuleHelper.OP_EQUAL,
    SelectionRuleHelper.OP_GREATER_THAN,
    SelectionRuleHelper.OP_GREATER_THAN_OR_EQUAL,
    SelectionRuleHelper.OP_LESS_THAN,
    SelectionRuleHelper.OP_LESS_THAN_OR_EQUAL,
  ];

  constructor(config: Config<PercentageTaxonomyCondition> = null) {
    // create operator combo
    const operators: any = config["operators"];
    const operator: any = config.operator;
    let opSelector: OperatorSelector;

    super((()=>{
      opSelector = this.initOpSelector(null, config.operatorNames, config.operatorEmptyText, operator,
        PercentageTaxonomyConditionBase.#OPERATORS, AbstractConditionBase.DEFAULT_OPERATOR_DISPLAY_NAMES);
      return config;
    })());

    this.addKeywordField();
    this.addTaxonomyButton();
    this.#addOpSelector(opSelector, operators, operator);
    this.addValueField(config);

  }

  #addOpSelector(opSelector: OperatorSelector, operators: any, operator: any): void {
    this.add(opSelector);

    const internalOperators: Array<any> = operators != null ? AbstractConditionBase.convertToInternalNames(operators) : PercentageTaxonomyConditionBase.#OPERATORS;
    this.on("afterrender", (): void => {
      opSelector.addListener("select", (): void => {
        this.fireEvent("modified");
      });
      // set the initial operator
      opSelector.setValue(operator ? AbstractConditionBase.internalFromExternalOperatorName(operator) : internalOperators[0]);
    });
  }
}

export default PercentageTaxonomyConditionBase;
