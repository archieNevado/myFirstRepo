package com.coremedia.livecontext.studio.forms.facets {
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ecommerce.studio.components.search.filters.FacetUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ecommerce.studio.model.SearchFacets;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EventUtil;

import mx.resources.IResourceBundle;
import mx.resources.ResourceManager;

public class CategoryFacetsPropertyFieldBase extends PropertyFieldGroup {
  protected static const NEW_EDITOR_ITEM_ID:String = "cmProductListFacetsFieldGroup";
  protected static const OLD_EDITOR_ITEM_ID:String = "legacyCmProductListFacetsFieldGroup";
  protected static const NO_CATEGORY_MSG_ITEM_ID:String = "noCategoryMessage";

  public static var PRODUCT_LIST_STRUCT_NAME:String = "productList";

  public static var SINGLE_FACET_NAME:String = "selectedFacet";
  public static var SINGLE_FACET_VALUE:String = "selectedFacetValue";

  public static var MULTI_FACETS_STRUCT_NAME:String = "filterFacets";
  public static var MULTI_FACETS_QUERIES_STRUCT_NAME:String = 'queries';

  [ExtConfig]
  public var externalIdPropertyName:String;

  [ExtConfig]
  public var structPropertyName:String;

  private var activeEditorExpression:ValueExpression;
  private var multiFacetEnabledExpression:ValueExpression;

  private var searchFacetsExpression:ValueExpression;
  private var facetsExpression:ValueExpression;
  private var legacyFacetsExpression:ValueExpression;

  private var autofixExpression:ValueExpression;

  public function CategoryFacetsPropertyFieldBase(config:CategoryFacetsPropertyField = null) {
    super(config);
  }

  /**
   * Calculates the new multi facet values out of the selected category.
   */
  protected function getSearchFacetsExpression(config:CategoryFacetsPropertyField = null):ValueExpression {
    if (!searchFacetsExpression) {
      searchFacetsExpression = ValueExpressionFactory.createFromFunction(function ():SearchFacets {
        var externalId:String = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        if (!externalId) {
          return null;
        }

        var category:Category = CatalogHelper.getInstance().getCatalogObject(externalId, bindTo) as Category;
        if (!category) {
          return null;
        }

        if (!category.isLoaded()) {
          category.load();
          return undefined;
        }

        if (!category.getSearchFacets().isLoaded()) {
          category.getSearchFacets().load();
          return undefined;
        }

        return category.getSearchFacets();
      });

      searchFacetsExpression.addChangeListener(searchFacetsChanged);
    }
    return searchFacetsExpression;
  }

  /**
   * This is a proxy VE, used to reset the list of facet input fields.
   * When a content is reverted, the editors must be destroyed in order to be re-initialized properly.
   * If we would work directly on the FunctionVE, the list would not necessarily change, and when switching
   * to multi-facet mode again, the previously selected multi facets would be visible again, although no persisted anymore.
   * @param config
   * @return
   */
  protected function getFacetsExpression(config:CategoryFacetsPropertyField = null):ValueExpression {
    if (!facetsExpression) {
      facetsExpression = ValueExpressionFactory.createFromValue([]);
    }
    return facetsExpression;
  }

  /**
   * Calculates the legacy facet values out of the selected category.
   */
  protected function getFacetsOrLegacyFacetsExpression(config:CategoryFacetsPropertyField = null):ValueExpression {
    if (!legacyFacetsExpression) {
      //wildcard return value since the value can be 'Facets' or 'SearchFacets'
      legacyFacetsExpression = ValueExpressionFactory.createFromFunction(function ():* {
        var externalId:String = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        if (!externalId) {
          return null;
        }

        var category:Category = CatalogHelper.getInstance().getCatalogObject(externalId, bindTo) as Category;
        if (!category) {
          return null;
        }

        if(!category.isLoaded()) {
          category.load();
          return undefined;
        }

        if (category.getFacets() === undefined) {
          category.getFacets().load();
          return undefined;
        }

        if (category.getFacets().getFacets() === undefined) {
          return undefined;
        }

        return category.getFacets();
      });
    }
    return legacyFacetsExpression;
  }

  /**
   * Determines if the single facet value or multi-facet editor should be active.
   * @param config
   * @return
   */
  protected function getActiveEditorExpression(config:CategoryFacetsPropertyField = null):ValueExpression {
    if (!activeEditorExpression) {
      activeEditorExpression = ValueExpressionFactory.createFromFunction(function ():String {
        var externalId:String = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        //show missing category hint
        if (!externalId) {
          return NO_CATEGORY_MSG_ITEM_ID;
        }

        //show new editor by default
        var multiFacetEnabled:Boolean = getMultiFacetEnabledExpression(config).getValue();
        if (multiFacetEnabled === undefined) {
          return NEW_EDITOR_ITEM_ID;
        }

        if (!multiFacetEnabled) {
          return OLD_EDITOR_ITEM_ID;
        }

        //show old editor if there are still legacy values persisted
        var legacyQueryValue:String = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, PRODUCT_LIST_STRUCT_NAME, SINGLE_FACET_VALUE).getValue();
        var legacyQueryName:String = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, PRODUCT_LIST_STRUCT_NAME, SINGLE_FACET_NAME).getValue();
        if (legacyQueryValue || legacyQueryName) {
          return OLD_EDITOR_ITEM_ID;
        }

        return NEW_EDITOR_ITEM_ID;
      });
    }
    return activeEditorExpression;
  }

  /**
   * The switch between the two editor is calculated through the persisted struct format.
   * So we simple add or remove the legacy values to select the new editor.
   */
  protected function toggleFacetMode():void {
    var itemId:String = getActiveEditorExpression().getValue();
    if (itemId === OLD_EDITOR_ITEM_ID) {
      migrateSingleToMultiValue();
    }
  }

  /**
   * Calculates if the multi-facet feature is enabled.
   */
  private function getMultiFacetEnabledExpression(config:CategoryFacetsPropertyField):ValueExpression {
    if (!multiFacetEnabledExpression) {
      multiFacetEnabledExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
        var searchFacets:SearchFacets = getSearchFacetsExpression(config).getValue();
        if (searchFacets === undefined) {
          return undefined;
        }

        return searchFacets !== null;
      });
    }
    return multiFacetEnabledExpression;
  }

  /**
   * Delegates the values of the actual facets VE to the editor facet list VE.
   */
  private function searchFacetsChanged(ve:ValueExpression):void {
    var searchFacet:SearchFacets = ve.getValue();
    if (searchFacet && searchFacet.getFacets()) {
      getFacetsExpression().setValue(searchFacet.getFacets());
      updateLabels(searchFacet.getFacets());
    } else {
      getFacetsExpression().setValue([]);
    }
  }

  /**
   * Writes the default label for facet fields into the matching resource bundle.
   * This is required to have a meaningful name inside the issues panel when an error occurs.
   * @param facets the list of facets to create labels for
   */
  private function updateLabels(facets:Array):void {
    var validationBundle:IResourceBundle = ResourceManager.getInstance().getResourceBundle(null, 'com.coremedia.cms.editor.ContentTypes');
    facets.forEach(function (f:Facet):void {
      validationBundle.content["CMProductList_localSettings.productList.filterFacets." + f.getKey() + "_text"] = resourceManager.getString('com.coremedia.livecontext.studio.LivecontextStudioPlugin', 'CMProductList_localSettings.productList_text');
    });
  }

  /**
   * Decides to show the migration button or not.
   */
  protected function getBtnVisibilityExpression(config:CategoryFacetsPropertyField):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var multiFacetsEnabled:Boolean = getMultiFacetEnabledExpression(config).getValue();
      if (!multiFacetsEnabled) {
        return true;
      }
      return getActiveEditorExpression().getValue() === NEW_EDITOR_ITEM_ID;
    });
  }

  /**
   * VE to calculate the auto fix button visibility
   */
  protected function getAutoFixExpression(config:CategoryFacetsPropertyField):ValueExpression {
    if (!autofixExpression) {
      autofixExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
        var multiFacetsExpression:ValueExpression = bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
        var filterFacetsStruct:Struct = multiFacetsExpression.getValue();
        if (filterFacetsStruct) {
          var searchFacets:SearchFacets = getSearchFacetsExpression(config).getValue();
          if (searchFacets && searchFacets.getFacets()) {
            var facets:Array = searchFacets.getFacets();
            var storedNames:Array = filterFacetsStruct.getType().getPropertyNames();

            //check if there is a stored multi facet key that does not exist anymore
            for each(var facetId:String in storedNames) {
              if (!FacetUtil.validateFacetId4Facets(facets, facetId)) {
                return true;
              }
            }
          }
        }
        return false;
      });
    }
    return autofixExpression;
  }

  /**
   * Used to fix invalid struct formats.
   * E.g. when a search facet is not available anymore, the struct still exists without the chance to overwrite it
   * with another configuration. This requires a manual fix by the user to avoid an automatic checkout.
   */
  protected function autoFixFormat():void {
    var multiFacetsExpression:ValueExpression = bindTo.extendBy(ContentPropertyNames.PROPERTIES, structPropertyName, PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
    var filterFacetsStruct:Struct = multiFacetsExpression.getValue();
    if (filterFacetsStruct) {
      var searchFacets:SearchFacets = getSearchFacetsExpression().getValue();
      if (searchFacets && searchFacets.getFacets()) {
        var facets:Array = searchFacets.getFacets();
        var storedNames:Array = filterFacetsStruct.getType().getPropertyNames();
        for each(var facetId:String in storedNames) {
          if (!FacetUtil.validateFacetId4Facets(facets, facetId)) {
            filterFacetsStruct.getType().removeProperty(facetId);
          }
        }
      }
    }
  }

  /**
   * For old Product Lists, the 'filterFacets' struct does not exist.
   * So for migration, we remove the old values from the 'productList' struct and add the 'filterFacets' struct.
   * Finally, we trigger the BindComponentsPlugin for the rendering of the multi facet editors.
   */
  private function migrateSingleToMultiValue():void {
    //this ensures that the editors are destroyed an re-initialized when content is reverted
    getFacetsExpression().setValue([]);

    var searchFacets:SearchFacets = getSearchFacetsExpression().getValue();
    var productListStructExpression:ValueExpression = bindTo.extendBy(ContentPropertyNames.PROPERTIES, structPropertyName, PRODUCT_LIST_STRUCT_NAME);
    var parentStruct:Struct = productListStructExpression.getValue();

    parentStruct.getType().addStructProperty(MULTI_FACETS_STRUCT_NAME);
    var facetQuery:String = parentStruct.get(SINGLE_FACET_VALUE);
    if (facetQuery) {
      var facetId:String = FacetUtil.findFacetIdForQuery(searchFacets.getFacets(), facetQuery);
      if (facetId) {
        productListStructExpression
                .extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME)
                .extendBy([facetId])
                .extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME)
                .setValue([facetQuery]);
      }
    }
    parentStruct.getType().removeProperty(SINGLE_FACET_VALUE);
    parentStruct.getType().removeProperty(SINGLE_FACET_NAME);

    //this invoke later is necessary, otherwise the BindComponentsPlugin of the group editor wouldn't notice a reset
    EventUtil.invokeLater(function ():void {
      searchFacetsChanged(getSearchFacetsExpression());
    });
  }

  override protected function onDestroy():void {
    super.onDestroy();
    searchFacetsExpression && searchFacetsExpression.removeChangeListener(searchFacetsChanged);
  }
}
}
