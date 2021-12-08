
class CatalogObjectPropertyNames {

  /**
   * @eventType name
   * @see CatalogObject#getName()
   */
  static readonly NAME: string = "name";

  /**
   * @eventType shortDescription
   * @see CatalogObject#getShortDescription()
   */
  static readonly SHORT_DESCRIPTION: string = "shortDescription";

  /**
   * @eventType externalId
   * @see CatalogObject#getExternalId()
   */
  static readonly EXTERNAL_ID: string = "externalId";

  /**
   * @eventType externalTechId
   * @see CatalogObject#getExternalTechId()
   */
  static readonly EXTERNAL_TECH_ID: string = "externalTechId";

  /**
   * @eventType store
   * @see CatalogObject#getStore()
   */
  static readonly STORE: string = "store";

  /**
   * @eventType catalog
   * @see Product#getCatalog() or Category#getCatalog()
   */
  static readonly CATALOG: string = "catalog";

  /**
   * @eventType default
   * @see Catalog#isDefault()
   */
  static readonly DEFAULT: string = "default";

  /**
   * @eventType defaultCatalog
   * @see Store#getDefaultCatalog()
   */
  static readonly DEFAULT_CATALOG: string = "defaultCatalog";

  /**
   * @eventType catalogs
   * @see Store#getCatalogs()
   */
  static readonly CATALOGS: string = "catalogs";

  /**
   * @eventType multiCatalog
   * @see Store#isMultiCatalog()
   */
  static readonly MULTI_CATALOG: string = "multiCatalog";

  /**
   * @eventType id
   * @see CatalogObject#getId()
   */
  static readonly ID: string = "id";

  /**
   * @eventType displayName
   * @see Category#getDisplayName()
   */
  static readonly DISPLAY_NAME: string = "displayName";

  /**
   * @eventType children
   * @see Category#getChildren()
   */
  static readonly CHILDREN: string = "children";

  /**
   * @eventType product
   * @see Category#getProducts()
   */
  static readonly PRODUCTS: string = "products";

  /**
   * @eventType category
   * @see Product#getCategory()
   */
  static readonly CATEGORY: string = "category";

  /**
   * @eventType thumbnailUrl
   * @see Product#getThumbnailUrl()
   */
  static readonly THUMBNAIL_URL: string = "thumbnailUrl";

  /**
   * @eventType visuals
   * @see Product#getVisuals
   */
  static readonly VISUALS: string = "visuals";

  /**
   * @eventType pictures
   * @see Product#getPictures
   */
  static readonly PICTURES: string = "pictures";

  /**
   * @eventType downloads
   * @see Product#getDownloads
   */
  static readonly DOWNLOADS: string = "downloads";

  /**
   * @eventType previewUrl
   * @see Product#getDefaultPreviewUrl() and Category#getPreviewUrl()
   */
  static readonly PREVIEW_URL: string = "previewUrl";

  /**
   * @eventType marketingSpots
   * @see Store#getMarketingSpots()
   */
  static readonly MARKETING_SPOTS: string = "marketingSpots";

  /**
   * @eventType segments
   * @see Store#getSegments()
   */
  static readonly SEGMENTS: string = "segments";

  /**
   * @eventType childrenData
   * @see Category#getChildrenData()
   */
  static readonly CHILDREN_DATA: string = "childrenData";

  /**
   * @eventType subCategories
   * @see Category#getSubCategories()
   */
  static readonly SUB_CATEGORIES: string = "subCategories";

  /**
   * @eventType parent
   * @see Category#getParent()
   * @see ProductVariant#getParent()
   */
  static readonly PARENT: string = "parent";

  /**
   * @eventType storeId
   * @see Store#getStoreId()
   */
  static readonly STORE_ID: string = "storeId";

  static readonly ROOT_CATEGORY: string = "rootCategory";

  static readonly MARKETING: string = "marketing";

  static readonly VENDOR_NAME: string = "vendorName";

  /**
   * @eventType longDescription
   * @see Product#getLongDescription()
   */
  static readonly LONG_DESCRIPTION: string = "longDescription";

  /**
   * @eventType content
   * @see CatalogObjectImpl#getContent
   */
  static readonly CONTENT: string = "content";

  /**
   * Name of the custom attributes property.
   *
   * @eventType customAttributes
   * @see CatalogObject#getCustomAttributes()
   */
  static readonly CUSTOM_ATTRIBUTES: string = "customAttributes";

  /**
   * Name of the facets attribute
   */
  static readonly SEARCH_FACETS: string = "searchFacets";

  /**
   * Name of the facets attribute
   */
  static readonly FACETS: string = "facets";

  /**
   * Multi Preview Support
   */
  static readonly PREVIEWS: string = "previews";

  /**
   * @private
   * This class only defines constants.
   */
  constructor() {
  }

}

export default CatalogObjectPropertyNames;
