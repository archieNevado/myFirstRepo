import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";
import int from "@jangaroo/runtime/int";

class SearchResult extends BeanImpl {

  getTotal(): int {
    return this.get(SearchResult.TOTAL);
  }

  setTotal(total: int) {
    this.set(SearchResult.TOTAL, total);
  }

  getHits(): Array<any> {
    return this.get(SearchResult.HITS);
  }

  setHits(hits: Array<any>) {
    this.set(SearchResult.HITS, hits);
  }

  reset(): void {
    this.set(SearchResult.TOTAL, 0);
    this.set(SearchResult.HITS, []);
  }

  static readonly HITS: string = "hits";

  static readonly TOTAL: string = "total";
}

export default SearchResult;
