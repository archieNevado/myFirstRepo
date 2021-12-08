import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Model from "@jangaroo/ext-ts/data/Model";
import Label from "@jangaroo/ext-ts/form/Label";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";

interface TopicPreviewPanelBaseConfig extends Config<Panel>, Partial<Pick<TopicPreviewPanelBase,
  "selectionExpression"
>> {
}

/**
 * Base class of the topic pages preview panel that access an iframe for applying the preview URL.
 */
class TopicPreviewPanelBase extends Panel {
  declare Config: TopicPreviewPanelBaseConfig;

  /**
   * The value expression that contains the selected topic record.
   */
  selectionExpression: ValueExpression = null;

  protected static readonly PREVIEW_FRAME: string = "topicPagesPreviewFrame";

  #frameLabel: Label = null;

  #lastUrl: string = null;

  constructor(config: Config<TopicPreviewPanelBase> = null) {
    super(config);
    this.#frameLabel = as(this.getComponent(TopicPreviewPanelBase.PREVIEW_FRAME), Label);
    this.selectionExpression.addChangeListener(bind(this, this.#selectionChanged));
  }

  /**
   * Fired when a new entry has been selected on the topic list.
   * The url is only updated when the selection has not changed for 2 seconds.
   */
  #selectionChanged(): void {
    const record: Model = this.selectionExpression.getValue();
    if (record) {
      const topic: Content = record.data.topic;
      topic.load((): void => {
        let url = topic.getDefaultPreviewUrl();
        url = url + "&site=" + editorContext._.getSitesService().getPreferredSiteId();
        //recheck URL after 2 seconds
        window.setTimeout((): void => {
          if (this.#lastUrl !== url) {
            this.#lastUrl = url;
            trace("[INFO]", "Updating topic page preview URL: " + url);
            this.#frameLabel.setText(TopicPreviewPanelBase.#getFrameHTML(url), false);
          }
        }, 2000);
      });
    } else {
      this.#lastUrl = undefined;
    }
  }

  static #getFrameHTML(url: string): string {
    return "<iframe src=\"" + url + "\" height=\"100%\" width=\"100%\" style=\"border: 0;\"></iframe>";
  }
}

export default TopicPreviewPanelBase;
