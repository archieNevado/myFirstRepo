package com.coremedia.ecommerce.studio.library {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.actions.DeleteSavedSearchActionBase;
import com.coremedia.cms.editor.sdk.desktop.*;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.studio.multisite.models.sites.Site;
import com.coremedia.cms.studio.multisite.models.sites.global.sitesService;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchFilters;
import com.coremedia.ecommerce.studio.components.search.filters.FacetUtil;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ecommerce.studio.model.SearchFacets;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.messagebox.MessageBoxUtilInternal;
import com.coremedia.ui.util.AsyncObserver;
import com.coremedia.ui.util.EventUtil;

import mx.resources.ResourceManager;

/**
 * Checks if the content folder of the saved search is still valid.
 */
public class CommerceCollectionViewStateInterceptor implements CollectionViewStateInterceptor {
  public function CommerceCollectionViewStateInterceptor() {
  }

  public function intercept(state:SavedSearchModel, callback:Function):void {
    if(!isApplicable(state)) {
      callback(state);
      return;
    }

    var siteId:String = state.getSiteId();
    var folder:RemoteBean = state.getFolder();

    if (siteId && siteId !== sitesService.getPreferredSiteId()) {
      var title:String = ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'saveSearch_invalidSite_title');
      var msg:String = ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'saveSearch_invalidSite_text');

      var buttons:Object = {
        yes: ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'saveSearch_invalidSite_change_site_btn_text'),
        cancel: ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'dialog_defaultCancelButton_text')
      };
      MessageBoxUtilInternal.show(title, msg, null, buttons, getSwitchSiteCallback(state, callback));
    } else {
      checkFilterState(folder as Category, state, callback);
    }
  }

  private function isApplicable(state:SavedSearchModel):Boolean {
    var folder:RemoteBean = state.getFolder();
    var name:String = state.getName();
    return name && folder is Category;
  }

  /**
   * Changes the preferred site and waits for all pending processes to be finished.
   * Afterwards, we check the filter status.
   *
   * @param state the persisted search filter state
   * @param callback the callback which applies the state to the library
   * @return
   */
  private function getSwitchSiteCallback(state:SavedSearchModel, callback:Function):Function {
    return function (btn:String):void {
      if (btn === 'yes') {
        var site:Site = sitesService.getSite(state.getSiteId());
        editorContext.getSitesService().getPreferredSiteIdExpression().setValue(site.getId());

        //we need the minimal setup loaded so that the filter is activated afterwards
        EventUtil.invokeLater(function ():void {
          //we can't determine when the library mode switch is finished, so we wait for all other stuff to be completed
          AsyncObserver.complete(function ():void {
            ValueExpressionFactory.createFromFunction(function ():RemoteBean {
              if (sitesService.getPreferredSiteId() !== site.getId()) {
                return undefined;
              }

              var root:Content = sitesService.getPreferredSite().getSiteRootFolder();
              if (!root.isLoaded() || !root.getPath()) {
                return undefined;
              }

              return state.getFolder();
            }).loadValue(function (bean:RemoteBean):void {
              checkFilterState(bean as Category, state, callback);
            });
          });
        });
      }
    };
  }

  /**
   * Validates the persisted commerce search facets against the actual values of the selected store.
   *
   * @param category
   * @param state
   * @param callback
   */
  private function checkFilterState(category:Category, state:SavedSearchModel, callback:Function):void {
    //load facet values first
    ValueExpressionFactory.createFromFunction(function ():Array {
      var searchFacets:SearchFacets = category.getSearchFacets();
      if (searchFacets === null) {
        return null;
      }

      if (!searchFacets.isLoaded()) {
        searchFacets.load();
        return undefined;
      }

      return searchFacets.getFacets();
    }).loadValue(function (facets:Array):void {
      //then validate all values
      if(!validateFilterState(facets, state)) {
        var title:String = ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidFacets_title');
        var msg:String = ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidFacets_text');

        var buttons:Object = {
          yes: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidFacets_delete_btn_text'),
          no: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidFacets_clear_btn_text'),
          cancel: ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'dialog_defaultCancelButton_text')
        };
        MessageBoxUtilInternal.show(title, msg, null, buttons, getInvalidFacetCallback(facets, state, callback));
      }
      else {
        callback(state);
      }
    });
  }

  /**
   * Validates if the stored facet and its query values are still available.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   * @return
   */
  private function validateFilterState(facets:Array, state:SavedSearchModel):Object {
    var commerceFilterState:Object = state.getData()[CatalogSearchFilters.FACET_FILTER_ID];
    for (var m:String in commerceFilterState) {
      var selectedValues:Array = commerceFilterState[m];
      var facet:Facet = FacetUtil.findFacetForKey(facets, m);
      if (!facet) {
        return false;
      }

      for each(var v:String in selectedValues) {
        if (!FacetUtil.validateFacetValue(facet, v)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Removes the invalid values from the commerce saved search.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   *
   * @return the updated search state
   */
  private function clearInvalidValues(facets:Array, state:SavedSearchModel):Object {
    var commerceFilterState:Object = state.getData()[CatalogSearchFilters.FACET_FILTER_ID];
    for (var m:String in commerceFilterState) {
      var selectedValues:Array = commerceFilterState[m];
      var facet:Facet = FacetUtil.findFacetForKey(facets, m);
      if (!facet) {
        delete commerceFilterState[m];
        continue;
      }

      var updated:Array = [];
      for each(var v:String in selectedValues) {
        if (FacetUtil.validateFacetValue(facet, v)) {
          updated.push(v);
        }
      }

      commerceFilterState[m] = updated;
    }
    return state;
  }

  /**
   * The action handler for the invalid facet dialog.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   * @param callback the callback to invoke with the updated search state
   * @return
   */
  private function getInvalidFacetCallback(facets:Array, state:SavedSearchModel, callback:Function):Function {
    return function (btn:String):void {
      if (btn === 'yes') {
        var name:String = state.getName();
        DeleteSavedSearchActionBase.deleteSearch(name);
      }
      else if (btn === 'no') {
        var updated:Object = clearInvalidValues(facets, state);
        callback(updated);
      }
    }
  }
}
}
