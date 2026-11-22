import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [show, setShow] = useState(false)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to={location.state?.from || '/'} replace />

  async function submit(event) {
    event.preventDefault()
    setError('')
    if (!email.trim() || !password) { setError('Enter your admin email and password.'); return }
    setBusy(true)
    try {
      await login(email.trim(), password)
      navigate(location.state?.from || '/', { replace: true })
    } catch (e) {
      setError(e.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="adm-login">
      <form className="adm-login-card" onSubmit={submit} noValidate>
        <div className="adm-login-logo"><img src="/bazaar-logo-burgundy.png" alt="Bazaar-e-Hunar Handmade Stories" /></div>
        <small>Admin Panel</small>
        <h1>Welcome Back, Admin</h1>
        <p>Sign in to manage your store.</p>
        {error && <div className="adm-error" role="alert"><span>{error}</span></div>}
        <div className="adm-field"><label>Email</label>
          <input className="adm-input" type="email" autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="admin@bazaarehunar.com" autoFocus />
        </div>
        <div className="adm-field"><label>Password</label>
          <div className="adm-pass">
            <input className="adm-input" type={show ? 'text' : 'password'} autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Enter your password" />
            <button type="button" onClick={() => setShow((v) => !v)} aria-label={show ? 'Hide password' : 'Show password'}>{show ? 'Hide' : 'Show'}</button>
          </div>
        </div>
        <button className="adm-btn" type="submit" disabled={busy} style={{ width: '100%', justifyContent: 'center' }}>{busy ? 'Signing in…' : 'Sign In'}</button>
      </form>
    </div>
  )
}
