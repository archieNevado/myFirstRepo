import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import ProjectContentContainer from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentContainer";
import ProjectContentContextMenu from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentContextMenu";
import BookmarkMenuItem from "@coremedia/studio-client.main.editor-components/sdk/bookmarks/BookmarkMenuItem";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface BookmarkProjectContentViewPluginConfig extends Config<NestedRulesPlugin> {
}

/* plugging into sub-extension points of <collab:projectContentContainer>: */
class BookmarkProjectContentViewPlugin extends NestedRulesPlugin {
  declare Config: BookmarkProjectContentViewPluginConfig;

  #projectContentContainer: ProjectContentContainer = null;

  constructor(config: Config<BookmarkProjectContentViewPlugin> = null) {
    super((()=>{
      this.#projectContentContainer = as(config.cmp, ProjectContentContainer);
      return ConfigUtils.apply(Config(BookmarkProjectContentViewPlugin, {

        rules: [
          Config(ProjectContentContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                index: 3,
                items: [
                  Config(BookmarkMenuItem, { contentValueExpression: this.#projectContentContainer.selectedItemsVE }),
                  Config(Separator),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default BookmarkProjectContentViewPlugin;
