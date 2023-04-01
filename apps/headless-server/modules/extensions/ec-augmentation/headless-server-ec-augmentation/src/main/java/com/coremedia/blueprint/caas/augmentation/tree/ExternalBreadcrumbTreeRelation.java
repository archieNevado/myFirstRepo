package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.tree.TreeRelation;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Collection;
import java.util.List;

@DefaultAnnotation(NonNull.class)
public class ExternalBreadcrumbTreeRelation implements TreeRelation<String> {

  private List<String> breadcrumb;

  public ExternalBreadcrumbTreeRelation(List<String> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  @Override
  public Collection<String> getChildrenOf(String parent) {
    List<String> breadcrumb = getBreadcrumb();
    int indexOfParent = breadcrumb.indexOf(parent);
    if (indexOfParent < 0) {
      throw new IllegalArgumentException(String.format("Could not find %s in %s", parent, String.join(" / ", breadcrumb)));
    }
    return indexOfParent + 1 < breadcrumb.size() ? List.of(breadcrumb.get(indexOfParent + 1)) : List.of();
  }

  @Override
  public String getParentOf(String child) {
    List<String> breadcrumb = getBreadcrumb();
    int indexOfChild = breadcrumb.indexOf(child);
    if (indexOfChild < 0) {
      throw new IllegalArgumentException(String.format("Could not find %s in %s", child, String.join(" / ", breadcrumb)));
    }
    return indexOfChild > 0 ? breadcrumb.get(indexOfChild - 1) : null;
  }

  @Override
  public String getParentUnchecked(String child) {
    return getParentOf(child);
  }

  @Override
  public List<String> pathToRoot(String child) {
    List<String> breadcrumb = getBreadcrumb();
    int indexOfChild = breadcrumb.indexOf(child);
    if (indexOfChild < 0) {
      return List.of();
    }
    return breadcrumb.subList(0, indexOfChild + 1);
  }

  @Override
  public boolean isRoot(String item) {
    return getBreadcrumb().indexOf(item) == 0;
  }

  @Override
  public boolean isApplicable(String item) {
    return getBreadcrumb().contains(item);
  }

  public List<String> getBreadcrumb() {
    return breadcrumb;
  }

  public void setBreadcrumb(List<String> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }
}
