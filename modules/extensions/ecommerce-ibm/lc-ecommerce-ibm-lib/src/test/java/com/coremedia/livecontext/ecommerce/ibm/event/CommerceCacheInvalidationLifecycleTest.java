package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnectionIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceConnectionImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommerceCacheInvalidationLifecycleTest.LocalConfig.class)
@ActiveProfiles(CommerceCacheInvalidationLifecycleTest.PROFILE)
public class CommerceCacheInvalidationLifecycleTest {

  static final String PROFILE = "CommerceCacheInvalidationLifecycleTest";

  @Autowired
  private CommerceCacheInvalidationLifecycle commerceCacheInvalidationLifecycle;

  @Autowired
  private TaskScheduler taskScheduler;

  @Autowired
  private SitesService sitesService;

  @Autowired
  private Cache cache;

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testLifecycle() throws InterruptedException {
    // no commerce connection available
    Mockito.verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));

    Content site = sitesService.getSite("mySiteIndicator").getSiteRootDocument();
    site.checkOut();
    StructBuilder localSettings = site.getStruct("localSettings").builder();
    localSettings.declareString(BaseCommerceConnectionIdProvider.CONFIG_KEY_CONNECTION_ID, 16, "wcs1");
    site.set("localSettings", localSettings.build());
    site.checkIn();

    Mockito.verify(taskScheduler).schedule(any(Runnable.class), any(Date.class));
    CacheInvalidatorRunnable runnable = Iterables.getFirst(commerceCacheInvalidationLifecycle.runnablesByEndpoint.values(), null);
    assertNotNull(runnable);

    site.checkOut();
    localSettings = site.getStruct("localSettings").builder();
    localSettings.set(BaseCommerceConnectionIdProvider.CONFIG_KEY_CONNECTION_ID, "explode");
    site.set("localSettings", localSettings.build());
    site.checkIn();

    assertTrue(commerceCacheInvalidationLifecycle.runnablesByEndpoint.isEmpty());
  }

  @Configuration
  @ImportResource(value = {
          "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml"
  }, reader = ResourceAwareXmlBeanDefinitionReader.class)
  @Import({XmlRepoConfiguration.class, IbmCommerceEventConfiguration.class})
  @Profile(PROFILE)
  public static class LocalConfig {

    @Autowired
    private Cache cache;

    @PostConstruct
    void initialize() {
      cache.setCapacity(Object.class.getName(), 10);
    }

    @Bean
    Commerce commerce() {
      return new Commerce();
    }

    @Bean
    WcRestConnector wcRestConnector() {
      return Mockito.mock(WcRestConnector.class);
    }

    @Bean
    TaskScheduler taskScheduler() {
      return Mockito.mock(TaskScheduler.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Bean
    XmlUapiConfig xmlUapiConfig() {
      // contains sites without commerce connections
      return new XmlUapiConfig("classpath:/content/testcontent.xml");
    }

    @Bean(name = "commerce:wcs1")
    @Autowired
    CommerceConnection commerceConnection(StoreContextProvider provider) {
      CommerceConnectionImpl commerceConnection = new CommerceConnectionImpl();
      commerceConnection.setStoreContextProvider(provider);
      return commerceConnection;
    }

  }

}