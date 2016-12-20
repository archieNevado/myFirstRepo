package com.coremedia.blueprint.studio.rest;

/**
 * Some attribute keys for communication from interceptor to UploadResource
 * via WriteRequest attributes.
 */
public interface UploadControlAttributes {
  /**
   * If a ContentWriteInterceptor sets this attribute to true, UploadResource
   * will not do anything.
   * <p>
   * Type: boolean
   */
  String DO_NOTHING = "com.coremedia.blueprint.studio.rest.upload.control.do-nothing";

  /**
   * If a ContentWriteInterceptor creates contents on its own, it can suggest
   * them as "the uploaded documents" for further processing.
   * <p>
   * Type: Collection&lt;Content&gt;
   */
  String UPLOADED_DOCUMENTS = "com.coremedia.blueprint.studio.rest.upload.control.uploaded-documents";
}
