package com.coremedia.blueprint.studio.rest.externalpreview;

import com.coremedia.rest.cap.exception.ParameterizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * The server side of the external preview with a registry of data from Studio instances.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("externalpreview")
public class ExternalPreviewResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPreviewResource.class);

  private static final String METHOD_UPDATE_PATH = "update";
  private static final String METHOD_CONFIG_PATH = "config";
  private static final String URL_ENCODING = "utf8";
  private static final String REQUEST_METHOD = "GET";

  private static final String PARAM_DATA = "data";
  private static final String PARAM_PREVIEW_URL = "previewUrl";
  private static final String PARAM_TOKEN = "token";
  private static final String PARAM_METHOD = "method";

  private static ExternalPreviewConfigRepresentation configRepresentation = new ExternalPreviewConfigRepresentation();

  @Required
  public void setRestUrl(String restUrl) {
    configRepresentation.setRestUrl(restUrl);
  }

  @Required
  public void setPreviewUrl(String url) {
    configRepresentation.setPreviewUrl(url);
  }

  @Required
  public void setUrlPrefix(String urlPrefix) {
    configRepresentation.setUrlPrefix(urlPrefix);
  }

  /**
   * Forwards the preview data information to the CAE, that that the polling that is executed in it returns
   * the correct content item to preview.
   *
   * @return True if the data was forward successfully.
   */
  @POST
  @Path(METHOD_UPDATE_PATH)
  public boolean updatePreviewData(@FormParam(PARAM_DATA) @DefaultValue("") String json,
                                   @FormParam(PARAM_TOKEN) @DefaultValue("") String token,
                                   @FormParam(PARAM_METHOD) @DefaultValue("") String method,
                                   @FormParam(PARAM_PREVIEW_URL) @DefaultValue("") String previewUrl) {
    String url = null;
    try {
      json = URLEncoder.encode(json, URL_ENCODING);
      url = previewUrl + "?token=" + token + "&method=" + method;
      if (json != null && !json.isEmpty()) {
        url += "&data=" + json;
      }
      return sendRequest(url);
    } catch (Exception e) {//NOSONAR
      LOGGER.error("Error applying preview data to " + url + ": " + e.getMessage(), e); //NOSONAR
    }
    return false;
  }

  /**
   * Returns the preview urls and host names for displaying the content to preview.
   *
   * @return configuration
   */
  @GET
  @Path(METHOD_CONFIG_PATH)
  public ExternalPreviewConfigRepresentation getConfigRepresentation() {
    return configRepresentation;
  }


  /**
   * Executes a GET request that is send to the preview CAE.
   *
   * @param urlString The url string including parameters.
   * @return True if the request was successful.
   * @throws java.io.IOException Thrown if the preview CAE is not available.
   */
  private boolean sendRequest(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
    httpCon.setRequestMethod(REQUEST_METHOD);
    httpCon.connect();
    if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
      LOGGER.info("Written preview token " + urlString);//NOSONAR
      return true;
    }

    LOGGER.error(httpCon.getResponseCode() + " Error in response: " + httpCon.getResponseMessage());//NOSONAR
    throw new ParameterizedException(Response.Status.SERVICE_UNAVAILABLE, "Failed to update preview CAE", "CAE Update Error", "Could not update CAE external preview status using URL '" + urlString + "'");
  }
}
