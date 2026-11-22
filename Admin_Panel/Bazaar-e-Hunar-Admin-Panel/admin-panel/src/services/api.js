const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'
export const TOKEN_KEY = 'beh_admin_token'   // separate from the storefront's "beh_token"
export const USER_KEY = 'beh_admin_user'

// Sends a request to the backend. Pass a FormData body for file uploads (the browser adds the multipart boundary).
export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY)
  const isForm = typeof FormData !== 'undefined' && options.body instanceof FormData
  const headers = { ...(isForm ? {} : { 'Content-Type': 'application/json' }), ...(options.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`

  let response
  try {
    response = await fetch(`${API_URL}${path}`, { cache: options.cache || 'no-store', ...options, headers })
  } catch {
    throw new Error('Cannot reach the server. Make sure the backend is running on port 8081.')
  }

  let data = null
  const text = await response.text()
  if (text) {
    try { data = JSON.parse(text) } catch { data = { message: text } }
  }

  if (!response.ok) {
    // An expired, invalid or deactivated session: sign out so the login page appears.
    if (response.status === 401 && token && !path.startsWith('/auth/')) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      if (window.location.pathname !== '/login') window.location.assign('/login')
    }
    const error = new Error(data?.message || `Request failed (${response.status})`)
    error.status = response.status
    error.data = data
    throw error
  }
  return data
}

export { API_URL }
