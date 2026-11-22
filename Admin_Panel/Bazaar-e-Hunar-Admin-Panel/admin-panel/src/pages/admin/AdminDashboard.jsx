import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { Donut, ErrorBox, formatDate, Icon, LineChart, Loading, STATUS_HEX, STATUS_LABEL, StatusPill, useLoad } from '../../components/admin/AdminUI.jsx'

function Trend({ value }) {
  if (value === undefined || value === null) return null
  const cls = value > 0 ? 'up' : value < 0 ? 'down' : 'flat'
  return <span className={`adm-trend ${cls}`}>{value > 0 ? '↑' : value < 0 ? '↓' : '•'} {Math.abs(value)}%</span>
}

export default function AdminDashboard() {
  const { user } = useAuth()
  const { money } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.dashboard, [])

  useEffect(() => {
    const refresh = () => reload()
    const interval = window.setInterval(refresh, 12000)
    window.addEventListener('focus', refresh)
    return () => {
      window.clearInterval(interval)
      window.removeEventListener('focus', refresh)
    }
  }, [reload])
  const today = new Date().toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' })

  const recentRevenue = (data?.salesSeries || []).reduce((sum, day) => sum + Number(day.value || 0), 0)
  const stats = data ? [
    ['Total Sales', money(data.totalSales), 'reports', data.salesGrowth],
    ['Total Orders', data.totalOrders.toLocaleString('en-US'), 'orders', data.ordersGrowth],
    ['Total Users', data.totalUsers.toLocaleString('en-US'), 'customers', data.usersGrowth],
    ['Total Products', data.totalProducts.toLocaleString('en-US'), 'products', null],
    ['Revenue (30 Days)', money(recentRevenue), 'reports', null]
  ] : []

  return (
    <>
      <div className="adm-head">
        <div>
          <h1>Welcome Back, {(user?.name || 'Admin').split(' ')[0]}!</h1>
          <p>Here's what's happening in your store today.</p>
        </div>
        <span className="adm-date"><Icon name="calendar" />{today}</span>
      </div>
      <ErrorBox message={error} onRetry={reload} />
      {loading && !data ? <Loading /> : data && (
        <>
          <div className="adm-stats">
            {stats.map(([label, value, icon, growth]) => (
              <div key={label} className="adm-card adm-stat">
                <Icon name={icon} className="ic" />
                <span>{label}</span><strong>{value}</strong>
                <Trend value={growth} />
              </div>
            ))}
          </div>

          <div className="adm-two">
            <section className="adm-card">
              <div className="adm-card-title">Sales Overview <small style={{ fontSize: 12, opacity: .8 }}>(last 30 days)</small></div>
              <div className="adm-card-body"><LineChart data={data.salesSeries} format={money} /></div>
            </section>
            <section className="adm-card">
              <div className="adm-card-title">Order Status</div>
              <div className="adm-card-body">
                <Donut segments={Object.entries(data.statusCounts).map(([status, value]) => ({ label: STATUS_LABEL[status], value, color: STATUS_HEX[status] }))} />
              </div>
            </section>
          </div>

          <div className="adm-two even adm-dashboard-lower">
            <section className="adm-card">
              <div className="adm-card-title">Recent Orders</div>
              <div className="adm-card-body adm-table-wrap">
                {data.recentOrders.length === 0 ? <div className="adm-empty">No orders yet.</div> : (
                  <table className="adm-table">
                    <thead><tr><th>Order</th><th>Customer</th><th>Total</th><th>Status</th></tr></thead>
                    <tbody>{data.recentOrders.map((o) => (
                      <tr key={o.orderNumber}><td>#{o.orderNumber}<br /><small>{formatDate(o.createdAt)}</small></td><td>{o.customer}</td><td>{money(o.total)}</td><td><StatusPill status={o.status} /></td></tr>
                    ))}</tbody>
                  </table>
                )}
                <div style={{ marginTop: 10 }}><Link className="adm-btn small ghost" to="/orders">View all orders</Link></div>
              </div>
            </section>
            <section className="adm-card">
              <div className="adm-card-title">Low Stock Alerts</div>
              <div className="adm-card-body">
                {data.lowStock.length === 0 ? <div className="adm-empty">All products are well stocked.</div> : (
                  <ul className="adm-lowlist">{data.lowStock.map((p) => (
                    <li key={p.id}><Link to={`/products/${encodeURIComponent(p.id)}/edit`}>{p.name}</Link><span className={`adm-pill ${p.stock === 0 ? 'red' : 'amber'}`}>{p.stock === 0 ? 'Out of stock' : `${p.stock} left`}</span></li>
                  ))}</ul>
                )}
              </div>
            </section>
            <section className="adm-card">
              <div className="adm-card-title">Quick Actions</div>
              <div className="adm-card-body adm-quick-actions">
                <Link to="/products/new"><Icon name="plus" />Add Product <span>›</span></Link>
                <Link to="/orders"><Icon name="orders" />Manage Orders <span>›</span></Link>
                <Link to="/customers"><Icon name="customers" />View Customers <span>›</span></Link>
                <Link to="/categories"><Icon name="categories" />Manage Categories <span>›</span></Link>
                <Link to="/reports"><Icon name="reports" />View Reports <span>›</span></Link>
              </div>
            </section>
          </div>
        </>
      )}
    </>
  )
}
