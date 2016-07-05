package com.coremedia.blueprint.common.importfilter;

import com.coremedia.blueprint.importer.validation.CssImportsValidator;
import com.coremedia.blueprint.importer.validation.JavaScriptCompressionValidator;
import com.coremedia.publisher.importer.AbstractTransformer;
import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.MultiSource;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

public class ValidationTransformer extends AbstractTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(ValidationTransformer.class);

  // --- Transformer ------------------------------------------------

  @Override
  public void transform(Source source, Result result) throws TransformerException {
    if (!(source instanceof MultiSource)) {
      throw new IllegalArgumentException("Source " + source + " is not a MultiSource");
    }
    if (!(result instanceof MultiResult)) {
      throw new IllegalArgumentException("Result " + result + " is not a MultiResult");
    }
    try {
      MultiSource multiSource = ((MultiSource) source).flattened();
      MultiResult multiResult = (MultiResult) result;

      for (int i = 0; i < multiSource.size(); ++i) {
        StreamSource streamSource = (StreamSource) multiSource.getSource(i, StreamSource.FEATURE);
        // Not sure how the validator's reported errors and thrown exceptions are related.
        // Handle both as errors here, omit the file and proceed with the import.
        validate(streamSource);
        multiResult.addNewResult(streamSource.getSystemId());
      }

    } catch (Exception e) {
      // An exception has been thrown by the importer. Fail.
      throw new TransformerException("Cannot validate MultiSource {}" + source.getSystemId(), e);
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * Do the actual validation
   * <p/>
   *
   */
  private void validate(StreamSource source) {
    try (InputStream is = source.getInputStream()) {
      String systemId = source.getSystemId();
      String type = SystemIdUtil.type(systemId);
      if ("js".equals(type)) {
        String js = IOUtils.toString(is, Charsets.UTF_8);
        JavaScriptCompressionValidator.isCompressibleJavaScript(systemId, js, true);
      } else if ("css".equals(type)) {
        String css = IOUtils.toString(is, Charsets.UTF_8);
        CssImportsValidator.hasImportStatements(systemId, css);
      }
    } catch (Exception e) {
      // An exception has been thrown by a validator.
      LOG.error("Validation errors in {}, omitted. See validation log files for details.", source.getSystemId());
      LOG.debug("Validation exception occurred.", e);
    }
  }
}
