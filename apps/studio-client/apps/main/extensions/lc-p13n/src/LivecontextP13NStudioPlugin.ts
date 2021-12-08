import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import CMPersonaForm from "@coremedia-blueprint/studio-client.main.p13n-studio/CMPersonaForm";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import ConditionsField from "@coremedia/studio-client.main.cap-personalization-ui/ConditionsField";
import SelectionRulesField from "@coremedia/studio-client.main.cap-personalization-ui/SelectionRulesField";
import PersonaPopupOverviewPanel from "@coremedia/studio-client.main.cap-personalization-ui/persona/popup/PersonaPopupOverviewPanel";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import AddCommerceSegmentConditionPlugin from "./AddCommerceSegmentConditionPlugin";
import CommerceGroupContainer from "./CommerceGroupContainer";
import CommerceObjectsDisplayField from "./CommerceObjectsDisplayField";
import LivecontextP13NStudioPluginBase from "./LivecontextP13NStudioPluginBase";

interface LivecontextP13NStudioPluginConfig extends Config<LivecontextP13NStudioPluginBase> {
}

/* Extend the standard Studio and Blueprint components for Live Context P13N*/
class LivecontextP13NStudioPlugin extends LivecontextP13NStudioPluginBase {
  declare Config: LivecontextP13NStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.p13n.studio.config.livecontextP13NStudioPlugin";

  constructor(config: Config<LivecontextP13NStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(LivecontextP13NStudioPlugin, {

      rules: [
        Config(SelectionRulesField, {
          plugins: [
            Config(AddCommerceSegmentConditionPlugin),
          ],
        }),

        Config(ConditionsField, {
          plugins: [
            Config(AddCommerceSegmentConditionPlugin),
          ],
        }),

        Config(CMPersonaForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                /*GroupContainer: Commerce*/
                Config(CommerceGroupContainer),
              ],
              after: [
                Config(Component, { itemId: CMPersonaForm.PERSONA_IMAGE_ITEM_ID }),
              ],
            }),
          ],
        }),

        /* Add Commerce Segments to persona Popup*/
        Config(PersonaPopupOverviewPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 0,
              applyTo: (panel: Container): Container =>
                as(panel.queryById(PersonaPopupOverviewPanel.PERSONA_PANEL_ITEM_ID), Container)
              ,
              items: [
                Config(CommerceObjectsDisplayField, {
                  fieldLabel: LivecontextStudioPlugin_properties.p13n_commerce_user_segments_label,
                  invalidMessage: LivecontextStudioPlugin_properties.p13n_commerce_user_segment_invalid,
                  emptyMessage: LivecontextStudioPlugin_properties.p13n_persona_commerce_segments_empty,
                  catalogObjectIdListName: LivecontextP13NStudioPluginBase.USER_SEGMENTS,
                }),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
          source: resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties),
        }),
      ],

    }), config));
  }
}

export default LivecontextP13NStudioPlugin;
