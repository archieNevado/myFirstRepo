
/**
 * Model that represents one meta data type of a media item.
 */
class MetaDataSection {
  #metaDataType: string = null;

  #data: Array<any> = [];

  constructor(type: string) {
    this.#metaDataType = type;
  }

  getMetaDataType(): string {
    return this.#metaDataType;
  }

  length(): number {
    return this.#data.length;
  }

  getData(): Array<any> {
    return this.#data;
  }

  addProperty(property: string, value: string): void {
    let formattedValue = value;
    if (value) {
      formattedValue = value;
    } else {
      value = undefined;
    }
    this.#data.push({
      property: property,
      value: value,
      formattedValue: formattedValue,
    });
  }
}

export default MetaDataSection;
