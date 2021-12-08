import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import ContributionAdministrationPropertyNames from "@coremedia/studio-client.main.es-models/ContributionAdministrationPropertyNames";
import AbstractContributionAdministration from "@coremedia/studio-client.main.es-models/impl/AbstractContributionAdministration";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";

interface ProductInformationContainerBaseConfig extends Config<Container>, Partial<Pick<ProductInformationContainerBase,
  "contributionAdministration"
>> {
}

class ProductInformationContainerBase extends Container {
  declare Config: ProductInformationContainerBaseConfig;

  protected static readonly TARGET_LABEL_ID: string = "cm-elastic-social-target-label";

  protected static readonly TARGET_BUTTON_ICON_ITEM_ID: string = "cm-elastic-social-target-icon";

  static readonly #ICON_CLS: string = CoreIcons_properties.type_product;

  #targetIconDisplayField: IconDisplayField = null;

  #targetLabel: DisplayField = null;

  #displayedContributionValueExpression: ValueExpression = null;

  contributionAdministration: AbstractContributionAdministration = null;

  constructor(config: Config<ProductInformationContainerBase> = null) {
    super(config);

    this.#displayedContributionValueExpression = ValueExpressionFactory.create(
      ContributionAdministrationPropertyNames.DISPLAYED, this.contributionAdministration);
  }

  protected override afterRender(): void {
    super.afterRender();
    this.#displayedContributionValueExpression.addChangeListener(bind(this, this.#toggleTarget));
    this.#displayedContributionValueExpression.addChangeListener(bind(this, this.#setContentTypeIconCssClass));
    this.#setContentTypeIconCssClass();
    this.#toggleTarget();
  }

  #toggleTarget(): void {
    const contribution = this.contributionAdministration.getDisplayed();
    if (contribution && contribution.getTarget()) {
      this.#getTargetIcon().show();
      this.#getTargetLabel().show();
    } else {
      this.#getTargetIcon().hide();
      this.#getTargetLabel().hide();
    }
    this.updateLayout();
  }

  #setContentTypeIconCssClass(): void {
    if (this.contributionAdministration) {
      const displayed = this.contributionAdministration.getDisplayed();
      if (displayed && bind(displayed, displayed.getTarget)) {
        displayed.getTarget((target: any): void => {
          //
        });
      }
      this.#getTargetIcon().iconCls = ProductInformationContainerBase.#ICON_CLS;
    }
  }

  #getTargetIcon(): IconDisplayField {
    if (!this.#targetIconDisplayField) {
      this.#targetIconDisplayField = as(this.queryById(ProductInformationContainerBase.TARGET_BUTTON_ICON_ITEM_ID), IconDisplayField);
    }

    return this.#targetIconDisplayField;
  }

  #getTargetLabel(): DisplayField {
    if (!this.#targetLabel) {
      this.#targetLabel = as(this.queryById(ProductInformationContainerBase.TARGET_LABEL_ID), DisplayField);
    }

    return this.#targetLabel;
  }

  protected override beforeDestroy(): void {
    this.#displayedContributionValueExpression && this.#displayedContributionValueExpression.removeChangeListener(bind(this, this.#toggleTarget));
    this.#displayedContributionValueExpression && this.#displayedContributionValueExpression.removeChangeListener(bind(this, this.#setContentTypeIconCssClass));
    super.beforeDestroy();
  }
}

export default ProductInformationContainerBase;
