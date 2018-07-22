package com.coremedia.livecontext.ecommerce.sfcc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.IOException;

import static org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;

public class SfccTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final Logger LOG = LoggerFactory.getLogger(SfccTestInitializer.class);

  public static final String WORKSPACE_CONFIG_DIR = "../../../../../workspace-config";
  public static final String BLUEPRINT_DEVELOPMENT_PROPERTIES_DIR = "../../../../workspace-configuration/development-properties/default";

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();

    addAfterSystemEnvironmentIfFound(environment, "sfcc-test-workspace-config",
            "file:${workspace.configuration.dir:" + WORKSPACE_CONFIG_DIR + "}/development-properties/sfcc.properties");

    addAfterSystemEnvironmentIfFound(environment, "sfcc-test-development-config",
            "file:${development-properties.dir:" + BLUEPRINT_DEVELOPMENT_PROPERTIES_DIR + "}/sfcc.properties");
  }

  /**
   * <p>
   * Will add the given properties after system environment.
   * </p>
   * <p>
   * Property files loaded that are not in version control should always win before files in version control.
   * Since the maven pom is in version control, this is the right way to do it, not addIfFound, that uses addLast.
   * </p>
   *
   * @param environment environment to add the property source location to
   * @param name        some unique name to identify it later on
   * @param location    location to add (any property inside will be resolved); if {@code null} it won't be added
   */
  protected static void addAfterSystemEnvironmentIfFound(@NonNull ConfigurableEnvironment environment, String name, @Nullable String location) {
    if (location == null) {
      return;
    }

    MutablePropertySources propertySources = environment.getPropertySources();
    String resolvedLocation = environment.resolvePlaceholders(location);

    Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
    if (resource.exists()) {
      try {
        propertySources.addAfter(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new ResourcePropertySource(name, resource));
        LOG.info("Added property source {} after system environment: {}", name, resolvedLocation);
      } catch (IOException ignored) {
        //do nothing
      }
    } else {
      LOG.info("Ignored: Resource {} with location {} (resolved from {}) does not exist. " +
              "In a blueprint workspace configure your properties below ", name, resolvedLocation, location);
    }
  }
}
