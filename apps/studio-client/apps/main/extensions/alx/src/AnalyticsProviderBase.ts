import EditorPlugin from "@coremedia/studio-client.main.editor-components/sdk/EditorPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AnalyticsProvider from "./AnalyticsProvider";

interface AnalyticsProviderBaseConfig {
}

class AnalyticsProviderBase implements EditorPlugin {
  declare Config: AnalyticsProviderBaseConfig;

  #config: Config<AnalyticsProvider> = null;

  /**
   * Register Analytics Providers as [providerName, localizedProviderName] two-element array;
   */
  static readonly ANALYTICS_PROVIDERS: Array<any> = [];

  constructor(config: Config<AnalyticsProvider> = null) {
    this.#config = config;
  }

  init(editorContext: IEditorContext): void {
    AnalyticsProviderBase.ANALYTICS_PROVIDERS.push([this.#config.providerName, this.#config.localizedProviderName]);
  }

}
mixin(AnalyticsProviderBase, EditorPlugin);

export default AnalyticsProviderBase;
