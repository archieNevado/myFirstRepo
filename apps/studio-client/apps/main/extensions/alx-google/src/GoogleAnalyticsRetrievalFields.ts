import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import SingleLinkEditor from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/SingleLinkEditor";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsRetrievalFieldsBase from "./GoogleAnalyticsRetrievalFieldsBase";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

interface GoogleAnalyticsRetrievalFieldsConfig extends Config<GoogleAnalyticsRetrievalFieldsBase> {
}

class GoogleAnalyticsRetrievalFields extends GoogleAnalyticsRetrievalFieldsBase {
  declare Config: GoogleAnalyticsRetrievalFieldsConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsRetrievalFields";

  constructor(config: Config<GoogleAnalyticsRetrievalFields> = null) {
    super((()=> ConfigUtils.apply(Config(GoogleAnalyticsRetrievalFields, {
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,
      itemId: "googleAnalyticsRetrievalForm",

      items: [
        Config(StringPropertyField, {
          itemId: "applicationName",
          propertyName: "localSettings.googleAnalytics.applicationName",
        }),
        Config(StringPropertyField, {
          itemId: "serviceAccountEmail",
          propertyName: "localSettings.googleAnalytics.serviceAccountEmail",
        }),
        Config(SingleLinkEditor, {
          itemId: "p12file",
          linkContentType: "CMDownload",
          bindTo: this.getP12FileVE(),
          parentContentValueExpression: config.bindTo,
          linkListLabel: GoogleAnalyticsStudioPlugin_properties.googleanalytics_p12file,
        }),
        Config(IntegerPropertyField, {
          itemId: "pid",
          propertyName: "localSettings.googleAnalytics.pid",
        }),
        Config(IntegerPropertyField, {
          itemId: "limit",
          propertyName: "localSettings.googleAnalytics.limit",
        }),
        Config(IntegerPropertyField, {
          itemId: "interval",
          propertyName: "localSettings.googleAnalytics.interval",
        }),
      ],

    }), config))());
  }
}

export default GoogleAnalyticsRetrievalFields;
