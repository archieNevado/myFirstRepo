import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";

/**
 * Declares an observable with properties and their default values for the fixed index feature.
 */
class FixedIndexViewModel extends BeanImpl {

  static readonly INDEX_PROPERTY_NAME: string = "index";

  constructor() {
    super();
    //empty constructor
  }

  index: number;
}

export default FixedIndexViewModel;
