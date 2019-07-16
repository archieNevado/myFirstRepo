package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import com.coremedia.blueprint.common.preview.PreviewDateFormatter;
import com.coremedia.blueprint.common.util.ContextAttributes;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

public class SiteDateFilter implements Filter {

  static final String REQUEST_PARAMETER_SITE_DATE = "__siteDate";
  private static final String REQUEST_ATTRIBUTE_SITE_DATE = "siteDateObj";

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
          throws IOException, ServletException {
    servletRequest.setAttribute(REQUEST_ATTRIBUTE_SITE_DATE, getPreviewDateFromRequestParameter());
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
  }

  @NonNull
  private static Calendar getPreviewDateFromRequestParameter() {
    String previewDateText = ContextAttributes.findRequestParameter(REQUEST_PARAMETER_SITE_DATE).orElse(null);

    return Optional.ofNullable(previewDateText)
            .flatMap(PreviewDateFormatter::parse)
            .<Calendar>map(GregorianCalendar::from)
            .orElseGet(Calendar::getInstance);
  }
}
