package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
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

import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.CATEGORY_PAGEGRID_STRUCT_PROPERTY;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.DEFAULT_BASE_FOLDER_NAME;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.EXTERNAL_ID;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.SEGMENT;
import static com.coremedia.ecommerce.studio.rest.CategoryAugmentationHelper.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, CategoryAugmentationHelperTest.LocalConfig.class})
public class CategoryAugmentationHelperTest {

  private static final String CATEGORY_ID = "test:///catalog/category/leafCategory";
  //External ids of category can contain '/'. See CMS-5075
  private static final String CATEGORY_DISPLAY_NAME = "le/af";
  private static final String ESCAPED_CATEGORY_DISPLAY_NAME = "le_af";
  private static final String ROOT = "root";
  private static final String TOP = "top";

  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private CategoryAugmentationHelper testling;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private Category rootCategory;

  @Mock
  private Category leafCategory;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    testling.setAugmentationService(augmentationService);

    Content rootCategoryContent = contentRepository.getContent("20");
    when(augmentationService.getContent(rootCategory)).thenReturn(rootCategoryContent);

    //mock category tree
    when(rootCategory.isRoot()).thenReturn(true);
    when(rootCategory.getDisplayName()).thenReturn(ROOT);
    Category topCategory = mock(Category.class);
    when(topCategory.getParent()).thenReturn(rootCategory);
    when(topCategory.getDisplayName()).thenReturn(TOP);
    leafCategory = mock(Category.class, RETURNS_DEEP_STUBS);
    when(leafCategory.getParent()).thenReturn(topCategory);
    when(leafCategory.getDisplayName()).thenReturn(CATEGORY_DISPLAY_NAME);
    when(leafCategory.getId()).thenReturn(CATEGORY_ID);
    List<Category> breadcrumb = new ArrayList<>();
    breadcrumb.add(rootCategory);
    breadcrumb.add(topCategory);
    breadcrumb.add(leafCategory);
    when(leafCategory.getBreadcrumb()).thenReturn(breadcrumb);
    when(leafCategory.getContext().getSiteId()).thenReturn("theSiteId");
  }

  @Test
  public void testAugment() {
    testling.augment(leafCategory);

    Content externalChannel = contentRepository.getChild("/Sites/Content Test/" + DEFAULT_BASE_FOLDER_NAME + "/"
            + ROOT + "/" + TOP + "/" + ESCAPED_CATEGORY_DISPLAY_NAME + "/" + ESCAPED_CATEGORY_DISPLAY_NAME);
    assertNotNull(externalChannel);
    assertEquals(ESCAPED_CATEGORY_DISPLAY_NAME, externalChannel.getName());
    assertEquals(CATEGORY_ID, externalChannel.getString(EXTERNAL_ID));
    assertEquals(CATEGORY_DISPLAY_NAME, externalChannel.getString(TITLE));
    assertEquals(CATEGORY_DISPLAY_NAME, externalChannel.getString(SEGMENT));

    //assert the initialized layout for category pages
    Struct categoryPageGridStruct = externalChannel.getStruct(CATEGORY_PAGEGRID_STRUCT_PROPERTY);
    assertNotNull(categoryPageGridStruct);
    Struct categoryPlacements2Struct = categoryPageGridStruct.getStruct(PageGridContentKeywords.PLACEMENTS_PROPERTY_NAME);
    assertNotNull(categoryPlacements2Struct);
    Content categoryLayout = (Content) categoryPlacements2Struct.get(PageGridContentKeywords.LAYOUT_PROPERTY_NAME);
    assertNotNull(categoryLayout);
    assertEquals("CategoryLayoutSettings", categoryLayout.getName());

    //assert the initialized layout for product pages
    Struct productPageGridStruct = externalChannel.getStruct(CATEGORY_PRODUCT_PAGEGRID_STRUCT_PROPERTY);
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
    CategoryAugmentationHelper categoryAugmentationResource() {
      return new CategoryAugmentationHelper();
    }

    @Bean
    public InterceptService interceptService() {
      return null;
    }
  }
}