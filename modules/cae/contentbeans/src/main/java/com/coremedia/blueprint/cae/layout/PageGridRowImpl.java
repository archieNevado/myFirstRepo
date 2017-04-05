package com.coremedia.blueprint.cae.layout;


import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGrid;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyle;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.layout.PageGridRow;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.viewtype.ViewtypeService;
import com.coremedia.objectserver.dataviews.AssumesIdentity;

import java.util.ArrayList;
import java.util.List;

/**
 * A PageGrid row based on content
 */
public class PageGridRowImpl implements PageGridRow, AssumesIdentity {
  private ValidationService<Linkable> validationService;
  private ContentBackedPageGridService contentBackedPageGridService;
  private ViewtypeService viewtypeService;

  private CMNavigation navigation;
  private int row;


  // --- construction -----------------------------------------------

  /**
   * Constructor called by your code. The one below is for dataviews only.
   *
   * @param navigation                   the navigation this row is for.
   * @param row                          id of the row
   * @param contentBackedPageGridService the service to extract items
   * @param validationService            the validation service to check items against
   * @param viewtypeService              the viewtype service for viewtype name resolution
   */
  public PageGridRowImpl(CMNavigation navigation,
                         int row,
                         ContentBackedPageGridService contentBackedPageGridService,
                         ValidationService<Linkable> validationService,
                         ViewtypeService viewtypeService) {
    this.navigation = navigation;
    this.row = row;
    this.contentBackedPageGridService = contentBackedPageGridService;
    this.validationService = validationService;
    this.viewtypeService = viewtypeService;
  }

  /**
   * Only for dataviews
   */
  @SuppressWarnings("UnusedDeclaration")
  public PageGridRowImpl() {
    // This constructor needs to be available, since it is used during dataview generation.
  }


  // --- PageGridRow ------------------------------------------------

  @Override
  public List<PageGridPlacement> getPlacements() {
    List<PageGridPlacement> placements = new ArrayList<>();
    ContentBackedStyleGrid styleGrid = getContentBackedPageGrid().getStyleGrid();
    List<ContentBackedStyle> styleRow = styleGrid.getRow(row);
    for (int colIndex = 0; colIndex < styleRow.size(); ++colIndex) {
      placements.add(new ContentBeanBackedPageGridPlacement(navigation,
              row, styleRow.size(), colIndex,
              contentBackedPageGridService,
              validationService,
              viewtypeService));
    }
    return placements;
  }

  @Override
  public boolean getHasItems() {
    boolean result = false;
    List<PageGridPlacement> placements = getPlacements();
    for (PageGridPlacement placement : placements) {
      if (placement.getName().equals("main") || !placement.getItems().isEmpty()) {
        result = true;
        break;
      }
    }
    return result;
  }

  // --- Dataviews --------------------------------------------------

  @Override
  public boolean equals(Object o) { //NOSONAR: cyclomatic complexity 11 is OK for #equals.
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PageGridRowImpl that = (PageGridRowImpl) o;

    if (row != that.row) {
      return false;
    }
    //noinspection RedundantIfStatement
    if (navigation != null ? !navigation.equals(that.navigation) : that.navigation != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = navigation != null ? navigation.hashCode() : 0;
    result = 31 * result + row;
    return result;
  }

  @Override
  public void assumeIdentity(Object o) {
    PageGridRowImpl other = (PageGridRowImpl) o;
    validationService = other.validationService;
    contentBackedPageGridService = other.contentBackedPageGridService;
    viewtypeService = other.viewtypeService;
    navigation = other.navigation;
    row = other.row;
  }


  // --- internal ---------------------------------------------------

  private ContentBackedPageGrid getContentBackedPageGrid() {
    return contentBackedPageGridService.getContentBackedPageGrid(navigation.getContent());
  }
}
