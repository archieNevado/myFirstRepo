import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BEMMixin from "@coremedia/studio-client.ext.ui-components/plugins/BEMMixin";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import BadgePlugin from "@coremedia/studio-client.ext.ui-components/plugins/BadgePlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import PanelHeader from "@jangaroo/ext-ts/panel/Header";
import Tool from "@jangaroo/ext-ts/panel/Tool";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import FacetFilterFieldWrapperBase from "./FacetFilterFieldWrapperBase";
import FacetUtil from "./FacetUtil";

interface FacetFilterFieldWrapperConfig extends Config<FacetFilterFieldWrapperBase> {
}

/**
 * @public
 */
class FacetFilterFieldWrapper extends FacetFilterFieldWrapperBase {
  declare Config: FacetFilterFieldWrapperConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.components.search.filters.facetFilterFieldWrapper";

  constructor(config: Config<FacetFilterFieldWrapper> = null) {
    super((()=> ConfigUtils.apply(Config(FacetFilterFieldWrapper, {
      ui: PanelSkin.FILTER.getSkin(),
      style: "margin-bottom:12px;",
      ariaLabel: FacetUtil.localizeFacetLabel(config.facet.getLabel()),
      itemId: this.formatItemId(config.facet),

      items: [
        /* Facet Tag or Combo will be added here  */
      ],
      plugins: [
        Config(BEMPlugin, {
          block: FacetFilterFieldWrapperBase.BLOCK.getIdentifier(),
          bodyElement: FacetFilterFieldWrapperBase.ELEMENT_BODY,
          modifier: this.getModifierVE(),
        }),
      ],
      header: Config(PanelHeader, {
        itemPosition: 0,
        titlePosition: 2,
        ...Config<BEMMixin>({ bemElement: FacetFilterFieldWrapperBase.ELEMENT_HEADER }),
        plugins: [
        /* make sure that the reset button is added last, so it appears behind the title */
          Config(AddItemsPlugin, {
            items: [
              Config(Container, {
                items: [
                  Config(DisplayField, {
                    value: FacetUtil.localizeFacetLabel(config.facet.getLabel()),
                    ui: DisplayFieldSkin.BOLD.getSkin(),
                  }),
                  Config(Component, { width: 24 }),
                  Config(DisplayField, {
                    plugins: [
                      Config(BadgePlugin, {
                        bindTo: this.getSelectedFacetValuesExpression(config),
                        maxValue: 99,
                        x: 2,
                        y: 3,
                      }),
                    ],
                  }),
                  Config(Component, { width: 12 }),
                ],
                layout: Config(HBoxLayout),
              }),
              Config(Component, { flex: 1 }),
              Config(Tool, {
                itemId: "resetButton",
                ui: ButtonSkin.SIMPLE.getSkin(),
                handler: bind(this, this.removeFilter),
                tooltip: Editor_properties.Filter_remove_btn_text,
                type: "close",
                ...Config<BEMMixin>({ bemElement: FacetFilterFieldWrapperBase.ELEMENT_REMOVE }),
              }),
            ],
          }),
        ],
      }),
    }), config))());
  }
}

export default FacetFilterFieldWrapper;
