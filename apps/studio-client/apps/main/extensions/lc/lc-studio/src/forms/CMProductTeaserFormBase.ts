import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import CMProductTeaserForm from "./CMProductTeaserForm";

interface CMProductTeaserFormBaseConfig extends Config<DocumentTabPanel> {
}

class CMProductTeaserFormBase extends DocumentTabPanel {
  declare Config: CMProductTeaserFormBaseConfig;

  static readonly INHERIT_SETTING: string = "inherit";

  static readonly DISABLE_OVERLAY: string = "disableOverlay";

  #viewSettingsExpression: ValueExpression = null;

  constructor(config: Config<CMProductTeaserForm> = null) {
    super(config);
  }

  protected getLabelValueExpression(value: string): ValueExpression {
    return ValueExpressionFactory.create("label", beanFactory._.createLocalBean({ label: value }));
  }

  getViewSettingsExpression(bindTo: ValueExpression): ValueExpression {
    if (!this.#viewSettingsExpression) {
      this.#viewSettingsExpression = bindTo.extendBy("properties.localSettings.viewSettings");
    }
    return this.#viewSettingsExpression;
  }
}

export default CMProductTeaserFormBase;
