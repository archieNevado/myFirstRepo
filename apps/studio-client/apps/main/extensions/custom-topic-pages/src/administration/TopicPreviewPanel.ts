import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Label from "@jangaroo/ext-ts/form/Label";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPreviewPanelBase from "./TopicPreviewPanelBase";

interface TopicPreviewPanelConfig extends Config<TopicPreviewPanelBase> {
}

class TopicPreviewPanel extends TopicPreviewPanelBase {
  declare Config: TopicPreviewPanelConfig;

  static readonly LABEL_ITEM_ID: string = "topic-pages-preview";

  static readonly TOPIC_PREVIEW_PANEL_BLOCK: BEMBlock = new BEMBlock("cm-topic-preview-panel");

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.topicPreviewPanel";

  constructor(config: Config<TopicPreviewPanel> = null) {
    super(ConfigUtils.apply(Config(TopicPreviewPanel, {
      itemId: TopicPreviewPanel.LABEL_ITEM_ID,

      items: [
        Config(Label, {
          itemId: TopicPreviewPanelBase.PREVIEW_FRAME,
          height: "100%",
          width: "100%",
        }),
      ],
      layout: Config(AnchorLayout, { manageOverflow: false }),
      defaults: Config<Component>({ anchor: "100%" }),
      ...ConfigUtils.append({
        plugins: [
          Config(BEMPlugin, { block: TopicPreviewPanel.TOPIC_PREVIEW_PANEL_BLOCK }),
        ],
      }),
    }), config));
  }
}

export default TopicPreviewPanel;
