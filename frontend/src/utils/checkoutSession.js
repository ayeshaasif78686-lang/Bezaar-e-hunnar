const CHECKOUT_ITEM_IDS_KEY = 'beh_checkout_item_ids'
const CHECKOUT_DETAILS_KEY = 'beh_checkout_details'
const LAST_ORDER_KEY = 'beh_last_order'

function getSessionStore() {
  try {
    return typeof window === 'undefined' ? null : window.sessionStorage
  } catch {
    return null
  }
}

function readJson(key, fallback) {
  try {
    const raw = getSessionStore()?.getItem(key)
    return raw ? JSON.parse(raw) : fallback
  } catch {
    return fallback
  }
}

function writeJson(key, value) {
  try {
    getSessionStore()?.setItem(key, JSON.stringify(value))
  } catch {
    // The checkout still works until navigation if browser session storage is unavailable.
  }
}

export function getCheckoutItemIds() {
  const ids = readJson(CHECKOUT_ITEM_IDS_KEY, null)
  return Array.isArray(ids) ? ids : null
}

export function setCheckoutItemIds(ids) {
  writeJson(CHECKOUT_ITEM_IDS_KEY, ids)
}

export function clearCheckoutItemIds() {
  try {
    getSessionStore()?.removeItem(CHECKOUT_ITEM_IDS_KEY)
  } catch {
    // Nothing else is required when storage is unavailable.
  }
}

export function getCheckoutItems(cart) {
  const selectedIds = getCheckoutItemIds()
  return selectedIds === null ? cart : cart.filter((item) => selectedIds.includes(item.id))
}

export function getCheckoutDetails() {
  return readJson(CHECKOUT_DETAILS_KEY, {
    fullName: '',
    phone: '',
    address: '',
    city: '',
    province: '',
    postalCode: '',
    saveAddress: false,
    delivery: 'standard'
  })
}

export function setCheckoutDetails(details) {
  writeJson(CHECKOUT_DETAILS_KEY, details)
}

export function getLastOrder() {
  return readJson(LAST_ORDER_KEY, null)
}

export function setLastOrder(order) {
  writeJson(LAST_ORDER_KEY, order)
}

export function calculateCheckoutTotals(items, delivery = 'standard', coupon = '') {
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const shipping = items.length === 0 ? 0 : delivery === 'express' ? 600 : 300
  const discount = coupon ? Math.round(subtotal * 0.10) : 0
  return {
    subtotal,
    shipping,
    discount,
    tax: 0,
    total: Math.max(0, subtotal + shipping - discount)
  }
}

export function deliveryLabel(delivery) {
  return delivery === 'express' ? 'Express Delivery, 1-3 Business Days' : 'Standard Delivery, 3-7 Business Days'
}

export function deliveryEstimate(delivery) {
  return delivery === 'express' ? '1-3 Business Days' : '3-7 Business Days'
}

export function createOrderNumber() {
  return `BEH${Math.floor(10000000 + Math.random() * 90000000)}`
}
