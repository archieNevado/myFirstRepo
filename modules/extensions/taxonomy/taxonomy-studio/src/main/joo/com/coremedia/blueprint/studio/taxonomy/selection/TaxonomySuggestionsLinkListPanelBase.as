package com.coremedia.blueprint.studio.taxonomy.selection {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.LoadMask;
import ext.grid.GridPanel;

/**
 *
 */
[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class TaxonomySuggestionsLinkListPanelBase extends GridPanel {

  private var suggestionsExpression:ValueExpression;
  private var bindTo:ValueExpression;

  private var propertyValueExpression:ValueExpression;
  private var taxonomyId:String;

  private var loadMask:LoadMask;
  private var cache:TaxonomyCache;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  public function TaxonomySuggestionsLinkListPanelBase(config:TaxonomySuggestionsLinkListPanel = null) {
    super(config);

    if (!config.disableSuggestions) {
      bindTo = config.bindTo;

      propertyValueExpression = ValueExpressionFactory.create('properties.' + config.propertyName, bindTo.getValue());
      propertyValueExpression.addChangeListener(propertyChanged);
      taxonomyId = config.taxonomyId;

      cache = new TaxonomyCache(bindTo.getValue() as Content, propertyValueExpression, taxonomyId);
    }
  }

  override protected function afterRender():void {
    super.afterRender();

    var loadMaskCfg:LoadMask = LoadMask({
      target: this
    });
    loadMaskCfg.msg = resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomySuggestions_loading');
    loadMask = new LoadMask(loadMaskCfg);
    loadMask.disable();

    updateSuggestions(true);
  }

  /**
   * Fired when the taxonomy property of the content has been changed.
   * We use this event to refresh (not reload) the taxonomy list.
   */
  private function propertyChanged():void {
    updateSuggestions(false);
  }

  protected function getSuggestionsExpression():ValueExpression {
    if (!suggestionsExpression) {
      suggestionsExpression = ValueExpressionFactory.create('hits', beanFactory.createLocalBean());
    }
    return suggestionsExpression;
  }

  //noinspection JSMethodCanBeStatic
  protected function formatUnreadableName(record:BeanRecord):String {
    var content:Content = record.getBean() as Content;
    return ContentLocalizationUtil.formatNotReadableName(content);
  }

  /**
   * Loads the values into the list.
   */
  private function updateSuggestions(reload:Boolean = false):void {
    setBusy(true);

    var callback:Function = function (list:TaxonomyNodeList):void {
      if (list) {
        convertResultToContentList(list);
      }
    };

    if (reload) {
      cache.invalidate(callback);
    }
    else {
      cache.loadSuggestions(callback);
    }
  }

  /**
   * Updates the empty list label so that loading is indicated.
   * @param busy
   */
  public function setBusy(busy:Boolean = false):void {
    if (busy) {
      if(loadMask) {
        loadMask.show();
      }
    } else {
      loadMask.hide();
    }
  }

  private function convertResultToContentList(list:TaxonomyNodeList):void {
    var items:Array = list.getNodes();
    var contents:Array = [];
    var callbackCount:int = items.length;
    for (var i:int = 0; i < items.length; i++) {
      var item:TaxonomyNode = items[i];
      var child:Content = beanFactory.getRemoteBean(item.getRef()) as Content;
      child.load(function (bean:Content):void {
        contents.push(bean);
        callbackCount--;
        if (callbackCount === 0) {
          getSuggestionsExpression().setValue(contents);

          //force re-rendering of records (e.g. if suggestion evaluation type has been changed)
          for(var i:int = 0; i<getStore().getCount(); i++) {
            var record:BeanRecord = getStore().getAt(i) as BeanRecord;
            record.data.html = null;
            record.commit();
          }
          setBusy(false);
        }
      });
    }
    if (items.length === 0) {
      getSuggestionsExpression().setValue([]);
      setBusy(false);
    }
  }

  /**
   * Adds all items of the list to the keyword list.
   */
  public function addAllKeywordsHandler():void {
    var suggestions:Array = getSuggestionsExpression().getValue();
    var existingEntries:Array = propertyValueExpression.getValue();
    var newEntries:Array = [];
    for (var i:int = 0; i < existingEntries.length; i++) {
      newEntries.push(existingEntries[i]);
    }
    for (var j:int = 0; j < suggestions.length; j++) {
      newEntries.push(suggestions[j]);
    }
    propertyValueExpression.setValue(newEntries);
    updateSuggestions(false);
  }

  /**
   * Trigger a new evaluation of the content for suggestions.
   */
  protected function reloadKeywordsHandler():void {
    updateSuggestions(true);
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    var siteId:String = editorContext.getSitesService().getSiteIdFor(bindTo.getValue());
    TaxonomyUtil.loadTaxonomyPath(record, bindTo.getValue(), taxonomyId, function (updatedRecord:BeanRecord):void {
      var content:Content = record.getBean() as Content;
      var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSuggestionsRenderer(record.data.nodes, getId(), cache.getWeight(content.getId()));
      renderer.doRender(function (html:String):void {
        if (record.data.html !== html) {
          record.data.html = html;
          record.commit(false);
        }
      });
    });
    if (!record.data.html) {
      return "<div class='loading'>" + resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyLinkList_status_loading_text') + "</div>";
    }
    return record.data.html;
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Removes the given taxonomy
   */
  public function plusMinusClicked(nodeRef:String):void {
    TaxonomyUtil.removeNodeFromSelection(propertyValueExpression, nodeRef);
    TaxonomyUtil.addNodeToSelection(propertyValueExpression, nodeRef);
  }

  override protected function onDestroy():void {
    if (propertyValueExpression) {
      propertyValueExpression.removeChangeListener(propertyChanged);
    }
    super.onDestroy();
  }
}
}
