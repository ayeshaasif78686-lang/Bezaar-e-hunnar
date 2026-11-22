import { useEffect, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { ErrorBox, Field, Icon, Loading, useLoad } from '../../components/admin/AdminUI.jsx'

const TABS = [
  ['general', 'General', 'settings'],
  ['store', 'Store Details', 'store'],
  ['access', 'Users & Access', 'customers'],
  ['payment', 'Payment', 'card'],
  ['notifications', 'Notifications', 'bell']
]

function Switch({ label, hint, checked, onChange }) {
  return (
    <label className="adm-switch">
      <span>{label}{hint && <small>{hint}</small>}</span>
      <input type="checkbox" checked={Boolean(checked)} onChange={(event) => onChange(event.target.checked)} />
    </label>
  )
}

export default function AdminSettings() {
  const { toast, setSettings } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.settings, [])
  const [tab, setTab] = useState('general')
  const [form, setForm] = useState(null)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  useEffect(() => { if (data) setForm({ ...data }) }, [data])
  if (loading && !form) return <Loading />
  if (!form) return <ErrorBox message={error || 'Settings could not be loaded'} onRetry={reload} />

  const text = (key) => (event) => setForm((f) => ({ ...f, [key]: event.target.value }))
  const flag = (key) => (value) => setForm((f) => ({ ...f, [key]: value }))

  function validate() {
    const next = {}
    if (!String(form.storeName || '').trim()) next.storeName = 'Store name is required'
    const email = String(form.storeEmail || '').trim()
    if (email && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) next.storeEmail = 'Enter a valid email address'
    const threshold = Number(form.lowStockThreshold)
    if (!Number.isInteger(threshold) || threshold < 0) next.lowStockThreshold = 'Enter a whole number (0 or more)'
    setErrors(next)
    if (next.storeName || next.storeEmail) setTab('general')
    else if (next.lowStockThreshold) setTab('notifications')
    return Object.keys(next).length === 0
  }

  async function save(event) {
    event.preventDefault()
    if (!validate()) return
    setSaving(true)
    try {
      const saved = await adminApi.saveSettings({ ...form, lowStockThreshold: Number(form.lowStockThreshold) })
      setForm(saved)
      setSettings(saved) // updates the currency symbol / store name across the panel straight away
      toast('Settings updated')
    } catch (e) {
      toast(e.message, 'error')
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <div className="adm-head"><div><h1>Settings</h1><p>Manage your store settings.</p></div></div>
      <form className="adm-settings" onSubmit={save} noValidate>
        <nav className="adm-card adm-set-tabs" aria-label="Settings sections">
          {TABS.map(([id, label, icon]) => (
            <button type="button" key={id} className={tab === id ? 'on' : ''} onClick={() => setTab(id)}><Icon name={icon} />{label}</button>
          ))}
        </nav>

        <section className="adm-card"><div className="adm-card-body">
          {tab === 'general' && (<>
            <h2 className="adm-h2">General Settings</h2>
            <Field label="Store Name" required error={errors.storeName}><input className="adm-input" value={form.storeName || ''} onChange={text('storeName')} /></Field>
            <Field label="Store Email" error={errors.storeEmail}><input className="adm-input" type="email" value={form.storeEmail || ''} onChange={text('storeEmail')} /></Field>
            <Field label="Currency">
              <select className="adm-select" value={form.currency} onChange={text('currency')}>
                <option value="PKR">PKR (Rs.)</option><option value="USD">USD ($)</option><option value="EUR">EUR (€)</option>
                <option value="GBP">GBP (£)</option><option value="AED">AED</option>
              </select>
            </Field>
          </>)}

          {tab === 'store' && (<>
            <h2 className="adm-h2">Store Details</h2>
            <Field label="Tagline"><input className="adm-input" value={form.storeTagline || ''} onChange={text('storeTagline')} /></Field>
            <Field label="Phone"><input className="adm-input" value={form.storePhone || ''} onChange={text('storePhone')} placeholder="+92 300 1234567" /></Field>
            <Field label="Address"><input className="adm-input" value={form.storeAddress || ''} onChange={text('storeAddress')} /></Field>
            <Field label="City"><input className="adm-input" value={form.storeCity || ''} onChange={text('storeCity')} /></Field>
            <Field label="Country"><input className="adm-input" value={form.storeCountry || ''} onChange={text('storeCountry')} /></Field>
          </>)}

          {tab === 'access' && (<>
            <h2 className="adm-h2">Users & Access</h2>
            <Switch label="Allow new customer registration" hint="When off, nobody can create a new account." checked={form.allowRegistration} onChange={flag('allowRegistration')} />
            <Switch label="Allow seller sign-ups" hint="When off, new accounts cannot register as sellers." checked={form.allowSellerSignup} onChange={flag('allowSellerSignup')} />
            <Switch label="Require email verification" hint="Saved for later use." checked={form.requireEmailVerification} onChange={flag('requireEmailVerification')} />
          </>)}

          {tab === 'payment' && (<>
            <h2 className="adm-h2">Payment Methods</h2>
            <p className="adm-muted">Customers cannot place an order with a method that is switched off.</p>
            <Switch label="Cash on Delivery" checked={form.payCod} onChange={flag('payCod')} />
            <Switch label="JazzCash" checked={form.payJazzCash} onChange={flag('payJazzCash')} />
            <Switch label="EasyPaisa" checked={form.payEasyPaisa} onChange={flag('payEasyPaisa')} />
            <Switch label="Credit / Debit Card" checked={form.payCard} onChange={flag('payCard')} />
          </>)}

          {tab === 'notifications' && (<>
            <h2 className="adm-h2">Notifications</h2>
            <Switch label="New order alerts" hint="Show pending orders in the bell menu." checked={form.notifyNewOrder} onChange={flag('notifyNewOrder')} />
            <Switch label="Low stock alerts" hint="Show products that are running out." checked={form.notifyLowStock} onChange={flag('notifyLowStock')} />
            <Switch label="New customer alerts" hint="Saved for later use." checked={form.notifyNewCustomer} onChange={flag('notifyNewCustomer')} />
            <div style={{ marginTop: 14 }}>
              <Field label="Low stock threshold" error={errors.lowStockThreshold} hint="A product is 'low' when its stock is at or below this number.">
                <input className="adm-input" inputMode="numeric" value={form.lowStockThreshold ?? ''} onChange={text('lowStockThreshold')} />
              </Field>
            </div>
          </>)}

          <div className="adm-form-actions">
            <button type="button" className="adm-btn ghost" onClick={() => { setForm({ ...data }); setErrors({}) }}>Reset</button>
            <button type="submit" className="adm-btn" disabled={saving}>{saving ? 'Saving…' : 'Update Settings'}</button>
          </div>
        </div></section>
      </form>
    </>
  )
}
