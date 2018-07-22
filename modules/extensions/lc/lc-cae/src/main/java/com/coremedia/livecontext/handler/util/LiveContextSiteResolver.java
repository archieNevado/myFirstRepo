package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Locale;

public interface LiveContextSiteResolver extends SiteResolver {

  @Nullable
  Site findSiteFor(@NonNull FragmentParameters fragmentParameters);

  @Nullable
  Site findSiteFor(@NonNull String storeId, @NonNull Locale locale);
}
