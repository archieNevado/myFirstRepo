import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentProxy from "@coremedia/studio-client.cap-rest-client/content/ContentProxy";
import RemoteBeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/RemoteBeanImpl";
import { is, mixin } from "@jangaroo/runtime";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogObject from "./CatalogObject";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Store from "./Store";

class CatalogObjectImpl extends RemoteBeanImpl implements CatalogObject, ContentProxy {

  constructor(uri: string) {
    super(uri);
    //Do not set immideate properties in subclasses.
    //ExternalId and SiteId need to be url en- and decoded.
    //This is done by jersey on the server side.
  }

  override get(property: any): any {
    try {
      return super.get(property);
    } catch (e) {
      if (is(e, Error)) {
      // catalog objects such as marketing spots do not use stable IDs and may vanish any time :(
        trace("[INFO] ignoring error while accesing property", property, e);
        return null;
      } else throw e;
    }
  }

  getContent(): Content {
    return this.get(CatalogObjectPropertyNames.CONTENT);
  }

  getName(): string {
    return this.get(CatalogObjectPropertyNames.NAME);
  }

  getShortDescription(): string {
    return this.get(CatalogObjectPropertyNames.SHORT_DESCRIPTION);
  }

  getExternalId(): string {
    return this.get(CatalogObjectPropertyNames.EXTERNAL_ID);
  }

  override getId(): string {
    return this.get(CatalogObjectPropertyNames.ID);
  }

  getExternalTechId(): string {
    return this.get(CatalogObjectPropertyNames.EXTERNAL_TECH_ID);
  }

  getStore(): Store {
    return this.get(CatalogObjectPropertyNames.STORE);
  }

  getSiteId(): string {
    return this.getUriPath().split("/")[2];
  }

  getCustomAttributes(): any {
    return this.get(CatalogObjectPropertyNames.CUSTOM_ATTRIBUTES);
  }

  getCustomAttribute(attribute: string): any {
    return this.getCustomAttributes() ? this.getCustomAttributes()[attribute] : null;
  }

  override invalidate(callback: AnyFunction = null): void {
    if (!this.hasListeners()) {
      super.invalidate();
      return;
    }

    super.invalidate(() => {
      const content = this.getContent();
      if (content && content.getIssues()) {
        content.getIssues().invalidate(callback);
      } else if (is(callback, Function)) {
        callback();
      }
    });
  }
}
mixin(CatalogObjectImpl, CatalogObject, ContentProxy);

export default CatalogObjectImpl;
