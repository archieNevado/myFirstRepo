import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";

interface TopicPagesEditorBaseConfig extends Config<Panel> {
}

/**
 * Base class of the taxonomy administration tab.
 */
class TopicPagesEditorBase extends Panel {
  declare Config: TopicPagesEditorBaseConfig;

  static readonly TOPIC_PAGES_EDITOR_ID: string = "topicPagesEditor";

  #selectionExpression: ValueExpression = null;

  constructor(config: Config<TopicPagesEditorBase> = null) {
    super(config);
  }

  /**
   * Returns the value expression that contains the active selection.
   * @return
   */
  protected getSelectionExpression(): ValueExpression {
    if (!this.#selectionExpression) {
      this.#selectionExpression = ValueExpressionFactory.create("selection", beanFactory._.createLocalBean());
    }
    return this.#selectionExpression;
  }
}

export default TopicPagesEditorBase;
