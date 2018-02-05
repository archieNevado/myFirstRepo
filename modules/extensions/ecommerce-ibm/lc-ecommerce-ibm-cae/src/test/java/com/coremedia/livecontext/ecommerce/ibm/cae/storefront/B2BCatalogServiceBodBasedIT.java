package com.coremedia.livecontext.ecommerce.ibm.cae.storefront;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImplBodBasedIT;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests for BOD REST interface.
 */
@ContextConfiguration(classes = {
        IbmServiceTestBase.LocalConfig.class,
        StoreFrontConfiguration.class,
        CatalogServiceImplBodBasedIT.LocalConfig.class})
public class B2BCatalogServiceBodBasedIT extends AbstractB2BCatalogServiceIT {
  @Configuration
  @ImportResource(
          value = {
                  "com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml"
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(IbmServiceTestBase.LocalConfig.PROFILE)
  public static class LocalConfig {
  }

  @Betamax(tape = "csi_testFindSubCategoriesWithContract", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("TW-151 - flaky // Resolve with CMS-8632")
  public void testFindSubCategoriesWithContract() throws Exception {
    super.testFindSubCategoriesWithContract();
  }

}
