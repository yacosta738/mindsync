import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import Logo from '@/components/atoms/Logo.vue';

describe('Logo', () => {
  it('renders properly', () => {
    const wrapper = mount(Logo);
    expect(wrapper.text()).toContain('MindSync');
  });
});
