package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.themeimporter.descriptors.ThemeDefinition;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.common.util.PathUtil;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.xml.XmlUtil5;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.activation.MimeTypeParseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ImportData {
  private static final Logger LOG = LoggerFactory.getLogger(ImportData.class);

  private static final String[] IMAGE_TYPES = new String[] {"jpeg", "png", "gif", "svg", "icon", "webp", "avif"};
  private static final String[] WEBFONT_TYPES = new String[] {"fontobject", "ttf", "otf", "woff"};
  private static final String[] STATIC_RESOURCES_TYPES = new String[] {"wasm", "json"};

  private final MimeTypeService mimeTypeService;
  private final CapConnection capConnection;

  private final Collection<Map<String, ?>> allFileMaps = new ArrayList<>();

  // LinkedHashMaps in order to preserve the put order.
  // Necessary for deterministic behaviour of multi theme import with
  // conflicting data.
  private final Map<String, String> styleSheets = createPathToObjectMap();
  private final Map<String, String> javaScripts = createPathToObjectMap();
  private final Map<String, Document> themeDescriptors = createPathToObjectMap();
  @VisibleForTesting
  final Map<String, String> resourceBundles = createPathToObjectMap();
  private final Map<String, Blob> webFonts = createPathToObjectMap();
  private final Map<String, Blob> images = createPathToObjectMap();
  private final Map<String, Blob> interactiveObjects = createPathToObjectMap();
  private final Map<String, Blob> templateSets = createPathToObjectMap();
  private final Map<String, String> settings = createPathToObjectMap();
  private final Map<String, Blob> staticResources = createPathToObjectMap();

  private final Map<String, byte[]> rawResourceBundles = new LinkedHashMap<>();


  // --- construct and configure ------------------------------------

  ImportData(MimeTypeService mimeTypeService, CapConnection capConnection) {
    this.mimeTypeService = mimeTypeService;
    this.capConnection = capConnection;
  }

  private <T> LinkedHashMap<String, T> createPathToObjectMap() {
    LinkedHashMap<String, T> result = new LinkedHashMap<>();
    allFileMaps.add(result);
    return result;
  }


  // --- build ------------------------------------------------------

  /**
   * Collect all files to input. Call this method if various themes depending on each other should be imported.
   *
   * @param zipFile the zipped theme to import
   */
  void collectFilesToImport(@NonNull InputStream zipFile) throws IOException, MimeTypeParseException, ParserConfigurationException, SAXException {
    try (ZipInputStream zipStream = new ZipInputStream(zipFile)) {
      for (ZipEntry entry=zipStream.getNextEntry(); entry!=null; entry=zipStream.getNextEntry()) {
        if (!entry.isDirectory()) {
          addFileToImport(zipStream, entry.getName());
        }
        zipStream.closeEntry();
      }
    }

    // Now we can access the theme descriptors, determine the resource bundle
    // encoding and turn the interim resource bundle bytes into Strings.
    Charset encoding = resourceBundleEncoding();
    for (Map.Entry<String, byte[]> entry : rawResourceBundles.entrySet()) {
      String resourceBundle = IOUtils.toString(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(entry.getValue()), encoding)));
      resourceBundles.put(entry.getKey(), resourceBundle);
    }
    rawResourceBundles.clear();
  }

  private Charset resourceBundleEncoding() {
    try {
      return Charset.forName(themeDescriptors.values().stream()
              .map(ThemeDefinitionHelper::themeDefinitionFromDom)
              .map(ThemeDefinition::getBundleEncoding)
              .filter(Objects::nonNull)
              .findFirst().orElse("ISO-8859-1"));  // backward compatible default
    } catch (Exception e) {
      LOG.warn("Cannot determine resource bundle encoding, fallback to default ISO-8859-1", e);
      return StandardCharsets.ISO_8859_1;
    }
  }


  // --- access -----------------------------------------------------

  Map<String, String> getStyleSheets() {
    return styleSheets;
  }

  Map<String, String> getJavaScripts() {
    return javaScripts;
  }

  Map<String, Document> getThemeDescriptors() {
    return themeDescriptors;
  }

  Map<String, String> getResourceBundles() {
    return resourceBundles;
  }

  Map<String, Blob> getWebFonts() {
    return webFonts;
  }

  Map<String, Blob> getImages() {
    return images;
  }

  Map<String, Blob> getInteractiveObjects() {
    return interactiveObjects;
  }

  Map<String, Blob> getTemplateSets() {
    return templateSets;
  }

  Map<String, String> getSettings() {
    return settings;
  }

  Map<String, Blob> getStaticResources() {
    return staticResources;
  }

  Set<String> getAffectedThemes() {
    Set<String> result = new HashSet<>();
    for (Map<String, ?> fileMap : allFileMaps) {
      for (String path : fileMap.keySet()) {
        String[] pathArcs = path.split("/");
        if (pathArcs.length > 0) {
          String theme = pathArcs[0];
          if (!ThemeImporterImpl.THEME_METADATA_DIR.equals(theme)) {
            result.add(theme);
          }
        }
      }
    }
    return result;
  }


  // --- internal ---------------------------------------------------

  void addFileToImport(InputStream stream, String originalPath) throws IOException, MimeTypeParseException, SAXException, ParserConfigurationException {
    String path = PathUtil.normalizePath(originalPath);
    if (PathUtil.isReferringToParent(path)) {
      throw new IllegalArgumentException("path leaving import folder not allowed for imported file");
    }

    String mimeType = getMimeType(path);
    String mimeTypeLC = mimeType.toLowerCase(Locale.ROOT);
    if (hasType(mimeTypeLC, "css")) {
      styleSheets.put(path, IOUtils.toString(stream, StandardCharsets.UTF_8));
    } else if (hasType(mimeTypeLC, "properties")) {
      rawResourceBundles.put(path, IOUtils.toByteArray(stream));
    } else if (hasType(mimeTypeLC, "javascript")) {
      javaScripts.put(path, IOUtils.toString(stream, StandardCharsets.UTF_8));
    } else if (hasType(mimeTypeLC, WEBFONT_TYPES)) {
      putBlob(stream, path, mimeType, webFonts);
    } else if (hasType(mimeTypeLC, IMAGE_TYPES)) {
      putBlob(stream, path, mimeType, images);
    } else if (hasType(mimeTypeLC, STATIC_RESOURCES_TYPES) && !ThemeImporterImpl.SETTINGS_JSON.matcher(path).matches()) {
      putBlob(stream, path, mimeType, staticResources);
    } else if (hasType(mimeTypeLC, "shockwave-flash")) {
      putBlob(stream, path, mimeType, interactiveObjects);
    } else if (hasType(mimeTypeLC, "java-archive")) {
      putBlob(stream, path, mimeType, templateSets);
    } else if (hasType(mimeTypeLC, "xml")) {
      themeDescriptors.put(path, new XmlUtil5(false).parse(IOUtils.toBufferedInputStream(stream)));
    } else if (hasType(mimeTypeLC, "json")) {
      settings.put(path, IOUtils.toString(stream, StandardCharsets.UTF_8));
    } else if (!path.endsWith(".map")) {
      LOG.warn("Ignoring file {} with mimetype {}", path, mimeType);
    }
  }

  private void putBlob(InputStream stream, String path, String mimeType, Map<String, Blob> collection) throws MimeTypeParseException, IOException {
    Blob data = capConnection.getBlobService().fromBytes(IOUtils.toByteArray(stream), mimeType);
    collection.put(path, data);
  }

  private String getMimeType(String path) {
    return mimeTypeService.getMimeTypeForResourceName(path);
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
