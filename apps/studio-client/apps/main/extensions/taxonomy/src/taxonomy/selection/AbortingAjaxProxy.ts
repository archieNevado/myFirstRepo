import Operation from "@jangaroo/ext-ts/data/operation/Operation";
import AjaxProxy from "@jangaroo/ext-ts/data/proxy/Ajax";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";

interface AbortingAjaxProxyConfig extends Config<AjaxProxy> {
}

/**
 * An AjaxProxy which aborts the last request before a new request is submitted.
 * This is useful in search fields when a search request was submitted for a
 * text entered by a user and the user enters an additional character which
 * starts a new search request.
 */
class AbortingAjaxProxy extends AjaxProxy {
  declare Config: AbortingAjaxProxyConfig;

  constructor(config: Config<AbortingAjaxProxy> = null) {
    super(config);
  }

  protected override doRequest(operation: Operation, callback: AnyFunction, scope: any): void {
    this.abort();
    super.doRequest(operation, callback, scope);
  }
}

export default AbortingAjaxProxy;
