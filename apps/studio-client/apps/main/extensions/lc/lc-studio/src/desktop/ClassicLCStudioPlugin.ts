import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import ActionsToolbarContainer from "@coremedia/studio-client.main.editor-components/sdk/desktop/ActionsToolbarContainer";
import EditorMainViewBase from "@coremedia/studio-client.main.editor-components/sdk/desktop/EditorMainViewBase";
import OnlyIf from "@coremedia/studio-client.main.editor-components/sdk/plugins/OnlyIf";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceWorkAreaTab from "./CommerceWorkAreaTab";

interface ClassicLCStudioPluginConfig extends Config<StudioPlugin> {
}

/*
  A plugin to switch the commerce part of the Studio view back to the classic layout.
 */
class ClassicLCStudioPlugin extends StudioPlugin {
  declare Config: ClassicLCStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.studio.config.classicLCStudioPlugin";

  constructor(config: Config<ClassicLCStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ClassicLCStudioPlugin, {

      rules: [
        Config(CommerceWorkAreaTab, {
          plugins: [
            Config(OnlyIf, {
              condition: EditorMainViewBase.isClassicView,
              then: Config(RemoveItemsPlugin, {
                items: [
                  Config(Component, { itemId: ActionsToolbarContainer.ACTIONS_TOOLBAR_CONTAINER_ITEM_ID }),
                ],
              }),
            }),
          ],
        }),
      ],
    }), config));
  }
}

export default ClassicLCStudioPlugin;
