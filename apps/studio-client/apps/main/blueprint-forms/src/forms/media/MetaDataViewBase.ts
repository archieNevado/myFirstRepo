import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import MetaDataSection from "./MetaDataSection";
import MetaDataView from "./MetaDataView";

interface MetaDataViewBaseConfig extends Config<Container> {
}

class MetaDataViewBase extends Container {
  declare Config: MetaDataViewBaseConfig;

  static readonly PROPERTIES_BLOCK: BEMBlock = new BEMBlock("meta-data-view");

  static readonly PROPERTIES_ELEMENT_LABEL: BEMElement = MetaDataViewBase.PROPERTIES_BLOCK.createElement("label");

  static readonly PROPERTIES_ELEMENT_TEXT: BEMElement = MetaDataViewBase.PROPERTIES_BLOCK.createElement("text");

  #metaDataExpression: ValueExpression = null;

  constructor(config: Config<MetaDataView> = null) {
    super(config);
  }

  getMetaDataExpression(metaDataSection: MetaDataSection): ValueExpression {
    if (!this.#metaDataExpression) {
      this.#metaDataExpression = ValueExpressionFactory.createFromValue(metaDataSection.getData());
    }
    return this.#metaDataExpression;
  }

  protected static getXTemplate(): XTemplate {
    const xTemplate = new XTemplate([
      "<tpl for=\".\">",
      "<div class=\"" + MetaDataViewBase.PROPERTIES_BLOCK + "\">",
      "<div class=\"" + MetaDataViewBase.PROPERTIES_ELEMENT_LABEL + "\">",
      "{property}:",
      "</div>",
      "<div data-qtip=\"{value:htmlEncode}\" class=\"" + MetaDataViewBase.PROPERTIES_ELEMENT_TEXT + "\">",
      "{formattedValue}",
      "</div>",
      "</div>",
      "</tpl>",
    ]);
    return xTemplate;
  }
}

export default MetaDataViewBase;
