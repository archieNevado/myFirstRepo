package com.coremedia.blueprint.assets.cae;

import com.coremedia.blueprint.assets.cae.DownloadCollectionZipCacheKey;
import com.coremedia.blueprint.assets.contentbeans.AMAsset;
import com.coremedia.blueprint.assets.contentbeans.AMAssetRendition;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.content.Content;
import com.coremedia.cotopaxi.common.CacheFactory;
import com.coremedia.cotopaxi.content.AbstractContentRepository;
import com.coremedia.mimetype.MimeTypeService;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.activation.MimeType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadCollectionZipCacheKeyTest {

  private static final String BLOB_ID_NUMBER = "2982";
  private static final String BLOB_ID = "coremedia:///cap/resources/" + BLOB_ID_NUMBER + "/data";
  public static final String JPG = "jpg";
  public static final String ASSET_CONTENT_NAME = "assetContentName";
  public static final String ORIGINAL_RENDITION = "original";

  @InjectMocks
  private DownloadCollectionZipCacheKey cacheKey;

  private Cache cache;

  @Mock
  private AbstractContentRepository contentRepository;

  @Mock
  private AbstractContentRepository contentRepositoryAnother;

  @Mock
  private MimeTypeService mimeTypeService;

  @Mock
  private AMAssetRendition renditionNoBlob;

  @Mock
  private AMAsset asset;

  @Mock
  private AMAsset assetInvalid;

  @Mock
  private Content assetContent;

  @Mock
  private Content assetContentInvalidID;

  @Mock
  private AMAssetRendition amAssetRendition;

  @Mock
  private AMAssetRendition amAssetRenditionNoBlob;

  @Mock
  private AMAssetRendition amAssetRenditionInvalidId;

  @Mock
  private CapBlobRef renditionBlob;

  @Mock
  private InputStream inputStream;


  private static File zipFile;

  private List<AMAssetRendition> renditions;

  @BeforeClass
  public static void prepare() throws IOException {
    zipFile = File.createTempFile("temp-file-name", ".zip");
  }

  @Before
  public void setUp() throws Exception {
    cache = new Cache("DCZCK");
    cache.setCapacity(Object.class.toString(), 10);

    when(mimeTypeService.getMimeTypeForExtension(any(String.class))).thenReturn("application/zip");
    when(mimeTypeService.getExtensionForMimeType(anyString())).thenReturn(JPG);
    when(contentRepository.createTempFileFor(any(String.class), any(MimeType.class))).thenReturn(zipFile);

    when(renditionBlob.getContentType()).thenReturn(new MimeType("image/jpeg"));
    when(renditionBlob.getInputStream()).thenReturn(IOUtils.toInputStream("some test data for my input stream"));

    when(asset.getContent()).thenReturn(assetContent);
    when(assetInvalid.getContent()).thenReturn(assetContentInvalidID);

    when(amAssetRendition.getBlob()).thenReturn(renditionBlob);
    when(amAssetRendition.getAsset()).thenReturn(asset);
    when(amAssetRendition.getName()).thenReturn(ORIGINAL_RENDITION);

    when(amAssetRenditionNoBlob.getBlob()).thenReturn(null);
    when(amAssetRenditionNoBlob.getAsset()).thenReturn(asset);
    when(amAssetRenditionNoBlob.getName()).thenReturn(ORIGINAL_RENDITION);

    when(assetContent.getId()).thenReturn(BLOB_ID);
    when(assetContent.getName()).thenReturn(ASSET_CONTENT_NAME);

    when(assetContentInvalidID.getId()).thenReturn("MyID:1");
    when(amAssetRenditionInvalidId.getAsset()).thenReturn(assetInvalid);
    when(amAssetRenditionInvalidId.getBlob()).thenReturn(renditionBlob);

    renditions = new ArrayList<>();
    renditions.add(amAssetRendition);

    cacheKey = new DownloadCollectionZipCacheKey(renditions, contentRepository, mimeTypeService);
  }

  @AfterClass
  public static void cleanUp() {
    //noinspection ResultOfMethodCallIgnored
    zipFile.delete();
  }

  @Test
  public void testEquals() throws Exception {
    DownloadCollectionZipCacheKey cacheKeySame = new DownloadCollectionZipCacheKey(renditions, contentRepository, mimeTypeService);
    assertTrue(cacheKey.equals(cacheKeySame));
  }

  @Test
  public void testEqualsFail() throws Exception {
    List<AMAssetRendition> renditionsOther = new ArrayList<>();
    renditionsOther.add(amAssetRendition);
    renditionsOther.add(amAssetRenditionInvalidId);
    DownloadCollectionZipCacheKey cacheKeyOther = new DownloadCollectionZipCacheKey(renditionsOther, contentRepository, mimeTypeService);

    assertFalse(cacheKey.equals(cacheKeyOther));
    assertFalse(cacheKeyOther.equals(cacheKey));

    //noinspection EqualsBetweenInconvertibleTypes
    assertFalse(cacheKey.equals("Test"));
  }

  @Test
  public void testNotEquals() throws Exception {
    assertNotEquals(new DownloadCollectionZipCacheKey(new ArrayList<AMAssetRendition>(), contentRepositoryAnother, mimeTypeService), cacheKey);
  }

  @Test
  public void testHashCode() throws Exception {
    List<AMAssetRendition> renditions = new ArrayList<>();
    renditions.add(amAssetRendition);
    DownloadCollectionZipCacheKey cacheKeyOne = new DownloadCollectionZipCacheKey(renditions, contentRepository, mimeTypeService);
    assertTrue(cacheKeyOne.hashCode() != 0);
    DownloadCollectionZipCacheKey cacheKeyTwo = new DownloadCollectionZipCacheKey(renditions, contentRepositoryAnother, mimeTypeService);
    assertNotEquals(cacheKeyOne.hashCode(), cacheKeyTwo.hashCode());
  }

  @Test
  public void testEvaluate() throws Exception {
    assertEquals(zipFile, cacheKey.evaluate(cache));
  }

  @Test
  public void testCreateDownloadCollectionZip() throws Exception {
    File zip = cacheKey.evaluate(cache);

    List<String> files = unZipIt(zip);

    assertEquals(1, files.size());
    assertEquals(ASSET_CONTENT_NAME + "_" + ORIGINAL_RENDITION + "_" + BLOB_ID_NUMBER + "." + JPG, files.get(0));
  }

  @Test
  public void testCreateDownloadCollectionZipBlobNull() throws Exception {
    List<AMAssetRendition> renditions = new ArrayList<>();
    renditions.add(amAssetRenditionNoBlob);
    DownloadCollectionZipCacheKey cacheKey = new DownloadCollectionZipCacheKey(renditions, contentRepository, mimeTypeService);
    File zip = cacheKey.evaluate(cache);
    List<String> files = unZipIt(zip);
    assertEquals(0, files.size());
  }

  @Test
  public void testCreateDownloadCollectionZipInvalidId() throws Exception {
    List<AMAssetRendition> renditions = new ArrayList<>();
    renditions.add(amAssetRenditionInvalidId);
    DownloadCollectionZipCacheKey cacheKey = new DownloadCollectionZipCacheKey(renditions, contentRepository, mimeTypeService);
    File zip = cacheKey.evaluate(cache);
    List<String> files = unZipIt(zip);
    assertEquals(0, files.size());
  }

  @Test
  public void testWeight() throws Exception {
    File zip = cacheKey.evaluate(cache);
    assertEquals(zip.length(), cacheKey.weight(null, zip, 0));
  }

  @Test
  public void testCacheClass() throws Exception {
    File zip = cacheKey.evaluate(cache);
    assertEquals(CacheFactory.CACHE_CLASS_DISK, cacheKey.cacheClass(cache, zip));
  }

  /**
   * Unzip it
   *
   * @param zipFile input zip file
   */
  private List<String> unZipIt(File zipFile) {

    List<String> files = new ArrayList<>();
    ZipInputStream zis = null;
    try {
      //get the zip file content
      zis = new ZipInputStream(new FileInputStream(zipFile));
      //get the zipped file list entry
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        files.add(ze.getName());
        ze = zis.getNextEntry();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      IOUtils.closeQuietly(zis);
    }
    return files;
  }
}
