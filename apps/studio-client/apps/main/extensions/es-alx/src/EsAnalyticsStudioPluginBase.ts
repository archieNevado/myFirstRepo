import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import RemoteError from "@coremedia/studio-client.client-core/data/error/RemoteError";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import EsAnalyticsStudioPlugin from "./EsAnalyticsStudioPlugin";

interface EsAnalyticsStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class EsAnalyticsStudioPluginBase extends StudioPlugin {
  declare Config: EsAnalyticsStudioPluginBaseConfig;

  /**
   * Value expression holding the analytics settings document (if it exists)
   */
  static readonly SETTINGS: ValueExpression = ValueExpressionFactory.create("analyticsSettings");

  static readonly #ALX_SETTINGS_DOCUMENT: string = "/Settings/Options/Settings/AnalyticsSettings";

  constructor(config: Config<EsAnalyticsStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);
    EsAnalyticsStudioPluginBase.fetchAnalyticsSettings();
  }

  static fetchAnalyticsSettings(): void {
    session._.getConnection().getContentRepository().getChild(EsAnalyticsStudioPluginBase.#ALX_SETTINGS_DOCUMENT, EsAnalyticsStudioPluginBase.receiveAnalyticsSettings);
  }

  static receiveAnalyticsSettings(content: Content, absPath: string, error: RemoteError): void {
    if (content) {
      EsAnalyticsStudioPluginBase.SETTINGS.setValue(content);
    } else if (error.status === 403) {
      trace("[WARN] analytics settings not readable for current user");
      error.setHandled(true);
    } else {
      // inform user that alx settings are missing
      trace("[WARN] analytics settings are missing: create or import that document");
    }
  }

}

export default EsAnalyticsStudioPluginBase;
