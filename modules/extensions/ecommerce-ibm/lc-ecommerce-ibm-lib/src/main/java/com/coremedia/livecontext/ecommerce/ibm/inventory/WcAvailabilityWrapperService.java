package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

/**
 * A service that uses the getRestConnector() to get inventory wrappers by certain search queries.
 */
public class WcAvailabilityWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<Map, Void> GET_AVAILABILITY_FOR_PRODUCT_VARIANTS =
          WcRestConnector.createServiceMethod(HttpMethod.GET,
                  "store/{storeId}/inventoryavailability/{productVariantList}", false, true, Map.class);

  public Map<String, Object> getInventoryAvailability(String skuIds, StoreContext storeContext) {
    if (skuIds == null || skuIds.isEmpty()) {
      return emptyMap();
    }

    List<String> variableValues = asList(getStoreId(storeContext), skuIds);

    Locale locale = getLocale(storeContext);
    Map<String, String[]> optionalParameters = createParametersMap(null, locale, null, storeContext);

    Map<String, Object> wcInventoryAvailabilityList = getRestConnector().callService(
            GET_AVAILABILITY_FOR_PRODUCT_VARIANTS, variableValues, optionalParameters, null, storeContext, null);

    if (wcInventoryAvailabilityList == null) {
      return emptyMap();
    }

    return wcInventoryAvailabilityList;
  }
}
