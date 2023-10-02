import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import Catalog from "./Catalog";
import CatalogObject from "./CatalogObject";
import Category from "./Category";
import Contracts from "./Contracts";
import Marketing from "./Marketing";
import Segments from "./Segments";
import Workspace from "./Workspace";
import Workspaces from "./Workspaces";

abstract class Store extends CatalogObject {

  abstract getTopLevel(): Array<any>;

  abstract getMarketing(): Marketing;

  abstract isMarketingEnabled(): boolean;

  abstract getRootCategory(): Category;

  abstract getSegments(): Segments;

  abstract getContracts(): Contracts;

  abstract getWorkspaces(): Workspaces;

  abstract getCatalogs(): Array<any>;

  abstract isMultiCatalog(): boolean;

  abstract getDefaultCatalog(): Catalog;

  abstract getCurrentWorkspace(): Workspace;

  /**
   * Return a mapping of the name of top level categories to the categories themselves
   *
   * @see CatalogObjectPropertyNames#CHILDREN_DATA
   */
  abstract getChildrenData(): Array<any>;

  abstract getStoreId(): string;

  abstract getVendorName(): string;

  abstract getVendorUrl(): string;

  abstract getVendorVersion(): string;

  abstract getTimeZoneId(): string;

  abstract resolveShopUrlForPbe(url: string): RemoteBean;

}

export default Store;
