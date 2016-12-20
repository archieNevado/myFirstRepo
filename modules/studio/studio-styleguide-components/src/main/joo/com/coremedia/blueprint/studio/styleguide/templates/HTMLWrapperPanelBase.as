package com.coremedia.blueprint.studio.styleguide.templates {
import com.coremedia.ui.skins.PanelSkin;

import ext.Ajax;
import ext.Component;
import ext.Ext;
import ext.container.Container;
import ext.panel.Panel;

import js.Element;

public class HTMLWrapperPanelBase extends Container {

  private var config:HTMLWrapperPanelBase;

  public var path:String;
  public var renderAsType:String;

  public function HTMLWrapperPanelBase(config:HTMLWrapperPanelBase = null) {
    super(config);
    this.config = config;
  }

  override protected function initComponent():void {
    super.initComponent();
    if (path && path.length > 0) {
      Ajax.request({
        disableCaching: true,
        dataType: "text/html",
        url: Ext.getResourcePath("html/" + path + '.html', null, "com.coremedia.blueprint__studio-styleguide-components"),
        method: "GET",
        panel: this,
        success: function (response:Object, options:Object):void {
          if (!response) {
            window.console.warn('Response is null');
            return;
          }
          var wrapper:HtmlWrapperPanel = options.panel as HtmlWrapperPanel;
          var html:Element = createHTML(response);
          var object:Object;
          if (renderAsType === HtmlWrapperPanel.PANEL) {
            var panel:Panel = Panel({
              title: getContent(html, 'title'),
              html: getContent(html, 'body'),
              ui: PanelSkin.CORPORATE_IDENTITY.getSkin(),
              bodyCls: 'sg-documentation-text'
            });
            object = panel;
          }
          else if (renderAsType === HtmlWrapperPanel.PANEL_SKINS_DOCUMENTATION) {
            var documentation:Container = Container({
              items: [],
              padding: 10
            });
            createDocumentation(documentation.items, html, 'Description', 'sg-skin-category-documentation');
            createDocumentation(documentation.items, html, 'Usage', 'sg-skin-category-documentation');
            createDocumentation(documentation.items, html, 'Related', 'sg-skin-category-documentation');
            object = documentation;
          }
          else {
            object = response.responseText;
          }
          wrapper.add(object);
        }
      });
    }
  }

  private static function createDocumentation(items:Array, html:Element, label:String, css:String):void {
    var contentTag:String = label.toLowerCase();
    var content:String = getContent(html, contentTag);
    var contentTrimmed:String = getContent(html, contentTag).replace(/\s|\n|\r/g, "");

    if (contentTrimmed !== 'n/a') {
      items.push(Container({
        width: '100px',
        html: '<span class="' + css + '"><b>' + label + '</b></span>'
      }));
      items.push(createBox(content, css));
    }
  }

  private static function createBox(content:String, css:String):Component {
    return Component({
      html: '<span class="' + css + '">' + content + '</span>'
    });
  }

  private static function createHTML(response:Object):Element {
    var html:* = window.document.createElement("HTML");
    html.innerHTML = response.responseText;
    return html;
  }

  private static function getContent(html:Element, content:String, index:int = 0):String {
    var result:Array = Ext.get(html).query('div[data-content="' + content + '"]');
    if (result && result.length > index) {
      return result[index].innerHTML;
    }
    return '<i>No content found for data-content ' + content + '.<i/>';
  }
}
}
