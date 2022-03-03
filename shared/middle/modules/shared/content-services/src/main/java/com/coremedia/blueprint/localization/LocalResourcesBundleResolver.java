package com.coremedia.blueprint.localization;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Development interceptor for the {@link ContentBundleResolver}.
 * <p>
 * Overrules the requested content bundle with the corresponding source file
 * in the blueprint workspace for faster frontend development roundtrips.
 * <p>
 * Do not use it in production setups!
 */
public class LocalResourcesBundleResolver implements BundleResolver {
  private static final Logger LOG = LoggerFactory.getLogger(LocalResourcesBundleResolver.class);

  private final BundleResolver defaultBundleResolver;
  private final StructService structService;
  private final ApplicationContext applicationContext;

  public LocalResourcesBundleResolver(BundleResolver defaultBundleResolver,
                                      StructService structService,
                                      ApplicationContext applicationContext) {
    this.defaultBundleResolver = defaultBundleResolver;
    this.structService = structService;
    this.applicationContext = applicationContext;
  }


  // --- BundleResolver ---------------------------------------------

  /**
   * Try to find a local resource bundle for the given bundle.
   * <p>
   * Pattern: content {@code /Themes/chefcorp/l10n/Chefcorp_de.properties} is substituted by local resource
   * ${@code /themes/chefcorp/l10n/Chefcorp_de.properties} that is resolved from the application context.
   * <p>
   * Falls back to default bundle resolving if no local bundle is found.
   */
  @Nullable
  @Override
  public Struct resolveBundle(@NonNull Content bundle) {
    String filepath = filepath(bundle.getPathArcs());
    Resource resource = applicationContext.getResource(filepath);
    try {
      return resourceToStruct(resource);
    } catch (IOException e) {
      LOG.warn("Falling back to regular resolution for bundle '{}', because local resource cannot be loaded: {}",
              bundle.getPath(), e.getMessage());
    }
    return defaultBundleResolver.resolveBundle(bundle);
  }


  // --- internal ---------------------------------------------------

  private Struct resourceToStruct(Resource resource) throws IOException {
    StructBuilder structBuilder = structService.createStructBuilder();
    PropertiesLoaderUtils.loadProperties(resource)
            .forEach((key, value) -> structBuilder.set(key.toString(), value));
    return structBuilder.build();
  }

  private static String filepath(List<String> pathArcs) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pathArcs.size(); ++i) {
      if (i > 0) {
        sb.append(File.separator);
      }
      String pathArc = pathArcs.get(i);
      if(pathArc.equals("Themes")){
        pathArc = pathArc.toLowerCase();
      }
      sb.append(pathArc);
    }
    return sb.toString();
  }
}
