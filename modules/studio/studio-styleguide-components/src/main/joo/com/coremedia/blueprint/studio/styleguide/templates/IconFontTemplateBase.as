package com.coremedia.blueprint.studio.styleguide.templates {
import com.coremedia.blueprint.studio.styleguide.tabs.icons.IconInformation;
import com.coremedia.blueprint.studio.styleguide.tabs.icons.IconsPanel;
import com.coremedia.blueprint.studio.styleguide.tabs.icons.InformationArea;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;
import com.coremedia.ui.util.IconUtils;

import ext.Ext;
import ext.XTemplate;
import ext.event.Event;
import ext.form.field.TextField;
import ext.panel.Panel;
import ext.view.DataView;

import js.HTMLElement;

public class IconFontTemplateBase extends Panel {

  private var activeVE:ValueExpression;
  private var config:IconFontTemplate;

  public var getInformationPanel:Function;

  public function IconFontTemplateBase(config:IconFontTemplate = null) {
    super(config);
    this.config = config;
  }

  override protected function afterRender():void {
    super.afterRender();
    addShowDocumentationListener(IconFontTemplate.ICONS_SMALL_VIEW_ITEM_ID);
    addShowDocumentationListener(IconFontTemplate.ICONS_MEDIUM_VIEW_ITEM_ID);
    addShowDocumentationListener(IconFontTemplate.ICONS_LARGE_VIEW_ITEM_ID);
    addSearchTextfieldListener();
  }

  private function addShowDocumentationListener(itemId:String):void {
    var view:DataView = queryById(itemId) as DataView;
    if (view) {
      if (view.getEl()) {
        attachClickHandler(view);
      }
      else {
        mon(view, "afterrender", function (oo:Object):void {
          attachClickHandler(view);
        });
      }
    }
  }

  private function attachClickHandler(view:DataView):void {
    mon(view.el, "click", function (event:Event):void {
      var item:HTMLElement = event.getTarget(view['itemSelector']) as HTMLElement;
      var iconType:String;
      if (item) {
        var icon_id:Array = Ext.get(item).query('.sg-icon__id');
        var icon_module:Array = Ext.get(item).query('.sg-icon__iconSetName');
        if (icon_id && icon_id.length === 1) {

          var informationPanel:Panel = getInformationPanel();
          informationPanel.expand(true);

          var informationArea:SwitchingContainer = informationPanel.queryById(IconsPanel.INFORMATION_AREA_ITEM_ID) as SwitchingContainer;
          informationArea.activeItemValueExpression.setValue(InformationArea.ICON_INFORMATION_ITEM_ID);

          iconType = icon_module[0].innerText;
          var iconInformation:IconInformation = informationArea.queryById(InformationArea.ICON_INFORMATION_ITEM_ID) as IconInformation;
          iconInformation.getMxmlCodeVE().setValue('iconCls="{' + iconType + '_properties.INSTANCE.' + icon_id[0].innerText + '}"');
          iconInformation.setDisabled(false);
        }
      }
    });
  }

  private function addSearchTextfieldListener():void {
    var searchField:TextField = queryById(IconFontTemplate.SEARCH_FIELD_ITEM_ID) as TextField;
    if (searchField) {
      mon(searchField, 'change', function (textfield:TextField, newValue:String, oldValue:String):void {
        var viewS:DataView = queryById(IconFontTemplate.ICONS_SMALL_VIEW_ITEM_ID) as DataView;
        var viewM:DataView = queryById(IconFontTemplate.ICONS_MEDIUM_VIEW_ITEM_ID) as DataView;
        var viewL:DataView = queryById(IconFontTemplate.ICONS_LARGE_VIEW_ITEM_ID) as DataView;
        if (newValue) {
          viewS.getStore().filter('id', newValue);
          viewM.getStore().filter('id', newValue);
          viewL.getStore().filter('id', newValue);
        }
        else {
          viewS.getStore().clearFilter();
          viewM.getStore().clearFilter();
          viewL.getStore().clearFilter();
        }
      });
    }
  }

  protected static function getIconData(icons:Object, scale:String, iconSetName:String):Object {
    var store:Object = {
      fields: ['id', 'iconModule', 'iconScale', 'iconSetName'],
      data: []
    };
    for (var key:String in icons) {
      if (icons[key] is String) {
        var iconClsWithoutScale:String = icons[key];
        var iconScaleCls:String = IconUtils.calculateIconScaleCls(iconClsWithoutScale, scale);
        if (iconScaleCls) {
          store.data.push({
            id: key,
            iconCls: iconClsWithoutScale + " " + iconScaleCls,
            iconScale: scale,
            iconSetName: iconSetName
          });
        }
      }
    }
    return store;
  }

  protected static function getIconTemplate(selector:String):XTemplate {
    selector = selector.replace('.', '');
    return new XTemplate(
            '<div class="sg-icon-grid">',
            ' <tpl for=".">',
            '   <span>',
            '     <div class="' + selector + ' sg-icon sg-icon--{iconScale}">',
            '       <div class="sg-icon__icon {iconCls}"></div>',
            '       <div class="sg-icon__id">{id}</div>',
            '       <div class="sg-icon__iconSetName" style="display:none">{iconSetName}</div>',
            '     </div>',
            '   </span>',
            ' </tpl>',
            '</div>');
  }

  protected function getActiveScaleVE():ValueExpression {
    if (!activeVE) {
      BeanFactoryImpl.initBeanFactory();
      activeVE = ValueExpressionFactory.createFromValue(IconFontTemplate.ICONS_LARGE_VIEW_ITEM_ID);
    }
    return activeVE;
  }
}
}
