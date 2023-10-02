import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Workspaces from "./Workspaces";

class WorkspacesImpl extends CatalogObjectImpl implements Workspaces {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/workspaces/{siteId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getWorkspaces(): Array<any> {
    return this.get(CatalogObjectPropertyNames.WORKSPACES);
  }
}
mixin(WorkspacesImpl, Workspaces);

export default WorkspacesImpl;
