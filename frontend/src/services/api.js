const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('beh_token')
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  let response
  try {
    response = await fetch(`${API_URL}${path}`, {
      cache: options.cache || 'no-store',
      ...options,
      headers
    })
  } catch {
    throw new Error(`Cannot reach the Bazaar-e-Hunar server at ${API_URL}. Start the shared backend on port 8081.`)
  }

  let data = null
  const text = await response.text()
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = { message: text }
    }
  }

  if (!response.ok) {
    const error = new Error(data?.message || 'Request failed')
    error.status = response.status
    error.data = data
    throw error
  }

  return data
}

export { API_URL }
