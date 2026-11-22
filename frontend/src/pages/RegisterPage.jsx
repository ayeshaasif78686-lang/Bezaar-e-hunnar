import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { AppleIcon, EyeIcon, GoogleIcon, LockIcon, MailIcon, UserIcon } from '../components/AuthIcons.jsx'

export default function RegisterPage() {
  // Every account on this storefront is a customer, so the role is fixed
  // and no longer asked from the visitor.
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'BUYER' })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(false)
  const { register } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  function updateField(event) {
    setForm((old) => ({ ...old, [event.target.name]: event.target.value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setNotice('')
    setLoading(true)
    try {
      await register(form)
      navigate(location.state?.from || '/', { replace: true })
    } catch (err) {
      if (err.data?.details) {
        setError(Object.values(err.data.details).join(' • '))
      } else {
        setError(err.message)
      }
    } finally {
      setLoading(false)
    }
  }

  function socialClick(provider) {
    setError('')
    setNotice(`${provider} sign-up is coming soon. Please create your account with email.`)
  }

  return (
    <main className="reference-auth-page register-reference-page login-reference-page">
      <div className="reference-auth-shade" />
      <section className="reference-auth-copy auth-hero-copy">
        <div className="auth-eyebrow"><span /> <b>Artisina</b> <span /></div>
        <h1>Handmade Stories,<br />Timeless Beauty</h1>
        <p className="auth-hero-script">Join us and discover heritage crafts that tell a story...</p>
        <div className="reference-auth-buttons">
          <Link to="/login" state={location.state} className="auth-explore-btn">
            Already a member? Login <i>→</i>
          </Link>
        </div>
      </section>

      <section className="reference-auth-card-wrap">
        <form className="reference-paper-card login-paper-card register-paper-card" onSubmit={handleSubmit}>
          <img className="auth-card-logo" src="/assets/bazaar-logo-new.png" alt="Bazaar-e-Hunar" />
          <h2>Create Account</h2>
          <p className="reference-script">Begin your heritage journey</p>
          <div className="reference-divider"><span />✦<span /></div>

          <label className="reference-input-row">
            <span className="field-icon"><UserIcon /></span>
            <input name="name" value={form.name} onChange={updateField} placeholder="Full name" autoComplete="name" required minLength="2" />
          </label>

          <label className="reference-input-row">
            <span className="field-icon"><MailIcon /></span>
            <input name="email" type="email" value={form.email} onChange={updateField} placeholder="Email address" autoComplete="email" required />
          </label>

          <label className="reference-input-row">
            <span className="field-icon"><LockIcon /></span>
            <input name="password" type={showPassword ? 'text' : 'password'} value={form.password} onChange={updateField} placeholder="Password (min. 6 characters)" autoComplete="new-password" required minLength="6" />
            <button type="button" className="field-eye" onClick={() => setShowPassword((old) => !old)} aria-label={showPassword ? 'Hide password' : 'Show password'}>
              <EyeIcon off={!showPassword} />
            </button>
          </label>

          {error && <div className="home-login-error">{error}</div>}
          {notice && <div className="auth-notice">{notice}</div>}

          <button type="submit" className="reference-submit" disabled={loading}>{loading ? 'Creating account...' : 'Create Account →'}</button>

          <div className="reference-or"><span />or<span /></div>
          <div className="reference-social-row auth-social-two">
            <button type="button" onClick={() => socialClick('Google')}><GoogleIcon /> Continue with Google</button>
            <button type="button" onClick={() => socialClick('Apple')}><AppleIcon /> Continue with Apple</button>
          </div>

          <p className="reference-switch">Already have an account? <Link to="/login" state={location.state}>Login</Link></p>
        </form>
      </section>
    </main>
  )
}
