import BeanFactoryImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanFactoryImpl";
import CatalogImpl from "./model/CatalogImpl";
import CategoryImpl from "./model/CategoryImpl";
import CommerceBeanPreviewsImpl from "./model/CommerceBeanPreviewsImpl";
import FacetsImpl from "./model/FacetsImpl";
import MarketingImpl from "./model/MarketingImpl";
import MarketingSpotImpl from "./model/MarketingSpotImpl";
import ProductImpl from "./model/ProductImpl";
import ProductVariantImpl from "./model/ProductVariantImpl";
import SearchFacetsImpl from "./model/SearchFacetsImpl";
import SegmentImpl from "./model/SegmentImpl";
import SegmentsImpl from "./model/SegmentsImpl";
import StoreImpl from "./model/StoreImpl";

BeanFactoryImpl.initBeanFactory().registerRemoteBeanClasses(
  CategoryImpl,
  StoreImpl,
  CatalogImpl,
  ProductImpl,
  ProductVariantImpl,
  SegmentImpl,
  SegmentsImpl,
  MarketingSpotImpl,
  MarketingImpl,
  FacetsImpl,
  SearchFacetsImpl,
  CommerceBeanPreviewsImpl,
);
