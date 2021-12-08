import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import Store from "./Store";

abstract class CatalogObject extends RemoteBean {
  abstract getName(): string;

  abstract getShortDescription(): string;

  abstract getExternalId(): string;

  abstract getId(): string;

  abstract getExternalTechId(): string;

  abstract getStore(): Store;

  abstract getSiteId(): string;

  abstract getCustomAttributes(): any;

  /**
   * Returns a custom attribute of this CatalogObject.
   *
   * @param attribute name of the attribute
   * @return the custom attribute value or null if the custom attribute does exist
   */
  abstract getCustomAttribute(attribute: string): any;
}

export default CatalogObject;
