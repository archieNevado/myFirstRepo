package com.coremedia.livecontext.ecommerce.ibm.workspace;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;

/**
 * A service that uses the rest connector to get workspaces from ibm wcs.
 */
public class WcWorkspaceWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<Map, Void>
    FIND_ALL_WORKSPACES = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/workspaces/byall/Active", true, true, Void.class, Map.class);

  /**
   * Gets a list of all existing workspaces.
   *
   * @param storeContext the current store context
   * @param userContext the current user context
   * @return Map
   */
  public Map<String, Object> findAllWorkspaces(final StoreContext storeContext, final UserContext userContext) {
    try {
      //noinspection unchecked
     return getRestConnector().callService(
             FIND_ALL_WORKSPACES, Collections.singletonList(getStoreId(storeContext)),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
        null, storeContext, userContext);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

}
