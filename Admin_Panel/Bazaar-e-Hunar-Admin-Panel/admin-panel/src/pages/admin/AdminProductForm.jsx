import { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { assetUrl, ErrorBox, Field, Icon, Loading, useLoad } from '../../components/admin/AdminUI.jsx'

const EMPTY = { name: '', category: '', price: '', stock: '', description: '', artisanId: '', image: '', active: true }
const MAX_BYTES = 5 * 1024 * 1024

export default function AdminProductForm() {
  const { productId } = useParams()
  const editing = Boolean(productId)
  const navigate = useNavigate()
  const { toast } = useAdmin()
  const fileRef = useRef(null)
  const [form, setForm] = useState(EMPTY)
  const [original, setOriginal] = useState(null)
  const [file, setFile] = useState(null)
  const [preview, setPreview] = useState('')
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const [serverError, setServerError] = useState('')
  const { data: categories } = useLoad(adminApi.categories, [])
  const { data: sellers } = useLoad(() => adminApi.sellers(''), [])
  const { data: loaded, loading, error } = useLoad(() => (editing ? adminApi.product(productId) : Promise.resolve(null)), [productId])

  useEffect(() => {
    if (loaded) {
      setOriginal(loaded)
      setForm({
        name: loaded.name || '', category: loaded.category || '', price: String(loaded.price ?? ''), stock: String(loaded.stock ?? ''),
        description: loaded.description || '', artisanId: loaded.artisanId || '', image: loaded.image || '', active: loaded.active !== false
      })
    }
  }, [loaded])
  useEffect(() => {
    if (!file) return undefined
    const url = URL.createObjectURL(file)
    setPreview(url)
    return () => URL.revokeObjectURL(url)
  }, [file])

  const set = (key) => (event) => setForm((f) => ({ ...f, [key]: event.target.value }))

  function pickFile(event) {
    const chosen = event.target.files?.[0]
    if (!chosen) return
    if (!/^image\/(png|jpe?g)$/i.test(chosen.type)) { toast('Please choose a PNG or JPG image', 'error'); return }
    if (chosen.size > MAX_BYTES) { toast('Image must be 5 MB or smaller', 'error'); return }
    setFile(chosen)
  }

  function validate() {
    const next = {}
    if (form.name.trim().length < 2) next.name = 'Enter the product name'
    if (!form.category) next.category = 'Select a category'
    const price = Number(form.price)
    if (form.price === '' || !Number.isFinite(price) || price < 0 || !Number.isInteger(price)) next.price = 'Enter a valid price (whole number)'
    const stock = Number(form.stock)
    if (form.stock === '' || !Number.isInteger(stock) || stock < 0) next.stock = 'Enter a valid stock quantity'
    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function submit(event) {
    event.preventDefault()
    setServerError('')
    if (!validate()) return
    setSaving(true)
    const seller = (sellers || []).find((s) => s.id === form.artisanId)
    const payload = {
      ...(original || {}),
      name: form.name.trim(), category: form.category, price: Number(form.price), stock: Number(form.stock),
      description: form.description.trim(), image: form.image.trim() || original?.image || '',
      artisanId: seller ? seller.id : form.artisanId || original?.artisanId || '',
      artisan: seller ? seller.name : original?.artisan || 'Bazaar-e-Hunar', active: form.active
    }
    try {
      let saved = editing ? await adminApi.updateProduct(productId, payload) : await adminApi.createProduct(payload)
      if (file) {
        try {
          const uploaded = await adminApi.uploadProductImage(saved.id, file)
          saved = await adminApi.updateProduct(saved.id, { ...saved, image: uploaded.url })
        } catch (uploadError) {
          toast(`Saved, but the image upload failed: ${uploadError.message}. You can paste an image URL instead.`, 'error')
          navigate(`/products/${encodeURIComponent(saved.id)}/edit`)
          return
        }
      }
      toast(editing ? 'Product updated' : 'Product added')
      navigate('/products')
    } catch (e) {
      setServerError(e.message)
    } finally {
      setSaving(false)
    }
  }

  if (editing && loading && !loaded) return <Loading />
  const shownImage = preview || form.image

  return (
    <>
      <div className="adm-head">
        <div><h1>{editing ? 'Edit Product' : 'Add Product'}</h1><p>{editing ? 'Update this product in your store.' : 'Add a new product to your store.'}</p></div>
      </div>
      <ErrorBox message={error || serverError} />
      <form className="adm-card" onSubmit={submit} noValidate>
        <div className="adm-card-body">
          <div className="adm-form-layout">
            <div>
              <div className="adm-drop" onClick={() => fileRef.current?.click()} role="button" tabIndex={0}
                onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') fileRef.current?.click() }}>
                {shownImage && <img src={preview || assetUrl(form.image)} data-original-src={form.image || ""} alt="Product preview" />}
                {!shownImage && (
                  <div>
                    <Icon name="upload" />
                    <strong>Upload Product Images</strong>
                    <small>(PNG, JPG – Max 5MB)</small>
                    <div className="plus">+</div>
                  </div>
                )}
              </div>
              <input ref={fileRef} type="file" accept="image/png,image/jpeg" hidden onChange={pickFile} />
              <div style={{ marginTop: 12 }}>
                <Field label="Or image URL" hint="Used when no file is uploaded">
                  <input className="adm-input" value={form.image} onChange={(e) => { setFile(null); setPreview(''); set('image')(e) }} placeholder="https://…" />
                </Field>
                {file && <button type="button" className="adm-btn small ghost" onClick={() => { setFile(null); setPreview('') }}>Remove selected file</button>}
              </div>
            </div>
            <div>
              <Field label="Product Name" required error={errors.name}>
                <input className="adm-input" value={form.name} onChange={set('name')} placeholder="Enter product name" maxLength={120} />
              </Field>
              <div className="adm-grid2">
                <Field label="Category" required error={errors.category}>
                  <select className="adm-select" value={form.category} onChange={set('category')}>
                    <option value="">Select category</option>
                    {(categories || []).map((c) => <option key={c.name} value={c.name}>{c.name}</option>)}
                  </select>
                </Field>
                <Field label="Seller" hint="Optional">
                  <select className="adm-select" value={form.artisanId} onChange={set('artisanId')}>
                    <option value="">{original?.artisan && !original.artisanId ? original.artisan : 'Bazaar-e-Hunar (store)'}</option>
                    {(sellers || []).map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
                  </select>
                </Field>
                <Field label="Price (Rs.)" required error={errors.price}>
                  <input className="adm-input" inputMode="numeric" value={form.price} onChange={set('price')} placeholder="e.g. 3499" />
                </Field>
                <Field label="Stock" required error={errors.stock}>
                  <input className="adm-input" inputMode="numeric" value={form.stock} onChange={set('stock')} placeholder="e.g. 10" />
                </Field>
              </div>
              <Field label="Description">
                <textarea className="adm-textarea" value={form.description} onChange={set('description')} placeholder="Write product description…" maxLength={2000} />
              </Field>
              <label className="adm-switch" style={{ borderBottom: 0 }}>
                <span>Visible in store<small>Inactive products are hidden from ordering</small></span>
                <input type="checkbox" checked={form.active} onChange={(e) => setForm((f) => ({ ...f, active: e.target.checked }))} />
              </label>
              <div className="adm-form-actions">
                <button type="button" className="adm-btn ghost" onClick={() => navigate('/products')}>Cancel</button>
                <button type="submit" className="adm-btn" disabled={saving}>{saving ? 'Saving…' : 'Save Product'}</button>
              </div>
            </div>
          </div>
        </div>
      </form>
    </>
  )
}
