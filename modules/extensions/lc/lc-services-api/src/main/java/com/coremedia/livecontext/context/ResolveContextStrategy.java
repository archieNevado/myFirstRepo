package com.coremedia.livecontext.context;


import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface ResolveContextStrategy {
  @Nullable
  LiveContextNavigation resolveContext(@NonNull Site site, @NonNull CommerceBean commerceBean);
}
