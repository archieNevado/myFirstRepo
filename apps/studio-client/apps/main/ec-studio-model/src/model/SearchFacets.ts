import CatalogObject from "./CatalogObject";

abstract class SearchFacets extends CatalogObject {

  abstract getFacets(): any;

}

export default SearchFacets;
