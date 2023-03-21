package com.coremedia.blueprint.caas.augmentation;

/**
 * This class contains ids and data which is found in the test xml repository at '/test/resources/content/contentrepository.xml'.
 */
public final class TestRepoConstants {
  public static final String SITES_REPO_PATH = "/Sites/TestSite";

  public static final String MASTER_SITE_ID = "the-site-id";
  public static final String MASTER_SITE_NAME = "the-site-name";
  public static final String MASTER_SITE_LOCALE = "en-US";
  public static final String MASTER_SITE_REPO_PATH = SITES_REPO_PATH + "/TestSiteUS";

  public static final String DERIVED_SITE_ID = "the-derived-site-id";
  public static final String DERIVED_SITE_NAME = "the-derived-site-name";
  public static final String DERIVED_SITE_LOCALE = "de-DE";
  public static final String DERIVED_SITE_REPO_PATH = SITES_REPO_PATH + "/TestSiteDE";

  public static final Integer ROOT_CHANNEL_ID = 111112;
  public static final String ROOT_CHANNEL_SEGMENT = "root-en";


  public static final int PRODUCT_LIST_ID = 111116;
  public static final String CATEGORY_EXTERNAL_ID = "cool-stuff";
  public static final String CATEGORY_REFERENCE = "mock:///catalog/category/" + CATEGORY_EXTERNAL_ID;

  public static final int AUGMENTED_PAGE_ID = 1111120;
  public static final String AUGMENTED_PAGE_EXTERNAL_ID = "augmented-page";

  public static final String PRODUCT_EXTERNAL_ID = "cool-product";
  public static final String PRODUCT_REFERENCE = "mock:///catalog/product/" + PRODUCT_EXTERNAL_ID;

}
