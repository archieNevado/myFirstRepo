package com.acme.coremedia.studio.request.predicate {
/**
 * Predicates for <code>method</code> elements in <code>requestOptions</code>.
 */
public class MethodPredicate {
  function MethodPredicate() {
  }

  /**
   * Matches PUT requests.
   * @param actual actual method
   * @return true, if the method is PUT
   */
  public static function putRequest(actual:String):Boolean {
    return actual && actual.toUpperCase() === 'PUT';
  }

  /**
   * Matches POST requests.
   * @param actual actual method
   * @return true, if the method is POST
   */
  public static function postRequest(actual:String):Boolean {
    return actual && actual.toUpperCase() === 'POST';
  }

  /**
   * Matches GET requests.
   * @param actual actual method
   * @return true, if the method is GET
   */
  public static function getRequest(actual:String):Boolean {
    return actual && actual.toUpperCase() === 'GET';
  }
}
}
