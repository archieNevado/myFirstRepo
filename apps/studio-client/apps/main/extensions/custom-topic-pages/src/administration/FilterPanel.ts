import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import Component from "@jangaroo/ext-ts/Component";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPages_properties from "../TopicPages_properties";
import FilterPanelBase from "./FilterPanelBase";

interface FilterPanelConfig extends Config<FilterPanelBase>, Partial<Pick<FilterPanel,
  "emptyText"
>> {
}

class FilterPanel extends FilterPanelBase {
  declare Config: FilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.filterPanel";

  constructor(config: Config<FilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(FilterPanel, {
      layout: "hbox",

      items: [
        Config(TextField, {
          itemId: "filterTextField",
          flex: 1,
          enableKeyEvents: true,
          selectOnFocus: true,
          emptyText: config.emptyText,
          listeners: { specialkey: bind(this, this.applyFilterInput) },
          plugins: [
            Config(BindPropertyPlugin, {
              bindTo: config.filterExpression,
              bidirectional: true,
            }),
          ],
        }),
        Config(IconButton, {
          itemId: "startSearch",
          ui: ButtonSkin.SIMPLE.getSkin(),
          scale: "small",
          text: TopicPages_properties.TopicPages_search_search_tooltip,
          tooltip: TopicPages_properties.TopicPages_search_search_tooltip,
          iconCls: CoreIcons_properties.search,
          handler: config.applyFilterFunction,
        }),
        Config(Component, { width: 10 }),
      ],

    }), config))());
  }

  emptyText: string = null;
}

export default FilterPanel;
