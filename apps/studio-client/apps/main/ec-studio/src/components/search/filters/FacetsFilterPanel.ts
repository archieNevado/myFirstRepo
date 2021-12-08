import EmptyContainer from "@coremedia/studio-client.ext.ui-components/components/EmptyContainer";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import Component from "@jangaroo/ext-ts/Component";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import FacetFilterFieldWrapper from "./FacetFilterFieldWrapper";
import FacetsChooser from "./FacetsChooser";
import FacetsFilterPanelBase from "./FacetsFilterPanelBase";

interface FacetsFilterPanelConfig extends Config<FacetsFilterPanelBase> {
}

class FacetsFilterPanel extends FacetsFilterPanelBase {
  declare Config: FacetsFilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.components.search.filters.facetsFilterPanel";

  static readonly FACETS_CHOOSER_ITEM_ID: string = "facetsChooser";

  static readonly FILTER_FACETS_ITEM_ID: string = "filterFacets";

  static readonly EMPTY_ITEM_ID: string = "emptyFilter";

  static readonly DISABLED_ITEM_ID: string = "disabledFilter";

  constructor(config: Config<FacetsFilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(FacetsFilterPanel, {
      itemId: config.filterId,

      items: [
        Config(FacetsChooser, {
          facetsExpression: this.getFacetsExpression(),
          itemId: FacetsFilterPanel.FACETS_CHOOSER_ITEM_ID,
          selectedFacetsExpression: this.getSelectedFacetsExpression(),
        }),

        Config(Component, { height: 12 }),

        Config(SwitchingContainer, {
          activeItemValueExpression: this.getActiveStateExpression(),
          items: [

            Config(EmptyContainer, {
              iconElementName: "logo-box",
              bemBlockName: "cm-filter-panel",
              ui: ContainerSkin.GRID_100.getSkin(),
              itemId: FacetsFilterPanel.EMPTY_ITEM_ID,
              title: ECommerceStudioPlugin_properties.CollectionView_search_filter_empty_title,
              text: ECommerceStudioPlugin_properties.CollectionView_search_filter_empty_text,
            }),

            Config(EmptyContainer, {
              iconElementName: "logo-box",
              bemBlockName: "cm-filter-panel",
              ui: ContainerSkin.GRID_100.getSkin(),
              itemId: FacetsFilterPanel.DISABLED_ITEM_ID,
              title: ECommerceStudioPlugin_properties.CollectionView_search_no_filter_empty_title,
              text: ECommerceStudioPlugin_properties.CollectionView_search_no_filter_empty_text,
            }),

            Config(Container, {
              itemId: FacetsFilterPanel.FILTER_FACETS_ITEM_ID,
              items: [
                Config(Container, {
                  items: [
                    Config(Button, {
                      ui: ButtonSkin.LINK.getSkin(),
                      itemId: "resetAllFilters",
                      text: ECommerceStudioPlugin_properties.CollectionView_search_filter_resetAll_text,
                      handler: bind(this, this.resetAllFilters),
                      ...ConfigUtils.append({
                        plugins: [
                          Config(BindPropertyPlugin, {
                            componentProperty: "hidden",
                            bindTo: this.getSelectedFacetsExpression(),
                            transformer: bind(this, this.emptyTransformer),
                          }),
                        ],
                      }),
                    }),
                  ],
                  layout: Config(HBoxLayout, {
                    align: "stretch",
                    pack: "end",
                  }),
                }),
                Config(Container, {
                  itemId: "facetsContainer",
                  plugins: [
                    Config(BindComponentsPlugin, {
                      valueExpression: this.getSelectedFacetsExpression(),
                      configBeanParameterName: "facet",
                      reuseComponents: true,
                      clearBeforeUpdate: false,
                      template: Config(FacetFilterFieldWrapper, {
                        removeHandler: bind(this, this.removeFromSelection),
                        stateBean: this.getStateBean(),
                      }),
                    }),
                  ],
                  layout: Config(VBoxLayout, { align: "stretch" }),
                }),
              ],
              layout: Config(VBoxLayout, { align: "stretch" }),
            }),

          ],
        }),

      ],
      layout: Config(VBoxLayout, { align: "stretch" }),

    }), config))());
  }
}

export default FacetsFilterPanel;
