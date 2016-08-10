package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.themeimporter.descriptors.Code;
import com.coremedia.blueprint.themeimporter.descriptors.Resource;
import com.coremedia.blueprint.themeimporter.descriptors.ResourceBundle;
import com.coremedia.blueprint.themeimporter.descriptors.ThemeDefinition;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.Version;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.util.PathUtil;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import com.coremedia.xml.XmlUtil5;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ThemeImporter takes a zip file created by grunt and uploads it into the content repository.
 */
public class ThemeImporter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeImporter.class);

  private static final String DISABLE_COMPRESS_PROPERTY = "disableCompress";
  private static final String CODE_PROPERTY = "code";
  private static final String DATA_PROPERTY = "data";
  private static final String ARCHIVE_PROPERTY = "archive";
  private static final String CM_IMAGE_DOCTYPE = "CMImage";
  private static final String CM_RESOURCE_BUNDLE_DOCTYPE = "CMResourceBundle";
  private static final String CM_INTERACTIVE_DOCTYPE = "CMInteractive";
  private static final String CM_TEMPLATE_SET_DOCTYPE = "CMTemplateSet";
  private static final String CM_JAVA_SCRIPT_DOCTYPE = "CMJavaScript";
  private static final String CMCSS_DOCTYPE = "CMCSS";
  private static final String CM_THEME_DOCTYPE = "CMTheme";

  private static final String THEME_METADATA_DIR = "THEME-METADATA";
  private static final String MARKUP_TEMPLATE = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>%s</p></div>";
  private static final String LINE_SEPARATOR = "(\r\r|\n\n|\r\n\r\n)";
  private static final String LINE_BREAK = "</p><p>";

  // The pattern means                                        url  ("        protocol   :  the/path    "        )
  private static final Pattern URL_PATTERN = Pattern.compile("url\\([\"\']?((([^)\"']*?):)?([^)\"\']*))[\"\']?\\)");
  // Capturing groups                                                      123         3 2 4         41

  private final MimeTypeService mimeTypeService;
  private final CapConnection capConnection;


  // --- construct and configure ------------------------------------

  public ThemeImporter(@Nonnull CapConnection capConnection, @Nonnull MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
    this.capConnection = capConnection;
  }


  // --- features ---------------------------------------------------

  /**
   * Imports themes from the given zip files.
   * <p>
   * Simply delegates to {@link #importThemes(String, File...)}.
   *
   * @param targetFolder the repository folder to import the theme to
   * @param zipFiles themes to import
   */
  public final void importThemes(@Nonnull String targetFolder, @Nonnull Collection<File> zipFiles) {
    importThemes(targetFolder, zipFiles.toArray(new File[zipFiles.size()]));
  }

  /**
   * Imports themes from the given zip files.
   *
   * @param targetFolder the repository folder to import the theme to
   * @param zipFiles themes to import, each must be nonnull
   */
  public void importThemes(@Nonnull String targetFolder, File... zipFiles) {
    try {
      ImportData importData = new ImportData(mimeTypeService, capConnection);
      for (File file : zipFiles) {
        try (FileInputStream is = new FileInputStream(file)) {
          importData.collectFilesToImport(is);
        }
      }
      importFiles(importData, normalize(targetFolder));
    } catch (Exception e) {
      throw new RuntimeException("Theme import failed", e);
    }
  }

  /**
   * Imports themes from the given input streams
   *
   * @param targetFolder the repository folder to import the theme to
   * @param zips the zipped themes to import, each must be nonnull
   */
  public void importThemes(@Nonnull String targetFolder, InputStream... zips) {
    try {
      ImportData importData = new ImportData(mimeTypeService, capConnection);
      for (InputStream zip : zips) {
        importData.collectFilesToImport(zip);
      }
      importFiles(importData, normalize(targetFolder));
    } catch (Exception e) {
      throw new RuntimeException("Theme import failed", e);
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * Import the importData
   *
   * @param targetFolder the folder to import the theme to
   */
  private void importFiles(ImportData importData, String targetFolder) {
    processImageFiles(importData, targetFolder);
    processPropertyFiles(importData, targetFolder);
    processWebFontFiles(importData, targetFolder);
    processInteractiveFiles(importData, targetFolder);
    processTemplateFiles(importData, targetFolder);
    processJsFiles(importData, targetFolder);
    processCssFiles(importData, targetFolder);
    processXmlFiles(importData, targetFolder);
  }

  private void processImageFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, Blob> image : importData.getImageFiles().entrySet()) {
      createContent(CM_IMAGE_DOCTYPE, Collections.singletonMap(DATA_PROPERTY, image.getValue()), targetFolder, image.getKey());
    }
  }

  private void processPropertyFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, String> propertyFile : importData.getPropertyFiles().entrySet()) {
      Struct localization = createStruct(propertyFile.getValue(), capConnection.getStructService());
      Map<String, Object> properties = new HashMap<>();
      properties.put("localizations", localization);
      String name = getName(propertyFile.getKey());
      properties.put("locale", getLocale(name).toString());
      createContent(CM_RESOURCE_BUNDLE_DOCTYPE, properties, targetFolder, propertyFile.getKey());
    }
  }

  private void processWebFontFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, Blob> webFont : importData.getWebFontFiles().entrySet()) {
      createContent(CM_IMAGE_DOCTYPE, Collections.singletonMap(DATA_PROPERTY, webFont.getValue()), targetFolder, webFont.getKey());
    }
  }

  private void processInteractiveFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, Blob> interactive : importData.getInteractiveFiles().entrySet()) {
      createContent(CM_INTERACTIVE_DOCTYPE, Collections.singletonMap(DATA_PROPERTY, interactive.getValue()), targetFolder, interactive.getKey());
    }
  }

  private void processTemplateFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, Blob> templateSet : importData.getTemplateSetFiles().entrySet()) {
      createContent(CM_TEMPLATE_SET_DOCTYPE, Collections.singletonMap(ARCHIVE_PROPERTY, templateSet.getValue()), targetFolder, templateSet.getKey());
    }
  }

  private void processJsFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, String> js : importData.getJsFiles().entrySet()) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(CODE_PROPERTY, createMarkup(js.getValue(), js.getKey(), targetFolder));
      if (js.getKey().endsWith(".min.js")) {
        properties.put(DISABLE_COMPRESS_PROPERTY, 1);
      }
      createContent(CM_JAVA_SCRIPT_DOCTYPE, properties, targetFolder, js.getKey());
    }
  }

  private void processCssFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, String> css : importData.getCssFiles().entrySet()) {
      Map<String, Object> properties = new HashMap<>();
      properties.put("code", createMarkup(css.getValue(), css.getKey(), targetFolder));
      if (css.getKey().endsWith(".min.css")) {
        properties.put(DISABLE_COMPRESS_PROPERTY, 1);
      }
      createContent(CMCSS_DOCTYPE, properties, targetFolder, css.getKey());
    }
  }

  private void processXmlFiles(ImportData importData, String targetFolder) {
    for (Map.Entry<String, Document> xmlFile : importData.getXmlFiles().entrySet()) {
      if (xmlFile.getKey().contains(THEME_METADATA_DIR)) {
        String theme = createTheme(xmlFile.getValue(), targetFolder);
        LOGGER.info("Successfully created Theme in {}", theme);
      }
    }
  }

  private static String normalize(@Nonnull String targetFolder) {
    return targetFolder.endsWith("/") ? targetFolder : targetFolder + "/";
  }

  private Content createContent(String newType, Map<String, ?> properties, String folder, String path) {
    String absolutePath = folder+"/"+path;
    try {
      Content newContent = getOrCreateContent(capConnection.getContentRepository(), newType, absolutePath);
      //Only do this if the document has not been checked out by somebody else.
      if (newContent != null) {
        if (!properties.isEmpty()) {
          newContent.setProperties(properties);
        }
        newContent.checkIn();
      }
      return newContent;
    } catch (Exception e) {
      LOGGER.error("Error creating content {} ", absolutePath, e);
      return null;
    }
  }

  private static Content getOrCreateContent(ContentRepository repository, String newType, String absolutePath) {
    Content newContent = repository.getChild(absolutePath);
    if (newContent != null) {
      //UPDATE
      newContent = repository.getChild(absolutePath);
      if (!newContent.getType().getName().equals(newType)) {
        //Set to null, because the type is different
        LOGGER.warn("Cannot update document {} since it is of type {} even though it should be of type {}", absolutePath, newContent.getType().getName(), newType);
        newContent = null;
      } else if (newContent.isCheckedIn()) {
        newContent.checkOut();
      } else if (newContent.isCheckedOut() && !newContent.isCheckedOutByCurrentSession()) {
        //Set to null, because a checked out document, that has been checked out by someone else is hard to handle
        newContent = null;
        LOGGER.warn("Cannot update document {} since it has been checkout out by somebody else.", absolutePath);
      }
    } else {
      ContentType type = repository.getContentType(newType);
      if (type != null) {
        newContent = type.create(repository.getRoot(), absolutePath);
      }
    }
    return newContent;
  }

  private static Struct createStruct(String text, StructService structService) {
    StructBuilder structBuilder = structService.createStructBuilder();
    for (StringTokenizer st = new StringTokenizer(text, "\n"); st.hasMoreTokens(); ) {
      String line = st.nextToken();
      if (line.trim().length() > 0 && line.split("=").length > 1) {
        String[] splitted = line.split("=", 2);
        structBuilder.declareString(splitted[0], Integer.MAX_VALUE, splitted[1]);
      }
    }

    return structBuilder.build();
  }

  private String createTheme(Document document, String targetFolder) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ThemeDefinition.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      ThemeDefinition themeDefinition = (ThemeDefinition) jaxbUnmarshaller.unmarshal(document);
      String baseFolder = targetFolder + themeDefinition.getName();

      Map<String, Object> properties = new HashMap<>();
      properties.put("viewRepositoryName", themeDefinition.getViewRepositoryName());
      properties.put("description", themeDefinition.getName());

      if (StringUtils.hasText(themeDefinition.getDescription())) {
        String detailText = String.format(ThemeImporter.MARKUP_TEMPLATE, themeDefinition.getDescription().replaceAll(LINE_SEPARATOR, LINE_BREAK));
        properties.put("detailText", MarkupFactory.fromString(detailText));
      }
      if (StringUtils.hasText(themeDefinition.getThumbnail())) {
        Content thumbnail = fetchContent(PathUtil.normalizePath(baseFolder + "/" + themeDefinition.getThumbnail()));
        if (thumbnail != null) {
          properties.put("icon", thumbnail.getBlob(DATA_PROPERTY));
          thumbnail.delete();
        }
      }

      updateCode(baseFolder, themeDefinition.getJavaScriptLibraries().getJavaScripts(), properties, "javaScriptLibs", CM_JAVA_SCRIPT_DOCTYPE, "js");
      updateCode(baseFolder, themeDefinition.getJavaScripts().getJavaScripts(), properties, "javaScripts", CM_JAVA_SCRIPT_DOCTYPE, "js");
      updateCode(baseFolder, themeDefinition.getStyleSheets().getCss(), properties, "css", CMCSS_DOCTYPE, "css");
      updateResourceBundles(baseFolder, themeDefinition.getResourceBundles().getResourceBundles(), properties, "resourceBundles");
      updateResources(baseFolder, themeDefinition.getTemplateSets().getTemplateSet(), properties, "templateSets");

      Content theme = createContent(CM_THEME_DOCTYPE, properties, baseFolder, StringUtils.capitalize(themeDefinition.getName()) + " Theme");
      if (theme != null) {
        return baseFolder;
      }
    } catch (JAXBException e) {
      LOGGER.error("Cannot create theme", e);
    }
    return null;
  }

  private void updateCode(String baseFolder, List<? extends Code> resources, Map<String, Object> properties, String propertyName, String type, String extension) {
    if (resources != null) {
      List<Content> result = new ArrayList<>();
      for (Code code : resources) {
        String link = code.getLink();
        Content content = fetchContent(PathUtil.normalizePath(baseFolder + "/" + link));
        Map<String, Object> codeProperties = new HashMap<>();

        boolean needsMinification = code.isDisableCompress() || link.endsWith(".min." + extension);
        if (needsMinification) {
          codeProperties.put(DISABLE_COMPRESS_PROPERTY, 1);
        }
        if (StringUtils.hasText(code.getIeExpression())) {
          codeProperties.put("ieExpression", code.getIeExpression());
        }

        if (content != null && !codeProperties.isEmpty()) {
          content.checkOut();
          content.setProperties(codeProperties);
          content.checkIn();
        } else if (link.startsWith("http://") || link.startsWith("https://") || link.startsWith("//")) {
          //This is a valid web url that is not relative, therefore there is no need to normalize it
          String path = "external/" + getName(link) + "." + getExtension(link);
          codeProperties.put("dataUrl", link);
          content = createContent(type, codeProperties, normalize(baseFolder), path);
        }
        if (content != null) {
          result.add(content);
        }
      }
      if (!result.isEmpty()) {
        properties.put(propertyName, result);
      }
    }
  }

  private void updateResourceBundles(String baseFolder, List<? extends ResourceBundle> resourceBundles, Map<String, Object> properties, String propertyName) {
    if (resourceBundles != null) {
      List<Content> result = new ArrayList<>();
      for (ResourceBundle resourceBundle : resourceBundles) {
        String link = resourceBundle.getLink();
        Content content = fetchContent(PathUtil.normalizePath(baseFolder + "/" + link));
        if (content != null) {
          if (!StringUtils.isEmpty(resourceBundle.getMaster())) {
            Content folder = content.getParent();
            if (folder != null) {
              Content master = fetchContent(PathUtil.normalizePath(folder.getPath() + "/" + resourceBundle.getMaster()));
              if (master != null) {
                content.checkOut();
                content.set("master", Collections.singletonList(master));
                Version version = master.getCheckedInVersion();
                if (version == null) {
                  version = master.getCheckedOutVersion();
                }
                if (version != null) {
                  content.set("masterVersion", IdHelper.parseVersionId(version.getId()));
                }
                content.checkIn();
              }
            }
          }
          if (resourceBundle.isLinkIntoTheme()) {
            result.add(content);
          }
        }
      }
      if (!result.isEmpty()) {
        properties.put(propertyName, result);
      }
    }
  }

  private void updateResources(String baseFolder, List<? extends Resource> resources, Map<String, Object> properties, String propertyName) {
    if (resources != null) {
      List<Content> result = new ArrayList<>();
      for (Resource resource : resources) {
        String link = resource.getLink();
        Content content = fetchContent(PathUtil.normalizePath(baseFolder + "/" + link));
        if (content != null) {
          result.add(content);
        }
      }
      if (!result.isEmpty()) {
        properties.put(propertyName, result);
      }
    }
  }

  private Markup createMarkup(String text, String fileName, String targetFolder) {
    String targetDocumentPath = targetFolder+fileName;
    String markupAsString = String.format(MARKUP_TEMPLATE, urlsToXlinks(text, targetDocumentPath));
    return MarkupFactory.fromString(markupAsString);
  }

  private String urlsToXlinks(String line, String targetDocumentPath) {
    Matcher matcher = URL_PATTERN.matcher(line);
    StringBuffer appender = new StringBuffer();  // NOSONAR Matcher#appendReplacement needs a StringBuffer
    StringBuilder result = new StringBuilder();
    int startNoMatch = 0;
    int endNoMatch;

    while (matcher.find()) {
      // Matcher does not support access to the non-matching part during an
      // appendReplacement loop. So we have to escape the part before the match...
      endNoMatch = matcher.start();
      result.append(XmlUtil5.escapeOrOmit(line.substring(startNoMatch, endNoMatch)));

      String uri = matcher.group(1);
      String protocol = matcher.group(3);  // NOSONAR magic number
      String path = matcher.group(4);  // NOSONAR magic number

      // replace url -> RichText (incl non-matching prefix)
      String replacement = urlReplacement(uri, protocol, path, targetDocumentPath);
      matcher.appendReplacement(appender, replacement);

      // extract the actual url replacement from the appendReplacement result
      result.append(appender.substring(endNoMatch - startNoMatch));

      // loop for next match
      startNoMatch = matcher.end();
      appender.setLength(0);
    }

    // ...and finally not use appendTail but do it manually
    result.append(XmlUtil5.escapeOrOmit(line.substring(startNoMatch)));
    return result.toString();
  }

  private String urlReplacement(String uri, String protocol, String path, String targetDocumentPath) {
    if (protocol == null) {
      return toRichtextInternalLink(path, targetDocumentPath);
    } else if (DATA_PROPERTY.equals(protocol)) {
      return toRichtextPlain(protocol, path);
    } else {
      return toRichtextHref(URI.create(uri), uri);
    }
  }

  private String toRichtextInternalLink(String uriMatch, String targetDocumentPath) {
    try {
      String linkPath = PathUtil.concatPath(targetDocumentPath, uriMatch);
      Content referencedContent = fetchContent(linkPath);
      if (referencedContent != null) {
        URI linkImportId = null;
        try {
          linkImportId = new URI("coremedia", "", "/cap/resources/" + IdHelper.parseContentId(referencedContent.getId()), null);
        } catch (URISyntaxException e) {
          LOGGER.error("Cannot create uri for {}", targetDocumentPath, e);
        }
        if (linkImportId != null) {
          String filetype = getExtension(linkPath);
          if ("css".equals(filetype)) {
            return toRichtextHref(linkImportId, uriMatch);
          } else {
            return toRichtextImg(linkImportId);
          }
        } else {
          return toRichtextPlain(null, uriMatch);
        }
      } else {
        // Could not resolve link. Preserve target as ordinary text.
        LOGGER.warn("Cannot resolve {}", linkPath);
        return toRichtextPlain(null, uriMatch);
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Cannot handle {}, {}", uriMatch, targetDocumentPath, e);
      return toRichtextPlain(null, uriMatch);
    }
  }

  private Content fetchContent(String path) {
    return capConnection.getContentRepository().getChild(path);
  }

  private String toRichtextPlain(String protocol, String uriMatch) {
    String protocolPrefix = protocol == null ? "" : protocol + ":";
    return "url(" + protocolPrefix + XmlUtil5.escapeOrOmit(uriMatch) + ")";
  }

  private String toRichtextImg(URI href) {
    StringBuilder builder = new StringBuilder("url(<img xlink:href=\"");
    builder.append(href);
    builder.append("/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>");
    builder.append(")");
    return builder.toString();
  }

  private String toRichtextHref(URI href, String linktext) {
    return "url(<a xlink:href=\"" + href + "\">" + XmlUtil5.escapeOrOmit(linktext) + "</a>)";
  }

  private static String getExtension(String uri) {
    int dot = uri.lastIndexOf('.');
    int slash = uri.lastIndexOf('/');
    return slash > dot || dot < 0 || dot >= uri.length() ? "" : uri.substring(dot + 1);
  }

  private static String getName(String uri) {
    String name = uri;
    name = name.substring(name.lastIndexOf('/') + 1, name.length());
    name = name.substring(0, name.lastIndexOf('.'));
    return name;
  }

  private static Locale getLocale(String name) {
    Locale locale = Locale.ENGLISH;
    if (name.contains("_")) {
      locale = LocaleUtils.toLocale(name.substring(name.indexOf('_') + 1, name.length()));
    }
    return locale;
  }
}
