import Label from "@jangaroo/ext-ts/form/Label";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPreviewPanelBase from "./TopicPreviewPanelBase";

interface TopicPreviewPanelConfig extends Config<TopicPreviewPanelBase> {
}

class TopicPreviewPanel extends TopicPreviewPanelBase {
  declare Config: TopicPreviewPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.topicPreviewPanel";

  constructor(config: Config<TopicPreviewPanel> = null) {
    super(ConfigUtils.apply(Config(TopicPreviewPanel, {
      layout: "fit",
      itemId: "topic-pages-preview",

      items: [
        Config(Label, {
          itemId: TopicPreviewPanelBase.PREVIEW_FRAME,
          width: "100%",
          height: "100%",
          flex: 1,
        }),
      ],

    }), config));
  }
}

export default TopicPreviewPanel;
