import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Segments from "./Segments";

class SegmentsImpl extends CatalogObjectImpl implements Segments {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/segments/{siteId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getSegments(): Array<any> {
    return this.get(CatalogObjectPropertyNames.SEGMENTS);
  }
}
mixin(SegmentsImpl, Segments);

export default SegmentsImpl;
