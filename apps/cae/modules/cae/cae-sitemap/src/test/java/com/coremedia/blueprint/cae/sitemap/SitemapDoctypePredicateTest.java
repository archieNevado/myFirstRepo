package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SitemapDoctypePredicateTest {

  private static final String BILLION_YEAR_BUNKER = "billion year bunker";
  private static final String BUSINESS_END = "business end";
  private static final String KILL_O_ZAP = "Kill-o-Zap blaster pistol";
  private static final String CRISIS_INDUCER = "crisis inducer";
  private static final String THINKING_CAP = "thinking cap";
  private static final String POINT_OF_VIEW_GUN = "point of view gun";

  @Mock
  private ContentType thinkingCap;

  @Mock
  private ContentType billionYearBunker;

  @Mock
  private ContentType pointOfViewGun;

  @Mock
  private ContentType crisisInducer;

  @Mock
  private Content content;

  private SitemapDoctypePredicate testling;

  @Before
  public void defaultSetup() {
    testling = new SitemapDoctypePredicate(
            List.of(BILLION_YEAR_BUNKER, BUSINESS_END, POINT_OF_VIEW_GUN),
            List.of(KILL_O_ZAP, CRISIS_INDUCER, POINT_OF_VIEW_GUN)
    );

    when(thinkingCap.getName()).thenReturn(THINKING_CAP);
    when(thinkingCap.isSubtypeOf(THINKING_CAP)).thenReturn(true);
    when(billionYearBunker.getName()).thenReturn(BILLION_YEAR_BUNKER);
    when(billionYearBunker.isSubtypeOf(BILLION_YEAR_BUNKER)).thenReturn(true);
    when(pointOfViewGun.getName()).thenReturn(POINT_OF_VIEW_GUN);
    when(pointOfViewGun.isSubtypeOf(POINT_OF_VIEW_GUN)).thenReturn(true);
    when(crisisInducer.getName()).thenReturn(CRISIS_INDUCER);
    when(crisisInducer.isSubtypeOf(CRISIS_INDUCER)).thenReturn(true);
  }

  @Test
  public void notAContentObject() {
    assertFalse(testling.test("no content object"));
  }

  @Test
  public void neitherIncludedNorExcluded() {
    when(content.getType()).thenReturn(thinkingCap);

    assertFalse("Neither included nor excluded must lead to false.", testling.test(content));
  }

  @Test
  public void includedAndNotExcluded() {
    when(content.getType()).thenReturn(billionYearBunker);

    assertTrue("Included and not excluded must be true", testling.test(content));
  }

  @Test
  public void includedAndExcluded() {
    when(content.getType()).thenReturn(pointOfViewGun);

    assertFalse("Included and excluded must be false.", testling.test(content));
  }

  @Test
  public void notIncludedButExcluded() {
    when(content.getType()).thenReturn(crisisInducer);

    assertFalse("Not included but excluded must be false.", testling.test(content));
  }

}
