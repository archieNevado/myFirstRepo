import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames
  from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import BeanState from "@coremedia/studio-client.client-core/data/BeanState";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LinkListWrapperBase from "@coremedia/studio-client.link-list-models/LinkListWrapperBase";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import augmentationService from "../../augmentation/augmentationService";
import CatalogHelper from "../../helper/CatalogHelper";

interface CatalogLinkListWrapperConfig extends Config<LinkListWrapperBase>, Partial<Pick<CatalogLinkListWrapper,
  "bindTo" |
  "propertyName" |
  "linkTypeNames" |
  "maxCardinality" |
  "model" |
  "createStructFunction" |
  "readOnlyVE" |
  "acceptAugmentedContent"
>> {
}

class CatalogLinkListWrapper extends LinkListWrapperBase {
  declare Config: CatalogLinkListWrapperConfig;

  bindTo: ValueExpression = null;

  propertyName: string = null;

  linkTypeNames: Array<any> = null;

  maxCardinality: int = 0;

  model: Bean = null;

  createStructFunction: AnyFunction = null;

  acceptAugmentedContent: boolean = true;

  readOnlyVE: ValueExpression = null;

  #linksVE: ValueExpression = null;

  #propertyExpression: ValueExpression = null;

  #catalogObjRemoteBean: RemoteBean = null;

  constructor(config: Config<CatalogLinkListWrapper> = null) {
    super();
    this.bindTo = config.bindTo;
    this.propertyName = config.propertyName;
    this.linkTypeNames = config.linkTypeNames;
    this.maxCardinality = config.maxCardinality || 0;
    this.model = config.model;
    this.createStructFunction = config.createStructFunction;
    this.readOnlyVE = config.readOnlyVE;
    this.acceptAugmentedContent = config.acceptAugmentedContent === false ? false : true;
  }

  override getVE(): ValueExpression {
    if (!this.#linksVE) {
      this.#linksVE = ValueExpressionFactory.createTransformingValueExpression(this.getPropertyExpression(), bind(
        this, this.#transformer), bind(this, this.#reverseTransformer), []);
    }
    return this.#linksVE;
  }

  #invalidateIssues(event: PropertyChangeEvent): void {
    if (event.newState === BeanState.NON_EXISTENT || event.oldState === BeanState.NON_EXISTENT) {
      const content = as(this.bindTo.getValue(), Content);
      if (content && content.getIssues()) {
        content.getIssues().invalidate();
      }
    }
  }

  protected getPropertyExpression(): ValueExpression {
    if (!this.#propertyExpression) {
      if (this.bindTo) {
        if (is(this.bindTo.getValue(), Content)) {
          this.#propertyExpression = this.bindTo.extendBy("properties").extendBy(this.propertyName);
        } else {
          this.#propertyExpression = this.bindTo.extendBy(this.propertyName);
        }
      } else {
        this.#propertyExpression = ValueExpressionFactory.create(this.propertyName, this.model);
      }
    }
    return this.#propertyExpression;
  }

  override getTotalCapacity(): int {
    return this.maxCardinality > 0 ? this.maxCardinality : int.MAX_VALUE;
  }

  override getFreeCapacity(): int {
    if (!this.maxCardinality) {
      return int.MAX_VALUE;
    }
    //noinspection JSMismatchedCollectionQueryUpdate
    const catalogItems = as(this.getVE().getValue(), Array);
    return this.maxCardinality - catalogItems.length;
  }

  override acceptsLinks(links: Array<any>, replaceLinks: boolean = false): boolean {
    if (links.length > (replaceLinks ? this.getTotalCapacity() : this.getFreeCapacity())) {
      return false;
    }
    const targetSiteId = this.#getTargetSiteId();
    return links.every((link: any): boolean => {
      const catalogObject = CatalogLinkListWrapper.#getCatalogObject(link);
      if (!catalogObject) {
        return false;
      }
      if (catalogObject.getSiteId() !== targetSiteId) {
        return false;
      }

      if (!this.acceptAugmentedContent) {
        const augmentedContent = as(catalogObject.get(CatalogObjectPropertyNames.CONTENT), Content);
        if (augmentedContent) {
          // the commerce object is augmented
          return false;
        }
      }

      return this.linkTypeNames.some((linkTypeName: string): boolean =>
        CatalogHelper.getInstance().isSubType(catalogObject, linkTypeName),
      );
    });
  }

  /**
   * Return the site of the currently bound content or the preferred site.
   * May be undefined.
   */
  #getTargetSite(): Site {
    if (this.bindTo) {
      const content = as(this.bindTo.getValue(), Content);
      if (content) {
        return editorContext._.getSitesService().getSiteFor(content);
      }
    }
    //no content there. so let's take the preferred site
    return editorContext._.getSitesService().getPreferredSite();
  }

  /**
   * Return the the target site id. May be undefined.
   */
  #getTargetSiteId(): string {
    const site = this.#getTargetSite();
    return site && site.getId();
  }

  static #getCatalogObject(link: any): CatalogObject {
    let catalogObject = as(link, CatalogObject);
    if (!catalogObject) {
      const content = as(link, Content);
      if (content) {
        catalogObject = augmentationService.getCatalogObject(content);
      }
    }
    return catalogObject;
  }

  override getLinks(): Array<any> {
    return this.getVE().getValue();
  }

  override setLinks(links: Array<any>): Promise<any> {
    return new Promise((resolve: AnyFunction): void => {
      if (this.createStructFunction) {
        this.createStructFunction.apply(null);
      }
      const myLinks = links.map(CatalogLinkListWrapper.#getCatalogObject);
      //are some links yet not loaded?
      //noinspection JSMismatchedCollectionQueryUpdate
      RemoteBeanUtil.loadAll((): void => {
        this.getVE().setValue(myLinks);
        resolve(myLinks);
      }, myLinks);
    });
  }

  override isReadOnly(): boolean {
    return this.readOnlyVE ? this.readOnlyVE.getValue() : false;
  }

  #transformer(value: any): Array<any> {
    let valuesArray = [];
    if (value) {
      //the value can be a string or a catalog object bean
      if (is(value, String) || is(value, CatalogObject)) {
        //this is a single catalog object stored
        valuesArray = [value];
      } else if (is(value, Array)) {
        //this are multiple catalog objects stored in an array
        valuesArray = value;
      }
    }

    return valuesArray.map((value: any): CatalogObject => {
      //the value can be a string or a catalog object bean
      let catalogObject: CatalogObject;

      if (is(value, CatalogObject)) {
        catalogObject = value;
      } else if (is(value, String)) {
        catalogObject = as(CatalogHelper.getInstance().getCatalogObject(value, this.bindTo), CatalogObject);
      } else {
        Logger.error("CatalogLink does not accept the value: " + value);
      }

      if (catalogObject === undefined) {
        return undefined;
      }

      if (catalogObject !== this.#catalogObjRemoteBean) {
        if (this.#catalogObjRemoteBean) {
          this.#catalogObjRemoteBean.removePropertyChangeListener(BeanState.PROPERTY_NAME, bind(this, this.#invalidateIssues));
        }
        this.#catalogObjRemoteBean = catalogObject;
        if (catalogObject) {
          this.#catalogObjRemoteBean.addPropertyChangeListener(BeanState.PROPERTY_NAME, bind(this, this.#invalidateIssues));
        }
      }
      return catalogObject;
    });
  }

  #reverseTransformer(value: Array<any>): any {
    if (value && value.length > 0) {
      if (this.maxCardinality === 1) {
        return cast(CatalogObject, value[0]).getId();
      } else {
        return value.map((bean: CatalogObject): string =>
          bean.getId(),
        );
      }
    }
    return this.maxCardinality === 1 ? "" : [];
  }
}

export default CatalogLinkListWrapper;
