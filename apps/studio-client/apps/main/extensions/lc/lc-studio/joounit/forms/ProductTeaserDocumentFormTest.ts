import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import AbstractProductTeaserComponentsTest
  from "@coremedia-blueprint/studio-client.main.lc-studio-test-helper/AbstractProductTeaserComponentsTest";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import TextArea from "@jangaroo/ext-ts/form/field/TextArea";
import QuickTipManager from "@jangaroo/ext-ts/tip/QuickTipManager";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ProductTeaserDocumentFormTestView from "./ProductTeaserDocumentFormTestView";
import IgnoreForCKEditor5Step from "./IgnoreForCKEditor5Step";

class ProductTeaserDocumentFormTest extends AbstractProductTeaserComponentsTest {
  #viewPort: Viewport = null;

  #productTeaserTitleField: TextField = null;

  #productTeaserTextArea: TextArea = null;

  override setUp(): void {
    super.setUp();
    QuickTipManager.init(true);
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewPort && this.#viewPort.destroy();
  }

  //noinspection JSUnusedGlobalSymbols
  testProductTeaserDocumentForm(): void {
    this.chain(
      this.#createTestlingStep(),
      this.loadContentRepository(),
      this.waitForContentRepositoryLoaded(),
      this.loadContentTypes(),
      this.waitForContentTypesLoaded(),
      this.loadProductTeaser(),
      this.waitForProductTeaserToBeLoaded(),
      this.waitForProductTeaserContentTypeToBeLoaded(),
      this.#waitForTeaserTitleFieldValue(AbstractCatalogTest.ORANGES_NAME),
      //this test step is ignored for ckeditor 5 for now, as we do currently not support the delegate to another property field.
      this.#waitForTeaserTextAreaValue(AbstractCatalogTest.ORANGES_SHORT_DESC),
    );
  }

  #waitForTeaserTitleFieldValue(value: string): Step {
    return new Step("Wait for the product teaser title field to be " + value,
      (): boolean =>
        this.#productTeaserTitleField.getValue() === value,

    );
  }

  #waitForTeaserTextAreaValue(value: string): Step {
    return new IgnoreForCKEditor5Step(bind(this, this.#getViewPort), "Wait for the product teaser text area to be " + value,
      (): boolean =>
        this.#productTeaserTextArea.getValue() && this.#productTeaserTextArea.getValue().indexOf(value) >= 0,
    );
  }

  #getViewPort(): Viewport {
    return this.#viewPort;
  }

  #createTestlingStep(): Step {
    return new Step("Create the testling",
      (): boolean =>
        true
      , bind(
        this, this.#createTestling),
    );
  }

  #createTestling(): void {
    const config = Config(ProductTeaserDocumentFormTestView);
    config.bindTo = this.getBindTo();
    this.#viewPort = new ProductTeaserDocumentFormTestView(config);
    this.#productTeaserTitleField = as(this.#viewPort.queryById("stringPropertyField"), TextField);
    this.#productTeaserTextArea = as(this.#viewPort.queryById("textAreaPropertyField"), TextArea);
  }
}

export default ProductTeaserDocumentFormTest;
