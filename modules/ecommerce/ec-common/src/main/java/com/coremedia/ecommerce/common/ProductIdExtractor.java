package com.coremedia.ecommerce.common;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.coremedia.cap.common.Blob;
import com.coremedia.common.util.Predicate;
import com.coremedia.util.StringUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class for extracting product ids from metadata.
 * To this end, the <code>AOSourceInvNo</code> attribute of any
 * <code>ArtworkOrObject</code> node from the XMP section of the IPTC data is read.
 */
public class ProductIdExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(ProductIdExtractor.class);

  private static final String IPTC_XMP_EXT_NS = "http://iptc.org/std/Iptc4xmpExt/2008-02-29/";
  private static final String ARTWORK_NODE = "ArtworkOrObject";
  private static final String INVENTORY_INFO = "Iptc4xmpExt:AOSourceInvNo";

  private static final XmpImageMetadataExtractor EXTRACTOR = XmpImageMetadataExtractor
          .builder()
          .atNameSpace(IPTC_XMP_EXT_NS)
          .atProperty(ARTWORK_NODE)
          .filteredBy(new Predicate<XMPPropertyInfo>() {
            @Override
            public boolean include(XMPPropertyInfo o) {
              return !StringUtil.isEmpty(o.getValue()) && o.getPath().endsWith(INVENTORY_INFO);
            }
          }).build();

  /**
   * Extract product ids from Blob metadata.
   *
   * @param blob the blob
   * @return the extracted product ids or an empty collection, if no product ids could be extracted
   */
  public static List<String> extractProductIds(Blob blob) {
    if (blob == null) {
      return Collections.emptyList();
    }

    MimeType contentType = blob.getContentType();
    if (!contentType.toString().startsWith("image/")) {
      // Naive Fix for warnings on PDFs. Using a more powerful meta-data-extraction like Tika recommended.
      LOG.debug("Product ID extraction only supported for blobs of type image/*. Current type: {}", contentType);
      return Collections.emptyList();
    }

    InputStream inputStream = blob.getInputStream();
    try {
      return extractProductIds(inputStream);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  /**
   * Extract product ids from metadata of the given input stream.
   *
   * @param inputStream the input stream
   * @return the extracted product ids or an empty collection, if no product ids could be extracted
   */
  public static List<String> extractProductIds(InputStream inputStream) {
    Metadata metadata;
    try {
      metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(inputStream), true);
    } catch (ImageProcessingException | IOException e) {
      LOG.warn("Could not extract metadata from input stream", e);
      return Collections.emptyList();
    }

    return new ArrayList<>(EXTRACTOR.apply(metadata).values());
  }
}
