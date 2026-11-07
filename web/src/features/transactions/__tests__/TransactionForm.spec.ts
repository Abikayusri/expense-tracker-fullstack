import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import TransactionForm from '../TransactionForm.vue'
import type { Category } from '@/features/categories/api'

const categories: Category[] = [
  { id: 1, name: 'Makan', type: 'EXPENSE', monthlyBudget: null },
  { id: 2, name: 'Gaji', type: 'INCOME', monthlyBudget: null },
]

async function fill(amount: string, note = '') {
  const wrapper = mount(TransactionForm, { props: { categories } })
  await wrapper.get('select[name=categoryId]').setValue('1')
  await wrapper.get('input[name=amount]').setValue(amount)
  await wrapper.get('input[name=date]').setValue('2026-11-10')
  await wrapper.get('input[name=note]').setValue(note)
  await wrapper.get('form').trigger('submit')
  return wrapper
}

describe('TransactionForm', () => {
  it.each(['25000.5', '0', '-100', '', 'abc', '1e3x'])('rejects amount %j', async (amount) => {
    const wrapper = await fill(amount)

    expect(wrapper.emitted('submit')).toBeUndefined()
    expect(wrapper.text()).toContain('Jumlah harus bilangan bulat lebih dari 0')
  })

  it('emits a clean payload for valid input', async () => {
    const wrapper = await fill(' 25000 ', '   ')

    expect(wrapper.emitted('submit')![0]).toEqual([{ categoryId: 1, amount: 25000, date: '2026-11-10', note: null }])
  })

  it('requires a category', async () => {
    const wrapper = mount(TransactionForm, { props: { categories } })
    await wrapper.get('input[name=amount]').setValue('1000')
    await wrapper.get('form').trigger('submit')

    expect(wrapper.emitted('submit')).toBeUndefined()
    expect(wrapper.text()).toContain('Pilih kategori')
  })

  it('limits the note to 255 characters', async () => {
    const wrapper = await fill('1000', 'x'.repeat(256))

    expect(wrapper.emitted('submit')).toBeUndefined()
    expect(wrapper.text()).toContain('Catatan maksimal 255 karakter')
  })

  it('prefills when editing', () => {
    const wrapper = mount(TransactionForm, {
      props: { categories, initial: { id: 9, categoryId: 2, amount: 5000000, date: '2026-11-01', note: 'Gaji Nov' } },
    })

    expect((wrapper.get('input[name=amount]').element as HTMLInputElement).value).toBe('5000000')
    expect((wrapper.get('select[name=categoryId]').element as HTMLSelectElement).value).toBe('2')
    expect(wrapper.text()).toContain('Ubah transaksi')
  })
})
