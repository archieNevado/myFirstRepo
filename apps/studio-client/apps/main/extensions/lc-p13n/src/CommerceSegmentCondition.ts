import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceSegmentConditionBase from "./CommerceSegmentConditionBase";

interface CommerceSegmentConditionConfig extends Config<CommerceSegmentConditionBase>, Partial<Pick<CommerceSegmentCondition,
  "bindTo" |
  "conditionName" |
  "propertyPrefix"
>> {
}

class CommerceSegmentCondition extends CommerceSegmentConditionBase {
  declare Config: CommerceSegmentConditionConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.p13n.studio.config.commerceSegmentCondition";

  bindTo: ValueExpression = null;

  constructor(config: Config<CommerceSegmentCondition> = null) {
    super(ConfigUtils.apply(Config(CommerceSegmentCondition, {
      height: 25,

      layout: Config(HBoxLayout),

    }), config));
  }

  conditionName: string = null;

  propertyPrefix: string = null;
}

export default CommerceSegmentCondition;
