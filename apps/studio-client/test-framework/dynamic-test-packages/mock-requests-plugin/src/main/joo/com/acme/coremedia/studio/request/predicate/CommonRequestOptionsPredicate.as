package com.acme.coremedia.studio.request.predicate {
/**
 * Contains common predicates for requestOptions elements.
 */
public class CommonRequestOptionsPredicate {
  /**
   * Singleton instance of {@link com.acme.coremedia.studio.request.predicate.CommonRequestOptionsPredicate.alwaysTrue}.
   */
  public static const ALWAYS_TRUE:Function = alwaysTrue;

  function CommonRequestOptionsPredicate() {
  }

  /**
   * Just answers 'true' for any value.
   * @param actual actual value, ignored
   * @return true
   */
  public static function alwaysTrue(actual:*):Boolean {
    return true;
  }

  /**
   * Values must be equal.
   * @param actual actual element value from <code>requestOptions</code>.
   * @param expected expected element value
   * @return true if the values match
   */
  public static function equals(actual:*, expected:*):Boolean {
    return actual === expected;
  }

}
}
