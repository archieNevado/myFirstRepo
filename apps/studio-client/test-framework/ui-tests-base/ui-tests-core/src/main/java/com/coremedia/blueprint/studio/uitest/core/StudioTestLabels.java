package com.coremedia.blueprint.studio.uitest.core;

import java.util.regex.Pattern;

/**
 * Labels known by release-tests.
 */
public final class StudioTestLabels {
  /**
   * If using this prefix the test or test suite will be marked as <em>Known Issue</em>, i. e. it is known that
   * the test fails because of a product issue. You typically append a unique identifier to this prefix to point
   * to the bug report where you can learn more about it.
   */
  public static final String KNOWN_ISSUE_PREFIX = "known-issue-";
  /**
   * Pattern used to filter out tests with known issues.
   */
  private static final String KNOWN_ISSUE_PATTERN_STRING = "known-issue.*";
  public static final Pattern KNOWN_ISSUE_PATTERN = Pattern.compile(KNOWN_ISSUE_PATTERN_STRING);

  @SuppressWarnings("squid:UnusedPrivateMethod")
  private StudioTestLabels() {
    // This is a vicious cycle: If you use an interface for the constants Sonar complains about the interface
    // which should be a final class or enum. If you use either of this to hold the constants (mind that they
    // need to be string constants) Sonar complains about the private constructor which is never used.
    // Solution taken for now: The final class approach ignoring Sonar...
    //
    // see https://confluence.coremedia.com/display/PCINF/Java-CoP+-+Minutes+2016-06-23 for the discussion
    // invoked in the Java-CoP
  }
}
