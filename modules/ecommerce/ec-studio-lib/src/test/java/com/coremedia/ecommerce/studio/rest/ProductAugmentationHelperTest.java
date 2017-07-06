package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.intercept.InterceptService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.DEFAULT_BASE_FOLDER_NAME;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.EXTERNAL_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, ProductAugmentationHelperTest.LocalConfig.class})
public class ProductAugmentationHelperTest {


  private static final String PRODUCT_ID = "test:///catalog/product/prodId";
  private static final String CATEGORY_ID = "test:///catalog/category/leafCategory";
  private static final String CATEGORY_DISPLAY_NAME = "leaf";
  private static final String PRODUCT_NAME = "productName";
  private static final String ROOT = "root";
  private static final String TOP = "top";

  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ProductAugmentationHelper testling;

  @Mock
  private AugmentationService productAugmentationService;

  @Mock
  private AugmentationService categoryAugmentationService;

  @Mock
  private Category rootCategory;

  @Mock
  private Category leafCategory;

  @Mock
  private Product product;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    testling.setAugmentationService(productAugmentationService);
    testling.setCategoryAugmentationService(categoryAugmentationService);

    Content rootCategoryContent = contentRepository.getContent("20");
    when(categoryAugmentationService.getContent(rootCategory)).thenReturn(rootCategoryContent);

    //mock category tree
    when(rootCategory.isRoot()).thenReturn(true);
    when(rootCategory.getDisplayName()).thenReturn(ROOT);
    Category topCategory = mock(Category.class);
    when(topCategory.getParent()).thenReturn(rootCategory);
    when(topCategory.getDisplayName()).thenReturn(TOP);
    //leafCategory = mock(Category.class, RETURNS_DEEP_STUBS);
    when(leafCategory.getParent()).thenReturn(topCategory);
    when(leafCategory.getDisplayName()).thenReturn(CATEGORY_DISPLAY_NAME);
    when(leafCategory.getId()).thenReturn(CATEGORY_ID);
    List<Category> breadcrumb = new ArrayList<>();
    breadcrumb.add(rootCategory);
    breadcrumb.add(topCategory);
    breadcrumb.add(leafCategory);
    when(leafCategory.getBreadcrumb()).thenReturn(breadcrumb);
    StoreContext storeContext = mock(StoreContext.class);
    when(leafCategory.getContext()).thenReturn(storeContext);
    when(storeContext.getSiteId()).thenReturn("theSiteId");

    when(product.getCategory()).thenReturn(leafCategory);
    when(product.getName()).thenReturn(PRODUCT_NAME);
    when(product.getId()).thenReturn(PRODUCT_ID);
  }

  @Test
  public void testAugment() {
    testling.augment(product);

    Content cmProduct = contentRepository.getChild("/Sites/Content Test/" + DEFAULT_BASE_FOLDER_NAME + "/"
            + ROOT + "/" + TOP + "/" + CATEGORY_DISPLAY_NAME + "/" + product.getName());
    assertNotNull(cmProduct);
    assertEquals(PRODUCT_NAME, cmProduct.getName());
    assertEquals(PRODUCT_ID, cmProduct.getString(EXTERNAL_ID));

    //assert the initialized layout for product pages
    Struct productPageGridStruct = cmProduct.getStruct(CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY);
    assertNotNull(productPageGridStruct);
    Struct productPlacements2Struct = productPageGridStruct.getStruct(PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME);
    assertNotNull(productPlacements2Struct);
    Content productLayout = (Content) productPlacements2Struct.get(PageGridContentKeywords.LAYOUT_PROPERTY_NAME);
    assertNotNull(productLayout);
    assertEquals("ProductLayoutSettings", productLayout.getName());
  }

  @Configuration
  @ComponentScan(basePackages = {
          "com.coremedia.rest.cap.content",
  })
  @ImportResource(value = {
          "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml"
  },
          reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/ecommerce/studio/rest/ec-studio-lib-test-content.xml");
    }

    @Bean
    ProductAugmentationHelper productAugmentationHelper() {
      return new ProductAugmentationHelper();
    }

    @Bean
    public InterceptService interceptService() {
      return null;
    }
  }
}