package com.coremedia.blueprint.themeimporter;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.xml.XmlUtil5;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ImportData {
  private static final Logger LOG = LoggerFactory.getLogger(ImportData.class);

  private static final String[] IMAGE_TYPES = new String[] {"jpeg", "png", "gif", "svg", "icon"};
  private static final String[] WEBFONT_TYPES = new String[] {"fontobject", "ttf", "otf", "woff"};

  private final MimeTypeService mimeTypeService;
  private final CapConnection capConnection;

  // LinkedHashMaps in order to preserve the put order.
  // Necessary for deterministic behaviour of multi theme import with
  // conflicting data.
  private Map<String, String> cssFiles = new LinkedHashMap<>();
  private Map<String, String> jsFiles = new LinkedHashMap<>();
  private Map<String, Document> xmlFiles = new LinkedHashMap<>();
  private Map<String, String> propertyFiles = new LinkedHashMap<>();
  private Map<String, Blob> webFontFiles = new LinkedHashMap<>();
  private Map<String, Blob> imageFiles = new LinkedHashMap<>();
  private Map<String, Blob> interactiveFiles = new LinkedHashMap<>();
  private Map<String, Blob> templateSetFiles = new LinkedHashMap<>();


  // --- construct and configure ------------------------------------

  ImportData(MimeTypeService mimeTypeService, CapConnection capConnection) {
    this.mimeTypeService = mimeTypeService;
    this.capConnection = capConnection;
  }


  // --- build ------------------------------------------------------

  /**
   * Collect all files to input. Call this method if various themes depending on each other should be imported.
   *
   * @param zipFile the zipped theme to import
   */
  void collectFilesToImport(@Nonnull InputStream zipFile) throws IOException, MimeTypeParseException, ParserConfigurationException, SAXException {
    try (ZipInputStream zipStream = new ZipInputStream(zipFile)) {
      for (ZipEntry entry=zipStream.getNextEntry(); entry!=null; entry=zipStream.getNextEntry()) {
        if (!entry.isDirectory()) {
          String mimeType = getMimeType(entry);
          processZipEntry(zipStream, entry, mimeType);
        }
        zipStream.closeEntry();
      }
    }
  }


  // --- request ----------------------------------------------------

  Map<String, String> getCssFiles() {
    return cssFiles;
  }

  Map<String, String> getJsFiles() {
    return jsFiles;
  }

  Map<String, Document> getXmlFiles() {
    return xmlFiles;
  }

  Map<String, String> getPropertyFiles() {
    return propertyFiles;
  }

  Map<String, Blob> getWebFontFiles() {
    return webFontFiles;
  }

  Map<String, Blob> getImageFiles() {
    return imageFiles;
  }

  Map<String, Blob> getInteractiveFiles() {
    return interactiveFiles;
  }

  Map<String, Blob> getTemplateSetFiles() {
    return templateSetFiles;
  }


  // --- internal ---------------------------------------------------

  private void processZipEntry(InputStream stream, ZipEntry entry, String mimeType) throws IOException, MimeTypeParseException, SAXException, ParserConfigurationException {
    String mimeTypeLC = mimeType.toLowerCase(Locale.ROOT);
    if (hasType(mimeTypeLC, "css")) {
      cssFiles.put(entry.getName(), IOUtils.toString(stream));
    } else if (hasType(mimeTypeLC, "properties")) {
      propertyFiles.put(entry.getName(), readProperties(stream));
    } else if (hasType(mimeTypeLC, "javascript")) {
      jsFiles.put(entry.getName(), IOUtils.toString(stream));
    } else if (hasType(mimeTypeLC, WEBFONT_TYPES)) {
      putBlob(stream, entry, mimeType, webFontFiles);
    } else if (hasType(mimeTypeLC, IMAGE_TYPES)) {
      putBlob(stream, entry, mimeType, imageFiles);
    } else if (hasType(mimeTypeLC, "shockwave-flash")) {
      putBlob(stream, entry, mimeType, interactiveFiles);
    } else if (hasType(mimeTypeLC, "java-archive")) {
      putBlob(stream, entry, mimeType, templateSetFiles);
    } else if (hasType(mimeTypeLC, "xml")) {
      xmlFiles.put(entry.getName(), new XmlUtil5(false).parse(IOUtils.toBufferedInputStream(stream)));
    } else if (!entry.getName().endsWith(".map")) {
      LOG.warn("Ignoring file {} with mimetype {}", entry.getName(), mimeType);
    }
  }

  @VisibleForTesting
  String readProperties(InputStream stream) throws IOException {
    // Properties files in the Java world are latin-1 encoded, due to
    // Properties#load(InputStream).
    return IOUtils.toString(new BufferedReader(new InputStreamReader(stream, StandardCharsets.ISO_8859_1)));
  }

  private void putBlob(InputStream stream, ZipEntry entry, String mimeType, Map<String, Blob> collection) throws MimeTypeParseException, IOException {
    Blob data = capConnection.getBlobService().fromBytes(IOUtils.toByteArray(stream), mimeType);
    collection.put(entry.getName(), data);
  }

  private String getMimeType(ZipEntry entry) {
    return mimeTypeService.getMimeTypeForResourceName(entry.getName());
  }

  private static boolean hasType(String canonicalMimeType, String... types) {
    for (String type : types) {
      if (canonicalMimeType.contains(type)) {
        return true;
      }
    }
    return false;
  }
}
