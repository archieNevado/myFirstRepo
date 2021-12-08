import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Premular from "@coremedia/studio-client.main.editor-components/sdk/premular/Premular";
import Base from "@jangaroo/ext-ts/Base";
import Component from "@jangaroo/ext-ts/Component";
import Plugin from "@jangaroo/ext-ts/Plugin";
import { as, bind, is, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyChangePlugin from "./TaxonomyChangePlugin";

interface TaxonomyChangePluginBaseConfig extends Config<Base>, Partial<Pick<TaxonomyChangePluginBase,
  "properties"
>> {
}

class TaxonomyChangePluginBase extends Base implements Plugin {
  declare Config: TaxonomyChangePluginBaseConfig;

  properties: string = null;

  #content: Content = null;

  #updateQueue: number = 0;

  #propertyList: Array<any> = null;

  #listeners: Array<any> = [];

  constructor(config: Config<TaxonomyChangePlugin> = null) {
    super();
    if (config.properties) {
      this.#propertyList = config.properties.split(",");
    }
  }

  init(component: Component): void {
    const form = as(component, DocumentTabPanel);

    //do not apply this plugin when opened as regular premular
    const parent: Component = form.findParentByType(Premular.xtype);
    if (parent) {
      return;
    }

    form.bindTo.addChangeListener(bind(this, this.#contentChanged));
    this.#content = form.bindTo.getValue();

    this.#content.load(bind(this, this.#addListeners));
  }

  #addListeners(): void {
    if (this.#propertyList) {
      for (var p of this.#propertyList as string[]) {
        if (p.indexOf(".") !== -1) {
          /*
           * Returns a ValueExpression that keeps a String representation
           * of the annotated link list. This is just a convenient way to
           * allow the ValueExpression to detect any changes in the link list.
           */
          const structListAsStringVE = ValueExpressionFactory.createFromFunction((): string => {
            const structValue = ValueExpressionFactory.create("properties." + p, this.#content).getValue();
            if (!structValue) {
              return undefined;
            }

            let listAsString = "";

            if (is(structValue, Array)) {
              structValue.forEach((listEntry: Struct): void => {
                listAsString = listAsString + listEntry.toJson();
              });
            } else {
              listAsString = "" + structValue;
            }

            return listAsString;
          });

          structListAsStringVE.addChangeListener(bind(this, this.#propertiesChanged));
          this.#listeners.push(structListAsStringVE);
        } else {
          const ve = ValueExpressionFactory.create("properties." + p, this.#content);
          ve.addChangeListener(bind(this, this.#propertiesChanged));
          this.#listeners.push(ve);
        }
      }
    } else {
      this.#content.addValueChangeListener(bind(this, this.#lazyChange));
    }
  }

  #removeListeners(): void {
    if (this.#propertyList) {
      for (const ve of this.#listeners as ValueExpression[]) {
        ve.removeChangeListener(bind(this, this.#propertiesChanged));
      }
    } else {
      this.#content && this.#content.removeValueChangeListener(bind(this, this.#lazyChange));
    }
  }

  #contentChanged(ve: ValueExpression): void {
    this.#content && this.#removeListeners();
    this.#content = ve.getValue();
    this.#content && this.#content.load(bind(this, this.#addListeners));
  }

  #propertiesChanged(ve: ValueExpression): void {
    this.#checkUpdate();
  }

  #lazyChange(event: PropertyChangeEvent = null): void {
    //apply only property change events
    if (event && event.property.indexOf("properties\n") !== 0) {
      return;
    }

    this.#checkUpdate();
  }

  #checkUpdate(): void {
    if (this.#updateQueue === 0) {
      this.#updateQueue++;
      window.setTimeout(bind(this, this.#commitNode), 1000);
    } else {
      this.#updateQueue++;
    }
  }

  #commitNode(): void {
    if (this.#updateQueue === 1) {
      const node = TaxonomyUtil.getLatestSelection();
      const content = session._.getConnection().getContentRepository().getContent(node.getRef());
      content.invalidate((): void =>
        node.commitNode((): void =>
          content.invalidate((): void =>
            this.#finishedUpdate(),
          ),
        ),
      );
    } else {
      this.#finishedUpdate();
    }
  }

  #finishedUpdate(): void {
    this.#updateQueue--;

    if (this.#updateQueue > 0) {
      this.#updateQueue = 0;
      this.#lazyChange();
    }
  }

  #onTabDestroy(): void {
    this.#content && this.#content.removeValueChangeListener(bind(this, this.#lazyChange));
  }
}
mixin(TaxonomyChangePluginBase, Plugin);

export default TaxonomyChangePluginBase;
