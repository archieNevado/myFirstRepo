import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import SingleLinkEditor from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/SingleLinkEditor";
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
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(GoogleAnalyticsRetrievalFields, {
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,
      itemId: "googleAnalyticsRetrievalForm",

      items: [
        Config(SingleLinkEditor, {
          itemId: "authfile",
          linkContentType: "CMDownload",
          bindTo: this$.getAuthFileVE(),
          parentContentValueExpression: config.bindTo,
          linkListLabel: GoogleAnalyticsStudioPlugin_properties.googleanalytics_authfile,
        }),
        Config(IntegerPropertyField, {
          itemId: "propertyId",
          propertyName: "localSettings.googleAnalytics.propertyId",
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

    }), config));
  }
}

export default GoogleAnalyticsRetrievalFields;
