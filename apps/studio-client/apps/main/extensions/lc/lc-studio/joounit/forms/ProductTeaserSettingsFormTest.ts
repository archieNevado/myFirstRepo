import AbstractProductTeaserComponentsTest from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/AbstractProductTeaserComponentsTest";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Ext from "@jangaroo/ext-ts";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import QuickTipManager from "@jangaroo/ext-ts/tip/QuickTipManager";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ViewSettingsRadioGroup from "../../src/components/product/ViewSettingsRadioGroup";
import ProductTeaserSettingsFormTestView from "./ProductTeaserSettingsFormTestView";

class ProductTeaserSettingsFormTest extends AbstractProductTeaserComponentsTest {
  #viewPort: Viewport = null;

  #viewSettings: ViewSettingsRadioGroup = null;

  override setUp(): void {
    super.setUp();
    QuickTipManager.init(true);
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewPort && this.#viewPort.destroy();
    this.#viewPort = null;
  }

  testProductTeaserSettingsForm(): void {
    this.chain(
      this.loadContentRepository(),
      this.waitForContentRepositoryLoaded(),
      this.loadContentTypes(),
      this.waitForContentTypesLoaded(),
      this.loadProductTeaser(),
      this.waitForProductTeaserToBeLoaded(),
      this.#createTestlingStep(),
      //inherit is the default
      this.#waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING),
      this.#checkRadio(ViewSettingsRadioGroup.ENABLED_SETTING),
      this.#waitForRadioToBeChecked(ViewSettingsRadioGroup.ENABLED_SETTING),
      this.#checkRadio(ViewSettingsRadioGroup.DISABLED_SETTING),
      this.#waitForRadioToBeChecked(ViewSettingsRadioGroup.DISABLED_SETTING),
      this.#checkRadio(ViewSettingsRadioGroup.INHERITED_SETTING),
      this.#waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING),
    );
  }

  #checkRadio(value: string): Step {
    return new Step("change view settings to " + value,
      (): boolean => true,
      (): void => {
        const valueObject: Record<string, any> = {};
        valueObject[this.#viewSettings.radioButtonFormName] = value;
        this.#viewSettings.setValue(valueObject);
      },
    );
  }

  #waitForRadioToBeChecked(itemId: string): Step {
    return new Step("Wait for radio button " + itemId + " to be checked",
      (): boolean => {
        const value: any = this.#viewSettings.getValue();
        return value[this.#viewSettings.radioButtonFormName] === itemId;
      },
    );
  }

  #createTestlingStep(): Step {
    let ve: ValueExpression;
    return new Step("Create the testling",
      (): boolean => {
        if (!this.#viewPort) {
          // create only once
          const config = Config(ProductTeaserSettingsFormTestView);
          config.bindTo = this.getBindTo();
          this.#viewPort = new ProductTeaserSettingsFormTestView(Config(ProductTeaserSettingsFormTestView, Ext.apply({}, config)));
          this.#viewSettings = as(this.#viewPort.queryById("viewSettingsPropertyField"), ViewSettingsRadioGroup);
          ve = Object(this.#viewSettings).getInheritOptionVisibleExpression(config.bindTo);
        }
        // but wait for inherit option to initialize
        return ve.getValue();
      },
    );
  }

}

export default ProductTeaserSettingsFormTest;
