package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RepresentationHelper {

  public static <T> List<T> sort(List<T> list) {
    List<T> copy = new ArrayList<>(list);
    Collections.sort(copy, new IdComparator());
    return copy;
  }

  public static List<ChildRepresentation> sortChildren(List<ChildRepresentation> list) {
    List<ChildRepresentation> listCopy = new ArrayList<>(list);
    Collections.sort(listCopy, new Comparator<ChildRepresentation>() {
      public int compare(ChildRepresentation o1, ChildRepresentation o2) {
        return (o1.getDisplayName()).compareTo(o2.getDisplayName());
      }
    });
    return listCopy;
  }

  public static class IdComparator implements Comparator<Object> {

    @Override
    public int compare(Object object1, Object object2) {

      if (!(object1 instanceof CommerceBean)) {
        return -1;
      }
      if (!(object2 instanceof CommerceBean)) {
        return 1;
      }

      String id1 = ((CommerceBean) object1).getExternalId();
      String id2 = ((CommerceBean) object2).getExternalId();

      if(object1 instanceof Category && object2 instanceof Category) {
        id1 = ((Category)object1).getDisplayName();
        id2 = ((Category)object2).getDisplayName();
      }

      if (id1 == null) {
        return -1;
      }
      if (id2 == null) {
        return 1;
      }

      return id1.compareTo(id2);
    }
  }

  /**
   * add contentTimestamp parameter in order to avoid race conditions between studio and preview cae
   *
   * @return modified string if the url is a asset image url. Otherwise the same url
   */
  public static String modifyAssetImageUrl(String thumbnailUrl, ContentRepository repository) {
    if (thumbnailUrl != null && thumbnailUrl.contains(AssetService.URI_PREFIX)) {
      //set the content time stamp on the last sequence number of the repository
      // so that the cae can wait for the sequence number before answering the request
      thumbnailUrl += "?contentTimestamp=" + repository.getTimestamp().getSequenceNumber()
              + "&currentTime=" + System.currentTimeMillis();
    }

    return thumbnailUrl;
  }
}
