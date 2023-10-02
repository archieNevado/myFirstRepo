import BeanFactoryImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanFactoryImpl";
import CatalogImpl from "./model/CatalogImpl";
import CategoryImpl from "./model/CategoryImpl";
import CommerceBeanPreviewsImpl from "./model/CommerceBeanPreviewsImpl";
import ContractImpl from "./model/ContractImpl";
import ContractsImpl from "./model/ContractsImpl";
import FacetsImpl from "./model/FacetsImpl";
import MarketingImpl from "./model/MarketingImpl";
import MarketingSpotImpl from "./model/MarketingSpotImpl";
import ProductImpl from "./model/ProductImpl";
import ProductVariantImpl from "./model/ProductVariantImpl";
import SearchFacetsImpl from "./model/SearchFacetsImpl";
import SegmentImpl from "./model/SegmentImpl";
import SegmentsImpl from "./model/SegmentsImpl";
import StoreImpl from "./model/StoreImpl";
import WorkspaceImpl from "./model/WorkspaceImpl";
import WorkspacesImpl from "./model/WorkspacesImpl";

BeanFactoryImpl.initBeanFactory().registerRemoteBeanClasses(
  CategoryImpl,
  StoreImpl,
  CatalogImpl,
  ProductImpl,
  ProductVariantImpl,
  SegmentImpl,
  SegmentsImpl,
  ContractImpl,
  ContractsImpl,
  WorkspaceImpl,
  WorkspacesImpl,
  MarketingSpotImpl,
  MarketingImpl,
  FacetsImpl,
  SearchFacetsImpl,
  CommerceBeanPreviewsImpl,
);
