package com.coremedia.livecontext.view;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

interface LiveContextContentTypeView {

  @Nullable
  String getType();

  @Nullable
  List<String> getPrefetchedViews();
}
