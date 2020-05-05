package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.joo.PropertiesClass;

import javax.inject.Named;

/**
 * A wrapper for the ActionScript Placements class
 * <code>com.coremedia.blueprint.base.pagegrid.PageGridPropertyField_properties</code>.
 */
@Named
public class PageGridPropertyField_properties extends PropertiesClass {

  public PageGridPropertyField_properties() {
    super("com.coremedia.blueprint.base.pagegrid.PageGridPropertyField_properties");
  }

  public String PageLayout_error_noLayout_text() {
    return get("PageLayout_error_noLayout_text");
  }

}
