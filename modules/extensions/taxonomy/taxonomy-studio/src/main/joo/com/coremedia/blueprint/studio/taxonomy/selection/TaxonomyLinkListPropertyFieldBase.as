package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.form.FieldContainer;

[ResourceBundle('com.coremedia.cms.editor.Editor')]
[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class TaxonomyLinkListPropertyFieldBase extends FieldContainer {
  protected static const TAXONOMY_SEARCH_FIELD_ITEM_ID:String = "taxonomySearchField";
  protected static const OPEN_TAXONOMY_CHOOSER_BUTTON_ITEM_ID:String = "openTaxonomyChooserButton";

  /**
   * the id of the taxonomy whose tree is used to add items from.
   */
  [Bindable]
  public var taxonomyId:String;

  [Bindable]
  public var bindTo:ValueExpression;

  /**
   * The property name that is edited
   */
  [Bindable]
  public var propertyName:String;


  /**
   * Optional. The maximum cardinality that the link list property should hold.
   If not specified the maximum cardinality of the property descriptor of the link list property will be applied.
   */
  [Bindable]
  public var maxCardinality:int;

  /**
   * The allowed type of links is usually derived from the link property descriptor found through bindTo and propertyName,
   * but to override this or provide an initial value for link properties in Structs that are created by this
   * property field, you may specify a custom link type.
   */
  [Bindable]
  public var linkType:String;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  [Bindable]
  public var forceReadOnlyValueExpression:ValueExpression;

  private var searchResultExpression:ValueExpression;
  private var siteSelectionExpression:ValueExpression;

  private var searchField:TaxonomySearchField;
  private var selectedValuesVE:ValueExpression;
  private var selectedPositionsVE:ValueExpression;

  public function TaxonomyLinkListPropertyFieldBase(config:TaxonomyLinkListPropertyFieldBase = null) {
    super(config);

    searchField = queryById(TAXONOMY_SEARCH_FIELD_ITEM_ID) as TaxonomySearchField;

    getSearchResultExpression().addChangeListener(searchResultChanged);
  }

  /**
   * Returns the value expression for the site the taxonomy link list is working on.
   * The site is calculated from the content path. By setting a value, the REST backend looks up
   * if there is a site depending taxonomy for the given taxonomyId, otherwise the global taxonomy is used.
   * @return
   */
  protected function getSiteSelectionExpression(bindTo:ValueExpression):ValueExpression {
    if (!siteSelectionExpression) {
      siteSelectionExpression = ValueExpressionFactory.create('site', beanFactory.createLocalBean());
      var content:Content = bindTo.getValue() as Content;
      var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
      if (content && !content.getPath()) {
        ValueExpressionFactory.create('path', content).loadValue(function ():void {
          siteSelectionExpression.setValue(siteId);
        });
      }
      else {
        siteSelectionExpression.setValue(siteId);
      }
    }
    return siteSelectionExpression;
  }

  /**
   * Fired when the field is used inside a property editor
   * and the user has selected an entry that should be added
   * to the taxonomy link list.
   */
  private function searchResultChanged():void {
    var selection:TaxonomyNodeList = searchResultExpression.getValue();
    if (selection) {
      var content:Content = SESSION.getConnection().getContentRepository().getContent(selection.getLeafRef());
      //do create the property expression here! see BARBUDA-1805
      var propertyValueExpression:ValueExpression = ValueExpressionFactory.create('properties.' + propertyName, bindTo.getValue());
      var taxonomies:Array = propertyValueExpression.getValue();
      for (var i:int = 0; i < taxonomies.length; i++) {
        var child:Content = taxonomies[i];
        //check if node has already been added
        if (TaxonomyUtil.parseRestId(child) === selection.getLeafRef()) {
          return;
        }
      }
      var newTaxonomies:Array = [];
      for (var j:int = 0; j < taxonomies.length; j++) {
        newTaxonomies.push(taxonomies[j]);
      }
      newTaxonomies.push(content);
      propertyValueExpression.setValue(newTaxonomies);
      searchField.focus();
    }
  }

  protected function getSelectedValuesVE():ValueExpression {
    if (!selectedValuesVE) {
      selectedValuesVE = ValueExpressionFactory.createFromValue([]);
    }
    return selectedValuesVE;
  }

  protected function getSelectedPositionsVE():ValueExpression {
    if (!selectedPositionsVE) {
      selectedPositionsVE = ValueExpressionFactory.createFromValue([]);
    }
    return selectedPositionsVE;
  }

  protected function getSearchResultExpression():ValueExpression {
    if (!searchResultExpression) {
      searchResultExpression = ValueExpressionFactory.createFromValue([]);
    }
    return searchResultExpression;
  }

}
}
