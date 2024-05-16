package com.coremedia.blueprint.cae.exception;

/**
 * A BlobTransformationException indicates that a particular blob
 * transformation failed.
 * <p>
 * You can configure the HTTP response code for these exceptions in
 * framework/spring/errorhandling.xml .
 */
public class BlobTransformationException extends RuntimeException {
  public BlobTransformationException(String message) {
    super(message);
  }

  public BlobTransformationException(String message, Throwable cause) {
    super(message, cause);
  }
}
