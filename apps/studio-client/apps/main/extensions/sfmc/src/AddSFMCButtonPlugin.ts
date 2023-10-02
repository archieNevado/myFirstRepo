import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import PushSFMCContentButton from "@coremedia/studio-client.main.bpbase-sfmc-studio/PushSFMCContentButton";
import ProjectContentControlsToolbar from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentControlsToolbar";
import ProjectContentToolbar from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentToolbar";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface AddSFMCButtonPluginConfig extends Config<NestedRulesPlugin> {
}

class AddSFMCButtonPlugin extends NestedRulesPlugin {
  declare Config: AddSFMCButtonPluginConfig;

  #toolbarConfig: ProjectContentToolbar = null;

  constructor(config: Config<AddSFMCButtonPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#toolbarConfig = cast(ProjectContentToolbar, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddSFMCButtonPlugin, {
      rules: [
        Config(ProjectContentControlsToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Separator),
                Config(PushSFMCContentButton, { contentValueExpression: this$.#toolbarConfig.selectedItemsVE }),
              ],
            }),
          ],
        }),
      ],
    }), config));
  }
}

export default AddSFMCButtonPlugin;
