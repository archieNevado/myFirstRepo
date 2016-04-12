package com.coremedia.livecontext.studio.forms {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.config.productTeaserDocumentForm;
import com.coremedia.livecontext.studio.config.productTeaserDocumentFormTestView;
import com.coremedia.ui.data.test.Step;

import ext.QuickTips;
import ext.Viewport;
import ext.form.TextArea;
import ext.form.TextField;

public class ProductTeaserDocumentFormTest extends AbstractProductTeaserComponentsTest{
  private var viewPort:Viewport;
  private var productTeaserTitleField:TextField;
  private var productTeaserTextArea:TextArea;

  override public function setUp():void {
    super.setUp();
    QuickTips.init(true);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort && viewPort.destroy();
  }

  //noinspection JSUnusedGlobalSymbols
  public function testProductTeaserDocumentForm():void {
    chain(
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            loadProductTeaser(),
            waitForProductTeaserToBeLoaded(),
            waitForProductTeaserContentTypeToBeLoaded(),
            createTestlingStep(),
            waitForTeaserTitleFieldValue(ORANGES_NAME),
            waitForTeaserTextAreaValue(ORANGES_SHORT_DESC)
    )
  }

  private function waitForTeaserTitleFieldValue(value:String):Step {
    return new Step("Wait for the product teaser title field to be " + value,
            function ():Boolean {
              return productTeaserTitleField.getValue() === value;
            }
    );
  }

  private function waitForTeaserTextAreaValue(value:String):Step {
    return new Step("Wait for the product teaser text area to be " + value,
            function ():Boolean {
              return productTeaserTextArea.getValue() && productTeaserTextArea.getValue().indexOf(value) >= 0;
            }
    );
  }

  private function createTestlingStep():Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            createTestling
    );
  }

  private function createTestling():void {
    var config:productTeaserDocumentForm = new productTeaserDocumentForm();
    config.bindTo = getBindTo();
    config.forceReadOnlyValueExpression = getForceReadOnlyValueExpression();
    viewPort = new ProductTeaserDocumentFormTestView(new productTeaserDocumentFormTestView(config));
    productTeaserTitleField = viewPort.find('itemId', 'stringPropertyField')[0] as TextField;
    productTeaserTextArea = viewPort.find('itemId', 'textAreaPropertyField')[0] as TextArea;
  }
}
}