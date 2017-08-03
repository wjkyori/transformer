package org.transformer.support.tag.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

/**
 * 不包含该任何一项权限.
 */
public class LacksAnyPermissionsTag extends PermissionTag {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5473426741335022496L;

  /** The Constant PERMISSION_NAMES_DELIMETER. */
  private static final String PERMISSION_NAMES_DELIMETER = ",";

  @Override
  protected boolean showTagBody(String permissionNames) {
    boolean lacksAnyPermissions = false;
    Subject subject = getSubject();
    if (subject != null) {
      // Iterate through permissions and check to see if the user has one of the permissions
      for (String permission : permissionNames.split(PERMISSION_NAMES_DELIMETER)) {
        lacksAnyPermissions = lacksAnyPermissions || subject.isPermitted(permission.trim());
        if (lacksAnyPermissions) {
          break;
        }
      }
    }

    return !lacksAnyPermissions;
  }

}
