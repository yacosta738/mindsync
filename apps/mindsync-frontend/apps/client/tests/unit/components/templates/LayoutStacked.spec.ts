import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

import LayoutStacked from '@/components/templates/LayoutStacked.vue';

describe('LayoutStacked', () => {
  it('renders properly', () => {
    const wrapper = mount(LayoutStacked, {
      slots: {
        default: 'Main Content',
      },
    });
    const componentText = wrapper.text();
    expect(componentText).toContain('Main Content');
    expect(componentText).toContain('MindSync');
    expect(componentText).toContain('Toggle');
    expect(componentText).toContain('Overview');
    expect(componentText).toContain('Pages');
    expect(componentText).toContain('Sales');
    expect(componentText).toContain('Messages');
    expect(componentText).toContain('Authentication');
  });
});
