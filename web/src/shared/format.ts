export function formatRupiah(amount: number): string {
  const sign = amount < 0 ? '-' : ''
  return `${sign}Rp${Math.abs(amount).toLocaleString('id-ID')}`
}

export function currentMonth(now: Date = new Date()): string {
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}
