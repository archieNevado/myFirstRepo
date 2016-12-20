package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.blueprint.themeimporter.descriptors.Code;
import com.coremedia.blueprint.themeimporter.descriptors.Css;
import com.coremedia.blueprint.themeimporter.descriptors.JavaScript;
import com.coremedia.blueprint.themeimporter.descriptors.Resource;
import com.coremedia.blueprint.themeimporter.descriptors.ResourceBundle;
import com.coremedia.blueprint.themeimporter.descriptors.ThemeDefinition;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.util.PathUtil;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import com.coremedia.xml.XmlUtil5;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The ThemeImporter takes a zip file created by grunt and uploads it into the content repository.
 */
public class ThemeImporter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeImporter.class);

  private static final String DISABLE_COMPRESS_PROPERTY = "disableCompress";
  private static final String IE_EXPRESSION_PROPERTY = "ieExpression";
  private static final String IN_HEAD_PROPERTY = "inHead";
  private static final String DATA_URL_PROPERTY = "dataUrl";
  private static final String CODE_PROPERTY = "code";
  private static final String DATA_PROPERTY = "data";
  private static final String ARCHIVE_PROPERTY = "archive";
  private static final String CM_IMAGE_DOCTYPE = "CMImage";
  private static final String CM_RESOURCE_BUNDLE_DOCTYPE = "CMResourceBundle";
  private static final String CM_INTERACTIVE_DOCTYPE = "CMInteractive";
  private static final String CM_TEMPLATE_SET_DOCTYPE = "CMTemplateSet";
  private static final String CM_JAVA_SCRIPT_DOCTYPE = "CMJavaScript";
  private static final String CM_CSS_DOCTYPE = "CMCSS";
  private static final String CM_THEME_DOCTYPE = "CMTheme";

  private static final String JAVA_SCRIPT_CODE_TYPE = "js";
  private static final String CSS_CODE_TYPE = "css";

  private static final String MARKUP_TEMPLATE = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>%s</p></div>";
  private static final String LINE_SEPARATOR = "(\r\r|\n\n|\r\n\r\n)";
  private static final String LINE_BREAK = "</p><p>";

  /**
   * Name of the metadata directory in a theme zip file.
   * <p>
   * The value is "THEME-METADATA".
   */
  public static final String THEME_METADATA_DIR = "THEME-METADATA";

  // In this context only the "data" protocol (base 64 encoding) is relevant.
  // Therefore we do not parse out //host:port but put all the rest into the
  // path.
  // The pattern means                     url  ("        protocol   :  the/path     suffix     "        )
  private static final String URL_REGEX = "url\\([\"\']?((([^)\"']*?):)?([^)\"\'#?]*)([^)\"']*))[\"\']?\\)";
  // Capturing groups                                   123         3 2 4           45        51
  @VisibleForTesting static final int CG_URL = 1;
  @VisibleForTesting static final int CG_PROTOCOL = 3;
  @VisibleForTesting static final int CG_PATH = 4;
  @VisibleForTesting static final int CG_SUFFIX = 5;

  @VisibleForTesting static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

  private final MimeTypeService mimeTypeService;
  private final CapConnection capConnection;
  private final LocalizationService localizationService;


  // --- construct and configure ------------------------------------

  public ThemeImporter(@Nonnull CapConnection capConnection,
                       @Nonnull MimeTypeService mimeTypeService,
                       @Nonnull LocalizationService localizationService) {
    this.mimeTypeService = mimeTypeService;
    this.capConnection = capConnection;
    this.localizationService = localizationService;
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
  public final ThemeImporterResult importThemes(@Nonnull String targetFolder, @Nonnull Collection<File> zipFiles) {
    return importThemes(targetFolder, zipFiles.toArray(new File[zipFiles.size()]));
  }

  /**
   * Imports themes from the given zip files.
   * <p>
   * In case of multiple zipFiles with conflicting data the last variant wins.
   *
   * @param targetFolder the repository folder to import the theme to
   * @param zipFiles themes to import, each must be nonnull
   */
  public ThemeImporterResult importThemes(@Nonnull String targetFolder, File... zipFiles) {
    ImportData importData;
    try {
      importData = extractImportDataFromFiles(zipFiles);
    } catch (Exception e) {
      throw new ThemeImporterException("Theme import failed, no changes in content repository", e);
    }
    return doImportThemes(targetFolder, importData);
  }

  /**
   * Imports themes from the given input streams
   * <p>
   * In case of multiple zips with conflicting data the last variant wins.
   *
   * @param targetFolder the repository folder to import the theme to
   * @param zips the zipped themes to import, each must be nonnull
   */
  public ThemeImporterResult importThemes(@Nonnull String targetFolder, InputStream... zips) {
    ImportData importData;
    try {
      importData = extractImportDataFromStreams(zips);
    } catch (Exception e) {
      throw new ThemeImporterException("Theme import failed, no changes in content repository", e);
    }
    return doImportThemes(targetFolder, importData);
  }


  // --- internal ---------------------------------------------------

  private ImportData extractImportDataFromFiles(File[] zipFiles) throws IOException, MimeTypeParseException, ParserConfigurationException, SAXException {
    ImportData importData = new ImportData(mimeTypeService, capConnection);
    for (File file : zipFiles) {
      try (FileInputStream is = new FileInputStream(file)) {
        importData.collectFilesToImport(is);
      }
    }
    return importData;
  }

  private ImportData extractImportDataFromStreams(InputStream[] zips) throws IOException, MimeTypeParseException, ParserConfigurationException, SAXException {
    ImportData importData = new ImportData(mimeTypeService, capConnection);
    for (InputStream zip : zips) {
      importData.collectFilesToImport(zip);
    }
    return importData;
  }

  private ThemeImporterResult doImportThemes(@Nonnull String targetFolder, ImportData importData) {
    ThemeImporterContentHelper contentHelper = new ThemeImporterContentHelper(capConnection);
    try {
      ThemeImporterResultImpl result = new ThemeImporterResultImpl();
      importFiles(importData, normalize(targetFolder), contentHelper, result);
      contentHelper.checkInAll();
      return result;
    } catch (Exception e) {
      contentHelper.revertAll();
      throw new ThemeImporterException("Theme import failed", e);
    }
  }

  /**
   * Import the importData
   *
   * @param importData the source data to be processed
   * @param targetFolder the folder to import the theme to
   * @param result will be returned to the invoker, to be populated with information here
   */
  private void importFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper, ThemeImporterResultImpl result) {
    // Feel free to enhance the result type and pass the result down to more of
    // the process* methods, if they can contribute useful information for the
    // invoker of the theme importer.

    processImageFiles(importData, targetFolder, contentHelper);
    processPropertyFiles(importData, targetFolder, contentHelper);
    processWebFontFiles(importData, targetFolder, contentHelper);
    processInteractiveFiles(importData, targetFolder, contentHelper);
    processTemplateFiles(importData, targetFolder, contentHelper);
    processJsFiles(importData, targetFolder, contentHelper);
    processCssFiles(importData, targetFolder, contentHelper);

    // The theme descriptors are the last files to be processed.
    // If you change this processing order, check whether the warning in
    // #enhancedCodeToContent is still appropriate.
    processXmlFiles(importData, targetFolder, contentHelper, result);
  }

  private void processImageFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, Blob> image : importData.getImageFiles().entrySet()) {
      contentHelper.updateContent(CM_IMAGE_DOCTYPE, targetFolder, image.getKey(), Collections.singletonMap(DATA_PROPERTY, image.getValue()));
    }
  }

  private void processPropertyFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, String> propertyFile : importData.getPropertyFiles().entrySet()) {
      Struct localization = contentHelper.propertiesToStruct(propertyFile.getValue());
      Map<String, Object> properties = new HashMap<>();
      properties.put("localizations", localization);
      String name = getName(propertyFile.getKey());
      properties.put("locale", getLocale(name).toString());
      contentHelper.updateContent(CM_RESOURCE_BUNDLE_DOCTYPE, targetFolder, propertyFile.getKey(), properties);
    }
  }

  private void processWebFontFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, Blob> webFont : importData.getWebFontFiles().entrySet()) {
      Map<String, Blob> properties = Collections.singletonMap(DATA_PROPERTY, webFont.getValue());
      contentHelper.updateContent(CM_IMAGE_DOCTYPE, targetFolder, webFont.getKey(), properties);
    }
  }

  private void processInteractiveFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, Blob> interactive : importData.getInteractiveFiles().entrySet()) {
      Map<String, Blob> properties = Collections.singletonMap(DATA_PROPERTY, interactive.getValue());
      contentHelper.updateContent(CM_INTERACTIVE_DOCTYPE, targetFolder, interactive.getKey(), properties);
    }
  }

  private void processTemplateFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, Blob> templateSet : importData.getTemplateSetFiles().entrySet()) {
      Map<String, Blob> properties = Collections.singletonMap(ARCHIVE_PROPERTY, templateSet.getValue());
      contentHelper.updateContent(CM_TEMPLATE_SET_DOCTYPE, targetFolder, templateSet.getKey(), properties);
    }
  }

  private void processJsFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, String> js : importData.getJsFiles().entrySet()) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(CODE_PROPERTY, createMarkup(js.getValue(), js.getKey(), targetFolder, contentHelper));
      properties.put(DISABLE_COMPRESS_PROPERTY, js.getKey().endsWith(".min.js") ? 1 : 0);
      contentHelper.updateContent(CM_JAVA_SCRIPT_DOCTYPE, targetFolder, js.getKey(), properties);
    }
  }

  private void processCssFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper) {
    for (Map.Entry<String, String> css : importData.getCssFiles().entrySet()) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(CODE_PROPERTY, createMarkup(css.getValue(), css.getKey(), targetFolder, contentHelper));
      properties.put(DISABLE_COMPRESS_PROPERTY, css.getKey().endsWith(".min.css") ? 1 : 0);
      contentHelper.updateContent(CM_CSS_DOCTYPE, targetFolder, css.getKey(), properties);
    }
  }

  private void processXmlFiles(ImportData importData, String targetFolder, ThemeImporterContentHelper contentHelper, ThemeImporterResultImpl result) {
    Collection<Content> themeDescriptors = new HashSet<>();
    for (Map.Entry<String, Document> xmlFile : importData.getXmlFiles().entrySet()) {
      if (xmlFile.getKey().contains(THEME_METADATA_DIR)) {
        ThemeDefinition themeDefinition = domToThemeDefinition(xmlFile.getValue());
        if (themeDefinition!=null) {
          themeDescriptors.add(createTheme(themeDefinition, targetFolder, contentHelper));
        }
      }
    }
    result.setThemeDescriptors(themeDescriptors);
  }

  private static ThemeDefinition domToThemeDefinition(Document document) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ThemeDefinition.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      return (ThemeDefinition) jaxbUnmarshaller.unmarshal(document);
    } catch (JAXBException e) {
      LOGGER.error("Cannot extract theme from DOM", e);
      return null;
    }
  }

  private Content createTheme(ThemeDefinition themeDefinition, String targetFolder, ThemeImporterContentHelper contentHelper) {
    String baseFolder = targetFolder + themeDefinition.getName();
    Map<String, Object> properties = new HashMap<>();

    properties.put("viewRepositoryName", themeDefinition.getViewRepositoryName());
    properties.put("description", themeDefinition.getName());
    properties.put("detailText", formatDescription(themeDefinition.getDescription()));
    properties.put("icon", fetchAndDeleteThumbnail(themeDefinition.getThumbnail(), baseFolder, contentHelper));

    List<Content> jScriptLibs = enhanceJavaScripts(themeDefinition.getJavaScriptLibraries().getJavaScripts(), baseFolder, contentHelper);
    List<Content> jScripts = enhanceJavaScripts(themeDefinition.getJavaScripts().getJavaScripts(), baseFolder, contentHelper);
    List<Content> csss = enhanceCsss(themeDefinition.getStyleSheets().getCss(), baseFolder, contentHelper);
    List<Content> resourceBundles = enhanceResourceBundles(themeDefinition.getResourceBundles().getResourceBundles(), baseFolder, contentHelper);
    List<Content> templateSets = fetchExistingResourceContents(themeDefinition.getTemplateSets().getTemplateSet(), baseFolder, contentHelper);

    properties.put("javaScriptLibs", jScriptLibs);
    properties.put("javaScripts", jScripts);
    properties.put("css", csss);
    properties.put("resourceBundles", resourceBundles);
    properties.put("templateSets", templateSets);

    String themeContentName = StringUtils.capitalize(themeDefinition.getName()) + " Theme";
    Content themeContent = contentHelper.updateContent(CM_THEME_DOCTYPE, baseFolder, themeContentName, properties);
    if (themeContent!=null) {
      LOGGER.info("Created Theme in {}", themeContent.getPath());
    } else {
      LOGGER.error("Cannot create theme content.");
    }
    return themeContent;
  }

  private Markup formatDescription(String description) {
    if (!StringUtils.hasText(description)) {
      return null;
    }
    String detailText = String.format(ThemeImporter.MARKUP_TEMPLATE, description.replaceAll(LINE_SEPARATOR, LINE_BREAK));
    return MarkupFactory.fromString(detailText);
  }

  private Blob fetchAndDeleteThumbnail(String thumbnailName, String baseFolder, ThemeImporterContentHelper contentHelper) {
    if (!StringUtils.hasText(thumbnailName)) {
      return null;
    }
    Content thumbnail = contentHelper.fetchContent(PathUtil.normalizePath(baseFolder + "/" + thumbnailName));
    Blob result = null;
    if (thumbnail!=null) {
      result = thumbnail.getBlob(DATA_PROPERTY);
      LOGGER.debug("Delete thumbnail {}, because it is supposed to be temporary.", thumbnail);
      contentHelper.deleteContent(thumbnail);
    }
    return result;
  }

  /**
   * Enhance code contents with data from the theme descriptor.
   * <p>
   * The code resources are imported before the theme descriptor, so they are
   * supposed to exist in the content repository when this is invoked.
   *
   * @return the list of processed code contents
   */
  private List<Content> enhanceCsss(@Nonnull List<Css> resources, String baseFolder, ThemeImporterContentHelper contentHelper) {
    return resources.stream().map(css -> enhanceCss(baseFolder, css, contentHelper)).filter(o -> o!=null).collect(Collectors.toList());
  }

  private List<Content> enhanceJavaScripts(@Nonnull List<JavaScript> resources, String baseFolder, ThemeImporterContentHelper contentHelper) {
    return resources.stream().map(javaScript -> enhaceJavaScript(baseFolder, javaScript, contentHelper)).filter(o -> o!=null).collect(Collectors.toList());
  }

  private Content enhanceCss(String baseFolder, Css code, ThemeImporterContentHelper contentHelper) {
    Map<String, Object> codeProperties = extractCssProperties(code);
    return enhancedCodeToContent(code, baseFolder, CM_CSS_DOCTYPE, codeProperties, contentHelper);
  }

  private Content enhaceJavaScript(String baseFolder, JavaScript code, ThemeImporterContentHelper contentHelper) {
    Map<String, Object> codeProperties = extractJavaScriptProperties(code);
    return enhancedCodeToContent(code, baseFolder, CM_JAVA_SCRIPT_DOCTYPE, codeProperties, contentHelper);
  }

  private Content enhancedCodeToContent(Code code, String baseFolder, String contentType, Map<String, Object> codeProperties, ThemeImporterContentHelper contentHelper) {
    String link = code.getLink();
    if (!isExternalLink(link)) {
      String path = PathUtil.normalizePath(baseFolder + "/" + link);
      // Since the theme descriptor is processed at last, any content code
      // resources must exist meanwhile.
      if (contentHelper.fetchContent(path)==null) {
        LOGGER.warn("Referenced code resource {} does not exist, ignore.", path);
        return null;
      } else {
        return contentHelper.updateContent(contentType, path, codeProperties);
      }
    } else {
      String path = "external/" + getName(link) + "." + extension(link);
      // No corresponding theme internal resource, so this content is created
      // here when it occurs in a theme descriptor.
      return contentHelper.updateContent(contentType, normalize(baseFolder), path, codeProperties);
    }
  }

  private Map<String, Object> extractCssProperties(Css code) {
    return extractCodeProperties(code, CSS_CODE_TYPE);
  }

  private Map<String, Object> extractJavaScriptProperties(JavaScript code) {
    Map<String, Object> codeProperties = extractCodeProperties(code, JAVA_SCRIPT_CODE_TYPE);
    codeProperties.put(IN_HEAD_PROPERTY, code.isInHead() ? 1 : 0);
    return codeProperties;
  }

  /**
   * Extract code properties from theme descriptor entry.
   * <p>
   * Returns a mutable map, for further enhancements.
   */
  private Map<String, Object> extractCodeProperties(Code code, String codeType) {
    Map<String, Object> codeProperties = new HashMap<>();
    boolean disableCompress = code.isDisableCompress() || code.getLink().endsWith(".min." + codeType);
    codeProperties.put(DISABLE_COMPRESS_PROPERTY, disableCompress ? 1 : 0);
    String ieExpression = code.getIeExpression();
    codeProperties.put(IE_EXPRESSION_PROPERTY, ieExpression==null ? "" : ieExpression);
    String link = code.getLink();
    if (isExternalLink(link)) {
      codeProperties.put(DATA_URL_PROPERTY, link);
    }
    return codeProperties;
  }

  /**
   * Enhance resource bundles with data from the theme descriptor.
   * <p>
   * The resource bundles are imported before the theme descriptor, so they are
   * supposed to exist in the content repository when this is invoked.
   *
   * @return the list of processed resource bundles
   */
  private List<Content> enhanceResourceBundles(List<? extends ResourceBundle> resourceBundles, String baseFolder, ThemeImporterContentHelper contentHelper) {
    List<Content> result = new ArrayList<>();
    if (resourceBundles != null) {
      for (ResourceBundle resourceBundle : resourceBundles) {
        String contentPath = PathUtil.normalizePath(baseFolder + "/" + resourceBundle.getLink());
        Content content = contentHelper.fetchContent(contentPath);
        if (content!=null) {
          localizationService.hierarchizeResourceBundles(content);
          result.add(content);
        }
      }
    }
    return result;
  }

  private List<Content> fetchExistingResourceContents(List<? extends Resource> resources, String baseFolder, ThemeImporterContentHelper contentHelper) {
    List<Content> result = new ArrayList<>();
    if (resources != null) {
      for (Resource resource : resources) {
        String path = PathUtil.normalizePath(baseFolder + "/" + resource.getLink());
        Content content = contentHelper.fetchContent(path);
        if (content != null) {
          result.add(content);
        } else {
          LOGGER.warn("Referenced code resource {} does not exist, ignore.", path);
        }
      }
    }
    return result;
  }

  private Markup createMarkup(String text, String fileName, String targetFolder, ThemeImporterContentHelper contentHelper) {
    String targetDocumentPath = targetFolder+fileName;
    String markupAsString = String.format(MARKUP_TEMPLATE, urlsToXlinks(text, targetDocumentPath, contentHelper));
    return MarkupFactory.fromString(markupAsString);
  }

  @VisibleForTesting
  String urlsToXlinks(String line, String targetDocumentPath, ThemeImporterContentHelper contentHelper) {
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

      String uri = matcher.group(CG_URL);
      String protocol = matcher.group(CG_PROTOCOL);
      String path = matcher.group(CG_PATH);
      String suffix = matcher.group(CG_SUFFIX);

      // replace url -> RichText (incl non-matching prefix)
      String replacement = urlReplacement(uri, protocol, path, suffix, targetDocumentPath, contentHelper);
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

  private String urlReplacement(String uri, String protocol, String path, String suffix, String targetDocumentPath, ThemeImporterContentHelper contentHelper) {
    if (protocol == null) {
      return toRichtextInternalLink(path, targetDocumentPath, suffix, contentHelper);
    } else if (DATA_PROPERTY.equals(protocol)) {
      return toRichtextPlain(protocol, path);
    } else {
      return toRichtextHref(URI.create(uri), uri);
    }
  }

  private String toRichtextInternalLink(String uriMatch, String targetDocumentPath, String suffix, ThemeImporterContentHelper contentHelper) {
    try {
      String linkPath = PathUtil.concatPath(targetDocumentPath, uriMatch);
      Content referencedContent = contentHelper.fetchContent(linkPath);
      if (referencedContent != null) {
        URI linkImportId = new URI("coremedia", "", "/cap/resources/" + contentHelper.id(referencedContent), null);
        if (!StringUtils.isEmpty(suffix)) {
          LOGGER.info("Ignoring link suffix {} of {}, no reasonable way to deal with.", suffix, uriMatch);
        }
        if ("css".equals(extension(linkPath))) {
          return toRichtextHref(linkImportId, uriMatch);
        } else {
          return toRichtextImg(linkImportId);
        }
      } else {
        LOGGER.warn("Cannot resolve {}", linkPath);
      }
    } catch (Exception e) {
      LOGGER.error("Cannot handle {}, {}", uriMatch, targetDocumentPath, e);
    }
    return toRichtextPlain(null, uriMatch);
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

  private static String extension(String uri) {
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

  private static String normalize(@Nonnull String folder) {
    return folder.endsWith("/") ? folder : folder + "/";
  }

  private static boolean isExternalLink(String link) {
    return link.startsWith("http://") || link.startsWith("https://") || link.startsWith("//");
  }
}
