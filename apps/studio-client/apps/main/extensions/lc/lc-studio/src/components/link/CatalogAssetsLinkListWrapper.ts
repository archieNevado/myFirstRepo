import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LinkListWrapperBase from "@coremedia/studio-client.link-list-models/LinkListWrapperBase";
import PropertyEditorUtil from "@coremedia/studio-client.main.editor-components/sdk/util/PropertyEditorUtil";
import { as, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";

interface CatalogAssetsLinkListWrapperConfig extends Config<LinkListWrapperBase>, Partial<Pick<CatalogAssetsLinkListWrapper,
  "bindTo" |
  "linksVE" |
  "assetContentTypes" |
  "maxCardinality" |
  "readOnlyVE"
>> {
}

class CatalogAssetsLinkListWrapper extends LinkListWrapperBase {
  declare Config: CatalogAssetsLinkListWrapperConfig;

  bindTo: ValueExpression = null;

  linksVE: ValueExpression = null;

  assetContentTypes: Array<any> = null;

  maxCardinality: int = 0;

  readOnlyVE: ValueExpression = null;

  constructor(config: Config<CatalogAssetsLinkListWrapper> = null) {
    super(config);
    this.bindTo = config.bindTo;
    this.linksVE = config.linksVE;
    this.assetContentTypes = config.assetContentTypes || [];
    this.maxCardinality = config.maxCardinality || 0;
    this.readOnlyVE = config.readOnlyVE;
  }

  override getVE(): ValueExpression {
    return this.linksVE;
  }

  override acceptsLinks(links: Array<any>, replaceLinks: boolean = false): boolean {
    if (links.length > (replaceLinks ? this.getTotalCapacity() : this.getFreeCapacity())) {
      return false;
    }
    for (var asset of links as Content[]) {
      if (!is(asset, Content)) {
        return false;
      }

      //check the content type
      const typeAccepted = this.assetContentTypes.some((assetContentType: string): boolean =>
        UndocContentUtil.filterMatchingTypes(CatalogAssetsLinkListWrapper.#getContentType(assetContentType), [asset], true).length === 0,
      );
      if (!typeAccepted) {
        return false;
      }

      if (PropertyEditorUtil.isReadOnly(asset)) {
        return false;
      }
    }
    return true;
  }

  override getLinks(): Array<any> {
    const value = this.getVE().getValue();
    return value === undefined ? undefined : as(value, Array);
  }

  override setLinks(links: Array<any>): Promise<any> {
    if (links) {
      return new Promise((resolve: AnyFunction): void => {
        const promises = [];
        for (const content of links as Content[]) {
          if (!PropertyEditorUtil.isReadOnly(content)) {
            promises.push(CatalogHelper.getInstance().createOrUpdateProductListStructs(ValueExpressionFactory.createFromValue(content),
              this.bindTo.getValue()));
          }
        }

        Promise.all(promises).then((results: Array<any>): void => {
          resolve(results.filter((product: Product): boolean =>
            product !== null,
          ));
        });
      });
    } else {
      return Promise.resolve([]);
    }
  }

  override getTotalCapacity(): int {
    return this.maxCardinality > 0 ? this.maxCardinality : int.MAX_VALUE;
  }

  override getFreeCapacity(): int {
    return this.getTotalCapacity() - (this.getLinks() ? this.getLinks().length : 0);
  }

  override isReadOnly(): boolean {
    return this.readOnlyVE ? this.readOnlyVE.getValue() : false;
  }

  static #getContentType(linkTypeName: string): ContentType {
    return as(session._.getConnection().getContentRepository().getContentType(linkTypeName), ContentType);
  }
}

export default CatalogAssetsLinkListWrapper;
