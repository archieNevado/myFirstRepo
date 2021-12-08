import PreviewsImpl from "@coremedia/studio-client.client-core-impl/data/impl/PreviewsImpl";

class CommerceBeanPreviewsImpl extends PreviewsImpl {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/previews/{resourceType:[^/]+}/{siteId:[^/]+}/{catalogAlias:[^/]+}/{externalId:.+}";

  /**
   * Do not invoke directly. Used by the bean factory to create content issues objects.
   *
   * @param uri the bean's URI
   */
  constructor(uri: string) {
    super(uri);
  }
}

export default CommerceBeanPreviewsImpl;
