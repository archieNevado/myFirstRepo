package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.common.personaldata.PersonalData;
import com.coremedia.common.personaldata.PolyPersonalData;
import org.apache.commons.lang3.StringUtils;

public class CMPersonImpl extends CMPersonBase {
  @Override
  public @PersonalData String getDisplayName() {
    @PersonalData String displayName = super.getDisplayName();
    if (!StringUtils.isBlank(displayName)) {
      return displayName.trim();
    }
    return (nullToEmpty(getFirstName()) + " " + nullToEmpty(getLastName())).trim();
  }

  private static @PolyPersonalData String nullToEmpty(@PolyPersonalData String str) {
    return str==null ? "" : str;
  }
}
