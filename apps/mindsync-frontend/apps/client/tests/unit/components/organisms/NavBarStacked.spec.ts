import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import NavBarStacked from '@/components/organisms/NavBarStacked.vue';

describe('NavBarStacked', () => {
  it('renders properly', () => {
    const wrapper = mount(NavBarStacked);
    const componentText = wrapper.text();
    expect(componentText).toContain('MindSync');
    expect(componentText).toContain('Toggle');
  });
});
