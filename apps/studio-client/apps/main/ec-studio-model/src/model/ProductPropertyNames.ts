
class ProductPropertyNames {

  /**
   * @eventType shortDescription
   * @see Product#getShortDescription()
   */
  static readonly SHORT_DESC: string = "shortDescription";

  /**
   * @eventType offerPrice
   * @see Product#getOfferPrice()
   */
  static readonly OFFER_PRICE: string = "offerPrice";

  /**
   * @eventType listPrice
   * @see Product#getListPrice()
   */
  static readonly LIST_PRICE: string = "listPrice";

  /**
   * @eventType currency
   * @see Product#getCurrency()
   */
  static readonly CURRENCY: string = "currency";

  /**
   * @eventType variants
   * @see Product#getVariants()
   */
  static readonly VARIANTS: string = "variants";

  /**
   * @eventType definingAttributes
   * @see ProductVariant#getDefiningAttributes
   */
  static readonly DEFINING_ATTRIBUTES: string = "definingAttributes";

  /**
   * @eventType definingAttributes
   * @see ProductVariant#getDescribingAttributes
   */
  static readonly DESCRIBING_ATTRIBUTES: string = "describingAttributes";

  /**
   * @private
   * This class only defines constants.
   */
  constructor() {
  }

}

export default ProductPropertyNames;
