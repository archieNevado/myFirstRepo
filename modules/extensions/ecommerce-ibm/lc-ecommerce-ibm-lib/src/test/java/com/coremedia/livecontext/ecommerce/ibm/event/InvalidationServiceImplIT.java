package com.coremedia.livecontext.ecommerce.ibm.event;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, IbmEventConfiguration.class})
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class InvalidationServiceImplIT extends IbmServiceTestBase {

  private static final long TEST_TIMESTAMP = 1403708220082L;

  @Inject
  private InvalidationServiceImpl invalidationService;

  @Inject
  private WcRestConnector restConnector;

  @Before
  @Override
  public void setup() {
    super.setup();
  }

  @Betamax(tape = "commercecache_testPollCacheInvalidations", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testPollCacheInvalidations() throws Exception {
    List<InvalidationEvent> commerceCacheInvalidations = invalidationService.getInvalidations(-1, getStoreContext());
    assertNotNull(commerceCacheInvalidations);
    assertTrue(!commerceCacheInvalidations.isEmpty());
  }

  @Betamax(tape = "commercecache_testPollCacheInvalidationsWithEntries", match = {MatchRule.path})
  @Test
  public void testPollCacheInvalidationsWithEntries() throws Exception {
    //if you have to re-record this test, you should change the timestamp to now-1h or something similar
    List<InvalidationEvent> commerceCacheInvalidations = invalidationService.getInvalidations(TEST_TIMESTAMP, getStoreContext());

    assertNotNull(commerceCacheInvalidations);
    assertFalse(commerceCacheInvalidations.isEmpty());

    InvalidationEvent commerceCacheInvalidation = commerceCacheInvalidations.get(0);
    assertNotNull(commerceCacheInvalidation.getContentType());
    assertNotNull(commerceCacheInvalidation.getTechId());
    long lastInvalidationTimestamp = Iterables.getLast(commerceCacheInvalidations).getTimestamp();

    assertTrue(lastInvalidationTimestamp > TEST_TIMESTAMP || lastInvalidationTimestamp <= 0);
  }

  @Test(expected = CommerceException.class)
  public void testPollCacheInvalidationsError() throws Exception {
    String origServiceEndpoint = restConnector.getServiceEndpoint(StoreContextHelper.getCurrentContext());
    try {
      restConnector.setServiceEndpoint("http://does.not.exists/blub");
      invalidationService.getInvalidations(0, getStoreContext());
    } finally {
      restConnector.setServiceEndpoint(origServiceEndpoint);
    }
  }

  @Test
  public void testAddCommerceBeanIdValid() {
    Map<String, Object> eventMap = new HashMap<>();
    eventMap.put("contentType", InvalidationServiceImpl.CONTENT_IDENTIFIER_PRODUCT);
    eventMap.put("techId", "4711");
    eventMap.put("name", "Filou");
    InvalidationEvent invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());

    eventMap = new HashMap<>();
    eventMap.put("contentType", InvalidationServiceImpl.CONTENT_IDENTIFIER_CATEGORY);
    eventMap.put("techId", "4711");
    eventMap.put("name", "Filou");
    invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());

    eventMap = new HashMap<>();
    eventMap.put("contentType", InvalidationServiceImpl.CONTENT_IDENTIFIER_TOP_CATEGORY);
    eventMap.put("techId", "4711");
    eventMap.put("name", "Filou");
    invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());

    eventMap = new HashMap<>();
    eventMap.put("contentType", InvalidationServiceImpl.CONTENT_IDENTIFIER_SEGMENT);
    eventMap.put("techId", "4711");
    eventMap.put("name", "Filou");
    invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());

    eventMap = new HashMap<>();
    eventMap.put("contentType", InvalidationServiceImpl.CONTENT_IDENTIFIER_MARKETING_SPOT);
    eventMap.put("name", "Filou");
    invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());
  }

  @Test
  public void testAddCommerceBeanIdInValid() {
    Map<String, Object> eventMap = new HashMap<>();
    eventMap.put("contentType", "invalidType");
    InvalidationEvent invalidationEvent = invalidationService.convertEvent(eventMap, 42L);
    assertEquals(42L, invalidationEvent.getTimestamp());
  }
}
