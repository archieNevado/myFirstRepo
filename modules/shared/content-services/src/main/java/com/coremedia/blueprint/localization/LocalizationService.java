package com.coremedia.blueprint.localization;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructService;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Coordinates CMResourceBundle fallback strategies.
 * <p>
 * The LocalizationService features multiple axis of fallbacks for resources
 * of CMResourceBundles:
 * <ol>
 * <li>The usual Locale semantics of language, country, variant</li>
 * <li>The master relation of the Blueprint CMLocalizable semantics</li>
 * <li>Multiple bundles</li>
 * </ol>
 * The resources are merged from the locale matching variants of the given
 * bundles (S. {@link ContentSiteAspect#getVariantsByLocale()}.  Generally,
 * a more specific locale takes precedence over the order of the given bundles.
 * If your bundles are master-linked appropriately, there is no contradiction
 * between Locales and master relation, so that precedence does not matter.
 * Otherwise, a more specific Locale takes precedence over a shorter master
 * path.
 */
public class LocalizationService {
  private static final String CM_RESOURCE_BUNDLE = "CMResourceBundle";
  private static final String MASTER = "master";
  private static final Locale GLOBAL = new Locale("");

  private final SitesService sitesService;
  private final StructService structService;
  private final BundleResolver bundleResolver;


  // --- construct and configure ------------------------------------

  public LocalizationService(@Nonnull StructService structService,
                             @Nonnull SitesService sitesService,
                             @Nonnull BundleResolver bundleResolver) {
    this.structService = structService;
    this.sitesService = sitesService;
    this.bundleResolver = bundleResolver;
  }


  // --- features ---------------------------------------------------

  /**
   * Returns the resources of the given bundles.
   * <p>
   * The resources are merged from the variants of the bundles that match the
   * given locale and their master variants.  I.e. each bundle in the bundles
   * collection represents the set of all its variants, and two variants of the
   * same bundle (like myBundle_de and myBundle_de_DE) in the bundles
   * collection are considered as duplicate.
   */
  @Nonnull
  public Struct resources(@Nonnull Collection<Content> bundles, @Nullable Locale locale) {
    checkAreBundles(bundles);
    List<Struct> localizations = new ArrayList<>();
    List<Content> fallback = localizationFallback(locale!=null ? locale : GLOBAL, variantsMaps(bundles));
    for (Content bundle : fallback) {
      Struct l10ns = bundleResolver.resolveBundle(bundle);
      if (l10ns != null) {
        localizations.add(l10ns);
      }
    }
    Struct result = StructUtil.mergeStructList(localizations);
    return result!=null ? result : structService.emptyStruct();
  }

  /**
   * Convenience variant of {@link #resources(Collection, Locale)} for a single
   * bundle.
   */
  @Nonnull
  public final Struct resources(@Nonnull Content bundle, @Nullable Locale locale) {
    return resources(Collections.singletonList(bundle), locale);
  }


  // --- internal ---------------------------------------------------

  @VisibleForTesting
  List<Content> localizationFallback(Locale locale, List<Map<Locale, Content>> vbls) {
    List<Content> fallback = new ArrayList<>();
    // Fetch locale-matching variants of all bundles, precedence by
    // 1. most specific locale
    // 2. order of vbls
    boolean[] foundByLocale = new boolean[vbls.size()];
    for (Locale lcl : deriveLocales(locale)) {
      for (int i=0; i<vbls.size(); ++i) {
        // If we have already found a locale specific variant, do not add the
        // global variant here, because it will be handled along the master
        // chain.  If there is no locale specific variant though, the global
        // variant is the appropriate point of entry for this bundle.
        if (!GLOBAL.equals(lcl) || !foundByLocale[i]) {
          Content content = vbls.get(i).get(lcl);
          if (content!=null) {
            fallback.add(content);
            foundByLocale[i] = true;
          }
        }
      }
    }
    // Follow the master links of the variants selected so far, breadth first
    for (int i=0; i<fallback.size(); ++i) {
      Content master = fallback.get(i).getLink(MASTER);
      if (master!=null && !fallback.contains(master)) {
        fallback.add(master);
      }
    }
    return fallback;
  }

  @VisibleForTesting
  List<Map<Locale, Content>> variantsMaps(Collection<Content> contents) {
    List<Map<Locale, Content>> variantsByLocales = new ArrayList<>();
    for (Content content : contents) {
      // In this context contents are equivalent wrt. their variants sets,
      // so there may be "duplicates" in the input collection.  Omit them.
      if (!contains(variantsByLocales, content)) {
        variantsByLocales.add(sitesService.getContentSiteAspect(content).getVariantsByLocale());
      }
    }
    return variantsByLocales;
  }

  private boolean contains(Collection<Map<Locale, Content>> variantsByLocales, Content content) {
    for (Map<Locale, Content> vbl : variantsByLocales) {
      if (vbl.values().contains(content)) {
        return true;
      }
    }
    return false;
  }

  private Collection<Locale> deriveLocales(Locale locale) {
    // LinkedHashSet to preserve order and omit duplicates
    LinkedHashSet<Locale> locales = new LinkedHashSet<>();
    locales.add(new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant()));
    locales.add(new Locale(locale.getLanguage(), locale.getCountry()));
    locales.add(new Locale(locale.getLanguage()));
    locales.add(GLOBAL);
    return locales;
  }

  private static void checkAreBundles(Collection<Content> contents) {
    for (Content content : contents) {
      checkIsBundle(content);
    }
  }

  private static void checkIsBundle(Content bundle) {
    if (!bundle.getType().isSubtypeOf(CM_RESOURCE_BUNDLE)) {
      throw new IllegalArgumentException(bundle + " is no CMResourceBundle");
    }
  }
}
