package com.coremedia.blueprint.taxonomies.strategy;

import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.cycleprevention.TaxonomyCycleValidator;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.common.CapSession;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentException;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.content.search.SearchService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of the ITaxonomyResolver.
 */
public class TaxonomyResolverImpl implements TaxonomyResolver {
  private static final Logger LOG = LoggerFactory.getLogger(TaxonomyResolverImpl.class);
  private static final String TAXONOMY_FOLDER_NAME = "Taxonomies";

  private ContentRepository contentRepository;
  private SearchService searchService;
  private TaxonomyCycleValidator taxonomyCycleValidator;
  private Map<String, Taxonomy> strategies;
  private Map<String, String> aliasMapping;
  private SitesService sitesService;
  private String contentType;
  private String siteConfigPath;
  private String globalConfigPath;

  public TaxonomyResolverImpl(@NonNull SitesService sitesService,
                              @NonNull ContentRepository contentRepository,
                              @NonNull SearchService searchService,
                              @NonNull TaxonomyCycleValidator taxonomyCycleValidator,
                              @NonNull Map<String, String> aliasMapping,
                              @NonNull String contentType,
                              @NonNull String siteConfigPath,
                              @NonNull String globalConfigPath) {
    this.sitesService = sitesService;
    this.contentRepository = contentRepository;
    this.searchService = searchService;
    this.taxonomyCycleValidator = taxonomyCycleValidator;
    this.aliasMapping = aliasMapping;
    this.contentType = contentType;
    this.siteConfigPath = siteConfigPath;
    this.globalConfigPath = globalConfigPath;
  }

  @Override
  public Collection<Taxonomy> getTaxonomies() {
    return strategies.values();
  }

  @Override
  public boolean reload() {
    loadTaxonomies();
    return true;
  }

  @Override
  public Taxonomy getTaxonomy(String siteId, String taxonomyId) {
    Taxonomy taxonomy = findTaxonomy(siteId, taxonomyId);
    //run validity check.
    if (taxonomy != null && !taxonomy.isValid()) {
      String key = toKey(taxonomy.getTaxonomyId(), taxonomy.getSiteId());
      strategies.remove(key);
    }
    return taxonomy;
  }


  @PostConstruct
  public void init() {
    strategies = Collections.synchronizedMap(new HashMap<>());
    loadTaxonomies();
  }

  /**
   * Builds the key that is put into the strategies map.
   *
   * @param taxonomyId The id of the taxonomy
   * @param site       The site the taxonomy is used for or null.
   * @return The key of the taxonomy.
   */
  private String toKey(String taxonomyId, String site) {
    return taxonomyId + "_" + site;
  }

  /**
   * Detects available taxonomy trees.
   */
  private void loadTaxonomies() {
    Map<String,Taxonomy> newTaxonomies = new HashMap<>();
    Set<Site> sites = sitesService.getSites();
    for (Site site : sites) {
      try {
        Content siteTaxonomyFolder = getSiteConfigFolder(site);
        if (siteTaxonomyFolder == null) {
          continue;
        }

        newTaxonomies.putAll(getStrategies(siteTaxonomyFolder, site.getId()));
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
      }
    }

    Content globalConfigFolder = getGlobalConfigFolder();
    if (globalConfigFolder != null) {
      newTaxonomies.putAll(getStrategies(globalConfigFolder, null));
    }

    strategies.clear();
    strategies.putAll(newTaxonomies);
  }

  @NonNull
  private Map<String, Taxonomy> getStrategies(@NonNull Content taxonomyRootFolder, @Nullable String id) {
    Content taxonomyFolder = taxonomyRootFolder.getChild(TAXONOMY_FOLDER_NAME);

    if (taxonomyFolder == null) {
      LOG.warn("Invalid taxonomy root folder [null]");
      return new HashMap<>();
    }


    ContentType contentType = contentRepository.getContentType(this.contentType);
    if (contentType == null) {
      return new HashMap<>();
    }

    return createStrategies(taxonomyFolder, contentType, id);
  }

  /**
   * Returns the site configuration folder or
   * null if that one is not readable.
   */
  private Content getSiteConfigFolder(Site site) {
    CapSession originalSession = contentRepository.getConnection().getConnectionSession().activate();
    try {
      return site.getSiteRootFolder().getChild(siteConfigPath);
    } catch (ContentException e) {
      LOG.error("Failed to read site config folder " + siteConfigPath+ " for site " + site.getSiteRootFolder().getPath() + ": " + e.getMessage());
    }
    finally {
      originalSession.activate();
    }
    return null;
  }

  /**
   * Returns the global configuration folder or
   * null if that one is not readable.
   */
  private Content getGlobalConfigFolder() {
    CapSession originalSession = contentRepository.getConnection().getConnectionSession().activate();
    try {
      return contentRepository.getChild(globalConfigPath);
    } catch (ContentException e) {
      LOG.error("Failed to read " + globalConfigPath + ": " + e.getMessage());
    }
    finally {
      originalSession.activate();
    }
    return null;
  }


  /**
   * Creates a taxonomy instance for the given (maybe side-depending) folder.
   *
   * @param taxFolderContent The folder to lookup keywords in.
   */
  @NonNull
  private Map<String,Taxonomy> createStrategies(@NonNull Content taxFolderContent, @NonNull ContentType contentType, @Nullable String siteId) {
    Map<String,Taxonomy> taxonomies = new HashMap<>();
    LOG.debug("Creating taxonomy strategy for folder '{}', site '{}'", (taxFolderContent.getPath()), siteId);
    //lookup the root folder
    try {
      Set<Content> taxonomyFolderChildren = taxFolderContent.getSubfolders();
      //check each subfolder that is a separate taxonomy tree
      if (!taxonomyFolderChildren.isEmpty()) {
        for (Content taxonomyFolder : taxonomyFolderChildren) {
          //we only have one strategy here, maybe some customers need more logic here and different strategies...
          DefaultTaxonomy strategy = new DefaultTaxonomy(taxonomyFolder, siteId, contentType, contentRepository, searchService, taxonomyCycleValidator);
          taxonomies.put(toKey(strategy.getTaxonomyId(), siteId), strategy);
        }
      }
    } catch (Exception e) {
      LOG.warn("Error resolving taxonomy strategy for '{}' and site id '{}'", taxFolderContent.getPath(), siteId, e);
    }

    return taxonomies;
  }


  /**
   * Recursive search for the taxonomy strategy matching the given id and site.
   * Lookup:
   * <ol>
   *   <li>Lookup taxonomy for site and (taxonomy) id</li>
   *   <li>Lookup common taxonomy, ignoring site value</li>
   *   <li>Lookup alias mapping</li>
   * </ol>
   *
   * @param siteId       The site id the taxonomy is working on or null if it is a global tree.
   * @param taxonomyId The id of the tree
   * @return The administrating object for the taxonomy tree.
   */
  private Taxonomy findTaxonomy(String siteId, String taxonomyId) {
    String key = toKey(taxonomyId, siteId);
    Taxonomy strategy = strategies.get(key);
    //it's most probably that the first lookup fails, means that a taxonomy belongs to a site but
    //the site does not define a taxonomy of type XY of its own, so search for the common one, using site=null value
    if (strategy == null) {
      key = toKey(taxonomyId, null);
      strategy = strategies.get(key);
    }
    if (strategy == null) {
      //still not found? ok then try an alias next...
      String mappedTaxonomy = aliasMapping.get(taxonomyId);
      strategy = strategies.get(toKey(mappedTaxonomy, siteId));

      //if site (e.g. querySubject+media) is set, try to find root taxonomy and ignore the site name
      if (strategy == null && siteId != null) {
        return getTaxonomy(null, taxonomyId);
      }
    }
    return strategy;
  }
}
