import { apiRequest } from './api.js'

const json = (method, body) => ({ method, body: JSON.stringify(body) })

// Thin wrappers around the backend endpoints used by the admin panel.
export const adminApi = {
  dashboard: () => apiRequest('/admin/dashboard'),
  notifications: () => apiRequest('/admin/notifications'),
  reports: (period) => apiRequest(`/admin/reports?period=${encodeURIComponent(period)}`),

  products: () => apiRequest('/products?includeInactive=true'),
  product: (id) => apiRequest(`/products/${encodeURIComponent(id)}`),
  createProduct: (product) => apiRequest('/products', json('POST', product)),
  updateProduct: (id, product) => apiRequest(`/products/${encodeURIComponent(id)}`, json('PUT', product)),
  deleteProduct: (id) => apiRequest(`/products/${encodeURIComponent(id)}`, { method: 'DELETE' }),
  uploadProductImage(id, file) {
    const form = new FormData()
    form.append('image', file)
    return apiRequest(`/products/${encodeURIComponent(id)}/images`, { method: 'POST', body: form })
  },

  orders: () => apiRequest('/orders'),
  updateOrderStatus: (orderNumber, status) => apiRequest(`/orders/${encodeURIComponent(orderNumber)}/status`, json('PATCH', { status })),
  receipt: (orderNumber) => apiRequest(`/orders/${encodeURIComponent(orderNumber)}/receipt`),

  categories: () => apiRequest('/admin/categories'),
  addCategory: (body) => apiRequest('/admin/categories', json('POST', body)),
  renameCategory: (body) => apiRequest('/admin/categories', json('PUT', body)),
  deleteCategory: (name) => apiRequest(`/admin/categories?name=${encodeURIComponent(name)}`, { method: 'DELETE' }),

  customers: (q = '') => apiRequest(`/admin/customers?q=${encodeURIComponent(q)}`),
  customer: (id) => apiRequest(`/admin/customers/${encodeURIComponent(id)}`),
  sellers: (q = '') => apiRequest(`/admin/sellers?q=${encodeURIComponent(q)}`),
  seller: (id) => apiRequest(`/admin/sellers/${encodeURIComponent(id)}`),
  createSeller: (body) => apiRequest('/admin/sellers', json('POST', body)),
  setUserActive: (id, active) => apiRequest(`/admin/users/${encodeURIComponent(id)}/status`, json('PATCH', { active })),

  messages: () => apiRequest('/admin/messages'),
  setMessageStatus: (id, status) => apiRequest(`/admin/messages/${encodeURIComponent(id)}`, json('PATCH', { status })),
  deleteMessage: (id) => apiRequest(`/admin/messages/${encodeURIComponent(id)}`, { method: 'DELETE' }),
  customOrders: () => apiRequest('/admin/custom-orders'),
  setCustomOrderStatus: (id, status) => apiRequest(`/admin/custom-orders/${encodeURIComponent(id)}`, json('PATCH', { status })),

  settings: () => apiRequest('/admin/settings'),
  saveSettings: (settings) => apiRequest('/admin/settings', json('PUT', settings))
}
