package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.hybris.AbstractHybrisServiceTest;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.hybris.SystemProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class InvalidationServiceImplIT extends AbstractHybrisServiceTest {

  @Inject
  InvalidationServiceImpl testling;

  @Inject
  TestConfig testConfig;

  @Test
  public void testInvalidationsBootstrap() {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    List<InvalidationEvent> invalidations = testling.getInvalidations(-1);
    assertThat(invalidations).isNotNull();

    if (!invalidations.isEmpty()) {
      InvalidationEvent invalidationEvent = invalidations.get(0);
      assertThat(invalidationEvent.getTimestamp()).isGreaterThan(0);
    }
  }

  @Test
  public void testInvalidationsWithBigTimeStamp() {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    List<InvalidationEvent> invalidations = testling.getInvalidations(10000);
    assertThat(invalidations).isEmpty();
  }
}
