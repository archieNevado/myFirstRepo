/**
 * Constructor of an object containing Google Analytics account data.
 *
 * @param measurementId the measurementId of the Google Analytics 4 account (e.g. 'G-BW1234ABCD') for which the page
 *    view will be counted
 */
function GaAccountData(measurementId) {
  this.measurementId = measurementId;
}

/**
 * Constructor of an object containing the data describing the current page view.
 *
 * @param contentId numeric content ID of the CoreMedia Content to be tracked
 * @param contentType type of the CoreMedia Content to be tracked, e.g. 'CMChannel'.
 * @param navigationPath a String containing the numeric content IDs of all channels from the root channel to
 *  the current page separated by '_'
 * @param pageUrl url of the current page
 * @param queryParameter the name of the url query-parameter you've configured in Google Analytics. To track site-internal
 *   searches, Google Analytics requires the query string to be supplied as the value of a parameter of the URL of the
 *   page. We'll use this value as the name of the query parameter. That is, if you supply 'query' and the query string is
 *   'foo', we'll append '?query=foo' to the URL of the page.
 * @param query the internal search query, if any
 * @param userSegments string representing the currently active CoreMedia Personalization user segments separated
 *    by '#', if any
 * @param disableAdFeaturesPlugin to disable Google's advertising feature plugin set to 'true' (default 'false')
 */
function GaPageviewData(contentId, contentType, navigationPath, pageUrl, queryParameter, query, userSegments, disableAdFeaturesPlugin) {
  this.contentId = contentId;
  this.contentType = contentType;
  this.navigationPath = navigationPath;
  this.pageUrl = pageUrl;
  this.queryParameter = queryParameter;
  this.query = query;
  this.userSegments = userSegments;
  this.disableAdvertisingFeaturesPlugin = typeof disableAdFeaturesPlugin !== 'undefined' ? disableAdFeaturesPlugin : false;
}

/**
 * Constructor of an object containing data describing an event on a page.
 *
 * @param category name of the category of the event (e.g. 'Videos')
 * @param action name of the action that has been performed  (e.g 'Play pressed')
 * @param name (optional) additional information used to specify the
 *    tracked event (e.g. content id)
 * @param value (optional) positive integer value to associate with
 *    the event (e.g. the number of seconds the video took to download)
 */
function GaEventData(category, action, name, value) {
  this.category = category;
  this.action = action;
  this.name = name;
  this.value = value;
}

/**
 * Tracks a pageview.
 *
 * Note that the actual tracking call will only be fired if Google's tracking library ('ga.js') is completely loaded.
 *
 * @param gtag the Google Analytics command queue
 * @param gaAccountData an object containing the Google Analytics account data
 * @param gaPageviewData an object containing the data about the view that is to be tracked
 */
function gaTrackPageview(gtag, gaAccountData, gaPageviewData) {

  //set Account
  gtag('config', gaAccountData.measurementId);

  if (!gaPageviewData.disableAdvertisingFeaturesPlugin) {
    gtag('set', 'allow_google_signals', true);
  }

  // if a search was performed on the website, retain the search query
  var query = "";
  if (gaPageviewData.query && gaPageviewData.queryParameter) {
    query = "?" + gaPageviewData.queryParameter + "=" + encodeURIComponent(gaPageviewData.query);
  }

  //send page view
  gtag('event', 'page_view', {
    page_location: gaPageviewData.pageUrl + query,
    dimension1: gaPageviewData.contentId,
    dimension2: gaPageviewData.contentType,
    dimension3: gaPageviewData.navigationPath,
    dimension4: gaPageviewData.userSegments,
    pagePathLevel1: getPagePathLevel1(gaPageviewData.pageUrl),
  })
}

/**
 * Tracks an event.
 *
 * @param ga the Google Analytics command queue
 * @param gaAccountData an object containing the Google Analytics account data
 * @param gaPageviewData an object containing the data about the view that is to be associated with the event
 * @param gaEventData an object containg the data about the event that is to be tracked
 */
function gaTrackEvent(ga, gaAccountData, gaPageviewData, gaEventData) {

  //set Account
  gtag('config', gaAccountData.measurementId);

  if (!gaPageviewData.disableAdvertisingFeaturesPlugin) {
    gtag('set', 'allow_google_signals', true);
  }

  //send event
  gtag('event', 'cm_event', {
    eventCategory: gaEventData.category,
    eventAction: gaEventData.action,
    eventLabel: gaEventData.name,
    eventValue: gaEventData.value,
    dimension1: gaPageviewData.contentId,
    dimension2: gaPageviewData.contentType,
    dimension3: gaPageviewData.navigationPath,
    dimension4: gaPageviewData.userSegments,
    pagePathLevel1: getPagePathLevel1(gaPageviewData.pageUrl),
  })
}

function getPagePathLevel1(pagePath) {
  var pagePathLevel1 = '';
  var parts = pagePath.split('/')
  if (parts !== undefined && parts.length > 0) {
    pagePathLevel1 = '/' + parts[1];
    if (parts.length > 1) {
      pagePathLevel1 = pagePathLevel1 + '/';
    }
  }
  return pagePathLevel1;
}
