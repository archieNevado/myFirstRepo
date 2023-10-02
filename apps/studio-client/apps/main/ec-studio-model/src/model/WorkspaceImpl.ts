import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import Workspace from "./Workspace";

class WorkspaceImpl extends CatalogObjectImpl implements Workspace {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/workspace/{siteId:[^/]+}/{externalId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }
}
mixin(WorkspaceImpl, Workspace);

export default WorkspaceImpl;
