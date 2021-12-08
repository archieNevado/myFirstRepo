import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Container from "@jangaroo/ext-ts/container/Container";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";
import GoogleAnalyticsWebPropertyIdValidator from "./GoogleAnalyticsWebPropertyIdValidator";

interface GoogleAnalyticsTrackingFieldsConfig extends Config<PropertyFieldGroup> {
}

class GoogleAnalyticsTrackingFields extends PropertyFieldGroup {
  declare Config: GoogleAnalyticsTrackingFieldsConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsTrackingFields";

  constructor(config: Config<GoogleAnalyticsTrackingFields> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsTrackingFields, {
      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
      itemId: "googleAnalyticsTrackingForm",
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,

      items: [
        Config(BooleanPropertyField, {
          dontTransformToInteger: true,
          propertyName: "localSettings.googleAnalytics.disabled",
        }),
        Config(StringPropertyField, {
          propertyName: "localSettings.googleAnalytics.webPropertyId",
          listeners: {
            afterrender: (c: Container): void => {
              cast(TextField, c.down("textfield")).vtype = GoogleAnalyticsWebPropertyIdValidator.WEB_PROPERTY_ID_KEY;
            },
          },
        }),
        Config(StringPropertyField, { propertyName: "localSettings.googleAnalytics.domainName" }),
        Config(BooleanPropertyField, {
          dontTransformToInteger: true,
          propertyName: "localSettings.googleAnalytics.disableAdvertisingFeaturesPlugin",
        }),
      ],

    }), config));
  }
}

export default GoogleAnalyticsTrackingFields;
