package com.coremedia.blueprint.studio.boot;

import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * <p>
 * Assuming that PROPERTIES_URL and JANGAROO_MODULE are variables pointing to a valid properties file or a jangaroo
 * module base directory respectively, the studio webapp can be run like this:
 * </p>
 * <pre>
 *   mvn spring-boot:run -f blueprint/modules/studio/studio-webapp/pom.xml
 *      -Dpropertieslocations=${PROPERTIES_URL}
 *      -Dserver.port=8888
 *      -Djangaroo.output=blueprint/modules/studio/studio-webapp/target/jangaroo-output,${JANGAROO_MODULE}/target/classes/META-INF/resources
 * </pre>
 */
@Configuration
@Import({ServerPropertiesAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class, JangarooResourcesConfiguration.class})
// application.xml is not on the classpath, so we have to import stuff here as well
@ImportResource(
        locations = {
                "classpath:/com/coremedia/blueprint/base/uapi/bpbase-uapi-cache-services.xml",
                "classpath:/com/coremedia/blueprint/segments/blueprint-segments.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class
)
public class StudioWebapp extends SpringBootServletInitializer {

  /**
   * Use spring-boot-maven plugin to run to avoid https://youtrack.jetbrains.com/issue/IDEA-107048
   */
  public static void main(String[] args) {
    new SpringApplication(StudioWebapp.class).run(args);
  }

}