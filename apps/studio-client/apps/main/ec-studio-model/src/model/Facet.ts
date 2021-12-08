import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";

/**
 * A search facet with all it's possible filter/query values.
 */
class Facet extends BeanImpl {
  constructor(data: any) {
    //ensure that the key can be stored within a struct property
    data.key = data.key.replaceAll(".", "_");
    super(data);
  }

  getKey(): string {
    return this.get("key");
  }

  getValues(): Array<any> {
    return this.get("values");
  }

  getLabel(): string {
    return this.get("label");
  }

  isMultiSelect(): boolean {
    return this.get("multiSelect");
  }
}

export default Facet;
