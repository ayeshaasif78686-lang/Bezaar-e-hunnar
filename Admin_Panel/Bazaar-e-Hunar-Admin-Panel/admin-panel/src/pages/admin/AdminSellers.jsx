import { useEffect, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { ErrorBox, Field, formatDate, Icon, Loading, Modal, SearchBox, useLoad } from '../../components/admin/AdminUI.jsx'

const BLANK = { name: '', email: '', phone: '', password: '' }

export default function AdminSellers() {
  const { money, toast } = useAdmin()
  const [q, setQ] = useState('')
  const [debounced, setDebounced] = useState('')
  useEffect(() => { const t = setTimeout(() => setDebounced(q), 250); return () => clearTimeout(t) }, [q])
  const { data, loading, error, reload } = useLoad(() => adminApi.sellers(debounced), [debounced])
  const [adding, setAdding] = useState(false)
  const [form, setForm] = useState(BLANK)
  const [formError, setFormError] = useState('')
  const [detail, setDetail] = useState(null)
  const [busy, setBusy] = useState(false)

  async function create(event) {
    event.preventDefault()
    setFormError('')
    setBusy(true)
    try {
      await adminApi.createSeller(form)
      toast('Seller added')
      setAdding(false)
      setForm(BLANK)
      reload()
    } catch (e) { setFormError(e.message) } finally { setBusy(false) }
  }
  async function open(id) {
    try { setDetail(await adminApi.seller(id)) } catch (e) { toast(e.message, 'error') }
  }
  async function toggle() {
    setBusy(true)
    try {
      const updated = await adminApi.setUserActive(detail.id, !detail.active)
      setDetail({ ...detail, active: updated.active })
      toast(updated.active ? 'Seller activated' : 'Seller deactivated')
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }
  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  return (
    <>
      <div className="adm-head">
        <div><h1>Sellers</h1><p>Manage registered sellers.</p></div>
        <button className="adm-btn" onClick={() => { setFormError(''); setAdding(true) }}><Icon name="plus" />Add Seller</button>
      </div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-toolbar"><SearchBox value={q} onChange={setQ} placeholder="Search seller by name or email..." /></div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : (data || []).length === 0 ? <div className="adm-empty">No sellers found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>#</th><th>Seller Name</th><th>Email</th><th>Products</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>{data.map((s, i) => (
                <tr key={s.id}>
                  <td>{i + 1}</td><td><strong>{s.name}</strong></td><td>{s.email}</td><td>{s.products}</td>
                  <td><span className={`adm-pill ${s.active ? 'green' : 'red'}`}>{s.active ? 'Active' : 'Inactive'}</span></td>
                  <td><button className="adm-act" aria-label={`View ${s.name}`} onClick={() => open(s.id)}><Icon name="eye" /></button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </div></section>

      {adding && (
        <Modal title="Add Seller" onClose={() => setAdding(false)}
          footer={<><button className="adm-btn ghost" onClick={() => setAdding(false)}>Cancel</button><button className="adm-btn" form="sellerForm" disabled={busy}>{busy ? 'Saving…' : 'Add Seller'}</button></>}>
          {formError && <div className="adm-error" role="alert">{formError}</div>}
          <form id="sellerForm" onSubmit={create}>
            <Field label="Seller name" required><input className="adm-input" value={form.name} onChange={set('name')} maxLength={80} autoFocus /></Field>
            <div className="adm-grid2">
              <Field label="Email" required><input className="adm-input" type="email" value={form.email} onChange={set('email')} /></Field>
              <Field label="Phone"><input className="adm-input" value={form.phone} onChange={set('phone')} /></Field>
            </div>
            <Field label="Temporary password" required hint="At least 6 characters. The seller can log in with it."><input className="adm-input" type="password" value={form.password} onChange={set('password')} autoComplete="new-password" /></Field>
          </form>
        </Modal>
      )}

      {detail && (
        <Modal wide title={detail.name} onClose={() => setDetail(null)}
          footer={<>
            <button className={`adm-btn${detail.active ? ' danger' : ''}`} disabled={busy} onClick={toggle}>{detail.active ? 'Deactivate seller' : 'Activate seller'}</button>
            <button className="adm-btn ghost" onClick={() => setDetail(null)}>Close</button>
          </>}>
          <dl className="adm-kv">
            <dt>Email</dt><dd>{detail.email}</dd><dt>Phone</dt><dd>{detail.phone || '—'}</dd>
            <dt>Joined</dt><dd>{formatDate(detail.joinedAt)}</dd>
            <dt>Status</dt><dd><span className={`adm-pill ${detail.active ? 'green' : 'red'}`}>{detail.active ? 'Active' : 'Inactive'}</span></dd>
          </dl>
          <div className="adm-sub">Products ({detail.products})</div>
          {detail.productList.length === 0 ? <div className="adm-empty">No products assigned yet. Choose this seller when adding a product.</div> : (
            <div className="adm-table-wrap"><table className="adm-table">
              <thead><tr><th>Product</th><th>Price</th><th>Stock</th></tr></thead>
              <tbody>{detail.productList.map((p) => <tr key={p.id}><td>{p.name}</td><td>{money(p.price)}</td><td>{p.stock}</td></tr>)}</tbody>
            </table></div>
          )}
        </Modal>
      )}
    </>
  )
}
