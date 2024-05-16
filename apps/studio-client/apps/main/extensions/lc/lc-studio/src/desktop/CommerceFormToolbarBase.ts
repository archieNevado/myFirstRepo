import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { as, asConfig } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CommerceFormToolbar from "./CommerceFormToolbar";

interface CommerceFormToolbarBaseConfig extends Config<Toolbar>, Partial<Pick<CommerceFormToolbarBase,
  "bindTo"
>> {
}

class CommerceFormToolbarBase extends Toolbar {
  declare Config: CommerceFormToolbarBaseConfig;

  #localeNameValueExpression: ValueExpression = null;

  /**
   * Create a new instance.
   */
  constructor(config: Config<CommerceFormToolbar> = null) {
    super(config);
  }

  /**
   * a value expression to the Commerce Object to create this toolbar for
   */
  bindTo: ValueExpression = null;

  protected getCatalogObject(): CatalogObject {
    return as(this.bindTo.getValue(), CatalogObject);
  }

  getLocaleValueExpression(): ValueExpression {
    if (!this.#localeNameValueExpression) {
      this.#localeNameValueExpression = ValueExpressionFactory.createFromFunction((): any => {
        const catalogObject = this.getCatalogObject();
        if (!catalogObject.getStore()) return undefined;
        if (!catalogObject.getStore().getSiteId()) return undefined;
        const site = editorContext._.getSitesService().getSite(catalogObject.getStore().getSiteId());
        if (site === undefined) return undefined;
        if (site && site.getLocale()) {
          const displayName = site.getLocale().getDisplayName();
          return {
            text: displayName,
            help: displayName,
            visible: true,
          };
        } else {
          return {
            text: "",
            help: "",
            visible: false,
          };
        }
      });
    }
    return this.#localeNameValueExpression;
  }

  static changeLocale(component: IconDisplayField, valueExpression: ValueExpression): void {
    const model: any = valueExpression.getValue();

    if (model) {
      const text: string = model.text;

      component.setVisible(model.visible);
      asConfig(component).value = text;
      component.tooltip = model.help;
    }
  }

  static changeType(iconDisplayField: IconDisplayField, valueExpression: ValueExpression): void {
    const catalogObject: CatalogObject = valueExpression.getValue();
    if (!catalogObject) {
      return;
    }
    const iconStyleClass = AugmentationUtil.getTypeCls(catalogObject);
    const text = AugmentationUtil.getTypeLabel(catalogObject);
    iconDisplayField.iconCls = iconStyleClass;
    asConfig(iconDisplayField).value = text;
  }
}

export default CommerceFormToolbarBase;
