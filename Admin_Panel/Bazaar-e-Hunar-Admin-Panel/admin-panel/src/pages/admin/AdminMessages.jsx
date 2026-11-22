import { useEffect, useMemo, useState } from 'react'
import { useAdmin } from '../../context/AdminContext.jsx'
import { adminApi } from '../../services/adminApi.js'
import { ConfirmModal, ErrorBox, formatDate, Loading, Modal, Pager, SearchBox, useLoad } from '../../components/admin/AdminUI.jsx'

const PAGE_SIZE = 10
const TABS = ['ALL', 'NEW', 'READ', 'REPLIED']
const LABEL = { NEW: 'New', READ: 'Read', REPLIED: 'Replied' }
const COLOR = { NEW: 'amber', READ: 'blue', REPLIED: 'green' }
const statusOf = (message) => message.status || 'NEW'

// Messages sent from the storefront's Contact page.
export default function AdminMessages() {
  const { toast } = useAdmin()
  const { data, loading, error, reload } = useLoad(adminApi.messages, [])

  useEffect(() => {
    const refresh = () => reload()
    const interval = window.setInterval(refresh, 12000)
    window.addEventListener('focus', refresh)
    return () => {
      window.clearInterval(interval)
      window.removeEventListener('focus', refresh)
    }
  }, [reload])
  const [tab, setTab] = useState('ALL')
  const [q, setQ] = useState('')
  const [page, setPage] = useState(1)
  const [selected, setSelected] = useState(null)
  const [toDelete, setToDelete] = useState(null)
  const [busy, setBusy] = useState(false)

  const messages = data || []
  const counts = useMemo(() => {
    const c = { ALL: messages.length }
    messages.forEach((m) => { c[statusOf(m)] = (c[statusOf(m)] || 0) + 1 })
    return c
  }, [messages])

  const filtered = useMemo(() => {
    const needle = q.trim().toLowerCase()
    return messages.filter((m) => (tab === 'ALL' || statusOf(m) === tab)
      && (!needle || `${m.name} ${m.email} ${m.subject}`.toLowerCase().includes(needle)))
  }, [messages, tab, q])
  const current = Math.min(page, Math.max(1, Math.ceil(filtered.length / PAGE_SIZE)))
  const rows = filtered.slice((current - 1) * PAGE_SIZE, current * PAGE_SIZE)

  async function setStatus(message, status, quiet = false) {
    setBusy(true)
    try {
      const updated = await adminApi.setMessageStatus(message.id, status)
      if (!quiet) toast(`Marked as ${LABEL[status].toLowerCase()}`)
      setSelected((s) => (s && s.id === updated.id ? updated : s))
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  function open(message) {
    setSelected(message)
    if (statusOf(message) === 'NEW') setStatus(message, 'READ', true)   // opening a new message marks it as read
  }

  async function remove() {
    setBusy(true)
    try {
      await adminApi.deleteMessage(toDelete.id)
      toast('Message deleted')
      setToDelete(null)
      setSelected(null)
      reload()
    } catch (e) { toast(e.message, 'error') } finally { setBusy(false) }
  }

  return (
    <>
      <div className="adm-head"><div><h1>Messages</h1><p>Messages sent from the Contact page.</p></div></div>
      <ErrorBox message={error} onRetry={reload} />
      <div className="adm-tabs" role="tablist">
        {TABS.map((t) => (
          <button key={t} role="tab" aria-selected={tab === t} className={tab === t ? 'on' : ''} onClick={() => { setTab(t); setPage(1) }}>
            {t === 'ALL' ? 'All Messages' : LABEL[t]} ({counts[t] || 0})
          </button>
        ))}
      </div>
      <div className="adm-toolbar"><SearchBox value={q} onChange={(v) => { setQ(v); setPage(1) }} placeholder="Search by name, email or subject..." /></div>
      <section className="adm-card"><div className="adm-card-body">
        {loading && !data ? <Loading /> : rows.length === 0 ? <div className="adm-empty">No messages found.</div> : (
          <div className="adm-table-wrap">
            <table className="adm-table">
              <thead><tr><th>From</th><th>Subject</th><th>Received</th><th>Status</th><th>Action</th></tr></thead>
              <tbody>{rows.map((m) => (
                <tr key={m.id}>
                  <td><strong>{m.name}</strong><br /><small>{m.email}</small></td>
                  <td>{m.subject}</td>
                  <td>{formatDate(m.createdAt)}</td>
                  <td><span className={`adm-pill ${COLOR[statusOf(m)] || 'grey'}`}>{LABEL[statusOf(m)] || statusOf(m)}</span></td>
                  <td><button className="adm-btn small ghost" onClick={() => open(m)}>View</button></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
        <Pager page={current} pageSize={PAGE_SIZE} total={filtered.length} onPage={setPage} />
      </div></section>

      {selected && (
        <Modal wide title={selected.subject || 'Message'} onClose={() => setSelected(null)}
          footer={<>
            <a className="adm-btn" href={`mailto:${selected.email}?subject=${encodeURIComponent(`Re: ${selected.subject || ''}`)}`}>Reply by email</a>
            {statusOf(selected) !== 'REPLIED' && <button className="adm-btn ghost" disabled={busy} onClick={() => setStatus(selected, 'REPLIED')}>Mark as replied</button>}
            {statusOf(selected) === 'REPLIED' && <button className="adm-btn ghost" disabled={busy} onClick={() => setStatus(selected, 'READ')}>Mark as read</button>}
            <button className="adm-btn danger" disabled={busy} onClick={() => setToDelete(selected)}>Delete</button>
          </>}>
          <dl className="adm-kv">
            <dt>From</dt><dd>{selected.name}</dd>
            <dt>Email</dt><dd>{selected.email}</dd>
            <dt>Received</dt><dd>{formatDate(selected.createdAt, true)}</dd>
            <dt>Status</dt><dd><span className={`adm-pill ${COLOR[statusOf(selected)] || 'grey'}`}>{LABEL[statusOf(selected)] || statusOf(selected)}</span></dd>
          </dl>
          <p className="adm-msg-body">{selected.message}</p>
        </Modal>
      )}
      {toDelete && <ConfirmModal message={`Delete the message from ${toDelete.name}? This cannot be undone.`} busy={busy} onConfirm={remove} onClose={() => setToDelete(null)} />}
    </>
  )
}
