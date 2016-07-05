package com.coremedia.blueprint.importer.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CssImportsValidator {
  private static final Logger LOG = LoggerFactory.getLogger(CssImportsValidator.class);


  private CssImportsValidator() {
    // static utility class
  }

  /**
   * Checks if the CSS file contains @import statements as they cannot be resolved by the CSS imported.
   *
   * @param systemId resource name, only for logging
   * @param css the css to be validated
   * @return <code>true</code> if there is at least one import statement otherwise <code>false</code>.
   */
  public static boolean hasImportStatements(String systemId, String css) {
    if (css.contains("@import")) {
      LOG.info("CSS validation warning: File {} uses @import", systemId);
      return true;
    }
    return false;
  }

}
