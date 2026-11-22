import { useEffect, useMemo, useState } from 'react'
import SiteFooter from '../components/SiteFooter.jsx'
import { apiRequest } from '../services/api.js'

const CATEGORIES = ['All', 'Creational', 'Structural', 'Behavioral']

// One card per pattern. "Run demo" calls GET /api/patterns/demo/{key} on the backend and shows the result.
function PatternCard({ pattern }) {
  const [state, setState] = useState({ status: 'idle', data: null, error: '' })

  async function runDemo() {
    setState({ status: 'loading', data: null, error: '' })
    try {
      const data = await apiRequest(`/patterns/demo/${pattern.key}`)
      setState({ status: 'done', data, error: '' })
    } catch (error) {
      setState({ status: 'error', data: null, error: error.message || 'Could not run the demo' })
    }
  }

  return (
    <article className={`pat-card pat-${pattern.category.toLowerCase()}`}>
      <header>
        <span className="pat-number">{pattern.number}</span>
        <div>
          <h3>{pattern.name}</h3>
          <span className="pat-badge">{pattern.category}</span>
        </div>
      </header>
      <p>{pattern.usedFor}</p>
      <code className="pat-package">{pattern.javaPackage.replace('com.bazaarehunar.', '')}</code>
      <div className="pat-actions">
        <button type="button" onClick={runDemo} disabled={state.status === 'loading'}>
          {state.status === 'loading' ? 'Running…' : state.status === 'done' ? 'Run again' : 'Run demo'}
        </button>
        {state.status === 'done' && <button type="button" className="pat-secondary" onClick={() => setState({ status: 'idle', data: null, error: '' })}>Hide</button>}
      </div>
      {state.status === 'error' && <p className="pat-error" role="alert">{state.error}</p>}
      {state.status === 'done' && <pre className="pat-output">{JSON.stringify(state.data, null, 2)}</pre>}
    </article>
  )
}

export default function PatternsPage() {
  const [info, setInfo] = useState(null)
  const [error, setError] = useState('')
  const [category, setCategory] = useState('All')
  const [query, setQuery] = useState('')

  useEffect(() => {
    let cancelled = false
    apiRequest('/patterns')
      .then((data) => { if (!cancelled) setInfo(data) })
      .catch((err) => { if (!cancelled) setError(err.message || 'Could not load patterns') })
    return () => { cancelled = true }
  }, [])

  const visible = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return (info?.patterns || []).filter((p) =>
      (category === 'All' || p.category === category) &&
      (!needle || `${p.name} ${p.usedFor} ${p.category}`.toLowerCase().includes(needle)))
  }, [info, category, query])

  const counts = info ? { All: info.total, Creational: info.creational, Structural: info.structural, Behavioral: info.behavioral } : {}

  return (
    <>
      <main className="pat-page">
        <section className="pat-hero">
          <span className="pat-eyebrow">Software Design Patterns</span>
          <h1>All 23 Gang-of-Four Patterns, Live</h1>
          <p>
            Every pattern is implemented in the Bazaar-e-Hunar backend and used for a real marketplace idea.
            Press <b>Run demo</b> on any card to execute that pattern on the server and see its result.
          </p>
          {info && (
            <ul className="pat-stats" aria-label="Pattern counts">
              <li><b>{info.total}</b> Total</li>
              <li><b>{info.creational}</b> Creational</li>
              <li><b>{info.structural}</b> Structural</li>
              <li><b>{info.behavioral}</b> Behavioral</li>
            </ul>
          )}
        </section>

        <section className="pat-toolbar" aria-label="Filter patterns">
          <div className="pat-tabs" role="tablist">
            {CATEGORIES.map((name) => (
              <button key={name} type="button" role="tab" aria-selected={category === name}
                className={category === name ? 'is-active' : ''} onClick={() => setCategory(name)}>
                {name}{info ? ` (${counts[name]})` : ''}
              </button>
            ))}
          </div>
          <input type="search" value={query} onChange={(e) => setQuery(e.target.value)}
            placeholder="Search patterns…" aria-label="Search patterns" />
        </section>

        {error && <p className="pat-error pat-banner" role="alert">{error}. Make sure the backend is running on port 8081.</p>}
        {!info && !error && <p className="pat-muted">Loading patterns…</p>}
        {info && visible.length === 0 && <p className="pat-muted">No patterns match your search.</p>}

        <section className="pat-grid">
          {visible.map((pattern) => <PatternCard key={pattern.key} pattern={pattern} />)}
        </section>
      </main>
      <SiteFooter />
    </>
  )
}
