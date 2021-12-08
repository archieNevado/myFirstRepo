import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AbstractTaxonomyConditionBase from "./AbstractTaxonomyConditionBase";

interface AbstractTaxonomyConditionConfig extends Config<AbstractTaxonomyConditionBase>, Partial<Pick<AbstractTaxonomyCondition,
  "propertyPrefix" |
  "keywordEmptyText" |
  "conditionName" |
  "keywordText" |
  "operatorEmptyText" |
  "operatorNames" |
  "operator" |
  "valueEmptyText" |
  "valueText" |
  "valueVType" |
  "suffixText"
>> {
}

/**
 * A Condition specialized for editing <i>taxonomy conditions</i>. A taxonomy condition consists of a linked taxonomy
 * (as Keyword), a comparison operator, and a value field (percentage).
 *
 * @see AbstractTaxonomyCondition
 *
 */
class AbstractTaxonomyCondition extends AbstractTaxonomyConditionBase {
  declare Config: AbstractTaxonomyConditionConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.abstractTaxonomyCondition";

  constructor(config: Config<AbstractTaxonomyCondition> = null) {
    super(ConfigUtils.apply(Config(AbstractTaxonomyCondition), config));
  }

  /**
   * prefix of context properties mapped to this condition instance. The characters following the prefix in a property name are assumed to represent the keyword
   */
  propertyPrefix: string = null;

  /**
   * the text to be shown in the keyword field if it is empty. Defaults to keyword
   */
  keywordEmptyText: string = null;

  conditionName: string = null;

  /**
   * the text to place into the keyword field. Defaults to null
   */
  keywordText: string = null;

  /**
   * the text to be shown if no operator is selected. Default to operator
   */
  operatorEmptyText: string = null;

  /**
   * user-presentable names of the operators. See below
   */
  operatorNames: any = null;

  /**
   * the operator to select initially. See below
   */
  operator: string = null;

  /**
   * the text to be shown in the value field if it is empty. Defaults to value
   */
  valueEmptyText: string = null;

  /**
   * the text to place into the value field
   */
  valueText: string = null;

  /**
   * the validation type of the value field. See below
   */
  valueVType: string = null;

  /**
   * the text to be shown after the value field. Defaults to null The property prefix is used to transform keyword properties to and from a user-presentable form. In a typical scenario, keyword properties in a profile will use a common prefix to identify them as keywords, e.g. 'keyword'. This prefix shouldn't be shown to users of the UI. If the propertyPrefix property is set to the internally used prefix, this condition component will remove the prefix (including the '.' separator) from the keyword property before it is displayed, and add it to the value in the keyword field when it is read via getPropertyName. The default validation types are: <ul><li>For the keyword field: <code>/^[a-zA-Z_][a-zA-Z_0-9\.]*$/</code>.</li><li>For the value field: <code>/^\d+(\.\d+)?$/</code>.</li></ul>The <b>operators</b> offered by this component are: <ul><li>'lt'</li><li style="list-style: none">less than</li><li>'le'</li><li style="list-style: none">less than or equals</li><li>'eq'</li><li style="list-style: none">equals</li><li>'ge'</li><li style="list-style: none">greater than or equals</li><li>'gt'</li><li style="list-style: none">greater than</li></ul>The names used for the available operators can be overridden by a dictionary supplied via the <b>operatorNames</b> property. The available operators and their default names are: <ul><li>'lt': 'less'</li><li>'le': 'less or equal'</li><li>'eq': 'equal'</li><li>'ge': 'greater or equal'</li><li>'gt': 'greater'</li></ul>You may override an arbitrary subset of these values.
   */
  suffixText: string = null;
}

export default AbstractTaxonomyCondition;
