import { useEffect, useMemo, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { assetUrl, onImgError, ErrorBox, formatDate, Loading, Modal, NEXT_STATUS, Pager, SearchBox, STATUS_LABEL, StatusPill, useLoad } from '../../components/admin/AdminUI.jsx'

const PAGE_SIZE = 10
const TABS = ['ALL', 'PLACED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED']
const RANGES = [['all', 'All Time'], ['today', 'Today'], ['7', 'Last 7 days'], ['30', 'Last 30 days']]

function customerOf(order) { return order.address?.fullName || order.userEmail }

export default function AdminOrders() {
  const { money, toast } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.orders, [])

  useEffect(() => {
    const refresh = () => reload()
    const interval = window.setInterval(refresh, 12000)
    window.addEventListener('focus', refresh)
    return () => {
      window.clearInterval(interval)
      window.removeEventListener('focus', refresh)
    }
  }, [reload])
  const [tab, setTab] = useState('ALL')
  const [q, setQ] = useState('')
  const [range, setRange] = useState('all')
  const [page, setPage] = useState(1)
  const [selected, setSelected] = useState(null)
  const [busy, setBusy] = useState(false)

  const orders = data || []
  const counts = useMemo(() => {
    const c = { ALL: orders.length }
    orders.forEach((o) => { c[o.status] = (c[o.status] || 0) + 1 })
    return c
  }, [orders])

  const filtered = useMemo(() => {
    const needle = q.trim().toLowerCase().replace('#', '')
    const now = Date.now()
    const startOfToday = new Date(); startOfToday.setHours(0, 0, 0, 0)
    return orders.filter((o) => {
      if (tab !== 'ALL' && o.status !== tab) return false
      if (needle && !`${o.orderNumber} ${customerOf(o)} ${o.userEmail}`.toLowerCase().includes(needle)) return false
      const created = new Date(o.createdAt).getTime()
      if (range === 'today') return created >= startOfToday.getTime()
      if (range !== 'all') return created >= now - Number(range) * 86400000
      return true
    })
  }, [orders, tab, q, range])
  const current = Math.min(page, Math.max(1, Math.ceil(filtered.length / PAGE_SIZE)))
  const rows = filtered.slice((current - 1) * PAGE_SIZE, current * PAGE_SIZE)

  async function changeStatus(order, status) {
    setBusy(true)
    try {
      const updated = await adminApi.updateOrderStatus(order.orderNumber, status)
      toast(`Order #${order.orderNumber} marked ${STATUS_LABEL[status]}`)
      setSelected(updated)
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head"><div><h1>Orders</h1><p>View and manage customer orders.</p></div></div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-tabs" role="tablist">
        {TABS.map((t) => (
          <button key={t} role="tab" aria-selected={tab === t} className={tab === t ? 'on' : ''} onClick={() => { setTab(t); setPage(1) }}>
            {t === 'ALL' ? 'All Orders' : STATUS_LABEL[t]} ({counts[t] || 0})
          </button>
        ))}
      </div>
      <div className="adm-toolbar">
        <SearchBox value={q} onChange={(v) => { setQ(v); setPage(1) }} placeholder="Search by Order ID or Customer..." />
        <select className="adm-select" value={range} onChange={(e) => { setRange(e.target.value); setPage(1) }} aria-label="Date range">
          {RANGES.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
      </div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : rows.length === 0 ? <div className="adm-empty">No orders found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>Order ID</th><th>Customer</th><th>Total</th><th>Status</th><th>Date</th><th>Action</th></tr></thead>
              <tbody>{rows.map((o) => (
                <tr key={o.orderNumber}>
                  <td><strong>#{o.orderNumber}</strong></td><td>{customerOf(o)}</td><td>{money(o.total)}</td>
                  <td><StatusPill status={o.status} /></td><td>{formatDate(o.createdAt)}</td>
                  <td><button className="adm-btn small ghost" onClick={() => setSelected(o)}>View</button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
        <Pager page={current} pageSize={PAGE_SIZE} total={filtered.length} onPage={setPage} />
      </div></section>

      {selected && (
        <Modal wide title={`Order #${selected.orderNumber}`} onClose={() => setSelected(null)}
          footer={<>
            {(NEXT_STATUS[selected.status] || []).map((next) => (
              <button key={next} disabled={busy} className={`adm-btn${next === 'CANCELLED' ? ' danger' : ''}`} onClick={() => changeStatus(selected, next)}>
                {next === 'CANCELLED' ? 'Cancel order' : `Mark as ${STATUS_LABEL[next]}`}
              </button>
            ))}
            <button className="adm-btn ghost" onClick={() => setSelected(null)}>Close</button>
          </>}>
          <dl className="adm-kv">
            <dt>Status</dt><dd><StatusPill status={selected.status} /></dd>
            <dt>Placed on</dt><dd>{formatDate(selected.createdAt, true)}</dd>
            <dt>Customer</dt><dd>{customerOf(selected)} ({selected.userEmail})</dd>
            <dt>Phone</dt><dd>{selected.address?.phone || '—'}</dd>
            <dt>Ship to</dt><dd>{[selected.address?.address, selected.address?.city, selected.address?.province, selected.address?.postalCode].filter(Boolean).join(', ') || '—'}</dd>
            <dt>Delivery</dt><dd>{selected.deliveryLabel || selected.delivery}</dd>
            <dt>Payment</dt><dd>{selected.paymentMethod}</dd>
          </dl>
          <div className="adm-sub">Items</div>
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th></th><th>Product</th><th>Price</th><th>Qty</th><th>Subtotal</th></tr></thead>
              <tbody>{(selected.items || []).map((item, i) => (
                <tr key={`${item.productId}-${i}`}>
                  <td><img className="adm-thumb" src={assetUrl(item.image)} alt="" onError={onImgError} /></td>
                  <td>{item.name}</td><td>{money(item.price)}</td><td>{item.quantity}</td><td>{money(item.price * item.quantity)}</td>
                </tr>
              ))}</tbody>
            </table>
          </div>
          <dl className="adm-kv" style={{ marginTop: 12 }}>
            <dt>Subtotal</dt><dd>{money(selected.subtotal)}</dd>
            <dt>Shipping</dt><dd>{money(selected.shipping)}</dd>
            <dt>Discount</dt><dd>− {money(selected.discount)}{selected.coupon ? ` (${selected.coupon})` : ''}</dd>
            <dt>Total</dt><dd>{money(selected.total)}</dd>
          </dl>
        </Modal>
      )}
    </>
  )
}
