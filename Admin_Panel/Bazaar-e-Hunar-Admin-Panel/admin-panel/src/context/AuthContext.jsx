import { createContext, useContext, useMemo, useState } from 'react'
import { apiRequest, TOKEN_KEY, USER_KEY } from '../services/api.js'

const AuthContext = createContext(null)

function stored() {
  try {
    const user = JSON.parse(localStorage.getItem(USER_KEY))
    return localStorage.getItem(TOKEN_KEY) && user ? user : null
  } catch { return null }
}

// Admin-only authentication against the same backend (/api/auth/login). Other roles are refused.
export function AuthProvider({ children }) {
  const [user, setUser] = useState(stored)

  async function login(email, password) {
    const data = await apiRequest('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) })
    if (String(data.role).toUpperCase() !== 'ADMIN') {
      throw new Error('This account is not an admin. Please use an admin account.')
    }
    localStorage.setItem(TOKEN_KEY, data.token)
    const safe = { id: data.id, name: data.name, email: data.email, role: data.role }
    localStorage.setItem(USER_KEY, JSON.stringify(safe))
    setUser(safe)
    return safe
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, logout }), [user])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() { return useContext(AuthContext) }
