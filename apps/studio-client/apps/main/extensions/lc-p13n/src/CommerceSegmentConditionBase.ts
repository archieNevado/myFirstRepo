import CommerceObjectSelector from "@coremedia-blueprint/studio-client.main.ec-studio/components/CommerceObjectSelector";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import AbstractCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/AbstractCondition";
import AbstractConditionBase from "@coremedia/studio-client.main.cap-personalization-ui/condition/AbstractConditionBase";
import SelectionRuleHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/SelectionRuleHelper";
import Events from "@jangaroo/ext-ts/Events";
import Model from "@jangaroo/ext-ts/data/Model";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CommerceSegmentCondition from "./CommerceSegmentCondition";
import LivecontextP13NStudioPluginBase from "./LivecontextP13NStudioPluginBase";

interface CommerceSegmentConditionBaseEvents extends Events<AbstractCondition> {

  /**
   * Fires after the data represented by this component was modified. Intended to be used for
   * automatically saving changes.
   */
  modified?(): any;
}

interface CommerceSegmentConditionBaseConfig extends Config<AbstractCondition> {
  listeners?: CommerceSegmentConditionBaseEvents;
}

// NOSONAR - no type

class CommerceSegmentConditionBase extends AbstractCondition {
  declare Config: CommerceSegmentConditionBaseConfig;

  static readonly #MODIFIED_EVENT: string = "modified";

  static readonly #OPERATORS: Array<any> = [SelectionRuleHelper.OP_CONTAINS];

  #segmentSelector: CommerceObjectSelector = null;

  // the prefix of the properties rendered by this component
  #propertyPrefix: string = "";

  constructor(config: Config<CommerceSegmentCondition> = null) {

    super((()=>{

      this.#initSegmentSelector(config);
      return config;
    })());

    // check the supplied configuration
    if (config.propertyPrefix === null || config.propertyPrefix === undefined) {
      throw new Error("config.propertyPrefix must not be null");
    }
    this.#propertyPrefix = config.propertyPrefix.length > 0 ? config.propertyPrefix + "." : "";

    // create operator combo
    const operatorSelector = this.initOpSelector(null, config["operatorNames"], config["operatorEmptyText"], config["operator"],
      CommerceSegmentConditionBase.#OPERATORS, AbstractConditionBase.DEFAULT_OPERATOR_DISPLAY_NAMES);

    this.add(operatorSelector);

    // init the segment selector
    this.add(this.#segmentSelector);

  }

  #initSegmentSelector(config: Config<CommerceSegmentCondition>): void {
    const segmentSelectorCfg = Config(CommerceObjectSelector);
    segmentSelectorCfg.contentValueExpression = config.bindTo;
    segmentSelectorCfg.itemId = "segmentSelector";
    segmentSelectorCfg.flex = 30;
    segmentSelectorCfg.emptyText = LivecontextStudioPlugin_properties.p13n_commerce_user_segments_selector_emptyText;
    segmentSelectorCfg.getCommerceObjectsFunction = LivecontextP13NStudioPluginBase.getSegments;
    segmentSelectorCfg.listConfig = { minWidth: 200 };
    segmentSelectorCfg.forceSelection = true;
    segmentSelectorCfg.selectOnFocus = true;
    segmentSelectorCfg.typeAhead = true;
    segmentSelectorCfg.allowBlank = false;
    segmentSelectorCfg.triggerAction = "all";
    segmentSelectorCfg.queryMode = "local";
    segmentSelectorCfg.quote = true;
    this.#segmentSelector = new CommerceObjectSelector(segmentSelectorCfg);

    this.#segmentSelector.addListener("change", (): void => {
      this.fireEvent(CommerceSegmentConditionBase.#MODIFIED_EVENT);
    });

    this.#segmentSelector.addListener("select", (combo: ComboBox, record: Model | Model[]): void => {
      this.fireEvent(CommerceSegmentConditionBase.#MODIFIED_EVENT);
    });

    // if data changed (e.g. segment deleted), validate again
    this.#segmentSelector.getStore().addListener("datachanged", bind(this, this.#validateStore));

    // validate again, if focus is lost (e.g. open dropdown, and click anywhere else)
    this.#segmentSelector.addListener("blur", bind(this, this.#validateStore));

    // validate again on afterrender
    this.#segmentSelector.addListener("afterrender", bind(this, this.#validateStore));

    // validate again on move
    this.#segmentSelector.addListener("move", bind(this, this.#validateStore));

  }

  /**
   * Validates the comboBox entry. The comboBox will be marked as invalid if the comboBox store doesn't
   * contain the value of the comboBox.
   */
  #validateStore(): void {
    //the segmentId must be unquoted otherwise the store will not find even the valid id.
    const segmentId = this.#segmentSelector.getUnquotedValue();
    if (segmentId) {
      // check if value is not in store
      if (this.#segmentSelector.getStore().find(this.#segmentSelector.valueField, segmentId) === -1) {
        // set segment name as rawValue to avoid checking out the document
        // mark as "invalid"
        this.#segmentSelector.markInvalid(LivecontextStudioPlugin_properties.p13n_context_commerce_segment_invalid);
      } else {
        this.#segmentSelector.clearInvalid();
      }
    }
  }

  /* ------------------------------------------

   Condition interface

   ------------------------------------------ */

  override getPropertyName(): string {
    return this.#propertyPrefix + "usersegments";
  }

  override setPropertyName(name: string): void {
    //
  }

  override getPropertyValue(): string {
    return this.#segmentSelector.getValue();
  }

  override setPropertyValue(v: string): void {
    if (v !== null) {
      this.#segmentSelector.setValue(v);
    } else {
      this.#segmentSelector.clearValue();
    }
  }

}

export default CommerceSegmentConditionBase;
