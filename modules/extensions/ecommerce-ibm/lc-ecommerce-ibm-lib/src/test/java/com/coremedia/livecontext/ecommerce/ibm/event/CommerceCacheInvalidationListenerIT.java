package com.coremedia.livecontext.ecommerce.ibm.event;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.ibm.common.BetamaxTestHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommerceCacheInvalidationListenerIT.LocalConfig.class)
@ActiveProfiles(CommerceCacheInvalidationListenerIT.PROFILE)
public class CommerceCacheInvalidationListenerIT {

  static final String PROFILE = "CommerceCacheInvalidationListenerIT";
  static final long TEST_TIMESTAMP = 1403708220082L;

  @Inject
  CommerceCacheInvalidationListener commerceCacheInvalidationListener;

  @Inject
  WcRestConnector restConnector;

  @Mock
  private CommerceConnection connection;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());
  private StoreContext storeContext;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Commerce.setCurrentConnection(connection);
    storeContext = newStoreContext();
    when(connection.getStoreContext()).thenReturn(storeContext);
  }

  @Betamax(tape = "commercecache_testPollCacheInvalidations", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testPollCacheInvalidations() throws Exception {
    List<CommerceCacheInvalidation> commerceCacheInvalidations = commerceCacheInvalidationListener.pollCacheInvalidations(storeContext);
    assertNotNull(commerceCacheInvalidations);
    assertTrue(commerceCacheInvalidations.isEmpty());
  }

  @Betamax(tape = "commercecache_testPollCacheInvalidationsWithEntries", match = {MatchRule.path})
  @Test
  public void testPollCacheInvalidationsWithEntries() throws Exception {
    //if you have to re-record this test, you should change the timestamp to now-1h or something similar
    commerceCacheInvalidationListener.lastInvalidationTimestamp = TEST_TIMESTAMP;
    List<CommerceCacheInvalidation> commerceCacheInvalidations = commerceCacheInvalidationListener.pollCacheInvalidations(storeContext);

    assertNotNull(commerceCacheInvalidations);
    assertFalse(commerceCacheInvalidations.isEmpty());

    CommerceCacheInvalidation commerceCacheInvalidation = commerceCacheInvalidations.get(0);
    assertNotNull(commerceCacheInvalidation.getContentType());
    assertNotNull(commerceCacheInvalidation.getTechId());

    if (!commerceCacheInvalidation.getContentType().equals(CommerceCacheInvalidation.EVENT_CLEAR_ALL_EVENT_ID)) {
      assertNotNull(commerceCacheInvalidation.getId());
    }

    assertTrue(commerceCacheInvalidationListener.lastInvalidationTimestamp > TEST_TIMESTAMP ||
            commerceCacheInvalidationListener.lastInvalidationTimestamp <= 0);
  }

  @Test(expected = CommerceException.class)
  public void testPollCacheInvalidationsError() throws Exception {
    String origServiceEndpoint = restConnector.getServiceEndpoint(StoreContextHelper.getCurrentContext());
    try {
      restConnector.setServiceEndpoint("http://does.not.exists/blub");
      commerceCacheInvalidationListener.pollCacheInvalidations(storeContext);
    } finally {
      restConnector.setServiceEndpoint(origServiceEndpoint);
    }
  }

  @Test
  public void testAddCommerceBeanIdValid() {
    CommerceCacheInvalidationImpl productInvalidation = new CommerceCacheInvalidationImpl();
    productInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_PRODUCT);
    productInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(productInvalidation.getDelegate());
    assertEquals("ibm:///catalog/product/techId:4711", productInvalidation.getId());

    CommerceCacheInvalidationImpl categoryInvalidation = new CommerceCacheInvalidationImpl();
    categoryInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_CATEGORY);
    categoryInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(categoryInvalidation.getDelegate());
    assertEquals("ibm:///catalog/category/techId:4711", categoryInvalidation.getId());

    CommerceCacheInvalidationImpl topCategoryInvalidation = new CommerceCacheInvalidationImpl();
    topCategoryInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_TOP_CATEGORY);
    topCategoryInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(topCategoryInvalidation.getDelegate());
    assertEquals("ibm:///catalog/category/techId:4711", topCategoryInvalidation.getId());

    CommerceCacheInvalidationImpl segmentInvalidation = new CommerceCacheInvalidationImpl();
    segmentInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_SEGMENT);
    segmentInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(segmentInvalidation.getDelegate());
    assertEquals("ibm:///catalog/segment/4711", segmentInvalidation.getId());

    CommerceCacheInvalidationImpl marketingInvalidation = new CommerceCacheInvalidationImpl();
    marketingInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_MARKETING_SPOT);
    marketingInvalidation.setName("name");
    commerceCacheInvalidationListener.convertEvent(marketingInvalidation.getDelegate());
    assertEquals("ibm:///catalog/marketingspot/name", marketingInvalidation.getId());
  }

  @Test
  public void testAddCommerceBeanIdInValid() {
    CommerceCacheInvalidationImpl productInvalidation = new CommerceCacheInvalidationImpl();
    productInvalidation.setContentType("invalidContentType");
    productInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(productInvalidation.getDelegate());
    assertNull(productInvalidation.getId());
  }

  @Configuration
  @ImportResource(value = {
          "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml"
  }, reader = ResourceAwareXmlBeanDefinitionReader.class)
  @Import({XmlRepoConfiguration.class, IbmCommerceEventConfiguration.class})
  @Profile(PROFILE)
  static class LocalConfig {
  }
}
