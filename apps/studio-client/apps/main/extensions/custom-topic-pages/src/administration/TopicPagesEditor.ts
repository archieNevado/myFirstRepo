import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import SplitterSkin from "@coremedia/studio-client.ext.ui-components/skins/SplitterSkin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Splitter from "@jangaroo/ext-ts/resizer/Splitter";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPages_properties from "../TopicPages_properties";
import TopicPagesEditorBase from "./TopicPagesEditorBase";
import TopicPreviewPanel from "./TopicPreviewPanel";
import TopicsPanel from "./TopicsPanel";

interface TopicPagesEditorConfig extends Config<TopicPagesEditorBase> {
}

class TopicPagesEditor extends TopicPagesEditorBase {
  declare Config: TopicPagesEditorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.topicPagesEditor";

  constructor(config: Config<TopicPagesEditor> = null) {
    super((()=> ConfigUtils.apply(Config(TopicPagesEditor, {
      title: TopicPages_properties.TopicPages_administration_title,
      closable: true,
      id: TopicPagesEditorBase.TOPIC_PAGES_EDITOR_ID,
      iconCls: TopicPages_properties.TopicPages_icon,
      ui: PanelSkin.FRAME.getSkin(),
      constrainHeader: true,
      layout: "fit",

      items: [
        Config(Panel, {
          items: [
            Config(TopicsPanel, {
              id: "topicsPanel",
              selectionExpression: this.getSelectionExpression(),
              width: 550,
            }),
            Config(Splitter, {
              width: "4px",
              ui: SplitterSkin.DARK.getSkin(),
            }),
            Config(TopicPreviewPanel, {
              selectionExpression: this.getSelectionExpression(),
              id: "topicPreviewPanel",
              flex: 1,
            }),
          ],
          layout: Config(HBoxLayout, { align: "stretch" }),
        }),
      ],
      tbar: Config(Toolbar, {
        height: 40,
        ui: ToolbarSkin.WORKAREA.getSkin(),
      }),

    }), config))());
  }
}

export default TopicPagesEditor;
