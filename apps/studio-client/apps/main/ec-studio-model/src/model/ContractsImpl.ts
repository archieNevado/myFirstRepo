import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Contracts from "./Contracts";

class ContractsImpl extends CatalogObjectImpl implements Contracts {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/contracts/{siteId:[^/]+}/{workspaceId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getContracts(): Array<any> {
    return this.get(CatalogObjectPropertyNames.CONTRACTS);
  }
}
mixin(ContractsImpl, Contracts);

export default ContractsImpl;
