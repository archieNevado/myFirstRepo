package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.ZonedDateTime.now;

/**
 * Builds JSON to be stored in sfcc content asset documents
 */
class SfccPushJsonFactory {

  static final String PAGE_KEY_PROPERTY = "pageKey";
  static final String FRAGMENTS_PROPERTY = "fragments";
  static final String MODIFICATION_DATE_PROPERTY = "modificationDate";
  static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm VV");

  private static final String FRAGMENT_KEY_PROPERTY = "fragmentKey";
  private static final String PAYLOAD_PROPERTY = "payload";

  private SfccPushJsonFactory() {
  }

  /**
   * Parse fragment payload per page and prepare JSON to be pushed to the sfcc system.
   * @param fragmentsPerPageKey map(pageKey, map(String fragmentKey, String fragmentPayload)
   * @return map(pageKey, JSON)
   */
  static Map<String, JSONObject> createJsonObjectsPerPage(Map<String, Map<String, String>> fragmentsPerPageKey) {
    Map<String, JSONObject> pageMap = new HashMap<>();
    fragmentsPerPageKey.forEach((k, v) -> pageMap.put(k, createJsonObjectForPageFragments(k, v)));

    return pageMap;
  }

  /**
   * Parse fragment payload per page and prepare JSON to be pushed to the sfcc system.
   * @param pageKey key of the current page
   * @param fragments map(String fragmentKey, String fragmentPayload)
   * @return JSON to be pushed for a single page(pageKey)
   */
  @NonNull
  @VisibleForTesting
  static JSONObject createJsonObjectForPageFragments(String pageKey, Map<String, String> fragments) {
    JSONObject rootJsonObject = new JSONObject();
    rootJsonObject.put(PAGE_KEY_PROPERTY, pageKey);
    List<JSONObject> fragmentsList = new ArrayList<>();

    fragments.forEach((k, v) -> fragmentsList.add(createFragmentJson(k, v)));

    rootJsonObject.put(FRAGMENTS_PROPERTY, fragmentsList);
    rootJsonObject.put(MODIFICATION_DATE_PROPERTY, FORMATTER.format(now())); //add timestamp

    return rootJsonObject;
  }

  @NonNull
  private static JSONObject createFragmentJson(String fragmentKey, String payload){
    JSONObject result = new JSONObject();
    result.put(FRAGMENT_KEY_PROPERTY, fragmentKey);
    result.put(PAYLOAD_PROPERTY, payload);
    return result;
  }
}
