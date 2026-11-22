import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import CheckoutOrderSummary from '../components/CheckoutOrderSummary.jsx'
import CheckoutSteps from '../components/CheckoutSteps.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { useShop } from '../context/ShopContext.jsx'
import { apiRequest } from '../services/api.js'
import {
  calculateCheckoutTotals,
  clearCheckoutItemIds,
  deliveryEstimate,
  deliveryLabel,
  getCheckoutDetails,
  getCheckoutItems,
  setLastOrder
} from '../utils/checkoutSession.js'

const paymentOptions = [
  { id: 'card', title: 'Credit / Debit Card', description: 'Visa, Mastercard and secure card payments', mark: '▣' },
  { id: 'wallet', title: 'JazzCash / Easypaisa', description: 'Pay securely with your mobile wallet', mark: '◉' },
  { id: 'bank', title: 'Bank Transfer', description: 'Direct bank transfer to our account', mark: '▤' },
  { id: 'cod', title: 'Cash on Delivery', description: 'Pay when you receive your order', mark: '▧' }
]

export default function PaymentPage() {
  const navigate = useNavigate()
  const { cart, coupon, applyCoupon, removeFromCart } = useShop()
  const [method, setMethod] = useState('card')
  const [couponInput, setCouponInput] = useState('')
  const [couponMessage, setCouponMessage] = useState('')
  const [card, setCard] = useState({ number: '', expiry: '', cvv: '', holder: '' })
  const [billingSame, setBillingSame] = useState(true)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const details = getCheckoutDetails()
  const items = getCheckoutItems(cart)
  const totals = calculateCheckoutTotals(items, details.delivery, coupon)

  function applyCheckoutCoupon(event) {
    event.preventDefault()
    const valid = applyCoupon(couponInput)
    setCouponMessage(valid ? '10% discount applied to this order.' : 'Try HUNAR10 or WELCOME10.')
  }

  function updateCard(event) {
    const { name, value } = event.target
    setCard((current) => ({ ...current, [name]: value }))
    setError('')
  }

  async function placeOrder(event) {
    event.preventDefault()
    if (submitting) return
    if (items.length === 0) {
      setError('Your selected cart items are no longer available. Return to the cart to continue.')
      return
    }
    if (!details.fullName?.trim() || !details.phone?.trim() || !details.address?.trim() || !details.city?.trim() || !details.province?.trim()) {
      setError('Add your shipping address before placing the order.')
      return
    }
    if (method === 'card' && (!card.number.trim() || !card.expiry.trim() || !card.cvv.trim() || !card.holder.trim())) {
      setError('Enter the card details or choose another payment method.')
      return
    }

    const selectedOption = paymentOptions.find((option) => option.id === method)
    const address = {
      fullName: details.fullName.trim(),
      phone: details.phone.trim(),
      address: details.address.trim(),
      city: details.city.trim(),
      province: details.province.trim(),
      postalCode: details.postalCode?.trim() || ''
    }
    const payload = {
      items: items.map((item) => ({
        productId: String(item.id),
        name: item.name,
        image: item.image || '',
        price: Math.round(Number(item.price) || 0),
        quantity: Math.max(1, Math.floor(Number(item.quantity) || 1))
      })),
      address,
      delivery: details.delivery === 'express' ? 'express' : 'standard',
      paymentMethod: selectedOption?.title || 'Cash on Delivery',
      coupon: coupon || ''
    }

    setSubmitting(true)
    setError('')
    try {
      // The backend creates the permanent MongoDB order. Never send card number, expiry or CVV.
      const saved = await apiRequest('/orders', {
        method: 'POST',
        body: JSON.stringify(payload)
      })

      const savedItems = Array.isArray(saved.items) ? saved.items : items
      const order = {
        id: saved.id,
        number: saved.orderNumber,
        items: savedItems,
        itemCount: savedItems.reduce((sum, item) => sum + Number(item.quantity || 0), 0),
        totals: {
          subtotal: Number(saved.subtotal ?? totals.subtotal),
          shipping: Number(saved.shipping ?? totals.shipping),
          discount: Number(saved.discount ?? totals.discount),
          tax: Number(saved.tax || 0),
          total: Number(saved.total ?? totals.total)
        },
        paymentMethod: saved.paymentMethod || payload.paymentMethod,
        delivery: saved.deliveryLabel || deliveryLabel(details.delivery),
        estimatedDelivery: saved.estimatedDelivery || deliveryEstimate(details.delivery),
        address: saved.address || address,
        status: saved.status || 'PLACED',
        createdAt: saved.createdAt
      }

      setLastOrder(order)
      items.forEach((item) => removeFromCart(item.id))
      clearCheckoutItemIds()
      navigate('/order-confirmed')
    } catch (requestError) {
      setError(requestError.message || 'Your order could not be saved. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="checkout-route-page page-shell">
      <section className="checkout-hero payment-hero">
        <div>
          <h1>Payment</h1>
          <p>Home &nbsp;›&nbsp; Cart &nbsp;›&nbsp; Checkout &nbsp;›&nbsp; Payment</p>
        </div>
      </section>

      <CheckoutSteps active="payment" />

      {items.length === 0 ? (
        <section className="checkout-empty-state">
          <h2>Your selected items are not available.</h2>
          <p>Return to your cart, choose the handmade pieces you want, then continue to payment.</p>
          <Link to="/cart">Return to Cart →</Link>
        </section>
      ) : (
        <section className="checkout-layout payment-layout">
          <form className="checkout-panel payment-panel" onSubmit={placeOrder}>
            <div className="checkout-panel-heading">
              <h2>Select Payment Method</h2>
              <p>Choose the option that suits you best</p>
            </div>

            <div className="payment-options">
              {paymentOptions.map((option) => (
                <label className={`payment-method-card ${method === option.id ? 'selected' : ''}`} key={option.id}>
                  <input type="radio" value={option.id} checked={method === option.id} onChange={() => { setMethod(option.id); setError('') }} />
                  <span className="payment-method-mark">{option.mark}</span>
                  <span><strong>{option.title}</strong><small>{option.description}</small></span>
                  {option.id === 'card' && <span className="card-brand-row"><b>VISA</b><b>MC</b><b>▣</b></span>}
                </label>
              ))}
            </div>

            {method === 'card' && (
              <div className="card-details-grid">
                <label className="checkout-field full-field">Card Number
                  <input name="number" inputMode="numeric" value={card.number} onChange={updateCard} placeholder="1234 5678 9012 3456" />
                </label>
                <label className="checkout-field">Expiry Date
                  <input name="expiry" value={card.expiry} onChange={updateCard} placeholder="MM/YY" />
                </label>
                <label className="checkout-field">CVV
                  <input name="cvv" inputMode="numeric" value={card.cvv} onChange={updateCard} placeholder="123" />
                </label>
                <label className="checkout-field full-field">Cardholder Name
                  <input name="holder" value={card.holder} onChange={updateCard} placeholder="Enter name on card" />
                </label>
              </div>
            )}

            <label className="save-address-check billing-check"><input type="checkbox" checked={billingSame} onChange={(event) => setBillingSame(event.target.checked)} /> Billing address same as shipping address</label>
            {error && <p className="checkout-error" role="alert">{error}</p>}
            <button className="checkout-continue-button" type="submit" disabled={submitting}>{submitting ? "Placing Order..." : "Place Order"} <span>→</span></button>
            <p className="secure-payment-note">▣ Your payment information is secure and encrypted.</p>
          </form>

          <div className="checkout-right-column">
            <CheckoutOrderSummary items={items} totals={totals} />
            <section className="checkout-coupon-card">
              <h3>Have a Coupon Code?</h3>
              <form onSubmit={applyCheckoutCoupon}>
                <input value={couponInput} onChange={(event) => setCouponInput(event.target.value)} placeholder="Enter coupon code" />
                <button type="submit">Apply</button>
              </form>
              {couponMessage && <p>{couponMessage}</p>}
            </section>
          </div>
        </section>
      )}

      <SiteFooter />
    </main>
  )
}
