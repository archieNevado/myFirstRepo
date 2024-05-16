import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatefulContainer from "@coremedia/studio-client.ext.ui-components/components/StatefulContainer";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import EsAnalyticsChartWidgetEditor from "./EsAnalyticsChartWidgetEditor";

interface EsAnalyticsChartWidgetEditorBaseConfig extends Config<StatefulContainer> {
}

class EsAnalyticsChartWidgetEditorBase extends StatefulContainer {
  declare Config: EsAnalyticsChartWidgetEditorBaseConfig;

  #rootChannelValueExpr: ValueExpression = null;

  constructor(config: Config<EsAnalyticsChartWidgetEditor> = null) {
    super(config);

    this.getRootChannelValueExpression().addChangeListener(bind(this, this.#rootChannelChanged));
  }

  protected override onDestroy(): void {
    this.getRootChannelValueExpression().removeChangeListener(bind(this, this.#rootChannelChanged));
  }

  protected getSelectedSiteExpression(): ValueExpression {
    return ValueExpressionFactory.create("content", this.getModel());
  }

  protected getRootChannelValueExpression(): ValueExpression {
    if (!this.#rootChannelValueExpr) {
      this.#rootChannelValueExpr = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const siteRootDocs = [];
        const sites = editorContext._.getSitesService().getSites();
        if (sites) {
          sites.forEach((site: Site): void => {
            siteRootDocs.push(site.getSiteRootDocument());
          });
        }
        return siteRootDocs;
      });
    }
    return this.#rootChannelValueExpr;
  }

  protected static getContentFromId(id: string): Content {
    return UndocContentUtil.getContent(id);
  }

  protected static getIdFromContent(content: Content): string {
    return content ? content.getId() : undefined;
  }

  #rootChannelChanged(): void {
    const comboBox = as(this.down("combo"), ComboBox);
    if (comboBox) {
      this.mon(comboBox.getStore(), "load", (): void =>
        this.#storeLoaded(comboBox),
      );
    }
  }

  #storeLoaded(comboBox: ComboBox): void {
    const value = this.getSelectedSiteExpression().getValue();
    const index: int = comboBox.getStore().find("id", value);
    if (index >= 0) {
      const beanRecord = as(comboBox.getStore().getAt(index), BeanRecord);
      if (beanRecord.data && beanRecord.data.value) {
        comboBox.setValue(value);
      } else {
        this.mon(comboBox.getStore(), "update", (): void =>
          EsAnalyticsChartWidgetEditorBase.#setComboBoxValue(comboBox, value),
        );
      }
    }
  }

  static #setComboBoxValue(comboBox: ComboBox, value: string | string[]): void {
    comboBox.setValue(value);
  }
}

export default EsAnalyticsChartWidgetEditorBase;
