import { useEffect, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { assetUrl, ConfirmModal, ErrorBox, Field, Icon, Loading, Modal, useLoad } from '../../components/admin/AdminUI.jsx'

export default function AdminCategories() {
  const { toast } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.categories, [])

  useEffect(() => {
    const refresh = () => reload()
    const interval = window.setInterval(refresh, 12000)
    window.addEventListener('focus', refresh)
    return () => {
      window.clearInterval(interval)
      window.removeEventListener('focus', refresh)
    }
  }, [reload])
  const [editor, setEditor] = useState(null)   // { oldName?, name, image }
  const [toDelete, setToDelete] = useState(null)
  const [busy, setBusy] = useState(false)
  const [formError, setFormError] = useState('')

  async function save() {
    setFormError('')
    if ((editor.name || '').trim().length < 2) { setFormError('Enter a category name (at least 2 characters)'); return }
    setBusy(true)
    try {
      if (editor.oldName) await adminApi.renameCategory({ oldName: editor.oldName, name: editor.name, image: editor.image })
      else await adminApi.addCategory({ name: editor.name, image: editor.image })
      toast(editor.oldName ? 'Category updated' : 'Category added')
      setEditor(null)
      reload()
    } catch (e) { setFormError(e.message) } finally { setBusy(false) }
  }

  async function remove() {
    setBusy(true)
    try {
      await adminApi.deleteCategory(toDelete.name)
      toast('Category deleted')
      setToDelete(null)
      reload()
    } catch (e) { toast(e.message, 'error'); setToDelete(null) } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head">
        <div><h1>Categories</h1><p>Manage product categories.</p></div>
        <button className="adm-btn" onClick={() => { setFormError(''); setEditor({ name: '', image: '' }) }}><Icon name="plus" />Add Category</button>
      </div>
      <ErrorBox message={error} onRetry={reload} />
      {loading && !data ? <Loading /> : (data || []).length === 0 ? <div className="adm-empty">No categories yet.</div> : (
        <div className="adm-cats">
          {data.map((c) => (
            <article key={c.name} className="adm-card adm-cat">
              {c.image ? <img src={assetUrl(c.image)} alt={c.name} loading="lazy" onError={(e) => { e.currentTarget.style.visibility = 'hidden' }} /> : <div className="ph">{c.name.charAt(0)}</div>}
              <strong>{c.name}</strong>
              <small>({c.productCount} Products)</small>
              <div className="adm-actions">
                <button className="adm-act" aria-label={`Edit ${c.name}`} onClick={() => { setFormError(''); setEditor({ oldName: c.name, name: c.name, image: c.custom ? c.image || '' : '' }) }}><Icon name="edit" /></button>
                <button className="adm-act red" aria-label={`Delete ${c.name}`} onClick={() => setToDelete(c)}><Icon name="trash" /></button>
              </div>
            </article>
          ))}
        </div>
      )}

      {editor && (
        <Modal title={editor.oldName ? 'Edit Category' : 'Add Category'} onClose={() => setEditor(null)}
          footer={<><button className="adm-btn ghost" onClick={() => setEditor(null)}>Cancel</button><button className="adm-btn" disabled={busy} onClick={save}>{busy ? 'Saving…' : 'Save Category'}</button></>}>
          {formError && <div className="adm-error" role="alert">{formError}</div>}
          <Field label="Category name" required>
            <input className="adm-input" autoFocus value={editor.name} maxLength={60} onChange={(e) => setEditor({ ...editor, name: e.target.value })} onKeyDown={(e) => { if (e.key === 'Enter') save() }} />
          </Field>
          <Field label="Image URL" hint="Optional. If empty, the image of the first product in this category is shown.">
            <input className="adm-input" value={editor.image} onChange={(e) => setEditor({ ...editor, image: e.target.value })} placeholder="https://…" />
          </Field>
          {editor.oldName && <p style={{ margin: 0, color: '#7a6150' }}>Renaming also moves all its products to the new name.</p>}
        </Modal>
      )}
      {toDelete && (
        <ConfirmModal message={toDelete.productCount > 0
          ? `"${toDelete.name}" still has ${toDelete.productCount} product(s). Move or delete them first.`
          : `Delete the category "${toDelete.name}"?`}
          confirmText="Delete" busy={busy} onConfirm={remove} onClose={() => setToDelete(null)} />
      )}
    </>
  )
}
