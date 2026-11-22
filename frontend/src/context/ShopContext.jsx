import { createContext, useContext, useEffect, useMemo, useState } from 'react'

const ShopContext = createContext(null)

function readStorage(key, fallback) {
  try {
    const value = JSON.parse(localStorage.getItem(key))
    return value ?? fallback
  } catch {
    return fallback
  }
}

export function ShopProvider({ children }) {
  const [notice, setNotice] = useState(null)
  const [cart, setCart] = useState(() => readStorage('beh_cart', []))
  const [wishlist, setWishlist] = useState(() => readStorage('beh_wishlist', []))
  const [coupon, setCoupon] = useState(() => localStorage.getItem('beh_coupon') || '')

  useEffect(() => localStorage.setItem('beh_cart', JSON.stringify(cart)), [cart])
  useEffect(() => localStorage.setItem('beh_wishlist', JSON.stringify(wishlist)), [wishlist])
  useEffect(() => localStorage.setItem('beh_coupon', coupon), [coupon])

  function addToCart(product, quantity = 1) {
    quantity = Math.max(1, Math.floor(Number(quantity) || 1))
    setNotice({ text: `${product.name} added to cart.`, to: '/cart', label: 'View cart' })
    setCart((old) => {
      const existing = old.find((item) => item.id === product.id)
      if (existing) {
        return old.map((item) => item.id === product.id ? { ...item, quantity: item.quantity + quantity } : item)
      }
      return [...old, {
        id: product.id,
        name: product.name,
        image: product.image,
        price: product.price,
        artisan: product.artisan,
        quantity
      }]
    })
  }

  function updateQuantity(productId, quantity) {
    setCart((old) => old.map((item) => item.id === productId ? { ...item, quantity: Math.max(1, quantity) } : item))
  }

  function removeFromCart(productId) {
    setCart((old) => old.filter((item) => item.id !== productId))
  }

  function clearCart() {
    setCart([])
  }

  function toggleWishlist(productId) {
    setNotice({ text: wishlist.includes(productId) ? 'Removed from favourites.' : 'Added to favourites.', to: '/wishlist', label: 'View favourites' })
    setWishlist((old) => old.includes(productId) ? old.filter((id) => id !== productId) : [...old, productId])
  }

  function applyCoupon(code) {
    const normalized = (code || '').trim().toUpperCase()
    const accepted = ['HUNAR10', 'WELCOME10'].includes(normalized)
    setCoupon(accepted ? normalized : '')
    return accepted
  }

  const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const discount = coupon ? Math.round(subtotal * 0.10) : 0
  const shipping = subtotal === 0 || subtotal >= 5000 ? 0 : 300
  const total = Math.max(0, subtotal - discount + shipping)

  const value = useMemo(() => ({
    cart, wishlist, coupon, subtotal, discount, shipping, total, notice, dismissNotice: () => setNotice(null),
    cartCount: cart.reduce((sum, item) => sum + item.quantity, 0),
    wishlistCount: wishlist.length,
    addToCart, updateQuantity, removeFromCart, clearCart,
    toggleWishlist, isWishlisted: (productId) => wishlist.includes(productId), applyCoupon
  }), [cart, wishlist, coupon, subtotal, discount, shipping, total, notice])

  return <ShopContext.Provider value={value}>{children}</ShopContext.Provider>
}

export function useShop() {
  return useContext(ShopContext)
}
