import VTypes from "@jangaroo/ext-ts/form/field/VTypes";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

class GoogleAnalyticsWebPropertyIdValidator {

  static WEB_PROPERTY_ID_KEY: string = "webPropertyId";

  static #static = (() => {
    VTypes["webPropertyIdVal"] = new RegExp(GoogleAnalyticsStudioPlugin_properties.googleanalytics_webpropertyid_val);
    VTypes["webPropertyIdMask"] = new RegExp(GoogleAnalyticsStudioPlugin_properties.googleanalytics_webpropertyid_mask);
    VTypes["webPropertyIdText"] = GoogleAnalyticsStudioPlugin_properties.googleanalytics_webpropertyid_text;
    VTypes["webPropertyId"] = ((v: any): any =>
      VTypes["webPropertyIdVal"].test(v)
    );
  })();
}

export default GoogleAnalyticsWebPropertyIdValidator;
