import OpenAnalyticsDeepLinkUrlButton from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/OpenAnalyticsDeepLinkUrlButton";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

interface GoogleAnalyticsReportPreviewButtonConfig extends Config<OpenAnalyticsDeepLinkUrlButton> {
}

/**
 * Button to open a new browser window with the Google Analytics report for the current preview document.
 */
class GoogleAnalyticsReportPreviewButton extends OpenAnalyticsDeepLinkUrlButton {
  declare Config: GoogleAnalyticsReportPreviewButtonConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsReportPreviewButton";

  constructor(config: Config<GoogleAnalyticsReportPreviewButton> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsReportPreviewButton, {
      windowName: "GoogleReportWindow",
      tooltip: GoogleAnalyticsStudioPlugin_properties.googleanalytics_preview_btn_tooltip,
      text: GoogleAnalyticsStudioPlugin_properties.googleanalytics_preview_btn_tooltip,
      itemId: "btn-analytics-report-google",
      iconCls: CoreIcons_properties.analytics,
      ui: ButtonSkin.WORKAREA.getSkin(),
      scale: "medium",
      serviceName: "googleAnalytics",

    }), config));
  }
}

export default GoogleAnalyticsReportPreviewButton;
