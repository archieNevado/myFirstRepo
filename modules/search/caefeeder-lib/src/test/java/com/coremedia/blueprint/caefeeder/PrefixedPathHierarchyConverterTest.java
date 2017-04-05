package com.coremedia.blueprint.caefeeder;

import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrefixedPathHierarchyConverterTest {

  @InjectMocks
  private  PrefixedPathHierarchyConverter prefixedPathHierarchyConverter;

  @Mock
  private TreePathKeyFactory treePathKeyFactory;

  @Test
  public void testConvertType_alwaysList() {
    assertEquals("Even with a null parameter the result is List", List.class, prefixedPathHierarchyConverter.convertType(null));
    assertEquals("The result is List no matter what the param value is", List.class, prefixedPathHierarchyConverter.convertType(String.class));
  }

  @Test
  public void testConvertValue_nullParam_returnsEmptyList() {
    Object result = prefixedPathHierarchyConverter.convertValue(null);
    assertTrue("The resulting value should always be a collection", result instanceof Collection && ((Collection)result).isEmpty());
  }

  @Test
  public void testConvertValue_legalContent_returnsResult() {
    ContentBean contentBean = mock(ContentBean.class);
    Content content = mock(Content.class);
    when(contentBean.getContent()).thenReturn(content);

    List<String> expectedResults = Arrays.asList("0/1", "1/1/2", "2/1/2/3");
    when(treePathKeyFactory.getPath(content)).thenReturn("/1/2/3");

    Object result = prefixedPathHierarchyConverter.convertValue(Arrays.asList(contentBean));

    assertTrue("Result should be some kind of non-empty collection", result instanceof Collection && !((Collection)result).isEmpty());
    for (Object o : (Collection) result) {
      assertTrue("Result contains " + String.valueOf(o) + " but should be one of " + expectedResults.toString(), expectedResults.contains(o));
    }
  }

  @Test
  public void testConvertValue_legalContentWithRootPathSegment_returnsRootPrefixedPathsResult() {
    ContentBean contentBean = mock(ContentBean.class);
    Content content = mock(Content.class);
    when(contentBean.getContent()).thenReturn(content);

    List<String> expectedResults = Arrays.asList("0/1", "1/1/2");
    when(treePathKeyFactory.getPath(content)).thenReturn("1/2");

    Object result = prefixedPathHierarchyConverter.convertValue(Arrays.asList(contentBean));

    assertTrue("Result should be some kind of non-empty collection", result instanceof Collection && !((Collection)result).isEmpty());
    for (Object o : (Collection) result) {
      assertTrue("Result contains " + String.valueOf(o) + " but should be one of " + expectedResults.toString(), expectedResults.contains(o));
    }
  }

  @Test
  public void testConvertValue_legalContentWithDuplicateSegments_returnsResultWithoutDuplicates() {
    ContentBean contentBean = mock(ContentBean.class);
    Content content = mock(Content.class);
    when(contentBean.getContent()).thenReturn(content);
    ContentBean contentBeanInSameRoot = mock(ContentBean.class);
    Content contentInSameRoot = mock(Content.class);
    when(contentBeanInSameRoot.getContent()).thenReturn(contentInSameRoot);

    List<String> expectedResults = Arrays.asList("0/1", "1/1/2", "2/1/2/3", "2/1/2/4");
    when(treePathKeyFactory.getPath(content)).thenReturn("1/2/3");
    when(treePathKeyFactory.getPath(contentInSameRoot)).thenReturn("1/2/4");

    Object result = prefixedPathHierarchyConverter.convertValue(Arrays.asList(contentBean, contentBeanInSameRoot));

    assertTrue("Result should be some kind of non-empty collection", result instanceof Collection && !((Collection)result).isEmpty());
    assertEquals("Result should contain only X paths", expectedResults.size(), ((Collection)result).size() );
    for (Object o : (Collection) result) {
      assertTrue("Result contains " + String.valueOf(o) + " but should be one of " + expectedResults.toString(), expectedResults.contains(o));
    }
  }

}