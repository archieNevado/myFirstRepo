package com.coremedia.blueprint.studio.taxonomy.selection {
import ext.data.operation.Operation;
import ext.data.proxy.AjaxProxy;

/**
 * An AjaxProxy which aborts the last request before a new request is submitted.
 * This is useful in search fields when a search request was submitted for a
 * text entered by a user and the user enters an additional character which
 * starts a new search request.
 */
public class AbortingAjaxProxy extends AjaxProxy {

  public function AbortingAjaxProxy(config:AbortingAjaxProxy = null) {
    super(config);
  }

  override protected function doRequest(operation:Operation, callback:Function, scope:Object):void {
    abort();
    super.doRequest(operation, callback, scope);
  }
}
}
