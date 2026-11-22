import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { adminApi } from '../services/adminApi.js'

const AdminContext = createContext(null)
const SYMBOLS = { PKR: 'Rs. ', USD: '$', EUR: '€', GBP: '£', AED: 'AED ' }

export function AdminProvider({ children }) {
  const [settings, setSettings] = useState({ currency: 'PKR', storeName: 'Bazaar-e-Hunar' })
  const [toasts, setToasts] = useState([])

  const refreshSettings = useCallback(() => adminApi.settings().then(setSettings).catch(() => {}), [])
  useEffect(() => { refreshSettings() }, [refreshSettings])

  const toast = useCallback((message, type = 'success') => {
    const id = Math.random().toString(36).slice(2)
    setToasts((list) => [...list, { id, message, type }])
    setTimeout(() => setToasts((list) => list.filter((t) => t.id !== id)), 3600)
  }, [])

  const value = useMemo(() => ({
    settings, setSettings, refreshSettings, toast,
    money: (amount) => `${SYMBOLS[settings.currency] ?? `${settings.currency} `}${Math.round(Number(amount) || 0).toLocaleString('en-US')}`
  }), [settings, refreshSettings, toast])

  return (
    <AdminContext.Provider value={value}>
      {children}
      <div className="adm-toasts" aria-live="polite">
        {toasts.map((t) => <div key={t.id} className={`adm-toast ${t.type}`}>{t.message}</div>)}
      </div>
    </AdminContext.Provider>
  )
}

export function useAdmin() { return useContext(AdminContext) }
