package com.coremedia.livecontext.view;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.objectserver.view.ViewUtils;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;

@DefaultAnnotation(NonNull.class)
public class PrefetchFragmentsViewUtils {
  private static final String DEFAULT_CHANNEL_VIEW = "asFragment";
  private static final String MATRIX_SEPERATOR = ";";

  /**
   * A channel in combination with the default view should always be rendered as full pagegrid (without header and footer).
   * This can be done by using the view "asFragment".
   * see also FragmentHandler#normalizedPageFragmentView()
   *
   * @param bean the render model
   * @return a magic "normalized" default view "asFragment" or empty string;
   */
  static String determineView(Object bean) {
    if (bean instanceof CMChannel) {
      return DEFAULT_CHANNEL_VIEW;
    }
    if (bean instanceof Page) {
      Page page = (Page) bean;
      if (page.getContent() instanceof CMChannel) {
        return DEFAULT_CHANNEL_VIEW;
      }
    }
    return "";
  }

  static String format(Instant instant) {
    OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
    return DateTimeFormatter.RFC_1123_DATE_TIME.format(offsetDateTime);
  }

  static String createFragmentKey(Object bean, String view, ServletRequest request) {
    String placement = getPlacementName(bean).orElse("");
    return createPageKey(request) + MATRIX_SEPERATOR +
            "view=" + view + MATRIX_SEPERATOR +
            "placement=" + nullToEmpty(placement).trim();
  }

  /**
   * Return the page grid placement name if the given bean is an instance of {@link PageGridPlacement}
   */
  static Optional<String> getPlacementName(Object bean) {
    return Optional.of(bean)
            .filter(PageGridPlacement.class::isInstance)
            .map(PageGridPlacement.class::cast)
            .map(PageGridPlacement::getName)
            .map(String::trim);
  }

  static String createPageKey(ServletRequest request) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return createPageKeyFromParameters(parameters);
  }

  @VisibleForTesting
  static String createPageKeyFromParameters(FragmentParameters parameters) {
    Map<String, String> parameterMap = new LinkedHashMap<>();
    parameterMap.put("externalRef", nullToEmpty(parameters.getExternalRef()));
    parameterMap.put("categoryId", nullToEmpty(parameters.getCategoryId()));
    parameterMap.put("productId", nullToEmpty(parameters.getProductId()));
    parameterMap.put("pageId", nullToEmpty(parameters.getPageId()));

    return parameterMap.entrySet().stream()
            .map(entry -> String.join("=", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(MATRIX_SEPERATOR));
  }

  /**
   * renders the view of the given bean and returns the payload.
   *
   * @param bean the given bean
   * @param view the given view
   */
  static String getPayload(Object bean, String view, HttpServletRequest request, HttpServletResponse response) {

    String effectiveView = view.isBlank() ? determineView(bean) : view;

    Writer out = new StringWriter();
    ViewUtils.render(bean, effectiveView, out, request, response);

    return out.toString().trim();
  }
}
