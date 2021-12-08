import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import RemoteError from "@coremedia/studio-client.client-core/data/error/RemoteError";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import AnalyticsStudioPlugin from "./AnalyticsStudioPlugin";

interface AnalyticsStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class AnalyticsStudioPluginBase extends StudioPlugin {
  declare Config: AnalyticsStudioPluginBaseConfig;

  /**
   * Value expression holding the analytics settings document (if it exists)
   */
  static readonly SETTINGS: ValueExpression = ValueExpressionFactory.create("analyticsSettings");

  static readonly #ALX_SETTINGS_DOCUMENT: string = "/Settings/Options/Settings/AnalyticsSettings";

  constructor(config: Config<AnalyticsStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);
    AnalyticsStudioPluginBase.fetchAnalyticsSettings();
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_YELLOW, ["CMALXPageList", "CMALXEventList"]);
  }

  static fetchAnalyticsSettings(): void {
    session._.getConnection().getContentRepository().getChild(AnalyticsStudioPluginBase.#ALX_SETTINGS_DOCUMENT, AnalyticsStudioPluginBase.receiveAnalyticsSettings);
  }

  static receiveAnalyticsSettings(content: Content, absPath: string, error: RemoteError): void {
    if (content) {
      AnalyticsStudioPluginBase.SETTINGS.setValue(content);
    } else if (error.status === 403) {
      trace("[WARN] analytics settings document not readable for current user: " + absPath);
      error.setHandled(true);
    } else {
      // inform user that alx settings are missing
      trace("[WARN] analytics settings are missing: create or import that document");
    }
  }

}

export default AnalyticsStudioPluginBase;
