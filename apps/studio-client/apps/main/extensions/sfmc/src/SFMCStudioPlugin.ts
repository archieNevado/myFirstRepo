import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import PushSFMCContentButton from "@coremedia/studio-client.main.bpbase-sfmc-studio/PushSFMCContentButton";
import SFMCCollectionViewPlugin from "@coremedia/studio-client.main.bpbase-sfmc-studio/SFMCCollectionViewPlugin";
import ProjectContentToolbar from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentToolbar";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import ActionsToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ActionsToolbar";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import Component from "@jangaroo/ext-ts/Component";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AddSFMCButtonPlugin from "./AddSFMCButtonPlugin";

interface SFMCStudioPluginConfig extends Config<StudioPlugin> {
}

/* Extend the standard Studio and Blueprint components for SFMC*/
class SFMCStudioPlugin extends StudioPlugin {
  declare Config: SFMCStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.sfmc.studio.config.sFMCStudioPlugin";

  constructor(config: Config<SFMCStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(SFMCStudioPlugin, {

      rules: [
        Config(ActionsToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Separator, { height: 1 }),
                Config(PushSFMCContentButton, { contentValueExpression: WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION }),
              ],
              after: [
                Config(Component, { itemId: ActionsToolbar.WITHDRAW_BUTTON_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CollectionView, {
          plugins: [
            Config(SFMCCollectionViewPlugin),
          ],
        }),

        Config(ProjectContentToolbar, {
          plugins: [
            Config(AddSFMCButtonPlugin),
          ],
        }),

      ],

    }), config));
  }
}

export default SFMCStudioPlugin;
