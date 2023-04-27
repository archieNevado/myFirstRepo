package com.coremedia.blueprint.studio.googleanalytics {

import ext.form.field.VTypes;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.blueprint.studio.googleanalytics.GoogleAnalyticsStudioPlugin')]
public class GoogleAnalyticsMeasurementIdValidator {

  public static var MEASUREMENT_ID_KEY :String = "measurementId";

  VTypes['measurementIdVal'] = new RegExp(ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.googleanalytics.GoogleAnalyticsStudioPlugin', 'googleanalytics_measurementid_val'));
  VTypes['measurementIdMask'] = new RegExp(ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.googleanalytics.GoogleAnalyticsStudioPlugin', 'googleanalytics_measurementid_mask'));
  VTypes['measurementIdText'] = ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.googleanalytics.GoogleAnalyticsStudioPlugin', 'googleanalytics_measurementid_text');
  VTypes['measurementIdId'] = function(v:*):* {
    return VTypes['measurementIdVal'].test(v);
  };
}
}
