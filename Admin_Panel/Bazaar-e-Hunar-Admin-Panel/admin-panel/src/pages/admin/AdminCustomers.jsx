import { useEffect, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { ErrorBox, formatDate, Icon, Loading, Modal, SearchBox, StatusPill, useLoad } from '../../components/admin/AdminUI.jsx'

export default function AdminCustomers() {
  const { money, toast } = useAdmin()
  const [q, setQ] = useState('')
  const [debounced, setDebounced] = useState('')
  useEffect(() => { const t = setTimeout(() => setDebounced(q), 250); return () => clearTimeout(t) }, [q])
  const { data, loading, error, reload } = useLoad(() => adminApi.customers(debounced), [debounced])
  const [detail, setDetail] = useState(null)
  const [busy, setBusy] = useState(false)

  async function open(id) {
    try { setDetail(await adminApi.customer(id)) } catch (e) { toast(e.message, 'error') }
  }
  async function toggle() {
    setBusy(true)
    try {
      const updated = await adminApi.setUserActive(detail.id, !detail.active)
      setDetail({ ...detail, active: updated.active })
      toast(updated.active ? 'Customer activated' : 'Customer deactivated')
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head"><div><h1>Customers</h1><p>Manage registered customers.</p></div></div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-toolbar"><SearchBox value={q} onChange={setQ} placeholder="Search by name or email..." /></div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : (data || []).length === 0 ? <div className="adm-empty">No customers found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>#</th><th>Customer Name</th><th>Email</th><th>Orders</th><th>Joined On</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>{data.map((c, i) => (
                <tr key={c.id}>
                  <td>{i + 1}</td><td><strong>{c.name}</strong></td><td>{c.email}</td><td>{c.orders}</td><td>{formatDate(c.joinedAt)}</td>
                  <td><span className={`adm-pill ${c.active ? 'green' : 'red'}`}>{c.active ? 'Active' : 'Deactivated'}</span></td>
                  <td><button className="adm-act" aria-label={`View ${c.name}`} onClick={() => open(c.id)}><Icon name="eye" /></button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </div></section>

      {detail && (
        <Modal wide title={detail.name} onClose={() => setDetail(null)}
          footer={<>
            <button className={`adm-btn${detail.active ? ' danger' : ''}`} disabled={busy} onClick={toggle}>{detail.active ? 'Deactivate account' : 'Activate account'}</button>
            <button className="adm-btn ghost" onClick={() => setDetail(null)}>Close</button>
          </>}>
          <dl className="adm-kv">
            <dt>Email</dt><dd>{detail.email}</dd>
            <dt>Phone</dt><dd>{detail.phone || '—'}</dd>
            <dt>Address</dt><dd>{[detail.address, detail.city, detail.province].filter(Boolean).join(', ') || '—'}</dd>
            <dt>Joined</dt><dd>{formatDate(detail.joinedAt)}</dd>
            <dt>Orders</dt><dd>{detail.orders}</dd>
            <dt>Total spent</dt><dd>{money(detail.totalSpent)}</dd>
            <dt>Account</dt><dd><span className={`adm-pill ${detail.active ? 'green' : 'red'}`}>{detail.active ? 'Active' : 'Deactivated'}</span></dd>
          </dl>
          <div className="adm-sub">Order history</div>
          {detail.orderList.length === 0 ? <div className="adm-empty">No orders yet.</div> : (
            <div className="adm-table-wrap"><table className="adm-table">
              <thead><tr><th>Order</th><th>Date</th><th>Total</th><th>Status</th></tr></thead>
              <tbody>{detail.orderList.map((o) => <tr key={o.orderNumber}><td>#{o.orderNumber}</td><td>{formatDate(o.createdAt)}</td><td>{money(o.total)}</td><td><StatusPill status={o.status} /></td></tr>)}</tbody>
            </table></div>
          )}
        </Modal>
      )}
    </>
  )
}
