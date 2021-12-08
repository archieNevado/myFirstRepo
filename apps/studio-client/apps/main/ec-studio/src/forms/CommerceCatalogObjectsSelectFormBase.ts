import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import { as, bind, cast, is, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import CommerceObjectSelector from "../components/CommerceObjectSelector";
import CatalogHelper from "../helper/CatalogHelper";
import CommerceCatalogObjectsSelectForm from "./CommerceCatalogObjectsSelectForm";

interface CommerceCatalogObjectsSelectFormBaseConfig extends Config<FieldContainer>, Config<HidableMixin>, Partial<Pick<CommerceCatalogObjectsSelectFormBase,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "hideText"
>> {
}

class CommerceCatalogObjectsSelectFormBase extends FieldContainer {
  declare Config: CommerceCatalogObjectsSelectFormBaseConfig;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  #storeForContentExpression: ValueExpression = null;

  #catalogObjectsExpression: ValueExpression<(Bean | CatalogObject)[]> = null;

  constructor(config: Config<CommerceCatalogObjectsSelectForm> = null) {
    super(config);
    this.#getStoreForContentExpression().addChangeListener(bind(this, this.#adjustLabel));
  }

  protected override onDestroy(): void {
    this.#getStoreForContentExpression().removeChangeListener(bind(this, this.#adjustLabel));
    super.onDestroy();
  }

  protected override afterRender(): void {
    super.afterRender();
    this.on("add", bind(this, this.#adjustLabel));
    this.on("remove", bind(this, this.#adjustLabel));
    this.#adjustLabel();
  }

  getCatalogObjectsExpression(config: Config<CommerceCatalogObjectsSelectForm>): ValueExpression<(Bean | CatalogObject)[]> {
    if (!this.#catalogObjectsExpression) {
      this.#catalogObjectsExpression = CatalogHelper.getCatalogObjectsExpression(config.bindTo,
        config.catalogObjectIdListName,
        config.invalidMessage,
        config.catalogObjectIdsExpression);
    }
    return this.#catalogObjectsExpression;
  }

  getHandleSelectFunction(config: Config<CommerceCatalogObjectsSelectForm>): AnyFunction {
    return (selector: CommerceObjectSelector, record: BeanRecord): void => {
      CatalogHelper.addCatalogObject(config.bindTo, config.catalogObjectIdListName, record.getBean().get("id"), config.catalogObjectIdsExpression);
      selector.clearValue();
    };
  }

  static getCatalogObjectKey(item: Bean): string {
    if (is(item, CatalogObject)) {
      return cast(CatalogObject, item).getUriPath();
    } else {
      //error handling: when the id is invalid then catalog object is just a bean with the id containing the invalid id
      return item.get("id");
    }
  }

  #getStoreForContentExpression(): ValueExpression {
    if (!this.#storeForContentExpression) {
      this.#storeForContentExpression = CatalogHelper.getInstance().
        getStoreForContentExpression(this.bindTo);
    }
    return this.#storeForContentExpression;
  }

  //////////custom functions to remove/add catalog object fields without touching the catalog object selector

  removeCommerceObjectFields(container: Container): void {
    if (!container.items || container.items.length === 0) return;

    container.items.each((item: Component): void =>{
      //don't remove the selector and the error label
      if (item.getItemId() === CommerceCatalogObjectsSelectForm.SELECTOR_ITEM_ID ||
              item.getItemId() === CommerceCatalogObjectsSelectForm.NO_STORE_LABEL) {
        return;
      }
      container.remove(item, true);
    });
  }

  addCommerceObjectFields(container: Container, components: Array<any>): void {
    if (!components || components.length === 0) return;
    components.forEach((item: Component): void =>{
      // the index of the selector must be computed every time
      const selectorIndex = container.items.indexOf(container.getComponent(CommerceCatalogObjectsSelectForm.SELECTOR_ITEM_ID));
      // add the item just before the selector
      container.insert(selectorIndex, item);
    });
  }

  #adjustLabel(): void {
    if (this.#getStoreForContentExpression().isLoaded()) {
      this.#doAdjustLabel(this.#getStoreForContentExpression().getValue());
    } else {
      this.#getStoreForContentExpression().loadValue(bind(this, this.#doAdjustLabel));
    }
  }

  #doAdjustLabel(store: Store): void {
    //show 'no store' label if no store available
    this.items.each((item: Component, index: number): void => {
      const container = as(item, Container);
      if (container) {
        container.setVisible(!!store);
      }
    });
    this.getComponent(CommerceCatalogObjectsSelectForm.NO_STORE_LABEL).setVisible(!store);
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }
}

interface CommerceCatalogObjectsSelectFormBase extends HidableMixin{}

mixin(CommerceCatalogObjectsSelectFormBase, HidableMixin);

export default CommerceCatalogObjectsSelectFormBase;
