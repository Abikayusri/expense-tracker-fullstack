// Only same-app paths are allowed, so a crafted ?redirect=//evil.example can't send users off-site.
export function safeRedirect(value: unknown): string {
  return typeof value === 'string' && value.startsWith('/') && !value.startsWith('//') ? value : '/'
}
