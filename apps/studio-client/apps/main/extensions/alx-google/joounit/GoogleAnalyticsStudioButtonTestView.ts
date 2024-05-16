import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsReportPreviewButton from "../src/GoogleAnalyticsReportPreviewButton";

interface GoogleAnalyticsStudioButtonTestViewConfig extends Config<Viewport>, Partial<Pick<GoogleAnalyticsStudioButtonTestView,
  "contentValueExpression"
>> {
}

class GoogleAnalyticsStudioButtonTestView extends Viewport {
  declare Config: GoogleAnalyticsStudioButtonTestViewConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsStudioButtonTestView";

  constructor(config: Config<GoogleAnalyticsStudioButtonTestView> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsStudioButtonTestView, {

      items: [
        Config(GoogleAnalyticsReportPreviewButton, { contentExpression: config.contentValueExpression }),
      ],

    }), config));
  }

  contentValueExpression: ValueExpression = null;
}

export default GoogleAnalyticsStudioButtonTestView;
