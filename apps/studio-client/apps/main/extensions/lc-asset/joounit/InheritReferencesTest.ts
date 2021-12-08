import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogLinkContextMenu
  from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkContextMenu";
import CatalogLinkPropertyField
  from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CatalogThumbnailResolver from "@coremedia-blueprint/studio-client.main.lc-studio/CatalogThumbnailResolver";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import StructRemoteBean from "@coremedia/studio-client.cap-rest-client/struct/StructRemoteBean";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import StatefulQuickTip from "@coremedia/studio-client.ext.ui-components/components/StatefulQuickTip";
import ReadOnlyStateMixin from "@coremedia/studio-client.ext.ui-components/mixins/ReadOnlyStateMixin";
import ContextMenuEventAdapter from "@coremedia/studio-client.ext.ui-components/util/ContextMenuEventAdapter";
import QtipUtil from "@coremedia/studio-client.ext.ui-components/util/QtipUtil";
import TableUtil from "@coremedia/studio-client.ext.ui-components/util/TableUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Button from "@jangaroo/ext-ts/button/Button";
import Item from "@jangaroo/ext-ts/menu/Item";
import QuickTipManager from "@jangaroo/ext-ts/tip/QuickTipManager";
import { as, asConfig, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import InheritReferencesAction from "../src/action/InheritReferencesAction";
import InheritReferencesButton from "../src/components/InheritReferencesButton";
import AbstractCatalogAssetTest from "./AbstractCatalogAssetTest";
import InheritReferencesTestView from "./InheritReferencesTestView";

class InheritReferencesTest extends AbstractCatalogAssetTest {
  #bindTo: ValueExpression = null;

  #forceReadOnlyValueExpression: ValueExpression = null;

  #contentReadOnlyExpression: ValueExpression = null;

  #register: AnyFunction = null;

  #getQuickTip: AnyFunction = null;

  #viewport: InheritReferencesTestView = null;

  #inheritButton: InheritReferencesButton = null;

  #myCatalogLink: CatalogLinkPropertyField = null;

  #removeMenuItem: Item = null;

  #removeButton: Button = null;

  #inheritExpression: ValueExpression = null;

  #referencesExpression: ValueExpression = null;

  #inheritAction: InheritReferencesAction = null;

  override setUp(): void {
    super.setUp();

    this.#bindTo = ValueExpressionFactory.createFromValue();
    this.#forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    this.#contentReadOnlyExpression = ValueExpressionFactory.createFromValue(false);

    this.#contentReadOnlyExpression.addChangeListener((ve: ValueExpression): void => {
      this.#forceReadOnlyValueExpression.setValue(ve.getValue());
    });

    //Obviously the inherit toggle button in the test setup has a problem with QuickTips...
    this.#register = bind(QuickTipManager, QuickTipManager.register);
    QuickTipManager.register = ((): void => {});

    //We have to mock QuickTips.getQuickTip as this returns undefined
    this.#getQuickTip = bind(QuickTipManager, QuickTipManager.getQuickTip);
    QuickTipManager.getQuickTip = ((): StatefulQuickTip =>
      Ext.create(StatefulQuickTip, {})
    );

    QtipUtil.registerQtipFormatter();
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver("CatalogObject"));
  }

  #setBindTo(path: string): void {
    const picture = as(beanFactory._.getRemoteBean(path), Content);
    //we need to mock the write access
    picture.getRepository().getAccessControl().mayWrite = ((): boolean =>
      !this.#contentReadOnlyExpression.getValue()
    );
    const localSettings = as(beanFactory._.getRemoteBean(path + "/structs/localSettings"), StructRemoteBean);
    //PUT should cause no trouble
    localSettings["doWriteChanges"] = ((): void => {
      //ignore
    });

    this.#bindTo.setValue(picture);

  }

  override tearDown(): void {
    super.tearDown();
    this.#viewport && this.#viewport.destroy();
    this.#register && (QuickTipManager.register = this.#register);
    this.#getQuickTip && (QuickTipManager.getQuickTip = this.#getQuickTip);
  }

  //noinspection JSUnusedGlobalSymbols
  testDisableStateWhenNoInherit(): void {
    this.chain(
      //open the grid with the content inherit=false
      this.#createTestling("content/200"),

      this.#waitForInheritButtonVisible(),
      this.#waitForInheritButtonUnpressed(),
      this.#waitForGridWritable(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonEnabled(),

      this.#forceReadOnly(),

      this.#waitForInheritButtonDisabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#forceWritable(),

      this.#waitForInheritButtonEnabled(),
      this.#waitForGridWritable(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonEnabled(),

      this.#makeContentReadOnly(),

      this.#waitForInheritButtonDisabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#makeContentWritable(),

      this.#waitForInheritButtonEnabled(),
      this.#waitForGridWritable(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonEnabled(),
    );
  }

  //noinspection JSUnusedGlobalSymbols
  testDisableStateWhenInherit(): void {
    this.chain(
      //open the grid with the content inherit=true
      this.#createTestling("content/202"),

      this.#waitForInheritButtonVisible(),
      this.#waitForInheritButtonPressed(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#forceReadOnly(),

      this.#waitForInheritButtonDisabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#forceWritable(),

      this.#waitForInheritButtonEnabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#makeContentReadOnly(),

      this.#waitForInheritButtonDisabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),

      this.#makeContentWritable(),

      this.#waitForInheritButtonEnabled(),
      this.#waitForGridReadonly(),
      this.#openContextMenu(),
      this.#waitContextMenuOpened(),
      this.#waitRemoveButtonDisabled(),
    );
  }

  //noinspection JSUnusedGlobalSymbols
  testInheritAction(): void {
    this.#inheritExpression = ValueExpressionFactory.createFromValue(true);
    const originalList = ["A", "B"];
    this.#referencesExpression = ValueExpressionFactory.createFromValue(originalList);
    const originReferencesExpression = ValueExpressionFactory.createFromValue(originalList);
    this.#inheritAction = new InheritReferencesAction(
      Config(InheritReferencesAction, {
        bindTo: ValueExpressionFactory.createFromValue(),
        inheritExpression: this.#inheritExpression,
        referencesExpression: this.#referencesExpression,
        originReferencesExpression: originReferencesExpression,
      }));

    const changedList = ["C", "D"];
    this.chain(
      this.#toggleInheritAction(),
      this.#waitInheritFalse(), //inherit is now false: we can edit the list manually
      this.#changeProductList(changedList),
      this.#toggleInheritAction(),
      this.#waitInheritTrue(), //inherit is now true: the list must be original again
      this.#waitProductListEqual(originalList),
      this.#toggleInheritAction(),
      this.#waitInheritFalse(), //inherit is now false the list must be the previously changed one
      this.#waitProductListEqual(changedList),
    );
  }

  #toggleInheritAction(): Step {
    return new Step("Toggle inherit",
      (): boolean =>
        true
      ,
      (): void =>
        this.#inheritAction.execute(),
    );
  }

  #changeProductList(list: Array<any>): Step {
    return new Step("Change the product list to " + list,
      (): boolean =>
        true

      ,
      (): void => {
        this.#referencesExpression.setValue(list);
      });
  }

  #waitInheritFalse(): Step {
    return new Step("Wait Inherit False",
      (): boolean =>
        !this.#inheritExpression.getValue(),
    );
  }

  #waitInheritTrue(): Step {
    return new Step("Wait Inherit True",
      (): boolean =>
        this.#inheritExpression.getValue(),
    );
  }

  #waitProductListEqual(list: Array<any>): Step {
    return new Step("Wait the Product list to be equal to " + list,
      (): boolean =>
        this.#referencesExpression.getValue() === list,
    );
  }

  #waitForGridReadonly(): Step {
    return new Step("Wait for grid is read-only",
      (): boolean =>
        this.#myCatalogLink && this.#isReadOnly(this.#myCatalogLink),
    );
  }

  #waitForGridWritable(): Step {
    return new Step("Wait for grid is writable",
      (): boolean =>
        this.#myCatalogLink && !this.#isReadOnly(this.#myCatalogLink),
    );
  }

  #isReadOnly(link: CatalogLinkPropertyField): boolean {
    return cast(ReadOnlyStateMixin, link.getView()).readOnly;
  }

  #createTestling(path: string): Step {
    return new Step("Create the testling",
      (): boolean =>
        true
      ,
      (): void => {
        this.#setBindTo(path);
        const conf = Config(InheritReferencesTestView);
        conf.bindTo = this.#bindTo;
        conf.forceReadOnlyValueExpression = this.#forceReadOnlyValueExpression;
        this.#viewport = new InheritReferencesTestView(conf);
        this.#myCatalogLink = this.#findCatalogLink();
      },
    );
  }

  #waitForInheritButtonVisible(): Step {
    return new Step("Wait for the inherit button to be visible",
      (): boolean =>
        this.#findInheritButton(),
    );
  }

  #waitForInheritButtonUnpressed(): Step {
    return new Step("Wait for the inherit button to be unpressed",
      (): boolean =>
        !this.#inheritButton.pressed,
    );
  }

  #waitForInheritButtonPressed(): Step {
    return new Step("Wait for the inherit button to be pressed",
      (): boolean =>
        this.#inheritButton.pressed,
    );
  }

  #forceReadOnly(): Step {
    return new Step("Force to read only",
      (): boolean =>
        true

      ,
      (): void => {
        this.#forceReadOnlyValueExpression.setValue(true);
      });
  }

  #forceWritable(): Step {
    return new Step("Force to writable",
      (): boolean =>
        true

      ,
      (): void => {
        this.#forceReadOnlyValueExpression.setValue(false);
      });

  }

  #makeContentReadOnly(): Step {
    return new Step("Make Content read only",
      (): boolean =>
        true

      ,
      (): void => {
        this.#contentReadOnlyExpression.setValue(true);
      });

  }

  #makeContentWritable(): Step {
    return new Step("Make Content writable",
      (): boolean =>
        true

      ,
      (): void => {
        this.#contentReadOnlyExpression.setValue(false);
      });
  }

  #waitForInheritButtonDisabled(): Step {
    return new Step("Wait for the inherit button to be disabled",
      (): boolean =>
        this.#inheritButton.disabled,
    );

  }

  #waitForInheritButtonEnabled(): Step {
    return new Step("Wait for the inherit button to be enabled",
      (): boolean =>
        !this.#inheritButton.disabled,
    );

  }

  #findInheritButton(): boolean {
    this.#inheritButton = as(ComponentManager.getAll().filter((component: Component): boolean =>
      component.isXType(InheritReferencesButton.xtype),
    )[0], InheritReferencesButton);

    return this.#inheritButton && this.#inheritButton.isVisible(true);
  }

  #findCatalogLink(): CatalogLinkPropertyField {
    this.#myCatalogLink = as(ComponentManager.getAll().filter((component: Component): boolean =>
      component.isXType(CatalogLinkPropertyField.xtype),
    )[0], CatalogLinkPropertyField);
    this.#removeButton = as(this.#myCatalogLink.getTopToolbar().queryById(ECommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID), Button);
    return this.#myCatalogLink;
  }

  #openContextMenu(): Step {
    return new Step("open Context Menu",
      (): boolean =>
        //wait for the list filled
        asConfig(this.#myCatalogLink.getStore()).data.length > 0
      ,
      (): void => {
        const event: Record<string, any> = {
          getXY: (): Array<any> =>
            TableUtil.getCell(this.#myCatalogLink, 0, 1).getXY()
          ,
          preventDefault: (): void => {
            //do nothing
          },
          getTarget: (): HTMLElement =>
            TableUtil.getCellAsDom(this.#myCatalogLink, 0, 1)
          ,
          type: ContextMenuEventAdapter.EVENT_NAME,
        };
        this.#myCatalogLink.fireEvent("rowcontextmenu", this.#myCatalogLink, null, null, 0, event);
      },
    );
  }

  #waitRemoveButtonDisabled(): Step {
    return new Step("Wait remove button disabled",
      (): boolean =>
        this.#removeButton.disabled,
    );
  }

  #waitRemoveButtonEnabled(): Step {
    return new Step("Wait remove button enabled",
      (): boolean =>
        !this.#removeButton.disabled,
    );
  }

  #waitContextMenuOpened(): Step {
    return new Step("Wait context menu opened",
      (): boolean =>
        !!this.#findContextMenu(),
    );
  }

  #findContextMenu(): CatalogLinkContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogLinkContextMenu.xtype),
    )[0], CatalogLinkContextMenu);
    if (contextMenu) {
      this.#removeMenuItem = as(contextMenu.getComponent(ECommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }

}

export default InheritReferencesTest;
