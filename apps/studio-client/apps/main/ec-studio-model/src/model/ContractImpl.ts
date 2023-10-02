import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import Contract from "./Contract";

class ContractImpl extends CatalogObjectImpl implements Contract {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/contract/{siteId:[^/]+}/{workspaceId:[^/]+}/{externalId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }
}
mixin(ContractImpl, Contract);

export default ContractImpl;
