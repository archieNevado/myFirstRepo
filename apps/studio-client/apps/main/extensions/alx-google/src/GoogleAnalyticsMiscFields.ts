import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Container from "@jangaroo/ext-ts/container/Container";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

interface GoogleAnalyticsMiscFieldsConfig extends Config<PropertyFieldGroup> {
}

class GoogleAnalyticsMiscFields extends PropertyFieldGroup {
  declare Config: GoogleAnalyticsMiscFieldsConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsMiscFields";

  constructor(config: Config<GoogleAnalyticsMiscFields> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsMiscFields, {
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,
      itemId: "googleAnalyticsMiscFieldsForm",

      items: [
        Config(StringPropertyField, {
          propertyName: "localSettings.googleAnalytics.homeUrl",
          listeners: {
            afterrender: (c: Container): void => {
              cast(TextField, c.down("textfield")).vtype = "url";
            },
          },
        }),
        Config(StringPropertyField, { propertyName: "localSettings.googleAnalytics.pageReport" }),
        Config(IntegerPropertyField, { propertyName: "localSettings.googleAnalytics.accountId" }),
        Config(IntegerPropertyField, { propertyName: "localSettings.googleAnalytics.wpid" }),
        Config(IntegerPropertyField, { propertyName: "localSettings.googleAnalytics.pid" }),
      ],

    }), config));
  }
}

export default GoogleAnalyticsMiscFields;
