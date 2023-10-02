import CMPersonaForm from "@coremedia-blueprint/studio-client.main.p13n-studio/CMPersonaForm";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import SFMCJourneyCondition from "@coremedia/studio-client.main.bpbase-sfmc-p13n-studio/components/SFMCJourneyCondition";
import SFMCPersonaGroupContainer from "@coremedia/studio-client.main.bpbase-sfmc-p13n-studio/components/persona/SFMCPersonaGroupContainer";
import SelectionRulesField from "@coremedia/studio-client.main.cap-personalization-ui/SelectionRulesField";
import Addconditionitems from "@coremedia/studio-client.main.cap-personalization-ui/plugin/Addconditionitems";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface SFMCP13NStudioPluginConfig extends Config<StudioPlugin> {
}

/* Extend the standard Studio and Blueprint components for SFMC P13N*/
class SFMCP13NStudioPlugin extends StudioPlugin {
  declare Config: SFMCP13NStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.sfmc.p13n.studio.config.sFMCP13NStudioPlugin";

  constructor(config: Config<SFMCP13NStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(SFMCP13NStudioPlugin, {

      rules: [
        Config(SelectionRulesField, {
          plugins: [
            Config(Addconditionitems, {
              items: [
                Config(SFMCJourneyCondition, {
                  conditionName: "SFMC Journey",
                  propertyPrefix: "sfmc",
                }),
              ],
            }),
          ],
        }),
        Config(CMPersonaForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                /*GroupContainer: Commerce*/
                Config(SFMCPersonaGroupContainer),
              ],
              after: [
                Config(Component, { itemId: CMPersonaForm.PERSONA_IMAGE_ITEM_ID }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default SFMCP13NStudioPlugin;
