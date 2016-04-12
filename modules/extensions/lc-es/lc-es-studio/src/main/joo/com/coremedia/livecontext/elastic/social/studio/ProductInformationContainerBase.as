package com.coremedia.livecontext.elastic.social.studio {

import com.coremedia.elastic.social.studio.config.contentInformationContainer;
import com.coremedia.elastic.social.studio.model.Contribution;
import com.coremedia.elastic.social.studio.model.ContributionAdministrationPropertyNames;
import com.coremedia.elastic.social.studio.model.impl.AbstractContributionAdministration;
import com.coremedia.ui.components.IconLabel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Container;
import ext.form.Label;

public class ProductInformationContainerBase extends Container {
  protected static const TARGET_LABEL_ID:String = "cm-elastic-social-target-label";
  protected static const TARGET_BUTTON_ICON_ITEM_ID:String = "cm-elastic-social-target-icon";

  private var moderationContributionAdministrationImpl:AbstractContributionAdministration;
  private var targetIconLabel:IconLabel;
  private var targetLabel:Label;
  private var displayedContributionValueExpression:ValueExpression;

  public function ProductInformationContainerBase(config:contentInformationContainer = null) {
    moderationContributionAdministrationImpl = config.contributionAdministration as AbstractContributionAdministration;

    displayedContributionValueExpression = ValueExpressionFactory.create(
            ContributionAdministrationPropertyNames.DISPLAYED, moderationContributionAdministrationImpl);

    super(config);
  }

  override protected function afterRender():void {
    super.afterRender();
    displayedContributionValueExpression.addChangeListener(toggleTarget);
    displayedContributionValueExpression.addChangeListener(setContentTypeIconCssClass);
    setContentTypeIconCssClass();
    toggleTarget();
  }

  private function toggleTarget():void {
    var contribution:Contribution = moderationContributionAdministrationImpl.getDisplayed();
    if (contribution && contribution.getTarget()) {
      getTargetIcon().show();
      getTargetLabel().show();
    } else {
      getTargetIcon().hide();
      getTargetLabel().hide();
    }
    this.doLayout();
  }

  private function setContentTypeIconCssClass():void {
    getTargetIcon().setIconClass("");

    if (moderationContributionAdministrationImpl) {
      var displayed:Contribution = moderationContributionAdministrationImpl.getDisplayed();
      if (displayed && displayed.getTarget) {
        displayed.getTarget(function (target:*):void {
          getTargetIcon().setIconClass("content-type-xs product-icon");
        });
      }
    }
  }

  private function getTargetIcon():IconLabel {
    if (!targetIconLabel) {
      targetIconLabel = get(TARGET_BUTTON_ICON_ITEM_ID) as IconLabel;
    }

    return targetIconLabel;
  }

  private function getTargetLabel():Label {
    if (!targetLabel) {
      targetLabel = get(TARGET_LABEL_ID) as Label;
    }

    return targetLabel;
  }

  override protected function beforeDestroy():void {
    displayedContributionValueExpression && displayedContributionValueExpression.removeChangeListener(toggleTarget);
    displayedContributionValueExpression && displayedContributionValueExpression.removeChangeListener(setContentTypeIconCssClass);
    super.beforeDestroy();
  }
}
}
