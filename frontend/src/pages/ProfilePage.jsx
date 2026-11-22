import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { apiRequest } from '../services/api.js'
import { useShop } from '../context/ShopContext.jsx'
import SiteFooter from '../components/SiteFooter.jsx'

const emptyForm = { name: '', phone: '', city: '', province: '', address: '', postalCode: '' }
const provinces = ['Punjab', 'Sindh', 'Khyber Pakhtunkhwa', 'Balochistan', 'Islamabad Capital Territory', 'Gilgit-Baltistan', 'Azad Kashmir']

function readLocal(key) {
  try { return JSON.parse(localStorage.getItem(key)) || {} } catch { return {} }
}

// Shrinks the chosen photo to a small square so it is cheap to keep in the browser.
function shrinkImage(file, size = 280) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onerror = () => reject(new Error('Could not read that image.'))
    reader.onload = () => {
      const img = new Image()
      img.onerror = () => reject(new Error('That file is not a valid image.'))
      img.onload = () => {
        const side = Math.min(img.width, img.height)
        const canvas = document.createElement('canvas')
        canvas.width = size
        canvas.height = size
        canvas.getContext('2d').drawImage(img, (img.width - side) / 2, (img.height - side) / 2, side, side, 0, 0, size, size)
        resolve(canvas.toDataURL('image/jpeg', 0.85))
      }
      img.src = reader.result
    }
    reader.readAsDataURL(file)
  })
}

export default function ProfilePage() {
  const { user, logout, updateUser } = useAuth()
  const { wishlistCount = 0, cartCount = 0 } = useShop()
  const navigate = useNavigate()
  const fileRef = useRef(null)
  const extrasKey = `beh_profile_${user.id || user.email}`
  const photoKey = `beh_photo_${user.id || user.email}`

  const [form, setForm] = useState({ ...emptyForm, name: user.name || '' })
  const [email, setEmail] = useState(user.email || '')
  const [photo, setPhoto] = useState(() => localStorage.getItem(photoKey) || '')
  const [status, setStatus] = useState({ type: '', text: '' })
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let alive = true
    async function load() {
      const local = readLocal(extrasKey)
      let server = null
      try { server = await apiRequest('/users/me') } catch { /* offline or older API: fall back to this device */ }
      if (!alive) return
      if (server?.email) setEmail(server.email)
      setForm((current) => ({ ...current, ...local, ...(server || {}), name: server?.name || local.name || user.name || '' }))
    }
    load()
    return () => { alive = false }
  }, [extrasKey, user.name])

  const set = (field) => (event) => setForm((current) => ({ ...current, [field]: event.target.value }))

  async function choosePhoto(event) {
    const file = event.target.files?.[0]
    event.target.value = ''
    if (!file) return
    try {
      const data = await shrinkImage(file)
      setPhoto(data)
      localStorage.setItem(photoKey, data)
      setStatus({ type: 'ok', text: 'Profile photo updated.' })
    } catch (err) {
      setStatus({ type: 'error', text: err.message })
    }
  }

  function removePhoto() {
    setPhoto('')
    localStorage.removeItem(photoKey)
  }

  async function save(event) {
    event.preventDefault()
    if (!form.name.trim()) { setStatus({ type: 'error', text: 'Please enter your name.' }); return }
    setSaving(true)
    setStatus({ type: '', text: '' })
    const clean = Object.fromEntries(Object.entries(form).map(([key, value]) => [key, String(value ?? '').trim()]))
    localStorage.setItem(extrasKey, JSON.stringify(clean))
    let onServer = true
    try {
      await apiRequest('/users/me', { method: 'PUT', body: JSON.stringify(clean) })
    } catch (err) {
      if (err.status && ![404, 405, 501].includes(err.status)) {
        setSaving(false)
        setStatus({ type: 'error', text: err.message })
        return
      }
      onServer = false
    }
    updateUser({ name: clean.name })
    setSaving(false)
    setStatus({ type: 'ok', text: onServer ? 'Your profile has been updated.' : 'Saved on this device. (The server does not accept profile updates yet.)' })
  }

  function handleLogout() {
    logout()
    navigate('/')
  }

  const initials = (form.name || user.name || '?').split(' ').filter(Boolean).slice(0, 2).map((part) => part[0].toUpperCase()).join('')

  return (
    <main className="pf-page">
      <section className="pf-banner">
        <div className="pf-banner-inner">
          <div className="pf-avatar-wrap">
            <div className="pf-avatar">{photo ? <img src={photo} alt="Your profile" /> : <span>{initials}</span>}</div>
            <button type="button" className="pf-avatar-btn" onClick={() => fileRef.current?.click()} aria-label="Change profile photo">✎</button>
            <input ref={fileRef} type="file" accept="image/*" hidden onChange={choosePhoto} />
          </div>
          <div className="pf-who">
            <p className="pf-eyebrow">✦ My Profile ✦</p>
            <h1>{form.name || user.name}</h1>
            <p>{email}</p>
          </div>
          <span className="pf-role">{user.role}</span>
        </div>
      </section>

      <div className="pf-layout">
        <form className="pf-card pf-form" onSubmit={save} noValidate>
          <h2>Personal Details</h2>
          <p className="pf-sub">Keep your details up to date so checkout is quick.</p>
          <div className="pf-grid">
            <label>Full name *<input value={form.name} onChange={set('name')} autoComplete="name" required /></label>
            <label>Email<input value={email} readOnly className="is-readonly" /></label>
            <label>Phone number<input value={form.phone} onChange={set('phone')} inputMode="tel" placeholder="03xx xxxxxxx" autoComplete="tel" /></label>
            <label>City<input value={form.city} onChange={set('city')} autoComplete="address-level2" /></label>
          </div>

          <h2 className="pf-h2-gap">Delivery Address</h2>
          <div className="pf-grid">
            <label className="pf-wide">Street address<input value={form.address} onChange={set('address')} placeholder="House / street / area" autoComplete="street-address" /></label>
            <label>Province
              <select value={form.province} onChange={set('province')}>
                <option value="">Select province</option>
                {provinces.map((name) => <option key={name} value={name}>{name}</option>)}
              </select>
            </label>
            <label>Postal code<input value={form.postalCode} onChange={set('postalCode')} inputMode="numeric" autoComplete="postal-code" /></label>
          </div>

          {status.text && <div className={`pf-status ${status.type}`} role="status">{status.text}</div>}
          <div className="pf-actions">
            <button type="submit" className="pf-save" disabled={saving}>{saving ? 'Saving…' : 'Save Changes'}</button>
            {photo && <button type="button" className="pf-ghost" onClick={removePhoto}>Remove photo</button>}
          </div>
        </form>

        <aside className="pf-side">
          <div className="pf-card">
            <h2>Quick Links</h2>
            <ul className="pf-links">
              <li><Link to="/wishlist"><span>♡ My Wishlist</span><b>{wishlistCount}</b></Link></li>
              <li><Link to="/cart"><span>Shopping Cart</span><b>{cartCount}</b></Link></li>
              <li><Link to="/custom-orders"><span>Custom Orders</span><b>→</b></Link></li>
              <li><Link to="/products"><span>Continue Shopping</span><b>→</b></Link></li>
            </ul>
          </div>
          <div className="pf-card pf-note">
            <h2>Crafted with care</h2>
            <p>Every purchase supports a Pakistani artisan and keeps traditional skills alive.</p>
            <button type="button" className="pf-ghost" onClick={handleLogout}>Log out</button>
          </div>
        </aside>
      </div>
      <SiteFooter />
    </main>
  )
}
