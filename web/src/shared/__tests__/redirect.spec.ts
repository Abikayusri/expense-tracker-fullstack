import { describe, expect, it } from 'vitest'
import { safeRedirect } from '../redirect'

describe('safeRedirect', () => {
  it('keeps in-app paths', () => {
    expect(safeRedirect('/transactions?month=2026-11')).toBe('/transactions?month=2026-11')
  })

  it('falls back to / for anything else', () => {
    expect(safeRedirect(undefined)).toBe('/')
    expect(safeRedirect('https://evil.example')).toBe('/')
    expect(safeRedirect('//evil.example')).toBe('/')
    expect(safeRedirect(['/a'])).toBe('/')
  })
})
