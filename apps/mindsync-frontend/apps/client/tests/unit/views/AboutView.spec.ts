import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import AboutView from '@/views/AboutView.vue';

describe('AboutView', () => {
  it('renders properly', () => {
    const wrapper = mount(AboutView);
    expect(wrapper.find('h1').text()).toBe('This is an about page');
  });
});
