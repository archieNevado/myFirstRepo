package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.blueprint.internal.lcstudio.util.JsCommerceBean;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.integration.test.util.ContentCleanupRegistry;
import com.coremedia.uitesting.cms.editor.JsContent;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.selection.RowModel;
import com.coremedia.uitesting.uapi.helper.conditions.SearchConditions;
import com.coremedia.uitesting.ui.components.CustomEditorGrid;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import net.joala.expression.Expression;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import static com.coremedia.hamcrest.matcher.webelement.WebElementInnerHtmlContains.innerHtmlContains;
import static com.coremedia.hamcrest.matcher.webelement.WebElementTextContains.textContainsString;
import static java.lang.String.format;
import static org.hamcrest.Matchers.not;

@SuppressWarnings("squid:MaximumInheritanceDepth")
@ExtJSObject
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CatalogRepositoryList extends CustomEditorGrid<RowModel> {

  public static final String XTYPE = "com.coremedia.ecommerce.studio.config.catalogRepositoryList";

  private static final String CATEGORY_TYPE = "Category";
  private static final String AUGMENTED_CATEGORY_TYPE = "Augmented Category";
  private static final String NOT_AUGMENTED_CATEGORY_ICON_CSS_CLASS = "cm-core-icons--type-category";
  private static final String AUGMENTED_CATEGORY_ICON_CSS_CLASS = "cm-core-icons--type-augmented-category";

  private static final String PRODUCT_TYPE = "Product";
  private static final String AUGMENTED_PRODUCT_TYPE = "Augmented Product";
  private static final String NOT_AUGMENTED_PRODUCT_ICON_CSS_CLASS = "cm-core-icons--type-product";
  private static final String AUGMENTED_PRODUCT_ICON_CSS_CLASS = "cm-core-icons--type-augmented-product";
  private final ConditionFactory conditionFactory;

  @SuppressWarnings("UnusedDeclaration")
  @FindByExtJS(xtype = CatalogRepositoryContextMenu.XTYPE, global = true)
  private CatalogRepositoryContextMenu contextMenu;

  private final ContentCleanupRegistry contentCleanupRegistry;
  private final SearchConditions searchConditions;

  public CatalogRepositoryList(ConditionFactory conditionFactory,
                               ContentCleanupRegistry contentCleanupRegistry,
                               SearchConditions searchConditions) {
    this.conditionFactory = conditionFactory;
    this.contentCleanupRegistry = contentCleanupRegistry;
    this.searchConditions = searchConditions;
  }

  public CatalogRepositoryContextMenu getContextMenu() {
    return contextMenu;
  }

  public void contextClickCommerceBeanByExternalIdOrName(String externalIdOrName) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalIdOrName(externalIdOrName);
    contextClick(commerceBeanModel);
    contextMenu.visible().waitUntilTrue();
  }

  public void contextClickCommerceBeanByExternalId(String externalId) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalId(externalId);
    contextClick(commerceBeanModel);
    contextMenu.visible().waitUntilTrue();
  }

  public void containsNonAugmentedCategory(String externalId) {
    containsCommerceBean(CommerceBeanType.CATEGORY, externalId, false);
  }

  public void containsAugmentedCategory(String externalId) {
    containsCommerceBean(CommerceBeanType.CATEGORY, externalId, true);
  }

  public void containsNonAugmentedProduct(String externalId) {
    containsCommerceBean(CommerceBeanType.PRODUCT, externalId, false);
  }

  public void containsAugmentedProduct(String externalId) {
    containsCommerceBean(CommerceBeanType.PRODUCT, externalId, true);
  }

  private void containsCommerceBean(CommerceBeanType type, String externalIdOrName, boolean augmented) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalIdOrName(externalIdOrName);
    commerceBeanModel.exists()
            .withMessage("CommerceBean with externalId/name should exist: " + externalIdOrName)
            .waitUntilTrue();
    commerceBeanModel.getBean().augmented()
            .withMessage("CommerceBean with externalId/name should " + (augmented ? "" : "not ") + "be augmented: " + externalIdOrName)
            .waitUntilEquals(augmented);

    validateCommerceBeanRowUi(type, externalIdOrName, augmented);
  }

  private void validateCommerceBeanRowUi(CommerceBeanType type, String externalIdOrName, boolean augmented) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalIdOrName(externalIdOrName);
    Condition<WebElement> iconElementCondition = getView().cellElement(commerceBeanModel, 0);
    Condition<WebElement> idElementCondition = getView().cellElement(commerceBeanModel, 1);
    Condition<WebElement> nameElementCondition = getView().cellElement(commerceBeanModel, 2);

    String expectedIconTextContained;
    String unexpectedIconClass;
    String expectedIconClass;

    switch (type) {
      case CATEGORY:
        expectedIconTextContained = augmented ? AUGMENTED_CATEGORY_TYPE : CATEGORY_TYPE;
        unexpectedIconClass = augmented ? NOT_AUGMENTED_CATEGORY_ICON_CSS_CLASS : AUGMENTED_CATEGORY_ICON_CSS_CLASS;
        expectedIconClass = augmented ? AUGMENTED_CATEGORY_ICON_CSS_CLASS : NOT_AUGMENTED_CATEGORY_ICON_CSS_CLASS;
        break;
      case PRODUCT:
        expectedIconTextContained = augmented ? AUGMENTED_PRODUCT_TYPE : PRODUCT_TYPE;
        unexpectedIconClass = augmented ? NOT_AUGMENTED_PRODUCT_ICON_CSS_CLASS : AUGMENTED_PRODUCT_ICON_CSS_CLASS;
        expectedIconClass = augmented ? AUGMENTED_PRODUCT_ICON_CSS_CLASS : NOT_AUGMENTED_PRODUCT_ICON_CSS_CLASS;
        break;
      default:
        throw new UnsupportedOperationException("Unsupported CommerceBean type: " + type);
    }

    // Inner HTML: As the icon class is not directly set at the icon element (which is currently the TD element),
    //             but set at a nested element (SPAN) within the TD element, we just check, that the inner HTML
    //             contains/does not contain the expected classes.
    iconElementCondition
            .withMessage(format("Icon should fulfill these expectations: contained text: '%s', not having class: '%s', having class: '%s'", expectedIconTextContained, unexpectedIconClass, expectedIconClass))
            .assertThat(Matchers.allOf(
                    textContainsString(expectedIconTextContained),
                    not(innerHtmlContains(unexpectedIconClass)),
                    innerHtmlContains(expectedIconClass)
            ));

    // // AbstractExpression extracted due to JDK 11 compiler bug, see https://confluence.coremedia.com/display/PCINF/Java-CoP+-+Minutes+2019-06-06 for details.
    Expression<Boolean> expression = new AbstractExpression<>() {
      // SSBasedInspection: (if Structural Search Expression for Condition.get() is active)
      // Inside an expression is the only place, where Condition.get() is allowed and actually
      // required.
      @SuppressWarnings("SSBasedInspection")
      @Override
      public Boolean get() {
        WebElement idElement = idElementCondition.get();
        WebElement nameElement = nameElementCondition.get();
        Matcher<WebElement> matcher = textContainsString(externalIdOrName);
        return matcher.matches(idElement) || matcher.matches(nameElement);
      }
    };
    conditionFactory.booleanCondition(expression)
            .withMessage(format("Either id-text, or name-text must match external ID: '%s'", externalIdOrName))
            .assertTrue();
  }

  /**
   * Asserts that all menu items for an augmented category exist on the context menu.
   *
   * @param externalIdOrName the element of a augmented category which context menu contains text
   */
  public void hasContextMenuForAugmentedCategory(String externalIdOrName) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalIdOrName(externalIdOrName);
    contextClick(commerceBeanModel);
    contextMenu.isAugmentedCategoryMenu();
  }

  /**
   * Asserts that all menu items for an augmented product exist on the context menu.
   *
   * @param externalId the text of an augmented product which context menu must be checked
   */
  public void hasContextMenuForAugmentedProduct(String externalId) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalId(externalId);
    contextClick(commerceBeanModel);
    contextMenu.isAugmentedProductMenu();
  }

  /**
   * Augments the given category.
   *
   * @param externalId external ID of the category to augment
   * @return created content
   */
  public Content augmentCategory(String externalId) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalId(externalId);
    JsCommerceBean jsCommerceBean = commerceBeanModel.getBean();
    JsContent jsContent = jsCommerceBean.getContent();

    containsNonAugmentedCategory(externalId);
    contextClick(commerceBeanModel);
    contextMenu.getAugmentCategoryMenuItem().click();

    containsAugmentedCategory(externalId);
    jsContent.exists().waitUntilTrue();

    Content content = jsContent.getBean();
    contentCleanupRegistry.register(content);

    searchConditions.indexed(content).waitUntilTrue();

    return content;
  }

  /**
   * Augments the given product.
   *
   * @param externalId external ID of the product to augment
   * @return created content
   */
  public Content augmentProduct(String externalId) {
    CommerceBeanModel commerceBeanModel = getStore().queryByExternalId(externalId);
    JsCommerceBean jsCommerceBean = commerceBeanModel.getBean();
    JsContent jsContent = jsCommerceBean.getContent();

    containsNonAugmentedProduct(externalId);
    contextClick(commerceBeanModel);
    contextMenu.getAugmentProductMenuItem().click();

    containsAugmentedProduct(externalId);
    jsContent.exists().waitUntilTrue();

    Content content = jsContent.getBean();
    contentCleanupRegistry.register(content);

    searchConditions.indexed(content).waitUntilTrue();

    return content;
  }

  @Override
  public CommerceBeanStore getStore() {
    return super.getStore().evalJsProxyProxy(CommerceBeanStore.class);
  }

  private enum CommerceBeanType {
    CATEGORY,
    PRODUCT
  }
}
