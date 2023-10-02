import VTypes from "@jangaroo/ext-ts/form/field/VTypes";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

class GoogleAnalyticsMeasurementIdValidator {

  static MEASUREMENT_ID_KEY: string = "measurementId";

  static {
    VTypes["measurementIdVal"] = new RegExp(GoogleAnalyticsStudioPlugin_properties.googleanalytics_measurementid_val);
  VTypes["measurementIdMask"] = new RegExp(GoogleAnalyticsStudioPlugin_properties.googleanalytics_measurementid_mask);
  VTypes["measurementIdText"] = GoogleAnalyticsStudioPlugin_properties.googleanalytics_measurementid_text;
  VTypes["measurementIdId"] = ((v: any): any =>
    VTypes["measurementIdVal"].test(v)
  );
  }
}

export default GoogleAnalyticsMeasurementIdValidator;
