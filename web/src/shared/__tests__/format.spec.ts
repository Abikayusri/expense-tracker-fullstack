import { describe, expect, it } from 'vitest'
import { currentMonth, formatRupiah } from '../format'

describe('formatRupiah', () => {
  it('formats with Indonesian thousand separators', () => {
    expect(formatRupiah(0)).toBe('Rp0')
    expect(formatRupiah(1_250_000)).toBe('Rp1.250.000')
  })

  it('puts the minus sign before Rp', () => {
    expect(formatRupiah(-150_000)).toBe('-Rp150.000')
  })
})

describe('currentMonth', () => {
  it('pads the month', () => {
    expect(currentMonth(new Date(2026, 0, 5))).toBe('2026-01')
    expect(currentMonth(new Date(2026, 10, 30))).toBe('2026-11')
  })
})
