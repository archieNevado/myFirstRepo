package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.tree.CommerceTreeRelation;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test for CommerceTreeRelation below module bpbase-lc-common.
 * Since we want to make use of the ibm betamax infrastructure the test is below the ibm module for now.
 */
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, CommerceTreeRelationTestIT.LocalConfig.class})
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class CommerceTreeRelationTestIT extends IbmServiceTestBase {
  @Configuration
  @Profile(IbmServiceTestBase.LocalConfig.PROFILE)
  public static class LocalConfig {
    @Bean
    public CommerceTreeRelation commerceTreeRelation() {
      return new CommerceTreeRelation();
    }
  }

  private String CATEGORY_SEO_SEGMENT = "pc-on-the-table";

  @Inject
  private CommerceTreeRelation testling;

  @Inject
  private CatalogService catalogService;

  @Before
  public void setup() {
    super.setup();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Betamax(tape = "ctr_testGetChildrenOf", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetChildrenOf() {
    Category testCategory = getCategory();
    assertNotNull(testling.getChildrenOf(testCategory));
  }

  @Betamax(tape = "ctr_testGetRootCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetRootCategory() {
    Category topCategory = getCategory();
    Category rootCategory = testling.getParentOf(topCategory);
    assertNotNull(rootCategory);
    assertNull(rootCategory.getParent());
    assertEquals(CatalogServiceImpl.EXTERNAL_ID_ROOT_CATEGORY, rootCategory.getExternalId());
    assertTrue(testling.isRoot(rootCategory));
    assertNull(rootCategory.getTitle());
  }


  @Betamax(tape = "ctr_testPathToRoot", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testPathToRoot() {
    Category category1 = getCategory();
    Category category2 = category1.getChildren().get(0);
    assertNotNull(category2);

    List<Category> pathToRoot = testling.pathToRoot(category2);
    assertEquals(3, pathToRoot.size());
    assertEquals(2, category2.getBreadcrumb().size());

    assertEquals(CatalogServiceImpl.EXTERNAL_ID_ROOT_CATEGORY, pathToRoot.get(0).getExternalId());
  }

  private Category getCategory() {
    return catalogService.findCategoryBySeoSegment(CATEGORY_SEO_SEGMENT);
  }
}
