package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.view.processing.Minifier;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

/**
 * {@link com.coremedia.cache.CacheKey} to compute processed code segments
 * Although some more Objects are required for processing, the only real dependency
 * is to the CoreMedia Richtext Markup containing the code.
 */
class CodeCacheKey extends CacheKey<String> {

  private static final Logger LOG = LoggerFactory.getLogger(CodeCacheKey.class);

  private String name;
  private String code;
  private Minifier minifier;

  /**
   * Standard Constructor.
   * @param code the code (coremedia richtext)
   * @param name the name of the script's container document
   * @param minifier the postprocessor. can be null if and only if postProcessing is disabled.
   */
  CodeCacheKey(String code, String name, Minifier minifier) {
    this.code = code;
    this.name = name;
    this.minifier = minifier;
  }

  @Override
  public String evaluate(Cache cache) throws Exception {
    StringWriter resultStringWriter = new StringWriter();
    String evaluation;
    try {
      minifier.minify(resultStringWriter, new StringReader(code), name);
      evaluation = resultStringWriter.getBuffer().toString();
    } catch (Exception e) {
      LOG.info("Could not minify file {}. Will write unminified version. Cause: {}", name, e.getMessage());
      evaluation = code;
    }
    return evaluation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodeCacheKey that = (CodeCacheKey) o;
    return Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return 31 * code.hashCode();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[name=" + name + "]";
  }
}
