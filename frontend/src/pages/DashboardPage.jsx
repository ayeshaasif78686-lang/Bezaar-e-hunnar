import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext.jsx'
import { apiRequest } from '../services/api.js'

export default function DashboardPage() {
  const { user } = useAuth()
  const [profile, setProfile] = useState(null)
  const [dashboard, setDashboard] = useState(null)
  const [error, setError] = useState('')
  const [proxyResult, setProxyResult] = useState('')

  useEffect(() => {
    async function load() {
      try {
        const [me, dash] = await Promise.all([
          apiRequest('/users/me'),
          apiRequest(`/dashboard/${user.role.toLowerCase()}`)
        ])
        setProfile(me)
        setDashboard(dash)
      } catch (err) {
        setError(err.message)
      }
    }
    load()
  }, [user.role])

  async function testProxy(area) {
    setProxyResult('Checking...')
    try {
      const data = await apiRequest(`/dashboard/${area}`)
      setProxyResult(`Allowed: ${data.title}`)
    } catch (err) {
      setProxyResult(`Blocked: ${err.message}`)
    }
  }

  if (error) {
    return <main className="dashboard-page"><div className="error-box">{error}</div></main>
  }

  return (
    <main className="dashboard-page">
      <section className="dashboard-hero">
        <div>
          <p className="eyebrow">SPRINT 1 AUTHENTICATED AREA</p>
          <h1>{dashboard?.title || 'Loading dashboard...'}</h1>
          <p>Welcome, {profile?.name || user.name}. Your role is <strong>{user.role}</strong>.</p>
        </div>
        <div className="role-badge">{user.role}</div>
      </section>

      <section className="dashboard-grid">
        <article className="panel">
          <p className="eyebrow">PROFILE FROM MONGODB</p>
          <h2>Account</h2>
          <dl className="profile-list">
            <div><dt>Name</dt><dd>{profile?.name || '...'}</dd></div>
            <div><dt>Email</dt><dd>{profile?.email || '...'}</dd></div>
            <div><dt>Role</dt><dd>{profile?.role || '...'}</dd></div>
          </dl>
        </article>

        <article className="panel">
          <p className="eyebrow">ROLE FEATURES</p>
          <h2>Coming in later sprints</h2>
          <ul className="clean-list">
            {(dashboard?.features || []).map((feature) => <li key={feature}>{feature}</li>)}
          </ul>
        </article>
      </section>

      <section className="proxy-demo panel">
        <p className="eyebrow">DESIGN PATTERN 01</p>
        <h2>Proxy Pattern Live Demo</h2>
        <p>
          These buttons call protected dashboard resources. The proxy only allows the area matching your account role.
        </p>
        <div className="proxy-buttons">
          <button onClick={() => testProxy('buyer')}>Try Buyer</button>
          <button onClick={() => testProxy('seller')}>Try Seller</button>
          <button onClick={() => testProxy('admin')}>Try Admin</button>
        </div>
        {proxyResult && <div className="proxy-result">{proxyResult}</div>}
      </section>
    </main>
  )
}
