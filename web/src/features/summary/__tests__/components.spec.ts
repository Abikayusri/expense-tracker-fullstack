import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import OverBudgetBanner from '../OverBudgetBanner.vue'
import BudgetProgress from '../BudgetProgress.vue'
import type { CategorySummary } from '../api'

const item = (overrides: Partial<CategorySummary>): CategorySummary => ({
  categoryId: 1,
  name: 'Makan',
  type: 'EXPENSE',
  spent: 0,
  budget: 1_000_000,
  overBudget: false,
  ...overrides,
})

describe('OverBudgetBanner', () => {
  it('renders nothing when no category is over budget', () => {
    const wrapper = mount(OverBudgetBanner, { props: { items: [item({ spent: 900_000 })] } })

    expect(wrapper.find('[role=alert]').exists()).toBe(false)
  })

  it('lists every over-budget category', () => {
    const wrapper = mount(OverBudgetBanner, {
      props: {
        items: [
          item({ categoryId: 1, name: 'Makan', spent: 1_250_000, overBudget: true }),
          item({ categoryId: 2, name: 'Hobi', spent: 150_000, budget: 100_000, overBudget: true }),
          item({ categoryId: 3, name: 'Transport', spent: 10_000 }),
        ],
      },
    })

    expect(wrapper.findAll('[data-over-budget]').map((p) => p.text())).toEqual([
      'Pengeluaran Makan sudah Rp1.250.000, melebihi budget Rp1.000.000',
      'Pengeluaran Hobi sudah Rp150.000, melebihi budget Rp100.000',
    ])
  })
})

describe('BudgetProgress', () => {
  it('caps the bar at 100% and turns red when over budget', () => {
    const wrapper = mount(BudgetProgress, { props: { item: item({ spent: 1_500_000, overBudget: true }) } })
    const bar = wrapper.get('[role=progressbar]')

    expect(bar.attributes('aria-valuenow')).toBe('100')
    expect(bar.find('div').classes()).toContain('bg-red-600')
  })

  it('shows the partial percentage', () => {
    const wrapper = mount(BudgetProgress, { props: { item: item({ spent: 333_333 }) } })

    expect(wrapper.get('[role=progressbar]').attributes('aria-valuenow')).toBe('33')
    expect(wrapper.text()).toContain('Rp333.333 / Rp1.000.000')
  })
})
