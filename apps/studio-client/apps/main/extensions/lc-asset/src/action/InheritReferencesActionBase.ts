import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import DependencyTrackedToggleAction from "@coremedia/studio-client.ext.ui-components/actions/DependencyTrackedToggleAction";
import PropertyEditorUtil from "@coremedia/studio-client.main.editor-components/sdk/util/PropertyEditorUtil";
import Ext from "@jangaroo/ext-ts";
import Config from "@jangaroo/runtime/Config";
import InheritReferencesAction from "./InheritReferencesAction";

interface InheritReferencesActionBaseConfig extends Config<DependencyTrackedToggleAction> {
}

class InheritReferencesActionBase extends DependencyTrackedToggleAction {
  declare Config: InheritReferencesActionBaseConfig;

  static readonly #PROPERTIES: string = "properties";

  static readonly #LOCAL_SETTINGS_STRUCT_NAME: string = "localSettings";

  static readonly #COMMERCE_STRUCT_NAME: string = "commerce";

  static readonly #INHERIT_PROPERTY_NAME: string = "inherit";

  #bindTo: ValueExpression = null;

  #inheritExpression: ValueExpression = null;

  #originReferencesExpression: ValueExpression = null;

  #referencesExpression: ValueExpression = null;

  #references: Array<any> = [];

  #forceReadOnlyValueExpression: ValueExpression = null;

  constructor(config: Config<InheritReferencesAction> = null) {

    super((()=>{
    // Copy values before super constructor call for calculateDisable.
      this.#bindTo = config.bindTo;
      this.#inheritExpression = config.inheritExpression || config.bindTo.extendBy(InheritReferencesActionBase.#PROPERTIES, InheritReferencesActionBase.#LOCAL_SETTINGS_STRUCT_NAME, InheritReferencesActionBase.#COMMERCE_STRUCT_NAME, InheritReferencesActionBase.#INHERIT_PROPERTY_NAME);
      this.#originReferencesExpression = config.originReferencesExpression || config.bindTo.extendBy(InheritReferencesActionBase.#PROPERTIES, InheritReferencesActionBase.#LOCAL_SETTINGS_STRUCT_NAME, InheritReferencesActionBase.#COMMERCE_STRUCT_NAME, CatalogHelper.ORIGIN_REFERENCES_LIST_NAME);
      this.#referencesExpression = config.referencesExpression || config.bindTo.extendBy(InheritReferencesActionBase.#PROPERTIES, InheritReferencesActionBase.#LOCAL_SETTINGS_STRUCT_NAME, InheritReferencesActionBase.#COMMERCE_STRUCT_NAME, CatalogHelper.REFERENCES_LIST_NAME);

      this.#forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
      return Config(InheritReferencesAction, Ext.apply({
        iconCls: LivecontextStudioPlugin_properties.InheritReferencesAction_icon,
        text: LivecontextStudioPlugin_properties.InheritReferencesAction_text,
        tooltip: LivecontextStudioPlugin_properties.InheritReferencesAction_tooltip,
        tooltipPressed: LivecontextStudioPlugin_properties.InheritReferencesAction_tooltipPressed,
      }, config));
    })());
  }

  protected override handleUnpress(): void {
    this.#inheritExpression.setValue(false);

    //restore the temporarily stored catalog object list
    //but only if the catalog object list is not empty
    if (this.#references && this.#references.length > 0) {
      this.#referencesExpression.setValue(this.#references);
    }
  }

  protected override handlePress(): void {
    this.#inheritExpression.setValue(true);

    //we are going to override the catalog object list with original value
    //we want to restore the catalog object list when the button is unpressed
    //so store the catalog object list before copying the original catalog object List
    this.#referencesExpression.loadValue((): void => {
      this.#references = this.#referencesExpression.getValue() || [];

    });

    //set the catalog object list to the origin catalog object list directly
    //before the value of the originReferencesExpression is loaded to a non-undefined value
    this.#referencesExpression.loadValue((): void => {
      this.#referencesExpression.setValue(this.#originReferencesExpression.getValue() || []);
    });

    this.#originReferencesExpression.loadValue((): void => {
      //check if we are in inherit mode.
      //when this asynchronous callback is called the inherit could be set to false before.
      if (this.#inheritExpression.getValue()) {
        this.#referencesExpression.setValue(this.#originReferencesExpression.getValue());
      }
    });
  }

  protected override calculateDisabled(): boolean {
    if (this.#forceReadOnlyValueExpression && this.#forceReadOnlyValueExpression.getValue()) {
      return true;
    }
    const formContent: Content = this.#bindTo.getValue();
    if (formContent === undefined) {
      return undefined;
    }
    const readOnly = PropertyEditorUtil.isReadOnly(formContent);
    if (readOnly !== false) {
      return readOnly;
    }

    return false;
  }

  protected override calculatePressed(): boolean {
    return ! !this.#inheritExpression.getValue();
  }
}

export default InheritReferencesActionBase;
