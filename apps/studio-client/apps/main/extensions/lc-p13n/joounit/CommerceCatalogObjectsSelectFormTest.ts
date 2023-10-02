import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import CommerceObjectField from "@coremedia-blueprint/studio-client.main.ec-studio/components/CommerceObjectField";
import CommerceObjectSelector from "@coremedia-blueprint/studio-client.main.ec-studio/components/CommerceObjectSelector";
import CommerceCatalogObjectsSelectForm from "@coremedia-blueprint/studio-client.main.ec-studio/forms/CommerceCatalogObjectsSelectForm";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import PropertyEditorUtil from "@coremedia/studio-client.main.editor-components/sdk/util/PropertyEditorUtil";
import StringUtil from "@jangaroo/ext-ts/String";
import Button from "@jangaroo/ext-ts/button/Button";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Store from "@jangaroo/ext-ts/data/Store";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import CommerceCatalogObjectsSelectFormTestView from "./CommerceCatalogObjectsSelectFormTestView";

class CommerceCatalogObjectsSelectFormTest extends AbstractCatalogTest {

  #persona: Content = null;

  #createReadOnlyValueExpression: AnyFunction = null;

  #viewport: Viewport = null;

  #selector: CommerceObjectSelector = null;

  override setUp(): void {
    super.setUp();
    this.resetCatalogHelper();

    this.#persona = as(beanFactory._.getRemoteBean("content/300"), Content);
    //we need to mock the write access
    this.#persona.getRepository().getAccessControl().mayWrite = ((): boolean => true);
  }

  #createTestling(catalogObjectIds: Array<any>): void {
    const bindTo = ValueExpressionFactory.createFromValue(this.#persona);
    const forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    const catalogObjectIdsExpression = ValueExpressionFactory.createFromValue(catalogObjectIds);

    //Mock PropertyEditorUtil#createReadOnlyValueExpression
    this.#createReadOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression;
    PropertyEditorUtil.createReadOnlyValueExpression = ((contentValueExpression: ValueExpression, forceReadOnlyValueExpression?: ValueExpression): ValueExpression =>
      ValueExpressionFactory.createFromFunction((): boolean => {
        if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
          return true;
        }
        if (!contentValueExpression) {
          return false;
        }
        const mayWrite: any = true;
        return mayWrite === undefined ? undefined : !mayWrite;
      })

    );

    const conf = Config(CommerceCatalogObjectsSelectFormTestView);
    conf.content = this.#persona;
    conf.bindTo = bindTo;
    conf.forceReadOnlyValueExpression = forceReadOnlyValueExpression;
    conf.catalogObjectIdsExpression = catalogObjectIdsExpression;

    this.#viewport = new CommerceCatalogObjectsSelectFormTestView(conf);
    this.#selector = as(this.#viewport.queryById(CommerceCatalogObjectsSelectForm.SELECTOR_ITEM_ID), CommerceObjectSelector);
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewport.destroy();
    PropertyEditorUtil.createReadOnlyValueExpression = this.#createReadOnlyValueExpression;
  }

  testSelectAndRemove(): void {
    this.#createTestling([]);
    this.chain(
      this.#waitForTheSelectorLoadTheContracts(),
      this.#selectTheExteriorContract(),
      this.#waitForTheSelectorHasOnlyInteriorContract(),
      this.#waitForOnlyOneContractFieldLoaded(),
      this.#waitForTheExteriorContractFieldDisplayed(),
      this.#selectTheInteriorContract(),
      this.#waitForTheSelectorHasNoContract(),
      this.#waitForTheContractFieldsLoaded(),
      this.#waitForTheContractFieldsDisplayed(),
      this.#removeTheExteriorContract(),
      this.#waitForOnlyOneContractFieldLoaded(),
      this.#waitForTheInteriorContractFieldDisplayed(),
      this.#waitForTheSelectorHasOnlyExteriorContract(),
    );
  }

  testInvalidCatalogObjectId(): void {
    this.#createTestling(["ibm:///catalog/contract/invalidid"]);
    this.chain(
      this.#waitForOnlyOneContractFieldLoaded(),
      this.#waitForContractFieldDisplaysInvalidMessage(),
    );
  }

  #waitForTheSelectorLoadTheContracts(): Step {
    return new Step("Wait for the selector to load the contracts",
      (): boolean => {
        const store = cast(Store, this.#selector.getStore());
        return store.getCount() === 2 &&
              store.getAt(0).data.name === "Contract for CoreMedia Preview Exterior" &&
              store.getAt(1).data.name === "Contract for CoreMedia Preview Interior";
      },
    );
  }

  #selectTheExteriorContract(): Step {
    return new Step("Select the exterior contract",
      (): boolean => true,
      (): void => {
        const model = this.#selector.findRecordByDisplay("Contract for CoreMedia Preview Exterior");
        const exteriorContractID = "ibm:///catalog/contract/4000000000000000507";
        this.#selector.setValue(exteriorContractID);
        this.#selector.fireEvent("select", this.#selector, model);

      },
    );
  }

  #selectTheInteriorContract(): Step {
    return new Step("Select the interior contract",
      (): boolean => true,
      (): void => {
        const model = this.#selector.findRecordByDisplay("Contract for CoreMedia Preview Interior");
        const interiorContractID = "ibm:///catalog/contract/4000000000000000508";
        this.#selector.setValue(interiorContractID);
        this.#selector.fireEvent("select", this.#selector, model);
      },
    );
  }

  #waitForTheSelectorHasOnlyInteriorContract(): Step {
    return new Step("Wait for the selector to have only the interior contracts",
      (): boolean => {
        const store = cast(Store, this.#selector.getStore());
        return store.getCount() === 1 &&
                      store.getAt(0).data.name === "Contract for CoreMedia Preview Interior";
      },
    );
  }

  #waitForTheSelectorHasOnlyExteriorContract(): Step {
    return new Step("Wait for the selector to have only the exterior contracts",
      (): boolean => {
        const store = cast(Store, this.#selector.getStore());
        return store.getCount() === 1 &&
                      store.getAt(0).data.name === "Contract for CoreMedia Preview Exterior";
      },
    );
  }

  #waitForTheSelectorHasNoContract(): Step {
    return new Step("Wait for the selector to have no contracts",
      (): boolean => {
        const store = cast(Store, this.#selector.getStore());
        return store.getCount() === 0;
      },
    );
  }

  #waitForOnlyOneContractFieldLoaded(): Step {
    return new Step("Wait for only one contract field to be loaded",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        return commerceObjectFields && commerceObjectFields.length === 1;
      },
    );
  }

  #getCommerceObjectFields(): Array<any> {
    return this.#viewport.query(createComponentSelector()._xtype(CommerceObjectField.xtype).build());
  }

  #waitForTheContractFieldsLoaded(): Step {
    return new Step("Wait for the contract fields to be loaded",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        return commerceObjectFields && commerceObjectFields.length === 2;
      },
    );
  }

  #waitForTheExteriorContractFieldDisplayed(): Step {
    return new Step("Wait for the exterior contract field to be displayed",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        const commerceDisplayField = as(cast(CommerceObjectField, commerceObjectFields[0])
          .getComponent(CommerceObjectField.DISPLAYFIELD_ITEM_ID), DisplayField);
        return commerceDisplayField.getValue() === "Contract for CoreMedia Preview Exterior";
      },
    );
  }

  #waitForTheInteriorContractFieldDisplayed(): Step {
    return new Step("Wait for the interior contract field to be displayed",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        const commerceDisplayField = as(cast(CommerceObjectField, commerceObjectFields[0])
          .getComponent(CommerceObjectField.DISPLAYFIELD_ITEM_ID), DisplayField);
        return commerceDisplayField.getValue() === "Contract for CoreMedia Preview Interior";
      },
    );
  }

  #waitForContractFieldDisplaysInvalidMessage(): Step {
    return new Step("Wait for the contract field to show invalid message.",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        const commerceDisplayField = as(cast(CommerceObjectField, commerceObjectFields[0])
          .getComponent(CommerceObjectField.DISPLAYFIELD_ITEM_ID), DisplayField);
        return commerceDisplayField.getValue() === StringUtil.format("Invalid e-Commerce user contract ID: {0}", "invalidid");
      },
    );
  }

  #waitForTheContractFieldsDisplayed(): Step {
    return new Step("Wait for the contract fields to be displayed",
      (): boolean => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        const commerceDisplayField = as(cast(CommerceObjectField, commerceObjectFields[0])
          .getComponent(CommerceObjectField.DISPLAYFIELD_ITEM_ID), DisplayField);
        const commerceDisplayField2 = as(cast(CommerceObjectField, commerceObjectFields[1])
          .getComponent(CommerceObjectField.DISPLAYFIELD_ITEM_ID), DisplayField);
        return commerceDisplayField.getValue() === "Contract for CoreMedia Preview Exterior"
                      && commerceDisplayField2.getValue() === "Contract for CoreMedia Preview Interior";
      },
    );
  }

  #removeTheExteriorContract(): Step {
    return new Step("Remove the exterior contract",
      (): boolean => true,
      (): void => {
        const commerceObjectFields = this.#getCommerceObjectFields();
        const commerceObjectRemoveButton1 = as(cast(CommerceObjectField, commerceObjectFields[0])
          .getComponent(CommerceObjectField.REMOVE_BUTTON_ITEM_ID), Button);
        commerceObjectRemoveButton1.baseAction.execute(); // simulate click
      },
    );
  }

}

export default CommerceCatalogObjectsSelectFormTest;
