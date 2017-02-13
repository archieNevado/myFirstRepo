package com.coremedia.livecontext.site;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolverImpl;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiveContextSiteResolverTest {

  private static final Locale LOCALE = Locale.forLanguageTag("en-US");

  private static final String SITE_NAME_1 = "site1";
  private static final String SITE_NAME_2 = "site2";

  @Mock
  private SitesService sitesService;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;

  @Mock
  private Content channel1;

  @Mock
  private Content channel2;

  @Mock
  private Site site1;

  @Mock
  private Site site2;

  @Mock
  private StoreContext storeContext;

  @Mock
  private ContentRepository contentRepository;

  private LiveContextSiteResolverImpl testling = new LiveContextSiteResolverImpl();

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  private CommerceConnection connection;

  @Before
  public void setup() {
    connection = MockCommerceEnvBuilder.create().setupEnv();

    testling.setSitesService(sitesService);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(site1.getSiteRootDocument()).thenReturn(channel1);
    when(site2.getSiteRootDocument()).thenReturn(channel2);

    when(site1.getLocale()).thenReturn(LOCALE);
    when(site2.getLocale()).thenReturn(LOCALE);

    when(site1.getName()).thenReturn(SITE_NAME_1);
    when(site2.getName()).thenReturn(SITE_NAME_2);

    when(contentLinkBuilder.getVanityName(channel1)).thenReturn(SITE_NAME_1);
    when(contentLinkBuilder.getVanityName(channel2)).thenReturn(SITE_NAME_2);
  }

  @Test
  public void handleFragmentSiteResolving() {
    when(commerceConnectionInitializer.findConnectionForSite(site1)).thenReturn(Optional.of(connection));
    when(sitesService.getSites()).thenReturn(ImmutableSet.of(site1));
    when(storeContext.get("storeId")).thenReturn("10001");

    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;placement=header;view=test";
    FragmentParameters params = FragmentParametersFactory.create(url);
    Site siteFor = testling.findSiteFor(params);
    assertThat(siteFor).isEqualTo(site1);
  }

  @Test
  public void handleFragmentSiteResolvingWithEnvironment() {
    when(sitesService.getSites()).thenReturn(ImmutableSet.of(site1, site2));
    when(storeContext.get("storeId")).thenReturn("10001");

    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;placement=header;environment=site:site2";
    FragmentParameters params = FragmentParametersFactory.create(url);
    Site siteFor = testling.findSiteFor(params);
    assertThat(siteFor).isEqualTo(site2);
  }
}
