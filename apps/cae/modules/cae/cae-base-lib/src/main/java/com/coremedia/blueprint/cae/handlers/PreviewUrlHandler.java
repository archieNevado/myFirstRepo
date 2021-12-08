package com.coremedia.blueprint.cae.handlers;

import com.coremedia.objectserver.web.links.LinkFormatter;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.cae.handlers.PreviewHandler.REQUEST_ATTR_IS_STUDIO_PREVIEW;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_INTERNAL;

/**
 * A handler used for previewurl purposes: Takes a "id" request parameter and returns the url to the resource that is denoted
 * by this id.
 */
@CrossOrigin
@RequestMapping
@DefaultAnnotation(NonNull.class)
public class PreviewUrlHandler extends AbstractUrlHandler {

  private static final String PREFIX_PREVIEW = "preview";
  private static final String PREFIX_HANDLER = "previewurl";

  /**
   * Uri pattern for preview URLs.
   * e.g. /internal/preview/previewurl?id=123&view=fragmentPreview
   */
  public static final String URI_PATTERN =
          '/' + PREFIX_INTERNAL +
                  '/' + PREFIX_PREVIEW +
                  '/' + PREFIX_HANDLER;

  public PreviewUrlHandler(LinkFormatter linkFormatter) {
    super(linkFormatter);
  }

  @GetMapping(value = URI_PATTERN, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> handle(@RequestParam("id") String id,
                                       @RequestParam(value = "view", required = false) String view,
                                       @RequestParam(value = "site", required = false) String siteId,
                                       @RequestParam(value = "context", required = false) Object context,
                                       @NonNull HttpServletRequest request,
                                       @NonNull HttpServletResponse response) {
    request.setAttribute(REQUEST_ATTR_IS_STUDIO_PREVIEW, true);
    return super.getLink(id, view, siteId, context, request, response);
  }

}
