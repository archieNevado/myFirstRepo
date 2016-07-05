package com.coremedia.blueprint.importer.validation;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.io.output.NullWriter;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JavaScriptCompressionValidator {
  private static final Logger LOG = LoggerFactory.getLogger(JavaScriptCompressionValidator.class);
  private static final NullWriter NULL_WRITER = new NullWriter();

  // static utility class
  private JavaScriptCompressionValidator() {
  }


  // --- features ---------------------------------------------------

  /**
   * Tests if the JavaScript can be compressed by the same compressor that is used by CoreMedia's delivery
   *
   * @param systemId resource name, only for logging
   * @param javaScript the JavaScript to be validated
   * @return the number of errors that occurred during the compression process
   */
  public static boolean isCompressibleJavaScript(String systemId, String javaScript, boolean verbose) {
    ErrorReporterWrapper errorHandler = new ErrorReporterWrapper(systemId, verbose);
    try {
      Reader reader = new StringReader(javaScript);
      JavaScriptCompressor compressor = new JavaScriptCompressor(reader, errorHandler);
      compressor.compress(NULL_WRITER, -1, false, true, true, true);
      if (errorHandler.numErrors() == 0) {
        return true;
      }
    } catch (IOException e) {
      LOG.error("Error validating JavaScript.", e);
    } catch (EvaluatorException e) {
      LOG.debug("EvaluatorException occurred.", e);
    }
    if (verbose) {
      LOG.warn("Compression test of file {} failed with {} errors. Enable debug logging to get more information.",
              systemId, errorHandler.numErrors());
    }
    return false;
  }


  // --- internal ---------------------------------------------------

  /**
   * An ErrorReporter which only logs on level info, because JS validation is only
   * informational in this context and has no effect on the actual operation.
   */
  private static class ErrorReporterWrapper implements ErrorReporter {
    private String systemId;
    private int errors = 0;
    private boolean verbose;

    public ErrorReporterWrapper(String systemId, boolean verbose) {
      this.systemId = systemId;
      errors = 0;
      this.verbose = verbose;
    }

    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
      // intentionally ignore
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
      ++errors;
      if (verbose) {
        LOG.debug("JS compression error: File {}, Line: {}, LineOffset: {}, LineSource: '{}': {}",
                systemId, line, lineOffset, lineSource, message);
      }
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
      error(message, sourceName, line, lineSource, lineOffset);
      return new EvaluatorException(message);
    }

    public int numErrors() {
      return errors;
    }
  }

}
