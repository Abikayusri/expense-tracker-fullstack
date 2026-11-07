export function formatRupiah(amount: number): string {
  const sign = amount < 0 ? '-' : ''
  return `${sign}Rp${Math.abs(amount).toLocaleString('id-ID')}`
}

export function currentMonth(now: Date = new Date()): string {
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}

export function today(now: Date = new Date()): string {
  return `${currentMonth(now)}-${String(now.getDate()).padStart(2, '0')}`
}

export function formatDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year!, month! - 1, day!).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric' })
}
