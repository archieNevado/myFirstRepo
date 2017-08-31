package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.links.VanityUrlMapper;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SeoSegmentExternalReferenceResolverTest {

  @Mock
  private UrlPathFormattingHelper urlPathFormattingHelper;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private Site site;

  @Mock
  private CMChannel rootChannel;

  @Mock
  private Content siteRootDocument;

  @Mock
  private CMChannel tuerkeiChannel;

  @Mock
  private Content tuerkeiChannelDocument;

  @Mock
  private CMChannel summer2017Channel;

  @Mock
  private Content summer2017ChannelDocument;

  @Mock
  private CMLinkable target;

  @Mock
  private Content targetDocument;

  @Mock
  private ContentType targetType;

  @Mock
  private VanityUrlMapper vanityUrlMapper;


  private SeoSegmentExternalReferenceResolver testling;

  private static final String ROOT_SEGMENT = "aurora";
  private static final String URL = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;placement=header;environment=site:site2";

  @Before
  public void setUp() {
    testling = new SeoSegmentExternalReferenceResolver();
    testling.setUrlPathFormattingHelper(urlPathFormattingHelper);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContentRepository(contentRepository);
    testling.setContextHelper(contextHelper);

    when(contentRepository.getContent(contains("815"))).thenReturn(targetDocument);
    when(contentRepository.getContent(contains("12345"))).thenReturn(null);

    when(targetDocument.getType()).thenReturn(targetType);
    when(summer2017ChannelDocument.getType()).thenReturn(targetType);

    when(site.getSiteRootDocument()).thenReturn(siteRootDocument);
    when(urlPathFormattingHelper.getVanityName(siteRootDocument)).thenReturn(ROOT_SEGMENT);
    when(navigationSegmentsUriHelper.parsePath(singletonList(ROOT_SEGMENT))).thenReturn(rootChannel);
    when(navigationSegmentsUriHelper.parsePath(asList(ROOT_SEGMENT, "reisen", "tuerkei"))).thenReturn(tuerkeiChannel);
    when(navigationSegmentsUriHelper.parsePath(asList(ROOT_SEGMENT, "reisen", "tuerkei", "sommer-2017"))).thenReturn(summer2017Channel);
    when(navigationSegmentsUriHelper.parsePath(asList(ROOT_SEGMENT, "reisen", "tuerkei", "sommer-12345"))).thenReturn(null);
    when(navigationSegmentsUriHelper.parsePath(asList(ROOT_SEGMENT, "reisen", "tuerkei", "istanbul-0815"))).thenReturn(null);
    when(tuerkeiChannel.getContext()).thenReturn(tuerkeiChannel);
    when(tuerkeiChannel.getContent()).thenReturn(tuerkeiChannelDocument);
    when(summer2017Channel.getContext()).thenReturn(summer2017Channel);
    when(summer2017Channel.getContent()).thenReturn(summer2017ChannelDocument);
    when(rootChannel.getVanityUrlMapper()).thenReturn(vanityUrlMapper);

    when(target.getContent()).thenReturn(targetDocument);
  }

  @Test
  public void testResolveExternalRef() {
    String ref = "cm-seosegment:reisen--tuerkei--istanbul-0815";
    FragmentParameters params = FragmentParametersFactory.create(URL);
    params.setExternalReference(ref);
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertNotNull(linkableAndNavigation);
    assertEquals(tuerkeiChannelDocument, linkableAndNavigation.getNavigation());
    assertEquals(targetDocument, linkableAndNavigation.getLinkable());
  }

  @Test
  public void testResolveExternalRefChannelWithNumber() {
    String ref = "cm-seosegment:reisen--tuerkei--sommer-2017";
    FragmentParameters params = FragmentParametersFactory.create(URL);
    params.setExternalReference(ref);
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertNotNull(linkableAndNavigation);
    assertEquals(summer2017ChannelDocument, linkableAndNavigation.getNavigation());
    assertEquals(summer2017ChannelDocument, linkableAndNavigation.getLinkable());
  }

  @Test
  public void testResolveInvalidExternalRef() {
    String ref = "cm-seosegment:reisen--tuerkei--sommer-12345";
    FragmentParameters params = FragmentParametersFactory.create(URL);
    params.setExternalReference(ref);
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertNotNull(linkableAndNavigation);
    assertEquals(tuerkeiChannelDocument, linkableAndNavigation.getNavigation());
    assertEquals(null, linkableAndNavigation.getLinkable());
  }


  @Test
  public void testResolveVanityExternalRef() {
    String ref = "cm-seosegment:reisen--istanbul";
    when(vanityUrlMapper.forPattern("reisen/istanbul")).thenReturn(target);
    when(contextHelper.findAndSelectContextFor(rootChannel, target)).thenReturn(tuerkeiChannel);

    FragmentParameters params = FragmentParametersFactory.create(URL);
    params.setExternalReference(ref);
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertNotNull(linkableAndNavigation);
    assertEquals(tuerkeiChannelDocument, linkableAndNavigation.getNavigation());
    assertEquals(targetDocument, linkableAndNavigation.getLinkable());
  }

}
