package com.coremedia.blueprint.studio.uitest.core;

import com.coremedia.uitesting.webdriver.PropertySourcesConfigurer;
import com.coremedia.uitesting.webdriver.WebAppProxyContextInitializer;
import edu.umd.cs.findbugs.annotations.NonNull;

import static com.coremedia.uitesting.webdriver.PropertySourcesConfigurer.propertySourceConfigurationBuilder;

/**
 * Ensures that properties set via system properties override properties from a given
 * user configuration file which again override the default values.
 * <p/>
 * Default values are defined in release-testing-defaults.properties, the user configuration
 * is located in ~/.cm16.test.properties or ~/_cm16.test.properties, and the system properties
 * are set at the command line via -Dkey=value.
 */
class StudioTestCaseInitializer extends WebAppProxyContextInitializer {
  private static final String CMS_VERSION = "16";
  private static final String USER_PROPERTIES_BASENAME = "cm" + CMS_VERSION + ".test.properties";
  private static final String NO_VERSION_USER_PROPERTIES_BASENAME = "studio.uitest.properties";
  private static final String TEST_PROPERTIES_URL = "test.properties.url";

  @Override
  protected void initializePropertySources(@NonNull PropertySourcesConfigurer propertySourcesConfigurer) {
    propertySourcesConfigurer
            .apply(
                    propertySourceConfigurationBuilder("local-user-properties-linux-deprecated")
                            .setBeforeSystemProperties()
                            .setLocationUserHomeLinux(USER_PROPERTIES_BASENAME)
                            .setDeprecationNotice("Use ." + NO_VERSION_USER_PROPERTIES_BASENAME + " instead.")
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("local-user-properties-windows-deprecated")
                            .setBeforeSystemProperties()
                            .setLocationUserHomeWindows(USER_PROPERTIES_BASENAME)
                            .setDeprecationNotice("Use _" + NO_VERSION_USER_PROPERTIES_BASENAME + " instead.")
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("local-user-properties-linux")
                            .setBeforeSystemProperties()
                            .setLocationUserHomeLinux(NO_VERSION_USER_PROPERTIES_BASENAME)
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("local-user-properties-windows")
                            .setBeforeSystemProperties()
                            .setLocationUserHomeWindows(NO_VERSION_USER_PROPERTIES_BASENAME)
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("remote-test-config")
                            .setBeforeSystemProperties()
                            .setLocationProperty(TEST_PROPERTIES_URL)
                            // Location is required to exist as soon as not-empty.
                            .setRequiredPredicate(r -> !r.getProperty(TEST_PROPERTIES_URL, "").isBlank())
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("testModuleDefaultProperties")
                            .setLocation("classpath:release-testing-module-defaults.properties")
                            .build()
            )
            .apply(
                    propertySourceConfigurationBuilder("release-testing-defaults")
                            .setLocation("classpath:/META-INF/coremedia/releasetest/base/release-testing-defaults.properties")
                            .build()
            );
  }

}
