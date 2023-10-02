import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import Addconditionitems from "@coremedia/studio-client.main.cap-personalization-ui/plugin/Addconditionitems";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceSegmentCondition from "./CommerceSegmentCondition";

interface AddCommerceSegmentConditionPluginConfig extends Config<Addconditionitems> {
}

class AddCommerceSegmentConditionPlugin extends Addconditionitems {
  declare Config: AddCommerceSegmentConditionPluginConfig;

  #componentConfig: any = null;

  constructor(config: Config<AddCommerceSegmentConditionPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#componentConfig = config.cmp.initialConfig;
    super(ConfigUtils.apply(Config(AddCommerceSegmentConditionPlugin, {
      items: [
        /* livecontext perso commerce conditions */
        Config(CommerceSegmentCondition, {
          bindTo: this$.#componentConfig.bindTo,
          conditionName: LivecontextStudioPlugin_properties.p13n_context_commerce_segment,
          propertyPrefix: "commerce",
        }),
      ],
    }), config));
  }
}

export default AddCommerceSegmentConditionPlugin;
