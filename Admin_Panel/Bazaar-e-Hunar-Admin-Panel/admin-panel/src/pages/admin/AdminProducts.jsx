import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { assetUrl, onImgError, ConfirmModal, ErrorBox, Icon, Loading, Pager, SearchBox, useLoad } from '../../components/admin/AdminUI.jsx'

const PAGE_SIZE = 8

export function productStatus(p) {
  if (!p.active) return { label: 'Inactive', cls: 'grey' }
  if (p.stock <= 0) return { label: 'Out of stock', cls: 'red' }
  return { label: 'Active', cls: 'green' }
}

export default function AdminProducts() {
  const { money, toast } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.products, [])

  useEffect(() => {
    const refresh = () => reload()
    const interval = window.setInterval(refresh, 12000)
    window.addEventListener('focus', refresh)
    return () => {
      window.clearInterval(interval)
      window.removeEventListener('focus', refresh)
    }
  }, [reload])
  const { data: categories } = useLoad(adminApi.categories, [])
  const [q, setQ] = useState('')
  const [category, setCategory] = useState('')
  const [page, setPage] = useState(1)
  const [toDelete, setToDelete] = useState(null)
  const [busy, setBusy] = useState(false)

  const filtered = useMemo(() => {
    const needle = q.trim().toLowerCase()
    return (data || []).filter((p) => (!category || p.category === category) && (!needle || (p.name || '').toLowerCase().includes(needle)))
  }, [data, q, category])
  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))
  const current = Math.min(page, pageCount)
  const rows = filtered.slice((current - 1) * PAGE_SIZE, current * PAGE_SIZE)

  async function toggleActive(product) {
    try {
      await adminApi.updateProduct(product.id, { ...product, active: !product.active })
      toast(`${product.name} is now ${product.active ? 'inactive' : 'active'}`)
      reload()
    } catch (e) { toast(e.message, 'error') }
  }

  async function remove() {
    setBusy(true)
    try {
      await adminApi.deleteProduct(toDelete.id)
      toast('Product deleted')
      setToDelete(null)
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head">
        <div><h1>Products</h1><p>Manage and update your products.</p></div>
        <Link to="/products/new" className="adm-btn"><Icon name="plus" />Add Product</Link>
      </div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-toolbar">
        <SearchBox value={q} onChange={(v) => { setQ(v); setPage(1) }} placeholder="Search product by name..." />
        <select className="adm-select" value={category} onChange={(e) => { setCategory(e.target.value); setPage(1) }} aria-label="Category filter">
          <option value="">All Categories</option>
          {(categories || []).map((c) => <option key={c.name} value={c.name}>{c.name}</option>)}
        </select>
      </div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : rows.length === 0 ? <div className="adm-empty">No products found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>Image</th><th>Product Name</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th>Actions</th></tr></thead>
              <tbody>
                {rows.map((p) => {
                  const status = productStatus(p)
                  return (
                    <tr key={p.id}>
                      <td><img className="adm-thumb" src={assetUrl(p.image)} data-original-src={p.image || ""} alt={p.name || "Product image"} loading="lazy" onError={onImgError} /></td>
                      <td><strong>{p.name}</strong><br /><small>{p.artisan}</small></td>
                      <td>{p.category}</td>
                      <td>{money(p.price)}</td>
                      <td>{p.stock}</td>
                      <td><button className={`adm-pill ${status.cls}`} title="Click to activate / deactivate" onClick={() => toggleActive(p)}>{status.label}</button></td>
                      <td>
                        <div className="adm-actions">
                          <Link className="adm-act" to={`/products/${encodeURIComponent(p.id)}/edit`} aria-label={`Edit ${p.name}`}><Icon name="edit" /></Link>
                          <button className="adm-act red" onClick={() => setToDelete(p)} aria-label={`Delete ${p.name}`}><Icon name="trash" /></button>
                        </div>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
        <Pager page={current} pageSize={PAGE_SIZE} total={filtered.length} onPage={setPage} />
      </div></section>
      {toDelete && <ConfirmModal message={`Delete "${toDelete.name}"? This cannot be undone.`} busy={busy} onConfirm={remove} onClose={() => setToDelete(null)} />}
    </>
  )
}
