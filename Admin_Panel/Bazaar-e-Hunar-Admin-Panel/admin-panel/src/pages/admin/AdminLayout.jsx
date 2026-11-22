import { useEffect, useRef, useState } from 'react'
import { Link, Navigate, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import { AdminProvider, useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { formatDate, Icon, Logo, STORE_URL } from '../../components/admin/AdminUI.jsx'
import '../../admin.css'

const NAV = [
  ['/', 'Dashboard', 'dashboard', true],
  ['/products', 'Products', 'products'],
  ['/orders', 'Orders', 'orders'],
  ['/categories', 'Categories', 'categories'],
  ['/customers', 'Customers', 'customers'],
  ['/sellers', 'Sellers', 'sellers'],
  ['/messages', 'Messages', 'messages'],
  ['/custom-orders', 'Custom Orders', 'custom'],
  ['/reports', 'Reports', 'reports'],
  ['/settings', 'Settings', 'settings']
]

function Skyline() {
  return (
    <svg className="adm-skyline" viewBox="0 0 216 190" preserveAspectRatio="xMidYMax slice" fill="currentColor" aria-hidden="true">
      <path d="M0 190V150h14v-14h6v-18l4-8 4 8v18h6v14h10v-30c0-10 8-16 14-26 6 10 14 16 14 26v30h8v-60l3-14 3 14v60h6v-24h10v-20c0-8 6-12 10-20 4 8 10 12 10 20v20h8v-44l3-16 3 16v44h12v-18h14v18h22v40z" />
    </svg>
  )
}

function Shell() {
  const { user, logout } = useAuth()
  const { settings } = useAdmin()
  const navigate = useNavigate()
  const location = useLocation()
  const [navOpen, setNavOpen] = useState(false)
  const [menu, setMenu] = useState(null) // 'bell' | 'user' | null
  const [alerts, setAlerts] = useState({ newOrders: [], pendingCount: 0, lowStock: [], newCustomers: [] })
  const popRef = useRef(null)

  useEffect(() => { setNavOpen(false); setMenu(null) }, [location.pathname])
  useEffect(() => {
    let alive = true
    const load = () => adminApi.notifications().then((data) => alive && setAlerts(data)).catch(() => {})
    load()
    const timer = setInterval(load, 15000)
    return () => { alive = false; clearInterval(timer) }
  }, [location.pathname])
  useEffect(() => {
    const close = (event) => { if (popRef.current && !popRef.current.contains(event.target)) setMenu(null) }
    document.addEventListener('mousedown', close)
    return () => document.removeEventListener('mousedown', close)
  }, [])

  const alertCount = (alerts.pendingCount || 0) + (alerts.lowStock?.length || 0) + (alerts.newCustomers?.length || 0)
  const initial = (user?.name || 'A').trim().charAt(0).toUpperCase()

  function signOut() { logout(); navigate('/login') }

  return (
    <div className={`adm${navOpen ? ' nav-open' : ''}`}>
      <aside className="adm-side">
        <div className="adm-brand">
          <span className="adm-logo"><Logo name={settings.storeName} /></span>
          <small>Admin Panel</small>
        </div>
        <nav className="adm-nav" aria-label="Admin">
          {NAV.map(([to, label, icon, end]) => (
            <NavLink key={to} to={to} end={end}><Icon name={icon} />{label}</NavLink>
          ))}
        </nav>
        <Skyline />
        <div className="adm-side-foot">
          <a href={STORE_URL} target="_blank" rel="noreferrer" className="adm-store-link"><Icon name="store" />View Store</a>
          <button className="adm-logout" onClick={signOut}><Icon name="logout" />Logout</button>
        </div>
      </aside>

      <div className="adm-main">
        <header className="adm-top" ref={popRef}>
          <button className="adm-menu-btn" onClick={() => setNavOpen((v) => !v)} aria-label="Toggle menu"><Icon name="menu" className="" /></button>
          <div className="adm-rel">
            <button className="adm-icon-btn" onClick={() => setMenu(menu === 'bell' ? null : 'bell')} aria-label="Notifications">
              <Icon name="bell" />{alertCount > 0 && <span className="adm-badge">{alertCount > 99 ? '99+' : alertCount}</span>}
            </button>
            {menu === 'bell' && (
              <div className="adm-pop" style={{ minWidth: 290 }}>
                <h4>New orders</h4>
                {alerts.newOrders?.length ? alerts.newOrders.map((o) => (
                  <Link key={o.orderNumber} to="/orders" className="row">#{o.orderNumber} · {o.customer}<small>{formatDate(o.createdAt, true)}</small></Link>
                )) : <div style={{ padding: 8, color: '#7a6150' }}>No pending orders</div>}
                {alerts.newCustomers?.length > 0 && (<>
                  <h4>New customers (7 days)</h4>
                  {alerts.newCustomers.map((c) => (
                    <Link key={c.id} to="/customers" className="row">{c.name}<small>{formatDate(c.joinedAt, true)}</small></Link>
                  ))}
                </>)}
                <h4>Low stock</h4>
                {alerts.lowStock?.length ? alerts.lowStock.map((p) => (
                  <Link key={p.id} to={`/products/${encodeURIComponent(p.id)}/edit`} className="row">{p.name}<small>{p.stock} left</small></Link>
                )) : <div style={{ padding: 8, color: '#7a6150' }}>All products are well stocked</div>}
              </div>
            )}
          </div>
          <div className="adm-rel">
            <button className="adm-user-btn" onClick={() => setMenu(menu === 'user' ? null : 'user')}>
              <span className="adm-avatar">{initial}</span>Admin ▾
            </button>
            {menu === 'user' && (
              <div className="adm-pop">
                <div className="row" style={{ padding: 8 }}><strong>{user?.name}</strong><small>{user?.email}</small></div>
                <Link to="/settings">Store settings</Link>
                <a href={STORE_URL} target="_blank" rel="noreferrer">View store</a>
                <button onClick={signOut}>Logout</button>
              </div>
            )}
          </div>
        </header>
        <main className="adm-content"><Outlet /></main>
      </div>
    </div>
  )
}

// Only signed-in admins may open the panel; everyone else is sent away.
export default function AdminLayout() {
  const { user } = useAuth()
  const location = useLocation()
  if (!user || String(user.role).toUpperCase() !== 'ADMIN') return <Navigate to="/login" state={{ from: location.pathname }} replace />
  return <AdminProvider><Shell /></AdminProvider>
}
