package com.coremedia.blueprint.cae.searchsuggestion;

import com.google.common.collect.ForwardingList;

import java.util.ArrayList;
import java.util.List;

public class Suggestions extends ForwardingList<Suggestion> {

  private List<Suggestion> delegate = new ArrayList<>();

  @Override
  public List<Suggestion> delegate() {
    return delegate;
  }


}
