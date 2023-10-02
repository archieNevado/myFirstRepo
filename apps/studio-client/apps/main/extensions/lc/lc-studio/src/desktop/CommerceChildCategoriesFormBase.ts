import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Container from "@jangaroo/ext-ts/container/Container";
import { bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CommerceChildCategoriesForm from "./CommerceChildCategoriesForm";

interface CommerceChildCategoriesFormBaseConfig extends Config<Container>, Partial<Pick<CommerceChildCategoriesFormBase,
  "bindTo"
>> {
}

class CommerceChildCategoriesFormBase extends Container {
  declare Config: CommerceChildCategoriesFormBaseConfig;

  static readonly #PROPERTIES: string = "properties";

  static readonly #LOCAL_SETTINGS_STRUCT_NAME: string = "localSettings";

  static readonly #COMMERCE_STRUCT_NAME: string = "commerce";

  static readonly #CHILDREN_LIST_NAME: string = "children";

  static readonly #SELECT_CHILDREN_NAME: string = "selectChildren";

  static readonly CHILDREN_PROPERTY_NAME: string = CommerceChildCategoriesFormBase.#LOCAL_SETTINGS_STRUCT_NAME + "." + CommerceChildCategoriesFormBase.#COMMERCE_STRUCT_NAME + "." + CommerceChildCategoriesFormBase.#CHILDREN_LIST_NAME;

  static readonly SELECT_CHILDREN_PROPERTY_NAME: string = CommerceChildCategoriesFormBase.#LOCAL_SETTINGS_STRUCT_NAME + "." + CommerceChildCategoriesFormBase.#COMMERCE_STRUCT_NAME + "." + CommerceChildCategoriesFormBase.#SELECT_CHILDREN_NAME;

  bindTo: ValueExpression = null;

  #selectChildrenExpression: ValueExpression = null;

  #categoryExpression: ValueExpression = null;

  constructor(config: Config<CommerceChildCategoriesForm> = null) {
    super(config);
  }

  protected override onDestroy(): void {
    this.#selectChildrenExpression.removeChangeListener(bind(this, this.#copyChildrenFromCatalog));
    super.onDestroy();
  }

  isSelectChildrenExpression(bindTo: ValueExpression): ValueExpression {
    if (!this.#selectChildrenExpression) {
      this.#selectChildrenExpression = bindTo.extendBy(CommerceChildCategoriesFormBase.#PROPERTIES).extendBy(CommerceChildCategoriesFormBase.SELECT_CHILDREN_PROPERTY_NAME);
      this.#selectChildrenExpression.addChangeListener(bind(this, this.#copyChildrenFromCatalog));
    }
    return this.#selectChildrenExpression;
  }

  isInheritExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean =>
      !this.isSelectChildrenExpression(bindTo).getValue(),
    );
  }

  getCategoryExpression(bindTo: ValueExpression): ValueExpression {
    this.#categoryExpression = AugmentationUtil.getCatalogObjectExpression(bindTo);
    return this.#categoryExpression;
  }

  createStructs(): void {
    const localSettingsStructExpression = this.bindTo.extendBy(CommerceChildCategoriesFormBase.#PROPERTIES, CommerceChildCategoriesFormBase.#LOCAL_SETTINGS_STRUCT_NAME);
    localSettingsStructExpression.loadValue((): void => {
      const localSettingsStruct: Struct = localSettingsStructExpression.getValue();
      cast(RemoteBean, localSettingsStruct).load((): void => {
        if (!localSettingsStruct.get(CommerceChildCategoriesFormBase.#COMMERCE_STRUCT_NAME)) {
          localSettingsStruct.getType().addStructProperty(CommerceChildCategoriesFormBase.#COMMERCE_STRUCT_NAME);
        }

        const commerceStruct: Struct = localSettingsStruct.get(CommerceChildCategoriesFormBase.#COMMERCE_STRUCT_NAME);
        const categoriesStruct: Struct = commerceStruct.get(CommerceChildCategoriesFormBase.#CHILDREN_LIST_NAME);
        if (!categoriesStruct) {
          commerceStruct.getType().addStringListProperty(CommerceChildCategoriesFormBase.#CHILDREN_LIST_NAME, 1000000);
        }
      });
    });
  }

  /**
   * copy the sub categories from the catalog to the list of selected children
   * if the list is empty in case of the switch from 'inherit' to 'select'
   */
  #copyChildrenFromCatalog(): void {
    if (this.#selectChildrenExpression.getValue()) {
      const childrenExpression = this.bindTo.extendBy(CommerceChildCategoriesFormBase.#PROPERTIES).extendBy(CommerceChildCategoriesFormBase.CHILDREN_PROPERTY_NAME);
      if (is(childrenExpression.getValue(), Array)) {
        //accept the previously saved children. nothing to do
      } else {
        this.createStructs();
        //copy from the catalog hierarchy.
        const category: Category = this.#categoryExpression.getValue();
        const subCategories = category.getSubCategories();
        if (subCategories && subCategories.length > 0) {
          childrenExpression.setValue(subCategories.map((subCategory: Category): string =>
            subCategory.getId(),
          ));
        }

      }
    }
  }
}

export default CommerceChildCategoriesFormBase;
