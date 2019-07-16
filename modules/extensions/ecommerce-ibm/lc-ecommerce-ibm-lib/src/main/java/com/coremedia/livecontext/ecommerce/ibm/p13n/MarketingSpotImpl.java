package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.MarketingImage;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingText;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Model class for marketing spots.
 * The marketing spot data are based on two different REST handlers providing data. Thus, this model class has to
 * distinguish between the different data formats retrieved by the handlers. The data format can be
 * identified by the property <code>resourceName</code> which is unique and provided by each REST handler.
 **/
public class MarketingSpotImpl extends AbstractIbmCommerceBean implements MarketingSpot {

  private static final Logger LOG = LoggerFactory.getLogger(MarketingSpotImpl.class);

  public static final String CONTENT_FORMAT_FILE = "File";
  public static final String CONTENT_FORMAT_TEXT = "Text";

  private Map<String, Object> delegate;
  private WcMarketingSpotWrapperService marketingSpotWrapperService;
  private String externalTechId;

  @NonNull
  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = getDelegateFromCache();
      if (delegate == null) {
        throw new NotFoundException(getId() + " (marketing spot not found in catalog)");
      }
    }

    return delegate;
  }

  /**
   * Perform by-id-call to get detail data
   *
   * @return detail data map
   */
  Map<String, Object> getDelegateFromCache() {
    UserContext userContext = UserContextHelper.getCurrentContext();
    return getCommerceCache().get(
            new MarketingSpotCacheKey(getId(), getContext(), userContext, getMarketingSpotWrapperService(),
                    getCommerceCache()));
  }

  @Override
  public void load() {
    getDelegate();
  }

  @Override
  public void setDelegate(Object delegate) {
    //noinspection unchecked
    this.delegate = (Map<String, Object>) delegate;
  }

  public WcMarketingSpotWrapperService getMarketingSpotWrapperService() {
    return marketingSpotWrapperService;
  }

  @Required
  public void setMarketingSpotWrapperService(WcMarketingSpotWrapperService marketingSpotWrapperService) {
    this.marketingSpotWrapperService = marketingSpotWrapperService;
  }

  @Override
  public String getExternalId() {
    return getName();
  }

  @Nullable
  @Override
  public String getName() {
    switch (getResourceName()) {
      case "spot":
        return DataMapHelper.findString(getDelegate(), "MarketingSpot[0].spotName").orElse(null);
      case "espot":
        return DataMapHelper.findString(getDelegate(), "MarketingSpotData[0].eSpotName").orElse(null);
      default:
        return null;
    }
  }

  @Nullable
  @Override
  public String getDescription() {
    switch (getResourceName()) {
      case "spot":
        return DataMapHelper.findString(getDelegate(), "MarketingSpot[0].description").orElse(null);
      case "espot":
        return getName();
      default:
        return null;
    }
  }

  @Override
  public String getExternalTechId() {
    if (externalTechId == null) {
      externalTechId = DataMapHelper.findString(getDelegate(), "MarketingSpotData[0].marketingSpotIdentifier")
              .orElse(null);
    }
    if (externalTechId == null) {
      externalTechId = DataMapHelper.findString(getDelegate(), "MarketingSpot[0].spotId").orElse(null);
    }
    return externalTechId;
  }

  protected String getResourceName() {
    return DataMapHelper.findString(getDelegate(), "resourceName").orElse(null);
  }

  @NonNull
  @Override
  public List<CommerceObject> getEntities() {
    // noinspection unchecked
    List<Map<String, Object>> activities =
            DataMapHelper.getList(getDelegate(), "MarketingSpotData[0].baseMarketingSpotActivityData");

    return activities.stream()
            .map(this::readBaseMarketingSpotDataType)
            .filter(Objects::nonNull)
            .collect(toList());
  }

  @Nullable
  private CommerceObject readBaseMarketingSpotDataType(@NonNull Map<String, Object> activity) {
    String baseMarketingSpotDataType = (String) activity.get("baseMarketingSpotDataType");
    switch (baseMarketingSpotDataType) {
      case "CatalogEntry":
        return readCatalogEntry(activity);
      case "CatalogGroup":
        return readCatalogGroup(activity);
      case "MarketingContent":
        return readMarketingContent(activity);
      default:
        return null;
    }
  }

  @Nullable
  protected CommerceBean readCatalogEntry(@NonNull Map<String, Object> activity) {
    String productId = (String) activity.get("productId");
    if (productId == null) {
      return null;
    }

    CommerceId id = getCommerceIdProvider().formatProductTechId(getCatalogAlias(), productId);
    return getCatalogService().findProductById(id, getContext());
  }

  @Nullable
  protected CommerceBean readCatalogGroup(@NonNull Map<String, Object> activity) {
    String categoryId = (String) activity.get("categoryId");
    if (categoryId == null) {
      return null;
    }

    CommerceId id = getCommerceIdProvider().formatCategoryTechId(getCatalogAlias(), categoryId);
    return getCatalogService().findCategoryById(id, getContext());
  }

  @Nullable
  protected CommerceObject readMarketingContent(@NonNull Map<String, Object> activity) {
    String contentFormatName = DataMapHelper.findString(activity, "contentFormatName").orElse(null);
    switch (!StringUtils.isEmpty(contentFormatName) ? contentFormatName : "") {
      case CONTENT_FORMAT_FILE:
        return getMarketingImage(activity);
      case CONTENT_FORMAT_TEXT:
        return getMarketingText(activity);
      default:
        LOG.warn("Unknown marketing content format: '{}'", contentFormatName);
        return null;
    }
  }

  @NonNull
  protected MarketingImage getMarketingImage(@NonNull Map<String, Object> activity) {
    String name = DataMapHelper.findString(activity, "attachmentDescription.attachmentName").orElse(null);
    String shortText = DataMapHelper.findString(activity, "attachmentDescription.attachmentShortDescription")
            .orElse(null);
    Locale currentLocale = StoreContextHelper.getLocale(StoreContextHelper.getCurrentContextOrThrow());
    String currentLanguageId = "-1";

    String value = getCatalogService().getLanguageId(currentLocale);
    if (!value.isEmpty()) {
      currentLanguageId = value;
    }

    String attachmentAssetPath = null;
    List attachments = (List) activity.get("attachmentAsset");
    if (attachments != null) {
      for (Object a : attachments) {
        Map attachment = (Map) a;
        List languageList = (List) attachment.get("attachmentAssetLanguage");
        if (languageList != null && !languageList.isEmpty() && languageList.get(0).equals(currentLanguageId)) {
          attachmentAssetPath = (String) attachment.get("attachmentAssetPath");
        }
      }
    }
    if (attachmentAssetPath == null && attachments != null && !attachments.isEmpty()) {
      Map attachment = (Map) attachments.get(0);
      attachmentAssetPath = (String) attachment.get("attachmentAssetPath");
    }
    String thumbnailUrl = null;
    if (attachmentAssetPath != null) {
      thumbnailUrl = getWcsAssetUrl(attachmentAssetPath);
    }

    return new MarketingImage(name, shortText, thumbnailUrl);
  }

  @NonNull
  protected MarketingText getMarketingText(@NonNull Map<String, Object> activity) {
    String text = DataMapHelper.findString(activity, "marketingContentDescription.marketingText").orElse(null);
    if (text == null) {
      //"makingText" is a typo by IBM in fep7...
      text = DataMapHelper.findString(activity, "marketingContentDescription.maketingText").orElse(null);
    }
    return new MarketingText(text);
  }

  @Nullable
  protected String getWcsAssetUrl(@Nullable String suffix) {
    if (suffix == null || suffix.isEmpty()) {
      return null;
    }

    try {
      if (suffix.startsWith("http")) {
        URL assetUrl = new URL(suffix);
        return assetUrl.toExternalForm();
      } else {
        URL baseUrl = new URL(getCatalogService().getWcsAssetsUrl(getContext()));
        URL assetUrl = new URL(baseUrl, suffix);
        return assetUrl.toExternalForm();
      }
    } catch (MalformedURLException e) {
      LOG.warn("Cannot assemble default image url for marketing content: " + suffix + "(" + e.getMessage() + ")");
    }

    return null;
  }
}
