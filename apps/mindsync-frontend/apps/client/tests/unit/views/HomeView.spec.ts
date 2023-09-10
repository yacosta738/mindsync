import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import HomeView from '@/views/HomeView.vue';

describe('HomeView', () => {
  it('renders properly', () => {
    const wrapper = mount(HomeView);
    const componentText = wrapper.text();
    expect(componentText).toContain('');
  });
});
