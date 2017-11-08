package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
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

  private boolean useServiceCallsForStudio = false;

  private static final WcRestServiceMethod<Map, Void>
          FIND_MARKETING_SPOTS = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/spot?q=byType&qType=MARKETING", true, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_MARKETING_SPOTS_V7_6 = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/marketingspot/byall/all", false, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_MARKETING_SPOTS_BY_SEARCH_TERM = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/spot?q=byTypeAndName&qType=MARKETING&qName={searchTerm}", true, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_MARKETING_SPOT_BY_TECH_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/spot/{id}", true, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_MARKETING_SPOT_BY_EXTERNAL_ID_CAE = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/espot/{id}", false, true, Map.class);

  /**
   * Gets a marketing spot wrapper map by a given technical id.
   *
   * @param technicalId  the technical id
   * @param storeContext the store context
   * @param userContext  the user context
   * @return the spot wrapper map or null if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @SuppressWarnings("unchecked")
  protected Map<String, Object> findMarketingSpotByExternalTechId(String technicalId, StoreContext storeContext,
                                                                  UserContext userContext) {
    try {
      Map<String, Object> data = getRestConnector().callService(
              FIND_MARKETING_SPOT_BY_TECH_ID, asList(getStoreId(storeContext), technicalId),
              createParametersMap(null, getLocale(storeContext), getCurrency(storeContext),
                      UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null,
                      storeContext),
              null, storeContext, userContext);

      if (data == null || StringUtils.isEmpty(DataMapHelper.getValueForPath(data, "MarketingSpot[0].spotName"))) {
        return null;
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
   * @return the marketing spot wrapper map or null if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  public Map<String, Object> findMarketingSpotById(@Nonnull CommerceId id, StoreContext storeContext, UserContext userContext) {
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
  public Map<String, Object> findMarketingSpots(StoreContext storeContext, UserContext userContext) {
    try {
      Map<String, Object> spotsWrapperMap;
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        //noinspection unchecked
        spotsWrapperMap = getRestConnector().callService(
                FIND_MARKETING_SPOTS_V7_6, singletonList(getStoreId(storeContext)),
                createParametersMap(null, getLocale(storeContext), getCurrency(storeContext),
                        UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext),
                        null, storeContext), null, storeContext, userContext);
      } else {
        //noinspection unchecked
        spotsWrapperMap = getRestConnector().callService(
                FIND_MARKETING_SPOTS, singletonList(getStoreId(storeContext)),
                createParametersMap(null, getLocale(storeContext), getCurrency(storeContext),
                        UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext),
                        null, storeContext), null, storeContext, userContext);
      }
      return spotsWrapperMap;
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
  public Map<String, Object> findMarketingSpotsBySearchTerm(String searchTerm, StoreContext storeContext,
                                                            UserContext userContext) {
    if (searchTerm == null || searchTerm.trim().isEmpty() || "*".equals(searchTerm)) {
      return findMarketingSpots(storeContext, userContext);
    }

    try {
      //noinspection unchecked
      return getRestConnector().callService(
              FIND_MARKETING_SPOTS_BY_SEARCH_TERM, asList(getStoreId(storeContext), searchTerm),
              createParametersMap(null, getLocale(storeContext), getCurrency(storeContext),
                      UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null,
                      storeContext),
              null, storeContext, userContext);
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
   * @return the spot wrapper map or null if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> findMarketingSpotByExternalIdCae(String externalId, StoreContext storeContext,
                                                               UserContext userContext) {
    try {
      Map<String, Object> data = getRestConnector().callService(
              FIND_MARKETING_SPOT_BY_EXTERNAL_ID_CAE, asList(getStoreId(storeContext), externalId),
              createParametersMap(null, getLocale(storeContext), getCurrency(storeContext),
                      UserContextHelper.getForUserId(userContext),
                      UserContextHelper.getForUserName(userContext), null, storeContext),
              null, storeContext, userContext);

      if (data == null || StringUtils.isEmpty(DataMapHelper.getValueForPath(data, "MarketingSpotData[0].eSpotName"))) {
        return null;
      }

      return data;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  private Map<String, Object> findMarketingSpotByExternalIdStudio(String externalId, StoreContext storeContext,
                                                                  UserContext userContext) {
    Map<String, Object> marketingSpotHits = findMarketingSpotsBySearchTerm(externalId, storeContext, userContext);
    // this method shall return the metadata resourceId and resourceName as well
    // in order to let this wrapper service return a uniform data format, thus
    Map<String, Object> marketingSpotWrappedHit = new HashMap<>();
    marketingSpotWrappedHit.put("resourceId", marketingSpotHits.get("resourceId"));
    String resourceName = (String) marketingSpotHits.get("resourceName");
    marketingSpotWrappedHit.put("resourceName", resourceName);

    //noinspection unchecked
    List<Map<String, Object>> marketingSpotsFromHits = DataMapHelper.getValueForKey(marketingSpotHits,
            "espot".equals(resourceName) ? "MarketingSpotData" : "MarketingSpot", List.class);

    if (marketingSpotsFromHits == null) {
      return null;
    }

    for (Map<String, Object> spot : marketingSpotsFromHits) {
      if (externalId.equals(DataMapHelper.getValueForKey(spot, "eSpotName"))) {
        marketingSpotWrappedHit.put("MarketingSpotData", asList(spot));
      } else if (externalId.equals(DataMapHelper.getValueForKey(spot, "spotName"))) {
        marketingSpotWrappedHit.put("MarketingSpot", asList(spot));
      }
    }

    return marketingSpotWrappedHit;
  }

  private Map<String, Object> findMarketingSpotByExternalId(String externalId, StoreContext storeContext,
                                                            UserContext userContext) {
    if (useServiceCallsForStudio) {
      return findMarketingSpotByExternalIdStudio(externalId, storeContext, userContext);
    } else {
      return findMarketingSpotByExternalIdCae(externalId, storeContext, userContext);
    }
  }

  @SuppressWarnings("unused")
  public Map<String, Object> searchMarketingSpots(String searchTerm, Map<String, String> searchParams,
                                                  StoreContext storeContext, UserContext userContext) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return emptyMap();
    }

    try {
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        List<Map<String, Object>> result = emptyList();
        Map<String, Object> allMarketingSpots = findMarketingSpots(storeContext, userContext);

        //noinspection unchecked
        List<Map<String, Object>> wrappedSpots = DataMapHelper.getValueForKey(allMarketingSpots, "MarketingSpots",
                List.class);

        if (wrappedSpots != null) {
          for (Map<String, Object> spot : wrappedSpots) {
            String spotName = DataMapHelper.getValueForKey(spot, "eSpotName", String.class);
            if (StringUtils.isEmpty(searchTerm) || "*".equals(searchTerm) ||
                    (!StringUtils.isEmpty(spotName) && spotName.toLowerCase().contains(searchTerm.toLowerCase()))) {
              result.add(spot);
            }
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
}

