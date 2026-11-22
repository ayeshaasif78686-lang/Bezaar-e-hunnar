import { useEffect, useMemo, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { ErrorBox, formatDate, Loading, Modal, Pager, SearchBox, useLoad } from '../../components/admin/AdminUI.jsx'

const PAGE_SIZE = 10
// Same list as the backend (AdminService.CUSTOM_ORDER_STATUSES).
const STATUSES = ['Request received', 'In review', 'Quote sent', 'In progress', 'Completed', 'Declined']
const COLOR = { 'Request received': 'amber', 'In review': 'blue', 'Quote sent': 'blue', 'In progress': 'blue', Completed: 'green', Declined: 'red' }
const statusOf = (request) => request.status || STATUSES[0]

// Requests sent from the storefront's Custom Orders page.
export default function AdminCustomOrders() {
  const { toast } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.customOrders, [])

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
  const [page, setPage] = useState(1)
  const [selected, setSelected] = useState(null)
  const [status, setStatus] = useState('')
  const [busy, setBusy] = useState(false)

  const requests = data || []
  const counts = useMemo(() => {
    const c = { ALL: requests.length }
    requests.forEach((r) => { c[statusOf(r)] = (c[statusOf(r)] || 0) + 1 })
    return c
  }, [requests])

  const filtered = useMemo(() => {
    const needle = q.trim().toLowerCase()
    return requests.filter((r) => (tab === 'ALL' || statusOf(r) === tab)
      && (!needle || `${r.reference} ${r.name} ${r.email} ${r.type}`.toLowerCase().includes(needle)))
  }, [requests, tab, q])
  const current = Math.min(page, Math.max(1, Math.ceil(filtered.length / PAGE_SIZE)))
  const rows = filtered.slice((current - 1) * PAGE_SIZE, current * PAGE_SIZE)

  function open(request) { setSelected(request); setStatus(statusOf(request)) }

  async function save() {
    setBusy(true)
    try {
      const updated = await adminApi.setCustomOrderStatus(selected.id, status)
      toast(`Status changed to "${updated.status}"`)
      setSelected(updated)
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head"><div><h1>Custom Orders</h1><p>Requests for made-to-order products.</p></div></div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-tabs" role="tablist">
        {['ALL', ...STATUSES].map((t) => (
          <button key={t} role="tab" aria-selected={tab === t} className={tab === t ? 'on' : ''} onClick={() => { setTab(t); setPage(1) }}>
            {t === 'ALL' ? 'All Requests' : t} ({counts[t] || 0})
          </button>
        ))}
      </div>
      <div className="adm-toolbar"><SearchBox value={q} onChange={(v) => { setQ(v); setPage(1) }} placeholder="Search by reference, name, email or type..." /></div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : rows.length === 0 ? <div className="adm-empty">No custom order requests found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>Reference</th><th>Customer</th><th>Product type</th><th>Budget</th><th>Received</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>{rows.map((r) => (
                <tr key={r.id}>
                  <td><strong>{r.reference || '—'}</strong></td>
                  <td>{r.name}<br /><small>{r.email}</small></td>
                  <td>{r.type}</td>
                  <td>{r.budget || '—'}</td>
                  <td>{formatDate(r.createdAt)}</td>
                  <td><span className={`adm-pill ${COLOR[statusOf(r)] || 'grey'}`}>{statusOf(r)}</span></td>
                  <td><button className="adm-btn small ghost" onClick={() => open(r)}>View</button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
        <Pager page={current} pageSize={PAGE_SIZE} total={filtered.length} onPage={setPage} />
      </div></section>

      {selected && (
        <Modal wide title={`Request ${selected.reference || ''}`} onClose={() => setSelected(null)}
          footer={<>
            <a className="adm-btn ghost" href={`mailto:${selected.email}?subject=${encodeURIComponent(`Your custom order request ${selected.reference || ''}`)}`}>Email customer</a>
            <button className="adm-btn" disabled={busy || status === statusOf(selected)} onClick={save}>{busy ? 'Saving…' : 'Update status'}</button>
            <button className="adm-btn ghost" onClick={() => setSelected(null)}>Close</button>
          </>}>
          <dl className="adm-kv">
            <dt>Customer</dt><dd>{selected.name}</dd>
            <dt>Email</dt><dd>{selected.email}</dd>
            <dt>Product type</dt><dd>{selected.type}</dd>
            <dt>Style</dt><dd>{selected.style || '—'}</dd>
            <dt>Budget</dt><dd>{selected.budget || '—'}</dd>
            <dt>Received</dt><dd>{formatDate(selected.createdAt, true)}</dd>
            <dt>Status</dt>
            <dd>
              <select className="adm-select" value={status} onChange={(e) => setStatus(e.target.value)} aria-label="Request status">
                {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </dd>
          </dl>
          <div className="adm-sub">Details</div>
          <p className="adm-msg-body">{selected.details}</p>
        </Modal>
      )}
    </>
  )
}
