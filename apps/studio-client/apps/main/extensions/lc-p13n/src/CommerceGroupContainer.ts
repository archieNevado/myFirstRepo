import CommerceCatalogObjectsSelectForm from "@coremedia-blueprint/studio-client.main.ec-studio/forms/CommerceCatalogObjectsSelectForm";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextP13NStudioPluginBase from "./LivecontextP13NStudioPluginBase";

interface CommerceGroupContainerConfig extends Config<PropertyFieldGroup>, Partial<Pick<CommerceGroupContainer,
  "contentBindTo"
>> {
}

class CommerceGroupContainer extends PropertyFieldGroup {
  declare Config: CommerceGroupContainerConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.p13n.studio.config.commerceGroupContainer";

  constructor(config: Config<CommerceGroupContainer> = null) {
    super(ConfigUtils.apply(Config(CommerceGroupContainer, {
      itemId: "commerceGroup",
      bindTo: config.contentBindTo,
      title: LivecontextStudioPlugin_properties.p13n_commerce_group_label,
      manageHeight: false,

      items: [
        Config(CommerceCatalogObjectsSelectForm, {
          itemId: "userSegments",
          bindTo: config.contentBindTo,
          fieldLabel: LivecontextStudioPlugin_properties.p13n_commerce_user_segments_label,
          catalogObjectIdListName: LivecontextP13NStudioPluginBase.USER_SEGMENTS,
          emptyText: LivecontextStudioPlugin_properties.p13n_commerce_user_segments_selector_for_persona_emptyText,
          invalidMessage: LivecontextStudioPlugin_properties.p13n_commerce_user_segment_invalid,
          removeActionName: "removeCommerceSegment",
          getCommerceObjectsFunction: LivecontextP13NStudioPluginBase.getSegments,
          noStoreMessage: LivecontextStudioPlugin_properties.p13n_commerce_no_store_configured,
        }),
      ],

    }), config));
  }

  /**
   * The bean value expression to hand through to all property fields contained in this group.
   */
  contentBindTo: ValueExpression = null;
}

export default CommerceGroupContainer;
