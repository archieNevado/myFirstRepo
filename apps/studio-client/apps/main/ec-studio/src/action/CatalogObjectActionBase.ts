import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ArrayUtils from "@coremedia/studio-client.client-core/util/ArrayUtils";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import DependencyTrackedAction from "@coremedia/studio-client.ext.ui-components/actions/DependencyTrackedAction";
import ValueExpressionAction from "@coremedia/studio-client.ext.ui-components/actions/ValueExpressionAction";
import { as, is, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogObjectAction from "./CatalogObjectAction";

interface CatalogObjectActionBaseConfig extends Config<DependencyTrackedAction> {
}

/**
 * <p>An abstract <code>ext.Action</code> that performs a operation on the configured catalog items.</p>
 * <p>Extend this class for an catalog item action.</p>
 * <p>The action is disabled when there is no configured catalog item.</p>
 * <p>Override the method <code>isDisabledFor</code> to provide a more specific disable behaviour.</p>
 */
class CatalogObjectActionBase extends DependencyTrackedAction implements ValueExpressionAction {
  declare Config: CatalogObjectActionBaseConfig;

  #catalogObjectExpression: ValueExpression = null;

  /**
   * @param config the config object
   */
  constructor(config: Config<CatalogObjectAction> = null) {
    super(config);
    this.#catalogObjectExpression = config.catalogObjectExpression;
    if (!this.#catalogObjectExpression) {
      throw new Error("catalogObjectExpression is not set");
    }
  }

  /**
   * @private
   */
  getValueExpression(): ValueExpression {
    return this.#catalogObjectExpression;
  }

  /**
   * Return whether this action is disabled on the given array of catalog objects.
   * Override this method to implement more specific disable behaviour.
   *
   * @param catalogObjects the array of catalog objects: never empty.
   * @return whether this action is disabled
   */
  protected isDisabledFor(catalogObjects: Array<any>): boolean {
    return false;
  }

  protected isHiddenFor(catalogObjects: Array<any>): boolean {
    return false;
  }

  static #catalogObjectOnly(entities: Array<any>): Array<any> {
    return entities.filter(ObjectUtils.isA(CatalogObject));
  }

  protected override calculateHidden(): boolean {
    const entities: Array<any> = this.#getEntities();
    const catalogObjects = CatalogObjectActionBase.#catalogObjectOnly(entities);
    if (catalogObjects.length < entities.length) {
      // Any non-Catalog in the current value? Hide me!
      return true;
    }
    return this.isHiddenFor(catalogObjects);
  }

  protected override calculateDisabled(): boolean {
    const catalogObjects = this.getCatalogObjects();
    return !catalogObjects || catalogObjects.length === 0 || this.isDisabledFor(catalogObjects);
  }

  /**
   * Return the catalog objects on which this action operates.
   * If there is no catalog object it returns an empty array.
   */
  protected getCatalogObjects(): Array<any> {
    const value = this.#getEntities();
    return is(value, CatalogObject) ? [as(value, CatalogObject)] : (is(value, Array)) ? as(value, Array).filter(ObjectUtils.isA(CatalogObject)) : [];
  }

  #getEntities(): any {
    return ArrayUtils.asArray(this.#catalogObjectExpression.getValue());
  }
}
mixin(CatalogObjectActionBase, ValueExpressionAction);

export default CatalogObjectActionBase;
