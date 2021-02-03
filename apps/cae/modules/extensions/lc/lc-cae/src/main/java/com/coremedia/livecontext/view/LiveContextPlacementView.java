package com.coremedia.livecontext.view;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.Nullable;

interface LiveContextPlacementView {

  @Nullable
  String getView();

  @Nullable
  Content getSection();
}
