package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.xml.sax.XMLFilter;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.coremedia.cap.common.XmlGrammar.RICH_TEXT_1_0_NAME;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Class under Test: {@link ReservedClassToElementFilter}.
 */
class ReservedClassToElementFilterTest {
  @SuppressWarnings("HttpUrlsUsage")
  private static final String RICH_TEXT_NAMESPACE = "http://www.coremedia.com/2003/richtext-1.0";
  private static final String EMPTY_RICH_TEXT_AS_STRING = wrapInline("");
  private static final Markup EMPTY_RICH_TEXT = asRichText(EMPTY_RICH_TEXT_AS_STRING);

  @Nested
  @DisplayName("Representation of <mark> element as <span class=\"mark\">")
  class MarkElementRepresentationUseCase {
    private final XMLFilter filter = new ReservedClassToElementFilter(List.of(
            ReservedClassToElementConfig.of("span", "mark")
    ));

    @Test
    void shouldMapReservedClassOnlyToElementWithoutClassAttribute() {
      String filtered = applyFilterToInline("Lorem <span class=\"mark\">ipsum</span> dolor", filter);
      assertThat(filtered)
              .contains("Lorem <mark>ipsum</mark> dolor");
    }

    @Test
    void shouldMapExpandedEmptySpan() {
      String filtered = applyFilterToInline("Lorem <span class=\"mark\"></span> dolor", filter);
      assertThat(filtered)
              .contains("Lorem <mark/> dolor");
    }

    @Test
    void shouldMapCollapsedEmptySpan() {
      String filtered = applyFilterToInline("Lorem <span class=\"mark\"/> dolor", filter);
      assertThat(filtered)
              .contains("Lorem <mark/> dolor");
    }

    @Test
    void shouldMapReservedClassButKeepOtherClasses() {
      String filtered = applyFilterToInline("Lorem <span class=\"mark marker-green\">ipsum</span> dolor", filter);
      assertThat(filtered)
              .contains("Lorem <mark class=\"marker-green\">ipsum</mark> dolor");
    }

    /**
     * This test is dedicated to an issue for {@code P2TagFilter} (CMS-21755).
     */
    @Test
    void shouldMapReservedClassButKeepOtherClassesAndAttributes() {
      String filtered = applyFilterToInline("Lorem <span class=\"mark marker-green\" dir=\"rtl\" lang=\"en\" xml:lang=\"de\">ipsum</span> dolor", filter);
      assertThat(filtered)
              .matches(".*Lorem <mark[^>]+class=\"marker-green\"[^>]*>ipsum</mark> dolor.*")
              .matches(".*Lorem <mark[^>]+dir=\"rtl\"[^>]*>ipsum</mark> dolor.*")
              .matches(".*Lorem <mark[^>]+lang=\"en\"[^>]*>ipsum</mark> dolor.*")
              .matches(".*Lorem <mark[^>]+xml:lang=\"de\"[^>]*>ipsum</mark> dolor.*");
    }

    @Test
    void shouldRespectNestedUsage() {
      String filtered = applyFilterToInline("<span class=\"mark marker-green\">Lorem <span class=\"mark marker-red\">ipsum</span> dolor</span>", filter);
      assertThat(filtered)
              .contains("<mark class=\"marker-green\">Lorem <mark class=\"marker-red\">ipsum</mark> dolor</mark>");
    }

    @Test
    void shouldKeepUnmatchedSpans() {
      String unmatchedSpan = "Lorem <span class=\"unmatched\">ipsum</span> dolor";
      String filtered = applyFilterToInline(unmatchedSpan, filter);
      assertThat(filtered)
              .contains(unmatchedSpan);
    }

    @Test
    void shouldKeepUnmatchedNestedSpans() {
      String filtered = applyFilterToInline("<span class=\"mark marker-green\">Lorem <span class=\"unmatched\">ipsum</span> dolor</span>", filter);
      assertThat(filtered)
              .contains("<mark class=\"marker-green\">Lorem <span class=\"unmatched\">ipsum</span> dolor</mark>");
    }
  }

  @Nested
  class Constructors {
    @Nested
    @DisplayName("Configurable: ReservedClassToElementFilter(Iterable<ReservedClassToElementConfig>)")
    class ConfigurationsConstructor {
      @Test
      void shouldProvideNoOperationInstanceForEmptyConfigurations() {
        XMLFilter filter = new ReservedClassToElementFilter(List.of());
        assertThatCode(() -> EMPTY_RICH_TEXT.transform(filter))
                .doesNotThrowAnyException();
      }

      @Test
      void shouldApplySingletonConfiguration() {
        XMLFilter filter = new ReservedClassToElementFilter(List.of(
                ReservedClassToElementConfig.of("span", "code")
        ));
        String filtered = applyFilterToInline("<span class=\"code\">Lorem</span>", filter);
        assertThat(filtered)
                .contains("<code>Lorem</code>");
      }

      @Test
      void shouldApplyMultipleConfigurations() {
        XMLFilter filter = new ReservedClassToElementFilter(List.of(
                ReservedClassToElementConfig.of("span", "code"),
                ReservedClassToElementConfig.of("span", "mark")
        ));
        String filtered = applyFilterToInline("<span class=\"code\">Lorem</span> <span class=\"mark\">ipsum</span>", filter);
        assertThat(filtered)
                .contains("<code>Lorem</code>")
                .contains("<mark>ipsum</mark>")
        ;
      }

      @Test
      void shouldOverridePreviousMapping() {
        XMLFilter filter = new ReservedClassToElementFilter(List.of(
                ReservedClassToElementConfig.of("span", "marker", "code"),
                ReservedClassToElementConfig.of("span", "marker", "mark")
        ));
        String filtered = applyFilterToInline("<span class=\"marker\">Lorem</span>", filter);
        assertThat(filtered)
                .as("Last configuration for mapping a class name should win.")
                .contains("<mark>Lorem</mark>");
        // And there should also have been some log output.
      }

      @Test
      void shouldAcceptDuplicateMappings() {
        XMLFilter filter = new ReservedClassToElementFilter(List.of(
                ReservedClassToElementConfig.of("span", "code"),
                ReservedClassToElementConfig.of("span", "code")
        ));
        String filtered = applyFilterToInline("<span class=\"code\">Lorem</span>", filter);
        assertThat(filtered)
                .contains("<code>Lorem</code>");
        // And there should also have been some log output.
      }
    }

    @Nested
    @DisplayName("Copy: ReservedClassToElementFilter(ReservedClassToElementFilter)")
    class CopyConstructor {
      @Test
      void shouldCopyNoOperationInstanceForEmptyConfigurations() {
        ReservedClassToElementFilter original = new ReservedClassToElementFilter(List.of());
        XMLFilter filter = new ReservedClassToElementFilter(original);
        assertThatCode(() -> EMPTY_RICH_TEXT.transform(filter))
                .doesNotThrowAnyException();
      }

      @Test
      void shouldApplyMultipleConfigurations() {
        ReservedClassToElementFilter original = new ReservedClassToElementFilter(List.of(
                ReservedClassToElementConfig.of("span", "code"),
                ReservedClassToElementConfig.of("span", "mark")
        ));
        XMLFilter filter = new ReservedClassToElementFilter(original);
        String filtered = applyFilterToInline("<span class=\"code\">Lorem</span> <span class=\"mark\">ipsum</span>", filter);
        assertThat(filtered)
                .contains("<code>Lorem</code>")
                .contains("<mark>ipsum</mark>")
        ;
      }
    }
  }

  @Nested
  class SpecialPurposeUseCases {
    @Test
    void shouldBeAbleToJustStripReservedClass() {
      XMLFilter filter = new ReservedClassToElementFilter(List.of(
              ReservedClassToElementConfig.of("p", "delete-this-class", "p")
      ));
      String filtered = applyFilter(wrapRichText("<p class=\"delete-this-class\">Lorem</p>"), filter);
      assertThat(filtered)
              .contains("<p>Lorem</p>");
    }
  }

  /**
   * These are tests dedicated to typical production setup.
   */
  @Nested
  class ProductionUseCase {
    private ReservedClassToElementFilter filter;

    @BeforeEach
    void setUp() {
      filter = new ReservedClassToElementFilter(List.of(
              // <span class="code"> -> <code>
              ReservedClassToElementConfig.of("span", "code"),
              // <span class="strike"> -> <s>
              ReservedClassToElementConfig.of("span", "strike", "s"),
              // <span class="underline"> -> <u>
              ReservedClassToElementConfig.of("span", "underline", "u"),
              // <td class="td--header"> -> <th>
              ReservedClassToElementConfig.of("td", "td--header", "th")
      ));
    }

    @ParameterizedTest(name = "[{index}] Should render as expected: {0}")
    @ArgumentsSource(ProductionUseCaseArgumentsProvider.class)
    void shouldRenderUseCaseDataSuccessfully(@NonNull ProductionUseCaseData data) {
      Markup markup = asRichText(data.richTextAsXml);
      Markup transformed = markup.transform(filter);
      assertSoftly(softly -> data.asFilteredXmlAssertions.accept(transformed.asXml(), softly));
    }
  }

  @Nested
  class Robustness {
    @ParameterizedTest(name = "[{index}] Probe for span nesting level: {0}")
    @ValueSource(ints = {0, 1, 10, 100, 1_000, 10_000})
    void shouldHandleDeeplyNestedSpans(int nestingLevel) {
      XMLFilter filter = new ReservedClassToElementFilter(List.of(
              ReservedClassToElementConfig.of("span", "mark")
      ));

      int count = nestingLevel;
      String markupAsString = "Lorem";
      while (count > 0) {
        markupAsString = format("<span class=\"mark\">%s</span>", markupAsString);
        count--;
      }

      String filtered = applyFilterToInline(markupAsString, filter);
      assertThat(filtered)
              .as("All (nested) spans should have been replaced.")
              .doesNotContain("<span");
    }
  }

  @NonNull
  private static String applyFilterToInline(@NonNull String inlineText, @NonNull XMLFilter filter) {
    return applyFilter(wrapInline(inlineText), filter);
  }

  @NonNull
  private static String applyFilter(@NonNull String markupAsString, @NonNull XMLFilter filter) {
    return asRichText(markupAsString).transform(filter).asXml();
  }

  @NonNull
  private static Markup asRichText(@NonNull String markupAsString) {
    return MarkupFactory.fromString(markupAsString)
            .withGrammar(RICH_TEXT_1_0_NAME);
  }

  @NonNull
  private static String wrapInline(@NonNull String inlineText) {
    if (inlineText.isBlank()) {
      return wrapRichText("<p/>");
    }
    return wrapRichText(format("<p>%s</p>", inlineText));
  }

  @NonNull
  private static String wrapRichText(@NonNull String richTextWithoutDiv) {
    return format("<div xmlns=\"%s\">%s</div>", RICH_TEXT_NAMESPACE, richTextWithoutDiv);
  }

  private static final class ProductionUseCaseArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
              ProductionUseCaseData.of("<p/>", "<p/>"),
              ProductionUseCaseData.of(
                      "<p><span class=\"code\">Lorem</span></p>",
                      "<code>Lorem</code>"
              ),
              ProductionUseCaseData.of(
                      "<p><span class=\"underline\">Lorem</span></p>",
                      "<u>Lorem</u>"
              ),
              ProductionUseCaseData.of(
                      "<p><span class=\"strike\">Lorem</span></p>",
                      "<s>Lorem</s>"
              ),
              ProductionUseCaseData.of(
                      "<table><tr><td>Lorem</td></tr></table>",
                      "<table><tr><td>Lorem</td></tr></table>"
              ),
              ProductionUseCaseData.of(
                      "<table><tr><td><p>Lorem</p></td></tr></table>",
                      "<table><tr><td><p>Lorem</p></td></tr></table>"
              ),
              ProductionUseCaseData.of(
                      "<table><tr><td class=\"td--header\">Lorem</td></tr></table>",
                      "<table><tr><th>Lorem</th></tr></table>"
              ),
              ProductionUseCaseData.of(
                      "<table><tr><td class=\"td--header\"><p>Lorem</p></td></tr></table>",
                      "<table><tr><th><p>Lorem</p></th></tr></table>"
              ),
              ProductionUseCaseData.of(
                      "<table><tr><td class=\"td--header\">Lorem</td><td>ipsum</td></tr><tr><td>dolor</td><td class=\"td--header\">sit</td></tr></table>",
                      "<th>Lorem</th>",
                      "<td>ipsum</td>",
                      "<td>dolor</td>",
                      "<th>sit</th>"
              )
      );
    }
  }

  private static final class ProductionUseCaseData {
    private final String richTextAsXml;
    private final BiConsumer<String, SoftAssertions> asFilteredXmlAssertions;

    private ProductionUseCaseData(@NonNull String richTextAsXml,
                                  @NonNull BiConsumer<String, SoftAssertions> asFilteredXmlAssertions) {
      this.richTextAsXml = richTextAsXml;
      this.asFilteredXmlAssertions = asFilteredXmlAssertions;
    }

    @NonNull
    private static Arguments of(@NonNull String richTextWithoutDiv,
                                @NonNull String... containedStrings) {
      return Arguments.of(new ProductionUseCaseData(richTextWithoutDiv, (xml, softly) -> {
        for (String containedString : containedStrings) {
          softly.assertThat(xml).contains(containedString);
        }
      }));
    }

    @Override
    public String toString() {
      return richTextAsXml;
    }
  }
}
