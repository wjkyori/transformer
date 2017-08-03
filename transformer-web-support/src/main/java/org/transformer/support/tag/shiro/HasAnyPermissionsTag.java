package org.transformer.support.tag.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

/**
 * 判断是否有权限中的任何一项.
 */
public class HasAnyPermissionsTag extends PermissionTag {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4786931833148680306L;

  /** The Constant PERMISSION_NAMES_DELIMETER. */
  private static final String PERMISSION_NAMES_DELIMETER = ",";

  @Override
  protected boolean showTagBody(String permissionNames) {
    boolean hasAnyPermission = false;

    Subject subject = getSubject();

    if (subject != null) {
      // Iterate through permissions and check to see if the user has one of the permissions
      for (String permission : permissionNames.split(PERMISSION_NAMES_DELIMETER)) {

        if (subject.isPermitted(permission.trim())) {
          hasAnyPermission = true;
          break;
        }

      }
    }

    return hasAnyPermission;
  }

}
