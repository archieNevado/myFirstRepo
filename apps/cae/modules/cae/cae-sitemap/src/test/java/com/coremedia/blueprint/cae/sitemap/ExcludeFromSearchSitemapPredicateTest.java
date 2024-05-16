package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExcludeFromSearchSitemapPredicateTest {
  private static final String DOCTYPE = "CMTeasable";
  private static final String NOT_SEARCHABLE_FLAG = "notSearchable";

  private ExcludeFromSearchSitemapPredicate testling;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    testling = new ExcludeFromSearchSitemapPredicate(DOCTYPE, NOT_SEARCHABLE_FLAG);
  }

  @Test
  public void testTeasableButSearchable() throws Exception {
    Content content1 = getContent(false, true);

    boolean include = testling.test(content1);
    assertTrue(include);
  }

  @Test
  public void testTeasableNotSearchable() throws Exception {
    Content content1 = getContent(true, true);

    boolean include = testling.test(content1);
    assertFalse(include);
  }

  @Test
  public void testOnlyContentIsIncluded() throws Exception {
    boolean include = testling.test(new Object());
    assertFalse(include);
  }

  private Content getContent(boolean notSearchableReturnValue,
                             boolean isSubTypeOf) {
    Content content = mock(Content.class);
    when(content.getBoolean(NOT_SEARCHABLE_FLAG)).thenReturn(notSearchableReturnValue);
    ContentType mock = mock(ContentType.class);
    when(content.getType()).thenReturn(mock);
    when(mock.isSubtypeOf(DOCTYPE)).thenReturn(isSubTypeOf);
    return content;
  }
}
