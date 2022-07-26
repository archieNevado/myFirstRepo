package com.coremedia.blueprint.headlessserver.contentschema;

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
  public static final String ROOT_CHANNEL_SEGMENT = "root";
  public static final String ROOT_CHANNEL_REPO_PATH = MASTER_SITE_REPO_PATH + "/Content/Navigation";

  public static final Integer DERIVED_ROOT_CHANNEL_ID = 11144;
  public static final String DERIVED_ROOT_CHANNEL_SEGMENT = "root";
  public static final String DERIVED_ROOT_CHANNEL_REPO_PATH = DERIVED_SITE_REPO_PATH + "/Content/Navigation";

  public static final Integer PICTURE_ID = 111114;
  public static final Integer VIDEO_ID = 111118;
  public static final Integer DOWNLOAD_ID = 111120;
  public static final Integer HTML_ID = 111122;
  public static final String MEDIA_REPO_PATH = MASTER_SITE_REPO_PATH + "/Content/Pictures";
  public static final String MEDIA_DELIVERY_PATH = "/caas/v1/media/";

  public static final Integer ARTICLE_ID = 111116;
  public static final String ARTICLE_REPO_PATH = MASTER_SITE_REPO_PATH + "/Content/Articles";

  public static final String SETTINGS_KEY = "testInt";
  public static final Integer SETTINGS_VALUE = 5;
}
