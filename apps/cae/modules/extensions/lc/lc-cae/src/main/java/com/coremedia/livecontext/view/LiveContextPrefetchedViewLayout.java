package com.coremedia.livecontext.view;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

interface LiveContextPrefetchedViewLayout {

  @Nullable
  Content getLayout();

  @Nullable
  List<String> getPrefetchedViews();

}
