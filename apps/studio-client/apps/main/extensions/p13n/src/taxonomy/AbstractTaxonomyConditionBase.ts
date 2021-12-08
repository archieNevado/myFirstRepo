import TaxonomyNodeList from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/TaxonomyUtil";
import OpenTaxonomyChooserAction from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/action/OpenTaxonomyChooserAction";
import TaxonomySearchField from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/selection/TaxonomySearchField";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import StatefulNumberField from "@coremedia/studio-client.ext.ui-components/components/StatefulNumberField";
import HorizontalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HorizontalSpacingPlugin";
import Personalization_properties from "@coremedia/studio-client.main.cap-personalization-ui/Personalization_properties";
import AbstractCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/AbstractCondition";
import SelectionRuleHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/SelectionRuleHelper";
import Ext from "@jangaroo/ext-ts";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import Label from "@jangaroo/ext-ts/form/Label";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import VTypes from "@jangaroo/ext-ts/form/field/VTypes";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AbstractTaxonomyCondition from "./AbstractTaxonomyCondition";
import TaxonomyConditionUtil from "./TaxonomyConditionUtil";

interface AbstractTaxonomyConditionBaseConfig extends Config<AbstractCondition> {
}

/**
 * A Condition specialized for editing <i>taxonomy conditions</i>. A taxonomy condition consists of a linked taxonomy
 * (as Keyword), a comparison operator, and a value field (percentage).
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.AbstractTaxonomyCondition
 */
class AbstractTaxonomyConditionBase extends AbstractCondition {
  declare Config: AbstractTaxonomyConditionBaseConfig;

  static #static = (() =>{
    // introduce new vtypes for keywords and values
    VTypes["keywordConditionKeywordVal"] = /^[a-zA-Z_][a-zA-Z_0-9.]*$/;
    VTypes["keywordConditionKeywordMask"] = /^[a-zA-Z_0-9.]/;
    VTypes["keywordConditionKeywordText"] = Personalization_properties.p13n_error_keywordText;
    VTypes["keywordConditionKeyword"] = ((v: any): any =>
      VTypes["keywordConditionKeywordVal"].test(v)
    );
    VTypes["keywordConditionValueVal"] = /^\d+(\.\d+)?$/;
    VTypes["keywordConditionValueMask"] = /^[0-9.]/;
    VTypes["keywordConditionValueText"] = Personalization_properties.p13n_error_valueText;
    VTypes["keywordConditionValue"] = ((v: any): any =>
      VTypes["keywordConditionValueVal"].test(v)
    );
  })();

  static readonly #VALUE_EMPTY_TEXT: string = Personalization_properties.p13n_op_value;

  //
  // ui components
  //
  #keywordField: TextField = null;

  #valueField: StatefulNumberField = null;

  // the internal prefix used for keywords. See class comment.
  #propertyPrefix: string = null;

  //active selection
  #taxonomySelectionExpr: ValueExpression = ValueExpressionFactory.create("taxonomy", beanFactory._.createLocalBean({ taxonomy: [] }));

  /**
   * Creates a new TaxonomyCondition.
   *
   * @cfg {String} conditionName name to be used for this condition instance in the condition combox
   * @cfg {String} propertyPrefix prefix of context properties mapped to this condition instance. The characters
   * following the prefix in a property name are assumed to represent the keyword
   * @cfg {Boolean} isDefault set to true if this condition is to be the default condition of the condition panel. The
   * first condition in the list of the registered conditions with the default flag set is used as the default
   * @cfg {String} keywordEmptyText the text to be shown in the keyword field if it is empty. Defaults to <i>keyword</i>
   * @cfg {String} keywordText the text to place into the keyword field. Defaults to <i>null</i>
   * @cfg {String} keywordVType the validation type of the keyword field. See below
   * @cfg {String} operatorEmptyText the text to be shown if no operator is selected. Default to <i>operator</i>
   * @cfg {Object} operatorNames user-presentable names of the operators. See below
   * @cfg {String} operator the operator to select initially. See below
   * @cfg {String} valueEmptyText the text to be shown in the value field if it is empty. Defaults to <i>value</i>
   * @cfg {String} valueText the text to place into the value field
   * @cfg {String} valueVType the validation type of the value field. See below
   * @cfg {String} suffixText the text to be shown after the value field. Defaults to <i>null</i>
   *
   * The property prefix is used to transform keyword properties to and from a user-presentable form. In a typical scenario,
   * keyword properties in a profile will use a common prefix to identify them as keywords, e.g. 'keyword'. This prefix
   * shouldn't be shown to users of the UI. If the propertyPrefix property is set to the internally used prefix, this condition
   * component will remove the prefix (including the '.' separator) from the keyword property before it is displayed, and
   * add it to the value in the keyword field when it is read via getPropertyName.
   *
   * The default validation types are:
   * <ul>
   * <li>For the keyword field: <code>/^[a-zA-Z_][a-zA-Z_0-9\.]*$/</code>.</li>
   * <li>For the value field: <code>/^\d+(\.\d+)?$/</code>.</li>
   * </ul>
   *
   * The <b>operators</b> offered by this component are:
   *
   * <ul>
   * <li>'lt'</li> less than
   * <li>'le'</li> less than or equals
   * <li>'eq'</li> equals
   * <li>'ge'</li> greater than or equals
   * <li>'gt'</li> greater than
   * </ul>
   *
   * The names used for the available operators can be overridden by a dictionary supplied via
   * the <b>operatorNames</b> property. The available operators and their default names are:
   *
   * <ul>
   * <li>'lt': 'less'</li>
   * <li>'le': 'less or equal'</li>
   * <li>'eq': 'equal'</li>
   * <li>'ge': 'greater or equal'</li>
   * <li>'gt': 'greater'</li>
   * </ul>
   *
   * You may override an arbitrary subset of these values.
   *
   * @param config configuration of this instance
   */
  constructor(config: Config<AbstractTaxonomyCondition> = null) {
    super(Config(AbstractCondition, Ext.apply(config, {
      /* obligatory configuration. overrides supplied properties */
      layout: "hbox",
      layoutConfig: { flex: 1 },
    }, { autoWidth: true })));

    // store the keyword prefix
    this.#propertyPrefix = config.propertyPrefix;
    if (this.#propertyPrefix === null) {
      throw new Error(Personalization_properties.p13n_error_propertyPrefix);
    }

    if (this.#propertyPrefix !== null && this.#propertyPrefix.charAt(this.#propertyPrefix.length - 1) === ".") {
      // remove the '.' at the end of the prefix
      this.#propertyPrefix = this.#propertyPrefix.substring(0, this.#propertyPrefix.length - 1);
    }
    this.#taxonomySelectionExpr.addChangeListener(bind(this, this.#taxonomiesSelected));
  }

  /**
   * Adds the field that contains the keyword.
   */
  addKeywordField(): void {
    this.#keywordField = new TaxonomySearchField(Config(TaxonomySearchField, {
      allowBlank: false,
      searchResultExpression: this.#taxonomySelectionExpr,
      taxonomyIdExpression: ValueExpressionFactory.createFromValue(TaxonomyConditionUtil.getTaxonomyId4Chooser(this.#propertyPrefix)),
      flex: 40,
      cls: "force-ellipsis combo-text-field",
    }));
    this.add(this.#keywordField);
  }

  /**
   * Adds the button that opens the TaxonomyChooser.
   */
  addTaxonomyButton(): void {
    const openChooserAction = new OpenTaxonomyChooserAction(Config(OpenTaxonomyChooserAction, {
      taxonomyIdExpression: ValueExpressionFactory.createFromValue(TaxonomyConditionUtil.getTaxonomyId4Chooser(this.#propertyPrefix)),
      singleSelection: true,
      tooltip: TaxonomyStudioPlugin_properties.Taxonomy_action_tooltip,
      propertyValueExpression: this.#taxonomySelectionExpr,
    }));

    const btnCfg = Config(IconButton);
    btnCfg.iconCls = CoreIcons_properties.add_tag;
    btnCfg.tooltip = TaxonomyStudioPlugin_properties.Taxonomy_action_tooltip;
    btnCfg.ariaLabel = btnCfg.tooltip;
    btnCfg.baseAction = openChooserAction;
    const btn = new IconButton(btnCfg);
    this.add(btn);
  }

  /**
   * Adds the input field that contains the taxonomy value (1 or 0 at BooleanTaxonomyCondition, oder 0 - 100 at
   * PercentageTaxonomyCondition).
   * @param config
   * @param visible <code>false</code> to render this value field hidden
   */
  addValueField(config: Config<AbstractTaxonomyCondition>, visible: boolean = true): void {
    const layoutConfig = Config(HBoxLayout);
    layoutConfig.align = "middle";

    const containerConfig = Config(FieldContainer);
    containerConfig.flex = 30;
    containerConfig.layout = layoutConfig;
    containerConfig.plugins = [Config(HorizontalSpacingPlugin)];

    const fieldContainer = new FieldContainer(containerConfig);
    this.#valueField = new StatefulNumberField(Config(StatefulNumberField, {
      emptyText: config.valueEmptyText !== null ? config.valueEmptyText : AbstractTaxonomyConditionBase.#VALUE_EMPTY_TEXT,
      allowBlank: false,
      flex: 1,
      minValue: 0,
      maxValue: 100,
      vtype: config.valueVType !== null ? config.valueVType : "keywordConditionValue",
      enableKeyEvents: true,
      hidden: !visible,
    }));
    this.#valueField.addListener("keyup", (): void => {
      this.fireEvent("modified");
    });

    this.#valueField.setValue(config.valueText);

    fieldContainer.add(this.#valueField);
    if (config.suffixText !== null) {
      const labelConfig = Config(Label);
      labelConfig.text = config.suffixText;
      fieldContainer.add(new Label(labelConfig));
    }
    this.add(fieldContainer);
  }

  /**
   * Invoked after the taxonomy chooser has been closed.
   * @param expr The value expression that contains the selection.
   */
  #taxonomiesSelected(expr: ValueExpression): void {
    if (expr.getValue()) {
      const selection = as(expr.getValue(), TaxonomyNodeList);
      if (selection) {
        const leafRef = selection.getLeafRef();
        const taxonomy = UndocContentUtil.getContent(leafRef);
        taxonomy.load((): void => {
          this.#taxonomySelectionExpr.setValue(taxonomy); //this will trigger it self, but a different cast will apply then!
        });
      } else if (as(expr.getValue(), Content)) {//will be executed after the second trigger event and finally sets the value.
        const taxonomy1 = as(expr.getValue(), Content);
        this.fireEvent("modified", this);
        this.#keywordField.setValue(TaxonomyUtil.getTaxonomyName(taxonomy1));
      }
    } else {
      this.#keywordField.setValue("");
      this.fireEvent("modified", this);
    }
  }

  override getPropertyName(): string {
    const taxonomy = as(this.#taxonomySelectionExpr.getValue(), Content);
    return TaxonomyConditionUtil.formatPropertyName(this.#propertyPrefix + ".", taxonomy);
  }

  override setPropertyName(name: string): void {
    const taxonomy = TaxonomyConditionUtil.getTaxonomyContent(name);
    if (taxonomy) {
      taxonomy.load((): void => {
        this.#taxonomySelectionExpr.removeChangeListener(bind(this, this.#taxonomiesSelected));
        this.#taxonomySelectionExpr.setValue(taxonomy);
        this.#keywordField.setValue(TaxonomyUtil.getTaxonomyName(taxonomy));
        this.#taxonomySelectionExpr.addChangeListener(bind(this, this.#taxonomiesSelected));
      });
    }
  }

  override getPropertyValue(): string {
    const v: string = this.#valueField.getValue();
    return v ? TaxonomyConditionUtil.formatPropertyValue4Store(v) : SelectionRuleHelper.EMPTY_VALUE;
  }

  override setPropertyValue(value: string): void {
    this.#valueField.setValue(value === SelectionRuleHelper.EMPTY_VALUE ? null : TaxonomyConditionUtil.formatPropertyValue4Textfield(value));
  }
}

export default AbstractTaxonomyConditionBase;
