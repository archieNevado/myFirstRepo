package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.Version;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A handler used for previewurl purposes: Takes a "id" request parameter and returns the url to the resource that is denoted
 * by this id.
 */
@CrossOrigin
@RequestMapping
public class PreviewUrlHandler {

  /**
   * Uri pattern for preview URLs.
   * e.g. /previewurl?id=123&view=fragmentPreview
   */
  public static final String URI_PATTERN = "/previewurl";

  private static final Logger LOG = LoggerFactory.getLogger(PreviewUrlHandler.class);
  private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
  private static final String REQUEST_ATTR_IS_STUDIO_PREVIEW = "isStudioPreview";

  private LinkFormatter linkFormatter;
  private IdProvider idProvider;
  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;

// --- Handler ----------------------------------------------------

  @GetMapping(value = URI_PATTERN, produces = "application/json;charset=UTF-8")
  @ResponseBody
  public String handleId(@RequestParam(value = "id", required = true) String id,
                               @RequestParam(value = "view", required = false) String view,
                               @RequestParam(value = "site", required = false) String siteId,
                               @RequestParam(value = "taxonomyId", required = false) String taxonomyId,
                               @NonNull HttpServletRequest request,
                               @NonNull HttpServletResponse response) {
    request.setAttribute(REQUEST_ATTR_IS_STUDIO_PREVIEW, true);
    storeSite(request, siteId);
    storeTaxonomy(request, taxonomyId);

    Object rootModel = getBean(id, view);
    if (rootModel == null) {
      return "";
    }

    // check if link to root model can be build - let common spring MVC exception handling kick in if link building fails
    // note that this is necessary because exceptions during rendering of RedirectView cannot be handled anymore
    String previewUrl = linkFormatter.formatLink(rootModel, view, request, response, true);
    LOG.trace("Preview Url: {}", previewUrl);
    return previewUrl;
  }

  public Object getBean(String id, String view) { //NOSONAR - ignore method complexity

    if (id == null) {
      return null;
    }

    String normalizedId = id;
    if (NUMBER_PATTERN.matcher(normalizedId).matches()) {
      // its a number: convert to a content id
      normalizedId = IdHelper.formatContentId(normalizedId);
    }

    Object bean;
    try {
      bean = idProvider.parseId(normalizedId);
    } catch (IllegalArgumentException e) { //NOSONAR - do not throw exception here.
      // object not found -> not found
      return null;
    }
    if (bean instanceof IdProvider.UnknownId) {
      // id unknown -> not found
      return null;
    }

    if (bean instanceof Version) {
      // convert a version into a content and the content into a ContentBean
      bean = contentBeanFactory.createBeanFor(((Version) bean).getContainingContent(), ContentBean.class);
    } else if (bean instanceof Content) {
      bean = contentBeanFactory.createBeanFor((Content) bean, ContentBean.class);
    }
    // all other beans:  keep them as a model


    // optionally build a dataview
    if (dataViewFactory != null) {
      bean = dataViewFactory.load(bean, null, true);
    }

    // redirect to the bean
    return bean;
  }

  @Required
  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }

  @Required
  public void setIdProvider(IdProvider idProvider) {
    this.idProvider = idProvider;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }
  /**
   * Stores the site parameter into the request.
   * The site parameter is used to resolve the context of a content if it does not belong to a specific site.
   * Therefore the studio default site will be passed as parameter to resolve the context.
   *
   * @param siteId The id of the site
   */
  protected static void storeSite(@NonNull HttpServletRequest request, @Nullable String siteId) {
    String attributeValue = emptyToNull(siteId);
    setSessionAttribute(request, RequestAttributeConstants.ATTR_NAME_PAGE_SITE, attributeValue);
  }

  /**
   * Stores the taxonomy content into the request.
   * The taxonomy parameter is used for the custom topic pages.
   *
   * @param taxonomyId The numeric content id of the taxonomy node.
   */
  protected static void storeTaxonomy(@NonNull HttpServletRequest request, @Nullable String taxonomyId) {
    if (!isNullOrEmpty(taxonomyId)) {
      String id = IdHelper.formatContentId(taxonomyId);
      setSessionAttribute(request, RequestAttributeConstants.ATTR_NAME_PAGE_MODEL, id);
    }
  }

  private static void setSessionAttribute(@NonNull HttpServletRequest request, @NonNull String name,
                                          @Nullable String value) {
    HttpSession session = request.getSession(true);
    session.setAttribute(name, value);
  }
}
