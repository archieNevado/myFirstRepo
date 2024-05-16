package com.coremedia.livecontext.feeder;

import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.TextParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.coremedia.livecontext.feeder.CommerceItemsPopulator.TYPE_LINKABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceItemsPopulatorTest {

  @Mock
  private MutableFeedable feedable;

  @Mock
  private Content content;

  @Mock
  private ContentType contentType;

  private CommerceItemsPopulator testling;

  @Before
  public void setUp() {
    testling = new CommerceItemsPopulator();

    //mock content type hierarchy
    when(content.getType()).thenReturn(contentType);
    when(contentType.getName()).thenReturn(TYPE_LINKABLE);
    when(contentType.isSubtypeOf(TYPE_LINKABLE)).thenReturn(true);
  }

  @Test
  public void populateAndFeed() {
    //this is the positive case
    List<String> productsPartNumbers = Arrays.asList("PC_WINE_GLASS", "PC_EVENING_DRESS-RED-M");

    try (var mocked = mockStatic(CommerceReferenceHelper.class)) {
      mocked.when(() -> CommerceReferenceHelper.getExternalIds(content)).thenReturn(productsPartNumbers);
      testling.populate(feedable, content);
    }

    verify(feedable).setElement(SearchConstants.FIELDS.COMMERCE_ITEMS.toString(), productsPartNumbers, TextParameters.NONE.asMap());
  }

  @Test
  public void populateDontFeed() {
    //test that nothing is feeded when the products are empty.
    try (var mocked = mockStatic(CommerceReferenceHelper.class)) {
      mocked.when(() -> CommerceReferenceHelper.getExternalIds(content)).thenReturn(List.of());
      testling.populate(feedable, content);
    }

    verify(feedable, never()).setElement(anyString(), any(), anyMap());
  }
}
