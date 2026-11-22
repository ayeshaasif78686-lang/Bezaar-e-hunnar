import { useCallback, useEffect, useState } from 'react'
import cloudinaryImageMap from '../../services/cloudinaryImageMap.json'

// The storefront (original project) runs separately; product images saved as "/assets/..." live there.
export const STORE_URL = (import.meta.env.VITE_STORE_URL || 'http://localhost:5173').replace(/\/$/, '')
export const PLACEHOLDER = 'data:image/svg+xml;utf8,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="64" height="64"><rect width="64" height="64" fill="#efe0c2"/><path d="M18 44l9-11 7 8 5-6 7 9z" fill="#c9a96e"/><circle cx="24" cy="22" r="5" fill="#c9a96e"/></svg>')
export function assetUrl(src) {
  if (!src || typeof src !== 'string') return PLACEHOLDER
  const value = src.trim()
  if (/^(https?:|data:|blob:)/i.test(value)) return value
  // Products synced to Cloudinary are indexed by category/file. Resolve those first
  // so the admin panel does not depend on the storefront's local assets directory.
  const normalized = value.replace(/^\/+/, '').replace(/^assets\//, '').replace(/^products\//, '')
  const cloudinaryUrl = cloudinaryImageMap[normalized] || cloudinaryImageMap[value] ||
    cloudinaryImageMap[`products/${normalized}`] || cloudinaryImageMap[`/${value.replace(/^\/+/, '')}`]
  if (cloudinaryUrl) return cloudinaryUrl
  return `${STORE_URL}${value.startsWith('/') ? '' : '/'}${value}`
}
export const onImgError = (event) => {
  const image = event.currentTarget
  if (image.dataset.fallbackTried !== '1') {
    image.dataset.fallbackTried = '1'
    const original = image.dataset.originalSrc || image.getAttribute('src') || ''
    const localPath = original.includes('/assets/') ? original.slice(original.indexOf('/assets/')) : ''
    if (localPath && !image.src.startsWith(STORE_URL)) {
      image.src = `${STORE_URL}${localPath}`
      return
    }
  }
  if (image.src !== PLACEHOLDER) image.src = PLACEHOLDER
}

export function Logo({ name = 'Bazaar-e-Hunar' }) {
  const [failed, setFailed] = useState(false)
  return failed
    ? <span className="adm-wordmark">{name}</span>
    : <img className="adm-brand-logo-image" src="/bazaar-logo-white.png" alt={name} onError={() => setFailed(true)} />
}

// ---------- icons (simple stroke icons) ----------
const PATHS = {
  dashboard: 'M3 3h8v8H3zM13 3h8v5h-8zM13 10h8v11h-8zM3 13h8v8H3z',
  products: 'M12 2 3 7v10l9 5 9-5V7zM3 7l9 5 9-5M12 12v10',
  orders: 'M6 2h12l2 4v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6zM4 6h16M9 10a3 3 0 0 0 6 0',
  categories: 'M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z',
  customers: 'M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM4 21a8 8 0 0 1 16 0',
  sellers: 'M3 9l2-5h14l2 5M3 9v11h18V9M3 9a3 3 0 0 0 6 0 3 3 0 0 0 6 0 3 3 0 0 0 6 0M9 20v-6h6v6',
  reports: 'M4 20V10M10 20V4M16 20v-7M22 20H2',
  settings: 'M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6zM19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z',
  logout: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9',
  bell: 'M18 8a6 6 0 1 0-12 0c0 7-3 9-3 9h18s-3-2-3-9M13.7 21a2 2 0 0 1-3.4 0',
  search: 'M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16zM21 21l-4.3-4.3',
  plus: 'M12 5v14M5 12h14',
  edit: 'M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z',
  trash: 'M3 6h18M8 6V4a1 1 0 0 1 1-1h6a1 1 0 0 1 1 1v2M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6M10 11v6M14 11v6',
  eye: 'M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12zM12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z',
  calendar: 'M4 5h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1zM3 10h18M8 3v4M16 3v4',
  cart: 'M6 6h15l-2 9H8zM6 6 5 3H2M9 20a1 1 0 1 0 0-2 1 1 0 0 0 0 2zM18 20a1 1 0 1 0 0-2 1 1 0 0 0 0 2z',
  upload: 'M4 16v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3M12 15V3M7 8l5-5 5 5',
  store: 'M3 9l2-5h14l2 5M4 9v11h16V9M9 20v-6h6v6',
  card: 'M3 6h18a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1zM2 10h20',
  download: 'M4 16v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3M12 3v12M7 10l5 5 5-5',
  menu: 'M3 6h18M3 12h18M3 18h18',
  messages: 'M3 5h18a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1zM2.5 6.5 12 13l9.5-6.5',
  custom: 'M12 3l2.2 5.8L20 11l-5.8 2.2L12 19l-2.2-5.8L4 11l5.8-2.2z'
}

export function Icon({ name, className }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d={PATHS[name] || ''} />
    </svg>
  )
}

// ---------- order status helpers ----------
export const STATUS_LABEL = { PLACED: 'Pending', PROCESSING: 'Processing', SHIPPED: 'Shipped', DELIVERED: 'Delivered', CANCELLED: 'Cancelled' }
export const STATUS_COLOR = { PLACED: 'amber', PROCESSING: 'blue', SHIPPED: 'blue', DELIVERED: 'green', CANCELLED: 'red' }
export const STATUS_HEX = { PLACED: '#c98a1b', PROCESSING: '#8a5a9b', SHIPPED: '#2f6f9f', DELIVERED: '#3f7d4e', CANCELLED: '#b3261e' }
// Mirrors the State pattern on the server: which statuses an order may move to next.
export const NEXT_STATUS = { PLACED: ['PROCESSING', 'CANCELLED'], PROCESSING: ['SHIPPED', 'CANCELLED'], SHIPPED: ['DELIVERED'], DELIVERED: [], CANCELLED: [] }

export function StatusPill({ status }) {
  return <span className={`adm-pill ${STATUS_COLOR[status] || 'grey'}`}>{STATUS_LABEL[status] || status}</span>
}

export function formatDate(value, withTime = false) {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '—'
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', ...(withTime ? { hour: 'numeric', minute: '2-digit' } : {}) })
}

// ---------- data loading ----------
export function useLoad(loader, deps = []) {
  const [state, setState] = useState({ data: null, loading: true, error: '' })
  const run = useCallback(() => {
    setState((s) => ({ ...s, loading: true, error: '' }))
    return loader()
      .then((data) => setState({ data, loading: false, error: '' }))
      .catch((error) => setState((s) => ({ data: s.data, loading: false, error: error.message || 'Something went wrong' })))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)
  useEffect(() => { run() }, [run])
  return { ...state, reload: run, setData: (data) => setState((s) => ({ ...s, data })) }
}

export function Loading({ text = 'Loading…' }) { return <div className="adm-spin">{text}</div> }
export function ErrorBox({ message, onRetry }) {
  if (!message) return null
  return (
    <div className="adm-error" role="alert">
      <span>{message}</span>
      {onRetry && <button className="adm-btn small ghost" onClick={onRetry}>Retry</button>}
    </div>
  )
}

// ---------- modal / confirm / pager ----------
export function Modal({ title, onClose, children, footer, wide }) {
  useEffect(() => {
    const onKey = (event) => { if (event.key === 'Escape') onClose() }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])
  return (
    <div className="adm-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose() }}>
      <div className={`adm-modal${wide ? ' wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
        <header><h3>{title}</h3><button type="button" onClick={onClose} aria-label="Close">×</button></header>
        <div className="body">{children}</div>
        {footer && <footer>{footer}</footer>}
      </div>
    </div>
  )
}

export function ConfirmModal({ title = 'Are you sure?', message, confirmText = 'Delete', busy, onConfirm, onClose }) {
  return (
    <Modal title={title} onClose={onClose} footer={<>
      <button className="adm-btn ghost" onClick={onClose}>Cancel</button>
      <button className="adm-btn danger" disabled={busy} onClick={onConfirm}>{busy ? 'Please wait…' : confirmText}</button>
    </>}>
      <p style={{ margin: 0 }}>{message}</p>
    </Modal>
  )
}

export function Pager({ page, pageSize, total, onPage }) {
  const pages = Math.max(1, Math.ceil(total / pageSize))
  const from = total === 0 ? 0 : (page - 1) * pageSize + 1
  const to = Math.min(total, page * pageSize)
  const numbers = []
  for (let p = 1; p <= pages; p++) {
    if (p === 1 || p === pages || Math.abs(p - page) <= 1) numbers.push(p)
    else if (numbers[numbers.length - 1] !== '…') numbers.push('…')
  }
  return (
    <div className="adm-pager">
      <span>Showing {from}–{to} of {total}</span>
      <div className="adm-pages">
        <button disabled={page <= 1} onClick={() => onPage(page - 1)} aria-label="Previous page">‹</button>
        {numbers.map((n, i) => n === '…'
          ? <button key={`gap${i}`} disabled>…</button>
          : <button key={n} className={n === page ? 'on' : ''} onClick={() => onPage(n)}>{n}</button>)}
        <button disabled={page >= pages} onClick={() => onPage(page + 1)} aria-label="Next page">›</button>
      </div>
    </div>
  )
}

export function SearchBox({ value, onChange, placeholder }) {
  return (
    <label className="adm-search">
      <Icon name="search" />
      <input value={value} onChange={(e) => onChange(e.target.value)} placeholder={placeholder} aria-label={placeholder} />
    </label>
  )
}

export function Field({ label, required, error, hint, children }) {
  return (
    <div className="adm-field">
      <label>{label}{required && <i> *</i>}</label>
      {children}
      {hint && !error && <span className="hint">{hint}</span>}
      {error && <span className="err">{error}</span>}
    </div>
  )
}

// ---------- charts (plain SVG, no extra dependencies) ----------
export function LineChart({ data, format = (v) => String(v) }) {
  const [hover, setHover] = useState(null)
  const W = 640, H = 250, L = 52, R = 14, T = 14, B = 30
  const max = Math.max(1, ...data.map((d) => d.value))
  const niceMax = niceCeil(max)
  const x = (i) => L + (data.length <= 1 ? 0 : (i * (W - L - R)) / (data.length - 1))
  const y = (v) => T + (H - T - B) * (1 - v / niceMax)
  const line = data.map((d, i) => `${i ? 'L' : 'M'}${x(i).toFixed(1)},${y(d.value).toFixed(1)}`).join(' ')
  const area = `${line} L${x(data.length - 1).toFixed(1)},${H - B} L${x(0).toFixed(1)},${H - B} Z`
  const ticks = [0, 1, 2, 3, 4].map((t) => (niceMax * t) / 4)
  const step = Math.max(1, Math.ceil(data.length / 6))
  return (
    <div className="adm-chart">
      <svg viewBox={`0 0 ${W} ${H}`} role="img" aria-label="Sales over time" onMouseLeave={() => setHover(null)}>
        <defs>
          <linearGradient id="admArea" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0" stopColor="#b3414c" stopOpacity=".45" /><stop offset="1" stopColor="#b3414c" stopOpacity=".02" />
          </linearGradient>
        </defs>
        {ticks.map((t) => (
          <g key={t}>
            <line x1={L} x2={W - R} y1={y(t)} y2={y(t)} stroke="#dcc392" strokeDasharray="3 4" />
            <text x={L - 8} y={y(t) + 4} textAnchor="end">{shortNumber(t)}</text>
          </g>
        ))}
        {data.map((d, i) => (i % step === 0 || i === data.length - 1) && <text key={d.label} x={x(i)} y={H - 8} textAnchor="middle">{d.label}</text>)}
        <path d={area} fill="url(#admArea)" />
        <path d={line} fill="none" stroke="#8a1f2c" strokeWidth="2.4" strokeLinejoin="round" />
        {data.map((d, i) => (
          <g key={i} onMouseEnter={() => setHover(i)}>
            <rect x={x(i) - 8} y={T} width="16" height={H - T - B} fill="transparent" />
            {(hover === i || d.value > 0) && <circle cx={x(i)} cy={y(d.value)} r={hover === i ? 5 : 2.6} fill="#8a1f2c" stroke="#fbf0df" strokeWidth="1.5" />}
          </g>
        ))}
        {hover !== null && (
          <g transform={`translate(${Math.min(Math.max(x(hover), 80), W - 80)},${Math.max(y(data[hover].value) - 12, 28)})`}>
            <rect x="-62" y="-24" width="124" height="38" rx="7" fill="#4a0d14" />
            <text x="0" y="-10" textAnchor="middle" style={{ fill: '#e2c99a' }}>{data[hover].label}</text>
            <text x="0" y="6" textAnchor="middle" style={{ fill: '#fff', fontWeight: 700 }}>{format(data[hover].value)}</text>
          </g>
        )}
      </svg>
    </div>
  )
}

export function Donut({ segments, centerLabel = 'Orders' }) {
  const total = segments.reduce((sum, s) => sum + s.value, 0)
  const R = 62, C = 2 * Math.PI * R
  let offset = 0
  return (
    <div className="adm-donut-wrap">
      <svg className="adm-donut" viewBox="0 0 170 170" role="img" aria-label="Breakdown chart">
        <circle cx="85" cy="85" r={R} fill="none" stroke="#e8dcc3" strokeWidth="24" />
        {total > 0 && segments.map((s) => {
          const len = (s.value / total) * C
          const circle = <circle key={s.label} cx="85" cy="85" r={R} fill="none" stroke={s.color} strokeWidth="24" strokeDasharray={`${len} ${C - len}`} strokeDashoffset={-offset} transform="rotate(-90 85 85)" />
          offset += len
          return circle
        })}
        <text className="big" x="85" y="88" textAnchor="middle">{total}</text>
        <text className="sm" x="85" y="106" textAnchor="middle">{centerLabel}</text>
      </svg>
      <ul className="adm-legend">
        {segments.map((s) => (
          <li key={s.label}><i style={{ background: s.color }} />{s.label}<b>{total ? Math.round((s.value / total) * 100) : 0}%</b></li>
        ))}
      </ul>
    </div>
  )
}

function niceCeil(value) {
  const pow = 10 ** Math.floor(Math.log10(value))
  const n = value / pow
  return (n <= 1 ? 1 : n <= 2 ? 2 : n <= 2.5 ? 2.5 : n <= 5 ? 5 : 10) * pow
}
function shortNumber(n) {
  if (n >= 1e6) return `${+(n / 1e6).toFixed(1)}M`
  if (n >= 1e3) return `${+(n / 1e3).toFixed(1)}k`
  return String(Math.round(n))
}

export function downloadCsv(filename, rows) {
  const csv = rows.map((row) => row.map((cell) => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(',')).join('\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
