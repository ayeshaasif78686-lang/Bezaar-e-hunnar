import { createContext, useContext, useMemo, useState } from 'react'
import { apiRequest } from '../services/api.js'

const AuthContext = createContext(null)

function loadStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('beh_user'))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser)

  function saveAuth(data) {
    localStorage.setItem('beh_token', data.token)
    const safeUser = {
      id: data.id,
      name: data.name,
      email: data.email,
      role: data.role
    }
    localStorage.setItem('beh_user', JSON.stringify(safeUser))
    setUser(safeUser)
  }

  async function login(email, password) {
    const data = await apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    })
    saveAuth(data)
    return data
  }

  async function register(payload) {
    const data = await apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    saveAuth(data)
    return data
  }

  function updateUser(changes) {
    setUser((current) => {
      const next = { ...current, ...changes }
      localStorage.setItem('beh_user', JSON.stringify(next))
      return next
    })
  }

  function logout() {
    localStorage.removeItem('beh_token')
    localStorage.removeItem('beh_user')
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, register, logout, updateUser }), [user])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
