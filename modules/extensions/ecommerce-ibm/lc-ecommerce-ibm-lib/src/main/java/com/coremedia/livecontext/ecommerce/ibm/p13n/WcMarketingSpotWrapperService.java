package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_6;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

/**
 * A service that uses the getRestConnector() to get marketing spot wrapper maps by certain search queries.
 * The maps represent the responses from the WCS REST interface. Responses are produced by two different handlers
 * returning two different data formats which must be handled by the client of this class. Please note the
 * IBM WCS documentation for more details on the format.
 */
public class WcMarketingSpotWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<Map, Void> FIND_MARKETING_SPOTS = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/spot?q=byType&qType=MARKETING", Void.class, Map.class)
          .secure(true)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private static final WcRestServiceMethod<Map, Void> FIND_MARKETING_SPOTS_V7_6 = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/marketingspot/byall/all", Void.class, Map.class)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private static final WcRestServiceMethod<Map, Void> FIND_MARKETING_SPOTS_BY_SEARCH_TERM = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/spot?q=byTypeAndName&qType=MARKETING&qName={searchTerm}", Void.class, Map.class)
          .secure(true)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private static final WcRestServiceMethod<Map, Void> FIND_MARKETING_SPOT_BY_TECH_ID = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/spot/{id}", Void.class, Map.class)
          .secure(true)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private static final WcRestServiceMethod<Map, Void> FIND_MARKETING_SPOT_BY_EXTERNAL_ID_CAE = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/espot/{id}", Void.class, Map.class)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private boolean useServiceCallsForStudio = false;

  /**
   * Gets a marketing spot wrapper map by a given technical id.
   *
   * @param technicalId  the technical id
   * @param storeContext the store context
   * @param userContext  the user context
   * @return the spot wrapper map; empty if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @NonNull
  @SuppressWarnings("unchecked")
  protected Map<String, Object> findMarketingSpotByExternalTechId(@NonNull String technicalId,
                                                                  @NonNull StoreContext storeContext,
                                                                  UserContext userContext) {
    try {
      List<String> variableValues = asList(getStoreId(storeContext), technicalId);

      Map<String, String[]> parameters = getOptionalParameters(storeContext, userContext);

      Map<String, Object> data = getRestConnector().callService(FIND_MARKETING_SPOT_BY_TECH_ID, variableValues,
              parameters, null, storeContext, userContext)
              .orElseGet(Collections::emptyMap);

      if (data.isEmpty() || StringUtils.isEmpty(DataMapHelper.getValueForPath(data, "MarketingSpot[0].spotName"))) {
        return emptyMap();
      }

      return data;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a marketing spot wrapper map by a given id (in a coremedia internal format like "ibm:///catalog/marketingspot/0815").
   *
   * @param id           the coremedia internal id
   * @param storeContext the store context
   * @param userContext  the user context
   * @return the marketing spot wrapper map; empty if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  @NonNull
  public Map<String, Object> findMarketingSpotById(@NonNull CommerceId id, @NonNull StoreContext storeContext,
                                                   UserContext userContext) {
    Optional<String> techId = id.getTechId();
    if (techId.isPresent()) {
      return findMarketingSpotByExternalTechId(techId.get(), storeContext, userContext);
    }

    String externalId = CommerceIdHelper.getExternalIdOrThrow(id);
    return findMarketingSpotByExternalId(externalId, storeContext, userContext);
  }

  /**
   * Gets a map containing a list of all marketing spots.
   *
   * @param storeContext the current store context
   * @return Map&lt;String, Object&gt; representing the REST resonse of WCS
   */
  @NonNull
  public Map<String, Object> findMarketingSpots(@NonNull StoreContext storeContext, UserContext userContext) {
    try {
      List<String> variableValues = singletonList(getStoreId(storeContext));

      Map<String, String[]> parameters = getOptionalParameters(storeContext, userContext);

      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        //noinspection unchecked
        return getRestConnector().callService(FIND_MARKETING_SPOTS_V7_6, variableValues, parameters, null, storeContext,
                userContext)
                .orElseGet(Collections::emptyMap);
      } else {
        //noinspection unchecked
        return getRestConnector().callService(FIND_MARKETING_SPOTS, variableValues, parameters, null, storeContext,
                userContext)
                .orElseGet(Collections::emptyMap);
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a map containing a list of matching marketing spots by search term.
   *
   * @param searchTerm   which will be used to find all spots with at least a partial match in the name or description
   * @param storeContext the current store context
   * @param userContext  the current user context
   * @return Map&lt;String, Object&gt; representing the REST response of WCS
   */
  @NonNull
  public Map<String, Object> findMarketingSpotsBySearchTerm(String searchTerm, @NonNull StoreContext storeContext,
                                                            UserContext userContext) {
    if (searchTerm == null || searchTerm.trim().isEmpty() || "*".equals(searchTerm)) {
      return findMarketingSpots(storeContext, userContext);
    }

    try {
      List<String> variableValues = asList(getStoreId(storeContext), searchTerm);

      Map<String, String[]> parameters = getOptionalParameters(storeContext, userContext);

      //noinspection unchecked
      return getRestConnector().callService(FIND_MARKETING_SPOTS_BY_SEARCH_TERM, variableValues, parameters, null,
              storeContext, userContext)
              .orElseGet(Collections::emptyMap);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a marketing spot wrapper map by a given external id.
   *
   * @param externalId   the external id
   * @param storeContext the store context
   * @param userContext  the user context
   * @return the spot wrapper map; empty if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @NonNull
  @SuppressWarnings("unchecked")
  private Map<String, Object> findMarketingSpotByExternalIdCae(String externalId, @NonNull StoreContext storeContext,
                                                               UserContext userContext) {
    try {
      List<String> variableValues = asList(getStoreId(storeContext), externalId);

      Map<String, String[]> parameters = getOptionalParameters(storeContext, userContext);

      Map<String, Object> data = getRestConnector().callService(FIND_MARKETING_SPOT_BY_EXTERNAL_ID_CAE, variableValues,
              parameters, null, storeContext, userContext)
              .orElseGet(Collections::emptyMap);

      if (data.isEmpty() || StringUtils.isEmpty(DataMapHelper.getValueForPath(data, "MarketingSpotData[0].eSpotName"))) {
        return emptyMap();
      }

      return data;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @NonNull
  private Map<String, Object> findMarketingSpotByExternalIdStudio(String externalId, @NonNull StoreContext storeContext,
                                                                  UserContext userContext) {
    Map<String, Object> marketingSpotHits = findMarketingSpotsBySearchTerm(externalId, storeContext, userContext);
    //no eSpot found -> exit early
    int hits = DataMapHelper.findValue(marketingSpotHits, "recordSetTotal", Integer.class).orElse(0);
    if (hits == 0) {
      return emptyMap();
    }

    // this method shall return the metadata resourceId and resourceName as well
    // in order to let this wrapper service return a uniform data format, thus
    Map<String, Object> marketingSpotWrappedHit = new HashMap<>();
    marketingSpotWrappedHit.put("resourceId", marketingSpotHits.get("resourceId"));
    String resourceName = (String) marketingSpotHits.get("resourceName");
    marketingSpotWrappedHit.put("resourceName", resourceName);

    //noinspection unchecked
    List<Map<String, Object>> marketingSpotsFromHits = DataMapHelper.findValue(marketingSpotHits,
            "espot".equals(resourceName) ? "MarketingSpotData" : "MarketingSpot", List.class)
            .orElseGet(Collections::emptyList);

    if (marketingSpotsFromHits.isEmpty()) {
      return emptyMap();
    }

    // read eSpot data from data map.
    // since the search method also returns hits for substrings,
    // we need to check if the externalId matches completely.
    for (Map<String, Object> spot : marketingSpotsFromHits) {
      if (externalId.equals(DataMapHelper.getValueForKey(spot, "eSpotName"))) {
        marketingSpotWrappedHit.put("MarketingSpotData", asList(spot));
      } else if (externalId.equals(DataMapHelper.getValueForKey(spot, "spotName"))) {
        marketingSpotWrappedHit.put("MarketingSpot", asList(spot));
      }
    }

    // if no externalId matched exactly, return empty map/no resource found
    if (!marketingSpotWrappedHit.containsKey("MarketingSpotData")
            && !marketingSpotWrappedHit.containsKey("MarketingSpot")) {
      return emptyMap();
    }

    return marketingSpotWrappedHit;
  }

  @NonNull
  private Map<String, Object> findMarketingSpotByExternalId(String externalId, @NonNull StoreContext storeContext,
                                                            UserContext userContext) {
    if (useServiceCallsForStudio) {
      return findMarketingSpotByExternalIdStudio(externalId, storeContext, userContext);
    } else {
      return findMarketingSpotByExternalIdCae(externalId, storeContext, userContext);
    }
  }

  @NonNull
  @SuppressWarnings("unused")
  public Map<String, Object> searchMarketingSpots(String searchTerm, Map<String, String> searchParams,
                                                  @NonNull StoreContext storeContext, UserContext userContext) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return emptyMap();
    }

    try {
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        List<Map<String, Object>> result = emptyList();
        Map<String, Object> allMarketingSpots = findMarketingSpots(storeContext, userContext);

        //noinspection unchecked
        List<Map<String, Object>> wrappedSpots = DataMapHelper.findValue(allMarketingSpots, "MarketingSpots",
                List.class)
                .orElseGet(Collections::emptyList);

        for (Map<String, Object> spot : wrappedSpots) {
          String spotName = DataMapHelper.findStringValue(spot, "eSpotName").orElse("");
          if (StringUtils.isEmpty(searchTerm)
                  || "*".equals(searchTerm)
                  || (!spotName.isEmpty() && spotName.toLowerCase().contains(searchTerm.toLowerCase()))) {
            result.add(spot);
          }
        }

        Map<String, Object> resultWithMetadata = new HashMap<>();
        resultWithMetadata.put("resourceId", allMarketingSpots.get("resourceId"));
        resultWithMetadata.put("resourceName", allMarketingSpots.get("resourceName"));
        resultWithMetadata.put("MarketingSpots", result);
        return resultWithMetadata;
      } else {
        return findMarketingSpotsBySearchTerm(searchTerm, storeContext, userContext);
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  public void setUseServiceCallsForStudio(boolean useServiceCallsForStudio) {
    this.useServiceCallsForStudio = useServiceCallsForStudio;
  }

  @SuppressWarnings("unused")
  public boolean isUseServiceCallsForStudio() {
    return useServiceCallsForStudio;
  }

  @NonNull
  private Map<String, String[]> getOptionalParameters(@NonNull StoreContext storeContext,
                                                      @Nullable UserContext userContext) {
    return buildParameterMap()
            .withCurrency(storeContext)
            .withLanguageId(storeContext)
            .withUserIdOrName(userContext)
            .build();
  }
}
