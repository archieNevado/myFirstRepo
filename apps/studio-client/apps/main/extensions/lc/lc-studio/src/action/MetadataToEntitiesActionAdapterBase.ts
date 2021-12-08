import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import DependencyTrackedAction from "@coremedia/studio-client.ext.ui-components/actions/DependencyTrackedAction";
import MetadataBeanAction from "@coremedia/studio-client.main.editor-components/sdk/actions/metadata/MetadataBeanAction";
import MetadataHelper from "@coremedia/studio-client.main.editor-components/sdk/preview/MetadataHelper";
import PreviewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewContextMenu";
import MetadataTreeNode from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/MetadataTreeNode";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import MetadataToEntitiesActionAdapter from "./MetadataToEntitiesActionAdapter";

interface MetadataToEntitiesActionAdapterBaseConfig extends Config<MetadataBeanAction> {
}

/**
 * Adapter that implements a MetadataAction based on a backing action.
 *
 * All critical methods are delegated to the backing action after extracting
 * a bean from the underlying metadata. If no bean can be obtained from
 * the MetadataTreeNode (or one of it's parents if useParentNode is enabled), the
 * backing action is configured with metadata properties (if available).
 *
 * @see com.coremedia.cms.editor.sdk.actions.ContentAction
 */
class MetadataToEntitiesActionAdapterBase extends MetadataBeanAction {
  declare Config: MetadataToEntitiesActionAdapterBaseConfig;

  #backingAction: Action = null;

  #store: Store = null;

  #properties: any = null;

  readonly resolvedBeanValueExpression: ValueExpression = ValueExpressionFactory.createFromValue();

  #myContextMenu: PreviewContextMenu = null;

  #component: Component = null;

  constructor(config: Config<MetadataToEntitiesActionAdapter> = null) {
    let setEntities: string;
    let newConfig: Config<MetadataToEntitiesActionAdapter>;
    super((()=>{
      this.#backingAction = as(config.backingAction, Action);
      setEntities = config.setEntities || "setContents";
      if (!is(this.#backingAction[setEntities], Function)) {
        throw new Error("config param setEntities cannot be resolved to a function");
      }
      newConfig = Config(MetadataToEntitiesActionAdapter, Ext.apply({
        iconCls: this.#backingAction.getIconCls(),
        text: this.#backingAction.getText(),
        handler: bind(this, this.#delegateToBackingAction),
      }, config));
      return newConfig;
    })());

    this.resolvedBeanValueExpression.addChangeListener((ve: ValueExpression): void => {
      const resolvedBean = ve.getValue();
      const values = [];
      if (is(resolvedBean, Content)) {
        // activate content actions
        values.push(resolvedBean);
      } else if (resolvedBean === null) {
        // activate 'augment this' actions
        values.push(this.#properties);
      }
      this.#backingAction[setEntities](values);
    });
  }

  protected override isDisabledFor(metadata: MetadataTreeNode): boolean {
    this.extractBeanAndProperties(metadata);
    this.registerResetResolvedBeanHandler();
    if (is(this.#backingAction, DependencyTrackedAction)) {
      return this.#backingAction["calculateDisabled"]();
    }
    return false;
  }

  registerResetResolvedBeanHandler(): void {
    if (!this.#myContextMenu) {
      this.#myContextMenu = as(this.#component.findParentByType(PreviewContextMenu.xtype), PreviewContextMenu);
      this.#myContextMenu.on("show", (): void => {
        // trigger reload
        this.resolvedBeanValueExpression.setValue(undefined);
      });
    }
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#component = comp;
  }

  #delegateToBackingAction(): void {
    this.extractBeanAndProperties(this.getMetadata());

    // copy items to delegate (if possible)
    if (this.items && this.items.length > 0) {
      this.#backingAction["items"] = this.items;
    }
    this.#backingAction.execute();
  }

  resolveBeanForAction(): void {
    const resolvedBean = this.resolvedBeanValueExpression.getValue();
    if (this.#store !== null && resolvedBean === undefined) {
      const rb = this.#store.resolveShopUrlForPbe(this.#properties.shopUrl);
      this.resolvedBeanValueExpression.setValue(rb);
    }
  }

  extractBeanAndProperties(metadata: MetadataTreeNode): void {
    const children = metadata.getChildren();
    if (!Ext.isEmpty(children)) {
      this.#properties = MetadataHelper.getAllProperties(children[0]);
    }
    this.#store = as(MetadataHelper.getBeanMetadataValue(metadata), Store);
    this.resolveBeanForAction();
  }
}

export default MetadataToEntitiesActionAdapterBase;
