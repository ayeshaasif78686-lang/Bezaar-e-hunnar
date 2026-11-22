import { useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { AppleIcon, EyeIcon, GoogleIcon, LockIcon, MailIcon } from '../components/AuthIcons.jsx'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [remember, setRemember] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(false)
  const emailRef = useRef(null)
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const redirectTo = location.state?.from || '/'

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setNotice('')
    setLoading(true)
    try {
      await login(email, password)
      navigate(redirectTo, { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function socialClick(provider) {
    setError('')
    setNotice(`${provider} sign-in is coming soon. Please log in with your email.`)
  }

  return (
    <main className="reference-auth-page login-reference-page">
      <div className="reference-auth-shade" />
      <section className="reference-auth-copy auth-hero-copy">
        <div className="auth-eyebrow"><span /> <b>Artisina</b> <span /></div>
        <h1>Handmade Stories,<br />Timeless Beauty</h1>
        <p className="auth-hero-script">Discover heritage crafts that tell a story...</p>
        <div className="reference-auth-buttons">
          <button type="button" className="auth-explore-btn" onClick={() => emailRef.current?.focus()}>
            Explore Collection <i>→</i>
          </button>
        </div>
      </section>

      <section className="reference-auth-card-wrap">
        <form className="reference-paper-card login-paper-card" onSubmit={handleSubmit}>
          <img className="auth-card-logo" src="/assets/bazaar-logo-new.png" alt="Bazaar-e-Hunar" />
          <h2>Welcome Back</h2>
          <p className="reference-script">Login to continue your heritage journey</p>
          <div className="reference-divider"><span />✦<span /></div>

          <label className="reference-input-row">
            <span className="field-icon"><MailIcon /></span>
            <input
              ref={emailRef}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email address"
              autoComplete="email"
              required
            />
          </label>

          <label className="reference-input-row">
            <span className="field-icon"><LockIcon /></span>
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              autoComplete="current-password"
              required
            />
            <button type="button" className="field-eye" onClick={() => setShowPassword((old) => !old)} aria-label={showPassword ? 'Hide password' : 'Show password'}>
              <EyeIcon off={!showPassword} />
            </button>
          </label>

          {error && <div className="home-login-error">{error}</div>}
          {notice && <div className="auth-notice">{notice}</div>}

          <div className="reference-login-meta">
            <label><input type="checkbox" checked={remember} onChange={(e) => setRemember(e.target.checked)} /> <span>Remember me</span></label>
            <button type="button" className="reference-text-button" onClick={() => setNotice('Password reset is coming soon. Please contact support from the Contact page after logging in.')}>Forgot password?</button>
          </div>

          <button type="submit" className="reference-submit" disabled={loading}>{loading ? 'Logging in...' : 'Login →'}</button>

          <div className="reference-or"><span />or<span /></div>
          <div className="reference-social-row auth-social-two">
            <button type="button" onClick={() => socialClick('Google')}><GoogleIcon /> Continue with Google</button>
            <button type="button" onClick={() => socialClick('Apple')}><AppleIcon /> Continue with Apple</button>
          </div>

          <p className="reference-switch">Don&apos;t have an account? <Link to="/register" state={location.state}>Sign Up</Link></p>
        </form>
      </section>
    </main>
  )
}
