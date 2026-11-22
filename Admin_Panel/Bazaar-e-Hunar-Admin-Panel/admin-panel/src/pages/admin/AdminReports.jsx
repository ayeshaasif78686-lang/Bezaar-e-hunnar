import { useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { assetUrl, onImgError, Donut, downloadCsv, ErrorBox, Icon, Loading, STATUS_HEX, STATUS_LABEL, useLoad } from '../../components/admin/AdminUI.jsx'

const PERIODS = [['week', 'This Week'], ['month', 'This Month'], ['year', 'This Year'], ['all', 'All Time']]
const COLORS = ['#7f242a', '#c98a1b', '#3f7d4e', '#b8874e', '#2f6f9f', '#8a5a9b']

export default function AdminReports() {
  const { money } = useAdmin()
  const [period, setPeriod] = useState('month')
  const { data, loading, error, reload } = useLoad(() => adminApi.reports(period), [period])
  const maxSold = Math.max(1, ...(data?.topSelling || []).map((p) => p.sold))

  function exportCsv() {
    const rows = [['Report', period], ['Total sales', data.totalSales], ['Total orders', data.totalOrders], ['Total products', data.totalProducts], [],
      ['Top selling product', 'Units sold', 'Revenue'], ...data.topSelling.map((p) => [p.name, p.sold, p.revenue]), [],
      ['Category', 'Revenue', 'Share %'], ...data.salesByCategory.map((c) => [c.category, c.revenue, c.percent])]
    downloadCsv(`bazaar-e-hunar-report-${period}.csv`, rows)
  }

  return (
    <>
      <div className="adm-head">
        <div><h1>Reports</h1><p>View sales and orders summaries.</p></div>
        <div style={{ display: 'flex', gap: 10 }}>
          <select className="adm-select" style={{ width: 150 }} value={period} onChange={(e) => setPeriod(e.target.value)} aria-label="Report period">
            {PERIODS.map(([v, l]) => <option key={v} value={v}>{l}</option>)}
          </select>
          <button className="adm-btn ghost" disabled={!data} onClick={exportCsv}><Icon name="download" />Export CSV</button>
        </div>
      </div>
      <ErrorBox message={error} onRetry={reload} />
      {loading && !data ? <Loading /> : data && (
        <>
          <div className="adm-three">
            {[['Total Sales', money(data.totalSales)], ['Total Orders', data.totalOrders.toLocaleString('en-US')], ['Total Products', data.totalProducts.toLocaleString('en-US')]].map(([l, v]) => (
              <div key={l} className="adm-card adm-stat"><span>{l}</span><strong>{v}</strong></div>
            ))}
          </div>
          <div className="adm-two even">
            <section className="adm-card">
              <div className="adm-card-title">Top Selling Products</div>
              <div className="adm-card-body">
                {data.topSelling.length === 0 ? <div className="adm-empty">No sales in this period.</div> : data.topSelling.map((p) => (
                  <div key={p.productId} className="adm-toprow">
                    <img className="adm-thumb" src={assetUrl(p.image)} alt="" onError={onImgError} />
                    <div style={{ flex: 1 }}><strong>{p.name}</strong><div className="adm-bar"><i style={{ width: `${(p.sold / maxSold) * 100}%` }} /></div></div>
                    <span><b>{p.sold}</b> Sold<br /><small>{money(p.revenue)}</small></span>
                  </div>
                ))}
              </div>
            </section>
            <section className="adm-card">
              <div className="adm-card-title">Sales by Category</div>
              <div className="adm-card-body">
                {data.salesByCategory.length === 0 ? <div className="adm-empty">No sales in this period.</div> : (
                  <Donut centerLabel="Revenue %" segments={data.salesByCategory.map((c, i) => ({ label: c.category, value: c.revenue, color: COLORS[i % COLORS.length] }))} />
                )}
              </div>
            </section>
          </div>
          <section className="adm-card">
            <div className="adm-card-title">Orders by Status</div>
            <div className="adm-card-body"><Donut segments={Object.entries(data.statusCounts).map(([s, v]) => ({ label: STATUS_LABEL[s], value: v, color: STATUS_HEX[s] }))} /></div>
          </section>
        </>
      )}
    </>
  )
}
