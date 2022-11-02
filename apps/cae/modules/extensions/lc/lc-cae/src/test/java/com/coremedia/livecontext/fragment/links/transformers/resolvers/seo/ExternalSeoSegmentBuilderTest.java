package com.coremedia.livecontext.fragment.links.transformers.resolvers.seo;

import com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.links.VanityUrlMapperCacheKey;
import com.coremedia.blueprint.base.links.impl.BlueprintUrlPathFormattingConfiguration;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.cache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlueprintUrlPathFormattingConfiguration.class)
public class ExternalSeoSegmentBuilderTest {

  @Mock
  private CMObject object;

  @Mock
  private CMLinkable linkable;

  @Mock
  private CMNavigation navigation;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private Cache cache;

  @Mock
  private SettingsBasedVanityUrlMapper vanityUrlMapper;

  @Inject
  private UrlPathFormattingHelper urlPathFormattingHelper;

  private ExternalSeoSegmentBuilder testling;

  @Before
  public void beforeEachTest() {
    testling = new ExternalSeoSegmentBuilder();
    testling.setCache(cache);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setUrlPathFormattingHelper(urlPathFormattingHelper);

    when(navigation.getRootNavigation()).thenReturn(navigation);

    when(cache.get(any(VanityUrlMapperCacheKey.class))).thenReturn(vanityUrlMapper);
    when(navigationSegmentsUriHelper.getPathList(navigation)).thenReturn(Arrays.asList("aurora", "pages", "perfect-dinner"));
  }

  @Test
  public void testPartialValuesReturnEmptyString() throws Exception {

    assertEquals("", testling.asSeoSegment(null, object));
    assertEquals("", testling.asSeoSegment(navigation, null));
  }

  @Test
  public void testAsSeoSegmentForCMLinkables() throws Exception {
    when(linkable.getContentId()).thenReturn(5678);
    when(linkable.getSegment()).thenReturn("---A Perfect-----Dinner @ Home!!!");
    String seoSegment = testling.asSeoSegment(navigation, linkable);
    //special charaters and '--' will be replaced by '-'. Beginning and trailing '-' will be removed.
    assertEquals("pages--perfect-dinner--a-perfect-dinner-home-5678", seoSegment);
  }

  @Test
  public void testAsSeoSegmentForCMLinkablesInJapanese() throws Exception {
    when(linkable.getContentId()).thenReturn(5678);
    when(linkable.getSegment()).thenReturn("ジーサマー-ルック");
    String seoSegment = testling.asSeoSegment(navigation, linkable);
    //special charaters and '--' will be replaced by '-'. Beginning and trailing '-' will be removed.
    assertEquals("pages--perfect-dinner--%E3%82%B8%E3%83%BC%E3%82%B5%E3%83%9E%E3%83%BC-%E3%83%AB%E3%83%83%E3%82%AF-5678", seoSegment);
  }

  @Test
  public void testAsSeoSegmentForChannels() throws Exception {
    String seoSegment = testling.asSeoSegment(navigation, navigation);
    assertEquals("pages--perfect-dinner", seoSegment);
  }

  @Test
  public void testVanityUrl() {
    when(vanityUrlMapper.patternFor(linkable.getContent())).thenReturn("deep/link");
    String seoSegment = testling.asSeoSegment(navigation, linkable);
    assertEquals("deep--link", seoSegment);
  }
}
