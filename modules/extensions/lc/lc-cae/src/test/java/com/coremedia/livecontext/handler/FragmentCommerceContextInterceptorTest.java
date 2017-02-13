package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.ContextBuilder;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FragmentCommerceContextInterceptorTest {

  private FragmentCommerceContextInterceptor testling;

  @Mock
  private LiveContextSiteResolver siteLinkHelper;

  @Mock
  private Site site;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private BaseCommerceConnection connection;

  @Mock
  private ContractService contractService;

  @Before
  public void setup() {
    initMocks(this);

    connection = MockCommerceEnvBuilder.create().setupEnv();
    connection.getStoreContext().put(StoreContextImpl.SITE, "siteId");
    connection.setContractService(contractService);
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(connection));

    testling = new FragmentCommerceContextInterceptor();
    testling.setSiteResolver(siteLinkHelper);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);
  }

  @Test
  public void testInitUserContextProvider() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initUserContext(connection, request);

    UserContext userContext = Commerce.getCurrentConnection().getUserContext();
    assertThat(userContext.getUserId()).isEqualTo("userId");
    assertThat(userContext.getUserName()).isEqualTo("loginId");
  }

  @Test
  public void testInitStoreContextWithContractIds() {
    testling.setPreview(true);
    Collection<Contract> contracts = new ArrayList<>();
    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);
    when(contract1.getExternalTechId()).thenReturn("contract1");
    when(contract2.getExternalTechId()).thenReturn("contract2");
    contracts.add(contract1);
    contracts.add(contract2);
    when(contractService.findContractIdsForUser(any(UserContext.class), any(StoreContext.class), anyString()))
            .thenReturn(contracts);

    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    testling.initUserContext(connection, request);
    String[] contractIdsInStoreContext = Commerce.getCurrentConnection().getStoreContext().getContractIds();
    List storeContextList = Arrays.asList(contractIdsInStoreContext);
    Collections.sort(storeContextList);
    List expected = Arrays.asList("contract1", "contract2");
    Collections.sort(storeContextList);

    assertThat(storeContextList.toArray()).isEqualTo(expected.toArray());
  }

  @Test
  public void testInitStoreContextWithContractIdsButDisabledProcessing() {
    testling.setPreview(true);
    testling.setContractsProcessingEnabled(false);
    Collection<Contract> contracts = new ArrayList<>();
    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);
    when(contract1.getExternalTechId()).thenReturn("contract1");
    when(contract2.getExternalTechId()).thenReturn("contract2");
    contracts.add(contract1);
    contracts.add(contract2);
    when(contractService.findContractIdsForUser(any(UserContext.class), any(StoreContext.class)))
            .thenReturn(contracts);

    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    testling.initUserContext(connection, request);
    String[] contractIdsInStoreContext = Commerce.getCurrentConnection().getStoreContext().getContractIds();
    assertThat(contractIdsInStoreContext).isNull();
  }

  @Test
  public void testInitStoreContextProviderInPreview() {
    testling.setPreview(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");
    LiveContextContextHelper.setContext(request, fragmentContext);

    Optional<CommerceConnection> commerceConnection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(commerceConnection).isPresent();

    StoreContext storeContext = commerceConnection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isEqualTo("memberGroup1, memberGroup2");
    assertThat(storeContext.getWorkspaceId()).isEqualTo("4711");

    assertThat(storeContext.getPreviewDate()).isEqualTo("02-07-2014 17:57 Europe/Berlin");

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    assertThat(storeContext.getPreviewDate()).isEqualTo(sdb.format(calendar.getTime()) + " Europe/Berlin");
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isEqualTo(calendar);
  }

  @Test
  public void testInitStoreContextProviderWithTimeShift() {
    testling.setPreview(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "US/Pacific");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);

    StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
    assertThat(storeContext.getPreviewDate()).isEqualTo("02-07-2014 17:57 US/Pacific");

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    String requestParam = FragmentCommerceContextInterceptor.convertToPreviewDateRequestParameterFormat(calendar);
    assertThat(storeContext.getPreviewDate()).isEqualTo(requestParam);
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isEqualTo(calendar);
  }

  @Test
  public void testConvertPreviewDate() {
    testling.setPreview(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");
    LiveContextContextHelper.setContext(request, fragmentContext);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isPresent();

    StoreContext storeContext = connection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isEqualTo("memberGroup1, memberGroup2");
    assertThat(storeContext.getWorkspaceId()).isEqualTo("4711");

    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    assertThat(storeContext.getPreviewDate()).isEqualTo("02-07-2014 17:57 Europe/Berlin");

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    assertThat(storeContext.getPreviewDate()).isEqualTo(sdb.format(calendar.getTime()) + " Europe/Berlin");
  }

  @Test
  public void testInitStoreContextProviderInLive() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");
    Context fragmentContext = ContextBuilder.create().build();
    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.workspaceId", "4711");

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isPresent();

    StoreContext storeContext = connection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isNull();
    assertThat(storeContext.getPreviewDate()).isNull();
    assertThat(storeContext.getWorkspaceId()).isNull();
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isNull();
  }

  @Nullable
  private static Calendar parsePreviewDateIntoCalendar(@Nullable String previewDate) {
    if (Strings.isNullOrEmpty(previewDate)) {
      return null;
    }

    Calendar calendar = null;

    try {
      calendar = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      calendar.setTime(sdf.parse(previewDate.substring(0, previewDate.lastIndexOf(' '))));
      calendar.setTimeZone(TimeZone.getTimeZone(previewDate.substring(previewDate.lastIndexOf(' ') + 1)));
    } catch (ParseException ignored) {
      // do nothing
    }

    return calendar;
  }
}
