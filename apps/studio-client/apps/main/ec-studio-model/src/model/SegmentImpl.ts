import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import Segment from "./Segment";

class SegmentImpl extends CatalogObjectImpl implements Segment {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/segment/{siteId:[^/]+}/{externalId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }
}
mixin(SegmentImpl, Segment);

export default SegmentImpl;
