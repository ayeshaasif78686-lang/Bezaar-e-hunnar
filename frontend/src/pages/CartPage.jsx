import { useEffect, useMemo, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import MarketplaceProductCard from '../components/MarketplaceProductCard.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { recommendedProducts } from '../data/storeData.js'
import { useShop } from '../context/ShopContext.jsx'
import { setCheckoutItemIds } from '../utils/checkoutSession.js'

export default function CartPage() {
  const navigate = useNavigate()
  const { cart, coupon, updateQuantity, removeFromCart, applyCoupon } = useShop()
  const [couponInput, setCouponInput] = useState('')
  const [couponMessage, setCouponMessage] = useState('')
  const [selectedIds, setSelectedIds] = useState(() => cart.map((item) => item.id))
  const [checkoutMessage, setCheckoutMessage] = useState('')

  const previousCartIds = useRef(cart.map((item) => item.id))
  useEffect(() => {
    const added = cart.filter((item) => !previousCartIds.current.includes(item.id)).map((item) => item.id)
    setSelectedIds((current) => [...current.filter((id) => cart.some((item) => item.id === id)), ...added])
    previousCartIds.current = cart.map((item) => item.id)
  }, [cart])

  const selectedCart = useMemo(() => cart.filter((item) => selectedIds.includes(item.id)), [cart, selectedIds])
  const subtotal = selectedCart.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const discount = coupon ? Math.round(subtotal * 0.10) : 0
  const shipping = subtotal === 0 || subtotal >= 5000 ? 0 : 300
  const total = Math.max(0, subtotal - discount + shipping)

  function handleCoupon(event) {
    event.preventDefault()
    const ok = applyCoupon(couponInput)
    setCouponMessage(ok ? '10% discount applied.' : 'Try HUNAR10 or WELCOME10.')
  }

  function toggleSelected(productId) {
    setSelectedIds((current) => current.includes(productId) ? current.filter((id) => id !== productId) : [...current, productId])
    setCheckoutMessage('')
  }

  function handleCheckout() {
    if (selectedCart.length === 0) {
      setCheckoutMessage('Select at least one handmade piece before proceeding.')
      return
    }
    setCheckoutItemIds(selectedIds)
    navigate('/checkout')
  }

  return (
    <main className="cart-page page-shell">
      <div className="catalog-breadcrumb">Home &nbsp;›&nbsp; Cart</div>

      <section className="cart-layout">
        <div className="cart-main-column">
          <div className="cart-title-row">
            <h1>My Cart ({cart.length} {cart.length === 1 ? 'item' : 'items'})</h1>
            <Link to="/products">Continue Shopping →</Link>
          </div>

          <div className="cart-items-panel">
            {cart.length === 0 ? (
              <div className="empty-cart">
                <div>🛍</div>
                <h2>Your cart is waiting for a story.</h2>
                <p>Add handmade pieces from our collection and they will appear here.</p>
                <Link to="/products">Shop Our Collection →</Link>
              </div>
            ) : cart.map((item) => (
              <article className="cart-item" key={item.id}>
                <input type="checkbox" checked={selectedIds.includes(item.id)} onChange={() => toggleSelected(item.id)} aria-label={`Select ${item.name}`} />
                <Link to={`/products/${item.id}`} className="cart-item-image"><img src={item.image} alt={item.name} /></Link>
                <div className="cart-item-info">
                  <h3><Link to={`/products/${item.id}`}>{item.name}</Link></h3>
                  <strong>Rs. {item.price.toLocaleString()}</strong>
                  <div className="cart-qty">
                    <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>−</button>
                    <span>{item.quantity}</span>
                    <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
                  </div>
                </div>
                <button className="cart-delete" onClick={() => removeFromCart(item.id)} aria-label={`Remove ${item.name}`}>♲</button>
              </article>
            ))}
          </div>
        </div>

        <aside className="cart-summary-column">
          <section className="order-summary-card">
            <h2>Order Summary</h2>
            <SummaryRow label="Subtotal" value={`Rs. ${subtotal.toLocaleString()}`} />
            <SummaryRow label="Shipping" value={shipping === 0 ? 'Free' : `Rs. ${shipping.toLocaleString()}`} />
            <SummaryRow label="Tax (If applicable)" value="Rs. 0" />
            {discount > 0 && <SummaryRow label="Coupon Discount" value={`− Rs. ${discount.toLocaleString()}`} />}
            <div className="summary-total"><span>Total</span><strong>Rs. {total.toLocaleString()}</strong></div>
            <button disabled={cart.length === 0 || selectedCart.length === 0} onClick={handleCheckout}>Proceed to Checkout →</button>
            {checkoutMessage && <p className="cart-checkout-note" role="status">{checkoutMessage}</p>}
          </section>

          <section className="coupon-card">
            <h3>Apply Coupon Code</h3>
            <form onSubmit={handleCoupon}>
              <input value={couponInput} onChange={(e) => setCouponInput(e.target.value)} placeholder="Enter coupon code" />
              <button>Apply</button>
            </form>
            {couponMessage && <p>{couponMessage}</p>}
          </section>
        </aside>
      </section>

      <section className="cart-recommendations">
        <h2>You May Also Like</h2>
        <div className="cart-recommendation-grid">
          {recommendedProducts.slice(0, 4).map((product) => <MarketplaceProductCard key={product.id} product={product} compact />)}
        </div>
      </section>

      <section className="cart-trust-row">
        <div>▣ <strong>Secure</strong><span>Payments</span></div>
        <div>◉ <strong>Worldwide</strong><span>Shipping</span></div>
        <div>◴ <strong>Easy</strong><span>Returns</span></div>
        <div>♙ <strong>Support</strong><span>Local Artisans</span></div>
      </section>

      <SiteFooter newsletter />
    </main>
  )
}

function SummaryRow({ label, value }) {
  return <div className="summary-row"><span>{label}</span><strong>{value}</strong></div>
}
