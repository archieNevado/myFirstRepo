import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ContentLocalizationUtilInternal from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtilInternal";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ValidationState from "@coremedia/studio-client.ext.ui-components/mixins/ValidationState";
import ValidationStateMixin from "@coremedia/studio-client.ext.ui-components/mixins/ValidationStateMixin";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import Component from "@jangaroo/ext-ts/Component";
import StringUtil from "@jangaroo/ext-ts/String";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface PreviewPanelTimeZoneValidationPluginConfig extends Config<AbstractPlugin>, Partial<Pick<PreviewPanelTimeZoneValidationPlugin,
  "model" |
  "previewPanel"
>> {
}

class PreviewPanelTimeZoneValidationPlugin extends AbstractPlugin {
  declare Config: PreviewPanelTimeZoneValidationPluginConfig;

  /**
   * The model for the date, time and timezone
   */
  model: Bean = null;

  previewPanel: PreviewPanel = null;

  #timeZoneIdValueExpression: ValueExpression = null;

  #validationStateMixin: ValidationStateMixin = null;

  #warningValueExpression: ValueExpression = null;

  constructor(config: Config<PreviewPanelTimeZoneValidationPlugin> = null) {
    super(config);
    this.model = config.model;
    this.model.addPropertyChangeListener("timeZone", bind(this, this.fillWarningValueExpression));

    this.previewPanel.addListener("beforedestroy", (): void => {
      this.model.removePropertyChangeListener("timeZone", bind(this, this.fillWarningValueExpression));
      this.#getWarningValueExpression().removeChangeListener(bind(this, this.#applyWarning));
    });
  }

  override init(host: Component): void {
    super.init(host);
    this.#validationStateMixin = as(host, ValidationStateMixin);
    if (this.#validationStateMixin) {
      this.#getWarningValueExpression().addChangeListener(bind(this, this.#applyWarning));
    }
    this.fillWarningValueExpression();
  }

  #getTimeZoneIdValueExpression(): ValueExpression {
    if (!this.#timeZoneIdValueExpression) {
      this.#timeZoneIdValueExpression = ValueExpressionFactory.createFromFunction((): string => {
        let text: string;
        const entityExpression = this.previewPanel.getCurrentPreviewContentValueExpression();
        let storeExpression: ValueExpression;
        if (is(entityExpression.getValue(), Content)) {
          storeExpression = CatalogHelper.getInstance().getStoreForContentExpression(WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION);
        } else if (is(entityExpression.getValue(), CatalogObject)) {
          storeExpression = entityExpression.extendBy(CatalogObjectPropertyNames.STORE);
        }

        if (storeExpression && storeExpression.getValue()) {
          const timeZoneId = cast(Store, storeExpression.getValue()).getTimeZoneId();
          if (timeZoneId) {
            text = timeZoneId;
          }
        }

        return text;
      });
    }
    return this.#timeZoneIdValueExpression;
  }

  #getWarningValueExpression(): ValueExpression {
    if (!this.#warningValueExpression) {
      this.#warningValueExpression = ValueExpressionFactory.createFromValue("");
    }
    return this.#warningValueExpression;
  }

  fillWarningValueExpression(): void {
    const commerceTimeZoneId: string = this.#getTimeZoneIdValueExpression().getValue();
    if (commerceTimeZoneId) {
      const timeZoneId: string = this.model.get("timeZone");
      if (timeZoneId !== commerceTimeZoneId) {
        this.#getWarningValueExpression().setValue(StringUtil.format(LivecontextStudioPlugin_properties.Preview_Wcs_Timezone_Divergation_Warning_Message,
          ContentLocalizationUtilInternal.localizeTimeZoneID(commerceTimeZoneId)));
      } else {
        this.#getWarningValueExpression().setValue(null);
      }

    }

  }

  #applyWarning(): void {
    const warningMessage: string = this.#warningValueExpression.getValue();
    if (warningMessage) {
      this.#validationStateMixin.validationState = ValidationState.WARNING;
      this.#validationStateMixin.validationMessage = warningMessage;
    } else {
      this.#validationStateMixin.validationMessage = null;
      this.#validationStateMixin.validationState = null;
    }

  }
}

export default PreviewPanelTimeZoneValidationPlugin;
