import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import SimpleLayout from '@/components/templates/SimpleLayout.vue';

describe('SimpleLayout', () => {
  it('renders properly', () => {
    const wrapper = mount(SimpleLayout, {
      slots: {
        default: 'Main Content',
      },
    });
    const componentText = wrapper.text();
    expect(componentText).toContain('Main Content');
  });
});
