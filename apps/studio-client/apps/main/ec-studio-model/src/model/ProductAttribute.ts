
/**
 * This interface is used to access the describing and defining attributes in a typed manner
 */
abstract class ProductAttribute {

  abstract readonly name: string;

  abstract readonly displayName: string;

  abstract readonly type: string;

  abstract readonly unit: string;

  abstract readonly description: string;

  abstract readonly externalId: string;

  abstract readonly value: any;

  abstract readonly values: Array<any>;

  abstract readonly defining: boolean;

}

export default ProductAttribute;
