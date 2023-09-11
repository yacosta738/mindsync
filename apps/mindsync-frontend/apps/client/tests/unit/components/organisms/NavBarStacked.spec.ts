import { mount } from '@vue/test-utils';
import { beforeEach, describe, expect, it } from 'vitest';

import NavBarStacked from '@/components/organisms/NavBarStacked.vue';
import { createPinia, setActivePinia } from 'pinia';

describe('NavBarStacked', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });
  it('renders properly', () => {
    const wrapper = mount(NavBarStacked);
    const componentText = wrapper.text();
    expect(componentText).toContain('MindSync');
    expect(componentText).toContain('Toggle');
  });
});
