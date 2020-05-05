package com.acme.coremedia.studio.request.predicate {
import ext.JSON;

/**
 * Predicates for <code>data</code> elements in <code>requestOptions</code>.
 */
public class DataPredicate {
  function DataPredicate() {
  }

  public static function matchesContentProperty(actual:String, propertyName:String, valuePredicate:Function):Boolean {
    var actualJson:Object = JSON.decode(actual, true);
    return actualJson &&
            actualJson['properties'] &&
            actualJson['properties'][propertyName] &&
            valuePredicate(actualJson['properties'][propertyName]);
  }
}
}
