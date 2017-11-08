package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;

/**
 * Wrapper service for person requests.
 */
public class WcPersonWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<Map, Map>
          FIND_PERSON_BY_SELF = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/person/@self", true, true, Map.class, Map.class);

  @Nullable
  public Map<String, Object> findPerson(UserContext userContext, @Nonnull StoreContext storeContext) {
    Map<String, String[]> parametersMap = createParametersMap(null, getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null, storeContext);
    //noinspection unchecked
    Map<String, Object> map = getRestConnector().callService(
            FIND_PERSON_BY_SELF, Collections.singletonList(getStoreId(storeContext)), parametersMap, null, storeContext, userContext);
    return map;
  }
}
