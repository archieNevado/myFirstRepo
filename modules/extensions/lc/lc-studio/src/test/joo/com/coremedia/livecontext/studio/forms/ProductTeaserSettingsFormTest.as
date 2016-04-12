package com.coremedia.livecontext.studio.forms {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.components.product.ViewSettingsRadioGroup;
import com.coremedia.livecontext.studio.config.productTeaserSettingsFormTestView;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.test.Step;

import ext.QuickTips;
import ext.Viewport;
import ext.form.Radio;

public class ProductTeaserSettingsFormTest extends AbstractProductTeaserComponentsTest {
  private var viewPort:Viewport;
  private var viewSettings:ViewSettingsRadioGroup;

  override public function setUp():void {
    super.setUp();
    QuickTips.init(true);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort && viewPort.destroy();
    viewPort = null;
  }

  public function testProductTeaserSettingsForm():void {
    chain(
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            loadProductTeaser(),
            waitForProductTeaserToBeLoaded(),
            createTestlingStep(),

            checkRadio(ViewSettingsRadioGroup.INHERITED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING),
            checkRadio(ViewSettingsRadioGroup.ENABLED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.ENABLED_SETTING),
            checkRadio(ViewSettingsRadioGroup.DISABLED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.DISABLED_SETTING),
            checkRadio(ViewSettingsRadioGroup.INHERITED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING)
    )
  }

  private function checkRadio(value:String):Step {
    return new Step("change view settings to " + value,
            function ():Boolean {return true;},
            function ():void {
              viewSettings.setValue(value);
              viewSettings.fireEvent('change');
            }
    );
  }

  private function waitForRadioToBeChecked(itemId:String):Step {
    return new Step("Wait for radio button " + itemId + " to be checked",
            function ():Boolean {
              var value:Radio = viewSettings.getValue();
              return value && value.getItemId() === itemId;
            }
    );
  }

  private function createTestlingStep():Step {
    var ve:ValueExpression;
    return new Step("Create the testling",
            function ():Boolean {
              if(!viewPort) {
                // create only once
                var config:productTeaserSettingsFormTestView = new productTeaserSettingsFormTestView();
                config.bindTo = getBindTo();
                viewPort = new ProductTeaserSettingsFormTestView(new productTeaserSettingsFormTestView(config));
                viewSettings = viewPort.find('itemId', 'viewSettingsPropertyField')[0] as ViewSettingsRadioGroup;
                ve = Object(viewSettings).getInheritOptionVisibleExpression(config.bindTo);
              }
              // but wait for inherit option to initialize
              return ve.getValue();
            }
    );
  }

}
}