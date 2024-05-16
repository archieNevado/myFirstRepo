import OpenAnalyticsDeepLinkUrlButton from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/OpenAnalyticsDeepLinkUrlButton";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import WebtrendsStudioPlugin_properties from "./WebtrendsStudioPlugin_properties";

interface WebtrendsReportPreviewButtonConfig extends Config<OpenAnalyticsDeepLinkUrlButton> {
}

/**
 * Button to open a new browser window with the Webtrends report for the current preview document.
 */
class WebtrendsReportPreviewButton extends OpenAnalyticsDeepLinkUrlButton {
  declare Config: WebtrendsReportPreviewButtonConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.webtrends.webtrendsReportPreviewButton";

  constructor(config: Config<WebtrendsReportPreviewButton> = null) {
    super(ConfigUtils.apply(Config(WebtrendsReportPreviewButton, {
      windowName: "WebtrendsReportWindow",
      tooltip: WebtrendsStudioPlugin_properties.webtrends_preview_btn_tooltip,
      text: WebtrendsStudioPlugin_properties.webtrends_preview_btn_tooltip,
      itemId: "btn-analytics-report-webtrends",
      iconCls: CoreIcons_properties.analytics,
      ui: ButtonSkin.WORKAREA.getSkin(),
      scale: "medium",
      serviceName: "webtrends",

    }), config));
  }
}

export default WebtrendsReportPreviewButton;
