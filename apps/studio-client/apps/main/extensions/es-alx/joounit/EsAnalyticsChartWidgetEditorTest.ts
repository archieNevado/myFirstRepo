import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentRepository from "@coremedia/studio-client.cap-rest-client/content/ContentRepository";
import Locale from "@coremedia/studio-client.client-core/data/Locale";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import SiteImpl from "@coremedia/studio-client.multi-site-models/SiteImpl";
import SitesService from "@coremedia/studio-client.multi-site-models/SitesService";
import Ext from "@jangaroo/ext-ts";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import TestCase from "@jangaroo/joounit/flexunit/framework/TestCase";
import { as, asConfig, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EsAnalyticsChartWidgetEditor from "../src/EsAnalyticsChartWidgetEditor";

class EsAnalyticsChartWidgetEditorTest extends TestCase {
  static readonly #FIELD_LABEL: string = "Site";

  static readonly #VALUE_FIELD: string = "id";

  static readonly #ID_1: string = "test-1";

  static readonly #NAME_1: string = "test-1-" + new Date();

  static readonly #ID_2: string = "test-2";

  static readonly #NAME_2: string = "test-2-" + new Date();

  #site1: SiteImpl = null;

  #site2: SiteImpl = null;

  override setUp(): void {
    super.setUp();
    EditorContextImpl.initEditorContext();
    this.#mockSites();
    (editorContext._ as unknown)["getSitesService"] = bind(this, this.#getSitesService);
  }

  testWidgetEditMode(): void {

    // create widget instance rendered to document body
    // rendering is necessary because bindPlugins load stores only when component is rendered
    const editorCfg = Config(EsAnalyticsChartWidgetEditor);
    editorCfg.renderTo = Ext.getBody();
    const esAlxWidgetEditor = as(ComponentManager.create(editorCfg), EsAnalyticsChartWidgetEditor);

    // the combobox provides the selection of root channels aka Sites
    const combo = as(esAlxWidgetEditor.down(createComponentSelector()._xtype(LocalComboBox.xtype).build()), ComboBox);
    const comboFieldLabel = asConfig(combo).fieldLabel;
    const comboStore = combo.getStore();

    // assertion of the ALX widget editor, combobox and fields
    Assert.assertNotUndefined(esAlxWidgetEditor);
    Assert.assertNotUndefined(combo);
    Assert.assertNotUndefined(comboFieldLabel);
    Assert.assertEquals(EsAnalyticsChartWidgetEditorTest.#FIELD_LABEL, comboFieldLabel);
    Assert.assertEquals(EsAnalyticsChartWidgetEditorTest.#VALUE_FIELD, combo.valueField);

    // assertion of the combobox store filled by the local bean properties
    Assert.assertNotUndefined(comboStore);
    Assert.assertEquals(2, comboStore.getCount());
    Assert.assertEquals(0, comboStore.find("id", EsAnalyticsChartWidgetEditorTest.#ID_1));
    Assert.assertEquals(0, comboStore.find("value", EsAnalyticsChartWidgetEditorTest.#NAME_1));
    Assert.assertEquals(1, comboStore.find("id", EsAnalyticsChartWidgetEditorTest.#ID_2));
    Assert.assertEquals(1, comboStore.find("value", EsAnalyticsChartWidgetEditorTest.#NAME_2));

  }

  #getSitesService(): SitesService {
    return Object.setPrototypeOf({
      "getSites": (): Array<any> =>
        [this.#site1, this.#site2],

    }, mixin(class {}, SitesService).prototype);
  }

  #mockSites(): void {
    const contentRepository = as(beanFactory._.getRemoteBean("content"), ContentRepository);
    this.#site1 = new SiteImpl(EsAnalyticsChartWidgetEditorTest.#ID_1, null, null, null, EsAnalyticsChartWidgetEditorTest.#mockContent({
      id: EsAnalyticsChartWidgetEditorTest.#ID_1,
      name: EsAnalyticsChartWidgetEditorTest.#NAME_1,
    }), EsAnalyticsChartWidgetEditorTest.#NAME_1, new Locale({ "displayName": "locale1" }), null, true, false);
    this.#site2 = new SiteImpl(EsAnalyticsChartWidgetEditorTest.#ID_2, null, null, null, EsAnalyticsChartWidgetEditorTest.#mockContent({
      id: EsAnalyticsChartWidgetEditorTest.#ID_2,
      name: EsAnalyticsChartWidgetEditorTest.#NAME_2,
    }), EsAnalyticsChartWidgetEditorTest.#NAME_2, new Locale({ "displayName": "locale2" }), null, true, false);
  }

  static #mockContent(props: any): Content {
    return Object.assign(Object.setPrototypeOf({
      addPropertyChangeListener: Ext.emptyFn,
      "get": function(prop: string): any {
        return this[prop];
      },
      getUriPath: function(): string {
        return this.id;
      },
    }, mixin(class {}, Content).prototype), props);
  }
}

export default EsAnalyticsChartWidgetEditorTest;
