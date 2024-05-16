package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.xml.Filter;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.cae.richtext.filter.SaxAttributes.classAttribute;
import static com.google.common.base.MoreObjects.toStringHelper;
import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * Filter to map combinations of given rich text element and given marker
 * class to an alternative representation in HTML.
 * </p>
 * <p>
 * <strong>Example:</strong>
 * You may configure that {@code <span class="code">} gets mapped to
 * {@code <code>} in HTML.
 * </p>
 * <p>
 * Additional non-marker classes are kept as is.
 * </p>
 * <p>
 * <strong>Example:</strong>
 * If you configured that {@code <span class="code">} gets mapped to
 * {@code <code>} in HTML rich text like {@code <span class="code other">}
 * gets mapped to {@code <code class="other">}.
 * </p>
 * <p>
 * Ambiguous mappings are resolved by removing conflicting classes.
 * </p>
 * <p>
 * <strong>Example:</strong>
 * For marker classes {@code code} and {@code mark} having rich text of
 * {@code <span class="code mark">} it either gets mapped to
 * {@code <code>} or {@code <mark>} in HTML, ignoring the other mapping.
 * </p>
 *
 * @since 2210.1
 */
public class ReservedClassToElementFilter extends Filter implements FilterFactory {
  private static final Logger LOG = getLogger(lookup().lookupClass());

  /**
   * Parsed mapping configurations for quick lookup during processing.
   * Structure:
   * <pre>{@code
   * elementNameRichText: {
   *   markerClass: elementNameHtml,
   * }
   * }</pre>
   */
  @NonNull
  private final Map<String, Map<String, String>> parsedMappingConfig = new HashMap<>();
  /**
   * <p>
   * Active mappings, where the last activated mapping is at the
   * last position in stack. This ensures that on {@code endElement} the
   * correct mappings are applied.
   * </p>
   * <p>
   * For simplicity we store all entered elements, not just those, which
   * triggered a replacement. For unmapped elements, no overridden information
   * is defined, which just passes all attributes unmodified on
   * subsequent {@code endElement} call.
   * </p>
   * <p>
   * Note, that this field represents an internal state not to be copied in
   * copy constructor.
   * </p>
   */
  @NonNull
  private final Deque<ActiveMapping> activeMappings = new ArrayDeque<>();

  /**
   * Store locator to possibly provide better information on exception.
   */
  @Nullable
  private Locator locator;

  /**
   * Copy constructor, which copies the configuration, not the processing state.
   *
   * @param original original filter to copy
   */
  @VisibleForTesting
  ReservedClassToElementFilter(@NonNull ReservedClassToElementFilter original) {
    original.parsedMappingConfig.forEach(
            (rtElement, classToHtmlElement) -> parsedMappingConfig.put(rtElement, new HashMap<>(classToHtmlElement))
    );
    locator = null;
  }

  /**
   * <p>
   * Constructor with given set of mapping configurations to apply.
   * </p>
   * <p>
   * Any issues regarding the configuration (overridden mappings, duplicate
   * mappings) are reported at debug log level.
   * </p>
   *
   * @param configurations mapping configurations
   */
  public ReservedClassToElementFilter(@NonNull Iterable<? extends ReservedClassToElementConfig> configurations) {
    for (ReservedClassToElementConfig configuration : configurations) {
      String elementNameRichText = configuration.getElementNameRichText();
      String markerClass = configuration.getMarkerClass();
      String elementNameHtml = configuration.getElementNameHtml();

      parsedMappingConfig.merge(
              elementNameRichText,
              // Must be mutable for further processing.
              new HashMap<>(Map.of(markerClass, elementNameHtml)),
              (previousMapping, newMapping) -> mergeClassToHtmlElementMappings(elementNameRichText, previousMapping, newMapping));
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug(describeMappings());
    }
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    super.setDocumentLocator(locator);
    this.locator = locator;
  }

  /**
   * Describes the mappings in a printable format.
   *
   * @return described mappings
   */
  @NonNull
  private String describeMappings() {
    if (parsedMappingConfig.isEmpty()) {
      return "No mappings provided";
    }

    List<String> mappings = parsedMappingConfig.entrySet().stream().flatMap((rtElementEntry) -> {
              String rtElement = rtElementEntry.getKey();
              return rtElementEntry.getValue().entrySet().stream().map((classToHtmlEntry) -> {
                String markerClass = classToHtmlEntry.getKey();
                String htmlElement = classToHtmlEntry.getValue();
                return format("<%s class=\"%s\"> to <%s>", rtElement, markerClass, htmlElement);
              });
            })
            .sorted()
            .collect(toList());
    return mappings.stream()
            .collect(joining("\n\t", format("Configured Mappings (%d):\n\t", mappings.size()), "\n"));
  }

  /**
   * Configuration parsing: For a given rich text element, merge the new mapping
   * to the possibly already existing mapping configurations.
   *
   * @param elementNameRichText name of rich text element
   * @param previousMapping     previously applied mappings for class name to HTML
   *                            element
   * @param newMapping          new mapping for class name to HTML element
   * @return merged previous and new mappings with possibly resolved (and
   * reported) collisions
   */
  @NonNull
  private static Map<String, String> mergeClassToHtmlElementMappings(@NonNull String elementNameRichText,
                                                                     @NonNull Map<String, String> previousMapping,
                                                                     @NonNull Map<String, String> newMapping) {
    newMapping.forEach((newClassname, newHtmlElementName) -> previousMapping.merge(
            newClassname,
            newHtmlElementName,
            (previousHtmlName, newHtmlName) -> resolveHtmlNameMappingCollision(elementNameRichText, newClassname, previousHtmlName, newHtmlName)
    ));
    return previousMapping;
  }

  /**
   * Configuration parsing: Handles and reports a mapping collision for class
   * name to HTML element. Collisions may either be a duplicate mapping or
   * an overridden mapping (class name now maps to a new element instead).
   *
   * @param elementNameRichText name of the rich text element, the mapping
   *                            applies to
   * @param className           class name to map to HTML element
   * @param previousHtmlName    previous HTML element name
   * @param newHtmlName         new HTML element name
   * @return new HTML name, thus, preferring the new mapping over the old on
   * conflict
   */
  @NonNull
  private static String resolveHtmlNameMappingCollision(@NonNull String elementNameRichText,
                                                        @NonNull String className,
                                                        @NonNull String previousHtmlName,
                                                        @NonNull String newHtmlName) {
    if (LOG.isDebugEnabled()) {
      if (!previousHtmlName.equals(newHtmlName)) {
        LOG.debug("Configuration override for mapping of <{} class=\"{}\">: Was mapped to <{}>, is now mapped to <{}>.", elementNameRichText, className, previousHtmlName, newHtmlName);
      } else {
        LOG.debug("Duplicate configuration for mapping of <{} class=\"{}\"> to <{}>.", elementNameRichText, className, previousHtmlName);
      }
    }
    return newHtmlName;
  }

  /**
   * FilterFactory: Get a new instance if the filter with the same configuration
   * but with initial processing state.
   *
   * @param request  HTTP request
   * @param response HTTP response
   * @return new filter instance
   */
  @Override
  public ReservedClassToElementFilter getInstance(HttpServletRequest request, HttpServletResponse response) {
    return new ReservedClassToElementFilter(this);
  }

  /**
   * Filter a start element event.
   *
   * @param uri        The element's Namespace URI, or the empty string.
   * @param localName  The element's local name, or the empty string.
   * @param qName      The element's qualified (prefixed) name, or the empty
   *                   string.
   * @param attributes The element's attributes.
   * @throws SAXException The client may throw
   *                      an exception during processing.
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    ActiveMapping activeMapping = new ActiveMapping(uri, localName, qName);

    activeMappings.push(activeMapping);

    Map<String, String> classToHtmlElement = parsedMappingConfig.get(qName);

    // No mapping? Just proceed.
    if (classToHtmlElement == null) {
      activeMapping.startElement(attributes);
      return;
    }

    Optional<SaxAttribute> optionalClassAttribute = classAttribute(attributes);

    // No class attribute, thus, no relevant mapping to check.
    if (optionalClassAttribute.isEmpty()) {
      activeMapping.startElement(attributes);
      return;
    }

    SaxAttribute classAttribute = optionalClassAttribute.get();

    List<String> allClasses = classAttribute.getDistinctValues();
    List<String> classNamesToHandle = allClasses.stream()
            .filter(classToHtmlElement::containsKey)
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));

    // No relevant class attributes, thus, no relevant mapping to check.
    if (classNamesToHandle.isEmpty()) {
      activeMapping.startElement(attributes);
      return;
    }

    String firstMatchedClassName = classNamesToHandle.stream().findFirst().orElseThrow();
    // We will ignore any classes mapped here when starting the element.
    allClasses.removeAll(classNamesToHandle);
    // If not empty, classNamesToHandle now contains ambiguous mappings.
    classNamesToHandle.remove(firstMatchedClassName);

    if (LOG.isWarnEnabled() && classNamesToHandle.size() > 1) {
      LOG.warn("Ambiguous mapping for element [uri={}, localName={}, qName={}]: Multiple marker classes detected for element. Accepted only: '{}'. Ignored class names: {}.",
              uri, localName, qName,
              firstMatchedClassName,
              classNamesToHandle);
    }

    Attributes newAttributes = classAttribute.withOverriddenValues(allClasses);
    String newName = classToHtmlElement.get(firstMatchedClassName);

    // We are heading towards HTML. No namespace URI to add.
    activeMapping.overrideElement("", newName, newName);

    activeMapping.startElement(newAttributes);
  }

  /**
   * Filter an end element event.
   *
   * @param uri       The element's Namespace URI, or the empty string.
   * @param localName The element's local name, or the empty string.
   * @param qName     The element's qualified (prefixed) name, or the empty
   *                  string.
   * @throws SAXException The client may throw
   *                      an exception during processing.
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    ActiveMapping activeMapping;
    try {
      activeMapping = activeMappings.pop();
    } catch (Exception e) {
      throw new SAXParseException(
              format("Unexpected state: More elements ended than started. Element requested to end: [uri=%s, localName=%s, qName=%s].", uri, localName, qName),
              locator,
              e
      );
    }
    activeMapping.endElement(uri, localName, qName);
  }

  /**
   * Exposes super call to {@code endElement} for processing in active mappings.
   */
  private void superStartElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    super.startElement(uri, localName, qName, attributes);
  }

  /**
   * Exposes super call to {@code endElement} for processing in active mappings.
   */
  private void superEndElement(String uri, String localName, String qName) throws SAXException {
    super.endElement(uri, localName, qName);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
            .add("parsedMappingConfig", parsedMappingConfig)
            .toString();
  }

  /**
   * Represents a possible mapped element. If no configured mapping exists for
   * tuple {@code <richTextElement, classValue>}, but the given
   * rich text element is equal to one for which mappings exist by class, we
   * still must remember them to correctly handle element nesting. To do so,
   * the active mapping will contain the same values for rich text element
   * name and HTML element name.
   */
  private final class ActiveMapping {
    @NonNull
    private final String uri;
    @NonNull
    private final String localName;
    @NonNull
    private final String qName;
    @Nullable
    private String overriddenUri;
    @Nullable
    private String overriddenLocalName;
    @Nullable
    private String overriddenQName;

    private ActiveMapping(@NonNull String uri,
                          @NonNull String localName,
                          @NonNull String qName) {
      this.uri = uri;
      this.localName = localName;
      this.qName = qName;
    }

    @NonNull
    private String getUri() {
      if (overriddenUri != null) {
        return overriddenUri;
      }
      return uri;
    }

    @NonNull
    private String getLocalName() {
      if (overriddenLocalName != null) {
        return overriddenLocalName;
      }
      return localName;
    }

    /**
     * Returns the qualified name, preferring overridden QName.
     *
     * @return qualified name
     */
    @NonNull
    private String getQName() {
      if (overriddenQName != null) {
        return overriddenQName;
      }
      return qName;
    }


    /**
     * Signals to replace the current element, which is subsequently respected
     * on calls to {@link #startElement(Attributes)} and
     * {@link #endElement(String, String, String)}.
     *
     * @param uri       URI to override
     * @param localName localName to override
     * @param qName     qualified name to override
     */
    @SuppressWarnings("SameParameterValue")
    private void overrideElement(String uri, String localName, String qName) {
      this.overriddenUri = uri;
      this.overriddenLocalName = localName;
      this.overriddenQName = qName;
    }

    /**
     * Starts the element with possibly overridden values.
     *
     * @param attributes attributes to use
     * @throws SAXException forwarded from {@code startElement}
     */
    private void startElement(Attributes attributes) throws SAXException {
      superStartElement(getUri(), getLocalName(), getQName(), attributes);
    }

    /**
     * Checks, if the current mapping is applicable and possibly transforms
     * to calling {@code endElement} with alternative qualified name. If not
     * applicable, {@code endElement} will be called anyway, but without
     * applied mapping and the returned value will be {@code false}, thus, the
     * mapping is not handled yet.
     *
     * @param uri       The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param qName     The element's qualified (prefixed) name, or the empty
     *                  string.
     * @throws SAXException forwarded from {@code endElement}
     */
    private void endElement(String uri, String localName, String qName) throws SAXException {
      if (this.uri.equals(uri) && this.localName.equals(localName) && this.qName.equals(qName)) {
        superEndElement(getUri(), getLocalName(), getQName());
      } else {
        throw new SAXParseException(format("Requested to end element [uri=%s, localName=%s, qName=%s] but expected to match active mapping: %s", uri, localName, qName, this), locator);
      }
    }

    @Override
    public String toString() {
      return toStringHelper(this)
              .add("uri", uri)
              .add("localName", localName)
              .add("qName", qName)
              .add("overriddenUri", overriddenUri)
              .add("overriddenLocalName", overriddenLocalName)
              .add("overriddenQName", overriddenQName)
              .toString();
    }
  }
}
