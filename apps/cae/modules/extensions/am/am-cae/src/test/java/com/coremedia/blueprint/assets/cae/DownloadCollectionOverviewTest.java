package com.coremedia.blueprint.assets.cae;

import com.coremedia.blueprint.assets.contentbeans.AMAssetRendition;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DownloadCollectionOverviewTest {

  @Test
  public void testRenditions() {
    List<AMAssetRendition> assetRenditions = List.of(mock(AMAssetRendition.class));
    DownloadCollectionOverview assetCollection = new DownloadCollectionOverview(assetRenditions);
    assertEquals(assetRenditions, assetCollection.getRenditions());
  }

  @Test
  public void testSearchTerm() {
    DownloadCollectionOverview assetCollection = new DownloadCollectionOverview(Collections.<AMAssetRendition>emptyList());
    assertEquals("", assetCollection.getSearchTerm());
  }
}
