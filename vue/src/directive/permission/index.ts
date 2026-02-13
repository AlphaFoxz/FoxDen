import {type Directive, type DirectiveBinding} from 'vue';
import {useUserStore} from '@/store/modules/user';
/**
 * 操作权限处理
 */
export const hasPermi: Directive = {
  mounted(element: HTMLElement, binding: DirectiveBinding) {
    const {permissions} = useUserStore();
    // 「其他角色」按钮权限校验
    const value = binding.value as unknown;
    if (value && Array.isArray(value) && value.length > 0) {
      const hasPermission = permissions.some((permi: string) => permi === '*:*:*' || value.includes(permi));
      if (!hasPermission) {
        if (element.parentNode) {
          element.remove(); // Element.parentNode.removeChild(element);
        }

        return false;
      }
    } else {
      throw new Error('check perms! Like v-has-permi="[\'system:user:add\',\'system:user:edit\']"');
    }
  },
};

/**
 * 角色权限处理
 */
export const hasRoles: Directive = {
  mounted(element: HTMLElement, binding: DirectiveBinding) {
    const value = binding.value as unknown;
    const {roles} = useUserStore();
    if (value && Array.isArray(value) && value.length > 0) {
      const hasRole = roles.some((role: string) => role === 'superadmin' || role === 'admin' || value.includes(role));
      if (!hasRole) {
        if (element.parentNode) {
          element.remove();
        }

        return false;
      }
    } else {
      throw new Error('check roles! Like v-has-roles="[\'admin\',\'test\']"');
    }
  },
};
