import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import SideBar from '@/components/organisms/SideBar.vue';

describe('SideBar', () => {
  it('renders properly', () => {
    const wrapper = mount(SideBar);
    const componentText = wrapper.text();
    expect(componentText).toContain('Overview');
    expect(componentText).toContain('Pages');
    expect(componentText).toContain('Sales');
    expect(componentText).toContain('Messages');
    expect(componentText).toContain('Authentication');
  });
});
