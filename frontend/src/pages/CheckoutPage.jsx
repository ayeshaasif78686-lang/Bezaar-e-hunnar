import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import CheckoutOrderSummary from '../components/CheckoutOrderSummary.jsx'
import CheckoutSteps from '../components/CheckoutSteps.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { useShop } from '../context/ShopContext.jsx'
import { calculateCheckoutTotals, getCheckoutDetails, getCheckoutItems, setCheckoutDetails } from '../utils/checkoutSession.js'

const provinces = ['Punjab', 'Sindh', 'Khyber Pakhtunkhwa', 'Balochistan', 'Islamabad Capital Territory', 'Gilgit-Baltistan', 'Azad Kashmir']

export default function CheckoutPage() {
  const navigate = useNavigate()
  const { cart, coupon } = useShop()
  const [details, setDetails] = useState(() => getCheckoutDetails())
  const [error, setError] = useState('')
  const items = getCheckoutItems(cart)
  const totals = calculateCheckoutTotals(items, details.delivery, coupon)

  function updateField(event) {
    const { name, value, checked, type } = event.target
    setDetails((current) => ({ ...current, [name]: type === 'checkbox' ? checked : value }))
    setError('')
  }

  function continueToPayment(event) {
    event.preventDefault()
    if (items.length === 0) {
      setError('Your selected cart items are no longer available. Please return to the cart and select an item.')
      return
    }
    setCheckoutDetails(details)
    navigate('/payment')
  }

  return (
    <main className="checkout-route-page page-shell">
      <section className="checkout-hero checkout-address-hero">
        <div>
          <h1>Checkout</h1>
          <p>Home &nbsp;›&nbsp; Cart &nbsp;›&nbsp; Checkout</p>
        </div>
      </section>

      <CheckoutSteps active="address" />

      {items.length === 0 ? (
        <section className="checkout-empty-state">
          <h2>Your cart is ready when you are.</h2>
          <p>Add at least one handmade piece, then return here to complete the checkout flow.</p>
          <Link to="/products">Shop Our Collection →</Link>
        </section>
      ) : (
        <section className="checkout-layout">
          <form className="checkout-panel address-panel" onSubmit={continueToPayment}>
            <div className="checkout-panel-heading">
              <h2>Shipping Address</h2>
              <p>Enter your delivery details</p>
            </div>

            <div className="checkout-form-grid">
              <label className="checkout-field full-field">Full Name <em>*</em>
                <input name="fullName" value={details.fullName} onChange={updateField} placeholder="Enter full name" required />
              </label>
              <label className="checkout-field full-field">Phone Number <em>*</em>
                <input name="phone" value={details.phone} onChange={updateField} placeholder="+92 300 1234567" type="tel" required />
              </label>
              <label className="checkout-field full-field">Address <em>*</em>
                <input name="address" value={details.address} onChange={updateField} placeholder="House No, Street, Area" required />
              </label>
              <label className="checkout-field">City <em>*</em>
                <input name="city" value={details.city} onChange={updateField} placeholder="Enter city" required />
              </label>
              <label className="checkout-field">Province <em>*</em>
                <select name="province" value={details.province} onChange={updateField} required>
                  <option value="">Select province</option>
                  {provinces.map((province) => <option key={province} value={province}>{province}</option>)}
                </select>
              </label>
              <label className="checkout-field full-field">Postal Code
                <input name="postalCode" value={details.postalCode} onChange={updateField} placeholder="Enter postal code" />
              </label>
            </div>

            <label className="save-address-check"><input name="saveAddress" type="checkbox" checked={details.saveAddress} onChange={updateField} /> Save this address for future orders</label>

            <div className="delivery-section">
              <h3>Delivery Method</h3>
              <label className={`delivery-option ${details.delivery === 'standard' ? 'selected' : ''}`}>
                <input name="delivery" type="radio" value="standard" checked={details.delivery === 'standard'} onChange={updateField} />
                <span className="delivery-icon">▣</span>
                <span><strong>Standard Delivery</strong><small>3-7 Business Days</small></span>
                <b>Rs. 300</b>
              </label>
              <label className={`delivery-option ${details.delivery === 'express' ? 'selected' : ''}`}>
                <input name="delivery" type="radio" value="express" checked={details.delivery === 'express'} onChange={updateField} />
                <span className="delivery-icon">▣</span>
                <span><strong>Express Delivery</strong><small>1-3 Business Days</small></span>
                <b>Rs. 600</b>
              </label>
            </div>

            {error && <p className="checkout-error" role="alert">{error}</p>}
            <button className="checkout-continue-button" type="submit">Continue to Payment <span>→</span></button>
          </form>

          <div className="checkout-right-column">
            <CheckoutOrderSummary items={items} totals={totals} editLabel="Edit Cart" />
            <section className="checkout-trust-icons">
              <div><span>▣</span><strong>Secure</strong><small>Payments</small></div>
              <div><span>◌</span><strong>Easy Returns</strong><small>Within 7 days</small></div>
              <div><span>◎</span><strong>Worldwide</strong><small>Shipping</small></div>
            </section>
          </div>
        </section>
      )}

      <SiteFooter />
    </main>
  )
}
