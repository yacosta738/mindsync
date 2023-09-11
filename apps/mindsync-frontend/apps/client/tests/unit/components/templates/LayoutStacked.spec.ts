import { mount } from '@vue/test-utils';
import { beforeEach, describe, expect, it } from 'vitest';

import LayoutStacked from '@/components/templates/LayoutStacked.vue';
import { createPinia, setActivePinia } from 'pinia';

describe('LayoutStacked', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });
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
