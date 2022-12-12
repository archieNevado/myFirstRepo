package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanIdConverter;
import com.coremedia.objectserver.configuration.CaeConfigurationProperties;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.UriComponentsBuilder;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Base implementation for resources that are represented by request handler and link schemes
 */
public abstract class HandlerBase extends WebContentGenerator {

  private static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings();
  private static final Joiner PATH_JOINER = Joiner.on('/');

  public static final String FRAGMENT_PREVIEW = "fragmentPreview";

  protected static final Logger LOG = LoggerFactory.getLogger(HandlerBase.class);
  protected ContentLinkBuilder contentLinkBuilder;
  protected UrlPathFormattingHelper urlPathFormattingHelper;

  private MimeTypeService mimeTypeService;
  private DataViewFactory dataViewFactory;
  private ContentBeanIdConverter contentBeanIdConverter = new ContentBeanIdConverter();
  private List<String> permittedLinkParameterNames = Collections.emptyList();
  private CaeConfigurationProperties caeConfigurationProperties;

  @Autowired
  public void setDeliveryConfigurationProperties(CaeConfigurationProperties deliveryConfigurationProperties) {
    this.caeConfigurationProperties = deliveryConfigurationProperties;
  }

  // --- Spring Config -------------------------------------------------------------------------------------------------

  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  /**
   * @param permittedLinkParameterNames Names of all
   *                                    {@link com.coremedia.objectserver.view.ViewUtils#getParameters(javax.servlet.ServletRequest) link parameters} to shall be copied
   *                                    to the link
   */
  public void setPermittedLinkParameterNames(List<String> permittedLinkParameterNames) {
    this.permittedLinkParameterNames = permittedLinkParameterNames;
  }

  /**
   * Exposes the list, in order to simply modify via customizer.
   */
  public List<String> getPermittedLinkParameterNames() {
    return permittedLinkParameterNames;
  }

  public void setContentBeanIdConverter(ContentBeanIdConverter contentBeanIdConverter) {
    this.contentBeanIdConverter = contentBeanIdConverter;
  }

  public void setContentLinkBuilder(ContentLinkBuilder contentLinkBuilder) {
    this.contentLinkBuilder = contentLinkBuilder;
  }

  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

  protected boolean isSingleNode() {
    return caeConfigurationProperties.isSingleNode();
  }

  // ===================================================================================================================

  /**
   * Either build model with view for the given bean or redirect to the very same bean when the request targets an
   * outdated version of the requested bean. Only redirect when running as single CAE instance because the requested
   * URL may be generated by another CAE instance whose RLS may be out of sync with the current CAE's RLS.
   */
  protected ModelAndView doCreateModelWithView(boolean isUpToDate,
                                               @NonNull Object bean,
                                               @Nullable String viewName,
                                               @Nullable HttpStatus httpStatus,
                                               @NonNull HttpServletResponse response) {
    if (!isUpToDate) {
      // the handler detected that the link wasn't generated for this CAE's latest version of the given bean
      if (isSingleNode()) {
        // redirect to latest version
        return HandlerHelper.redirectTo(bean, viewName, httpStatus);
      } else {
        // serve current bean but prevent caching
        applyCacheSeconds(response, 0);
      }
    }

    return HandlerHelper.createModelWithView(bean, viewName);
  }

  /**
   * Provides a file extension (e.g. "jpg" or "html") for a given content type
   *
   * @param contentType The content type, e.g. "text/html"
   * @param fallback    The fallback (if no extension could be determined)
   * @return The extension
   */
  protected String getExtension(String contentType, String fallback) {
    String extension = mimeTypeService.getExtensionForMimeType(contentType);
    return StringUtils.isNotBlank(extension) ? extension : fallback;
  }

  /**
   * Provides a ContentBean's numeric ID
   *
   * @return The id as a string
   */
  protected String getId(ContentBean bean) {
    return contentBeanIdConverter.convert(bean);
  }

  protected String getId(Content content) {
    return String.valueOf(IdHelper.parseContentId(content.getId()));
  }

  protected String getExtension(MimeType contentType, String fallback) {
    return getExtension(contentType.toString(), fallback);
  }

  protected MimeTypeService getMimeTypeService() {
    return mimeTypeService;
  }

  protected DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  protected Logger getLogger() {
    return LOG;
  }

  /**
   * Adds all permitted
   * {@link com.coremedia.objectserver.view.ViewUtils#getParameters(javax.servlet.ServletRequest) cm parameters} to
   * the URI under construction.
   *
   * @param parameters The link parameters that have been obtained via {@link com.coremedia.objectserver.view.ViewUtils#getParameters}
   */
  protected UriComponentsBuilder addLinkParametersAsQueryParameters(UriComponentsBuilder source, Map<String, Object> parameters) {

    if (permittedLinkParameterNames.isEmpty()) {
      return source;
    }

    for (Map.Entry<String, Object> parameter : parameters.entrySet()) {

      String name = parameter.getKey();
      if (permittedLinkParameterNames.contains(name)) {

        String value = parameter.getValue() == null ? "" : parameter.getValue().toString();
        source.queryParam(name, value);
      }
    }

    return source;

  }

  protected String removeSpecialCharacters(String segment) {
    return urlPathFormattingHelper==null ? segment : urlPathFormattingHelper.tidyUrlPath(segment);
  }

  public List<String> splitPathInfo(String path) {
    return newArrayList(PATH_SPLITTER.split(path));
  }

  public String joinPath(List<String> nodes) {
    return PATH_JOINER.join(nodes);
  }

  @Override
  public String toString() {
    return "HandlerBase{" +
            "contentLinkBuilder=" + contentLinkBuilder +
            ", urlPathFormattingHelper=" + urlPathFormattingHelper +
            ", mimeTypeService=" + mimeTypeService +
            ", dataViewFactory=" + dataViewFactory +
            ", contentBeanIdConverter=" + contentBeanIdConverter +
            ", permittedLinkParameterNames=" + permittedLinkParameterNames +
            '}';
  }
}
