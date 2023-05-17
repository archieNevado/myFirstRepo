package com.coremedia.blueprint.analytics.settings.google;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GoogleAnalyticsSettingsProviderTest {
  @Test
  public void testBuildReportUrlEmptySettings() throws Exception {
    assertNull("no settings - no reports url", testling.buildReportUrl(null, getClass().getName()));
  }

  @Test
  public void testBuildReportUrl() throws Exception {
    when(settings.get("propertyId")).thenReturn("myPropertyId");
    assertEquals("https://analytics.google.com/analytics/web/#/pmyPropertyId/reports/intelligenthome",
            testling.buildReportUrl(settings, getClass().getName()));
  }

  @Mock
  private Map<String, Object> settings;
  private final GoogleAnalyticsSettingsProvider testling = new GoogleAnalyticsSettingsProvider();
}
