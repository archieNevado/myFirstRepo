package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.InvalidLoginException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.hasText;

public class WcLoginWrapperService extends AbstractWcWrapperService {

  public static final String ERROR_KEY_AUTHENTICATION_ERROR = "_ERR_AUTHENTICATION_ERROR";

  private static final WcRestServiceMethod<WcSession, WcLoginParam>
          LOGIN_IDENTITY = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/loginidentity", true, false, false, WcLoginParam.class, WcSession.class);

  private static final WcRestServiceMethod<Void, Void>
          LOGOUT_IDENTITY = WcRestConnector.createServiceMethod(HttpMethod.DELETE, "store/{storeId}/loginidentity/@self", true, true, false, null, Void.class);

  private static final WcRestServiceMethod<WcPreviewToken, WcPreviewTokenParam>
          PREVIEW_TOKEN = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/previewToken", true, true, false, WcPreviewTokenParam.class, WcPreviewToken.class);

  private static final WcRestServiceMethod<HashMap, Void>
          USER_CONTEXT_DATA = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/usercontext/@self/contextdata", false, false, false, true, null, HashMap.class);

  public WcSession login(String logonId, String password, StoreContext storeContext) {
    try {
      return getRestConnector().callServiceInternal(
              LOGIN_IDENTITY, asList(getStoreId(storeContext)), emptyMap(), new WcLoginParam(logonId, password), storeContext, null);

      //if login not successfully a RemoteException is thrown
    } catch (CommerceRemoteException e) {
      if (ERROR_KEY_AUTHENTICATION_ERROR.equals(e.getErrorKey())) {
        throw new InvalidLoginException("The specified logon ID '" + logonId + "' or the used password is incorrect.");
      } else {
        throw e;
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean isLoggedIn(String logonId, StoreContext storeContext, UserContext userContext) {
    try {
      Map<String, String[]> parameters = buildParameterMap()
              .withCurrency(storeContext)
              .withLanguageId(storeContext)
              .build();

      Map userContextData = getRestConnector().callServiceInternal(USER_CONTEXT_DATA, asList(getStoreId(storeContext)),
              parameters, null, storeContext, userContext);

      if (userContextData != null && hasText(logonId)) {
        Double value = DataMapHelper.getValueForPath(userContextData, "basicInfo.callerId", Double.class);
        if(value != null) {
          return equalsWithTypeConversion(logonId, value);
        }
      }
      return false;

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @VisibleForTesting
  boolean equalsWithTypeConversion(String logonId, double value) {
    int logonIdInt = Integer.parseInt(logonId);
    int integer = (int) value;
    return Objects.equals(integer, logonIdInt);
  }

  public boolean logout(String storeId) {
    getRestConnector().callServiceInternal(LOGOUT_IDENTITY, asList(storeId), emptyMap(), null, null, null);
    // Todo: if no exception is thrown we assume that the user was logged out successfully. is that correct?
    return true;
  }

  public WcPreviewToken getPreviewToken(WcPreviewTokenParam bodyData, StoreContext storeContext) {
    try {
      return getRestConnector().callService(
              PREVIEW_TOKEN, singletonList(getStoreId(storeContext)),
              emptyMap(), bodyData, storeContext, null);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

}
