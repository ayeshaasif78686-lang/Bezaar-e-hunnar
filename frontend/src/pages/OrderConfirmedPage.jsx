import { useState } from 'react'
import { Link } from 'react-router-dom'
import CheckoutSteps from '../components/CheckoutSteps.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { getLastOrder } from '../utils/checkoutSession.js'

const nextSteps = [
  { icon: '✉', title: 'Order Confirmed', description: "We've received your order." },
  { icon: '▣', title: 'Processing', description: 'Your items are being prepared.' },
  { icon: '▣', title: 'Shipped', description: "You'll receive a tracking update." },
  { icon: '▣', title: 'Delivered', description: 'Enjoy your handmade treasures.' }
]

export default function OrderConfirmedPage() {
  const order = getLastOrder()
  const [showDetails, setShowDetails] = useState(false)

  return (
    <main className="checkout-route-page page-shell">
      <section className="checkout-hero order-confirmed-hero">
        <div>
          <h1>Order Confirmed</h1>
          <p>Home &nbsp;›&nbsp; Order Confirmed</p>
        </div>
      </section>

      <CheckoutSteps active="review" />

      {!order ? (
        <section className="checkout-empty-state">
          <h2>No recent order was found.</h2>
          <p>Your order confirmation will appear here after you complete checkout.</p>
          <Link to="/cart">Go to Cart →</Link>
        </section>
      ) : (
        <section className="order-confirmed-layout">
          <section className="confirmation-card">
            <div className="confirmation-check">✓</div>
            <h2>Order Placed Successfully!</h2>
            <p>Thank you for supporting our artisans.<br />Your order has been received.</p>

            <div className="confirmation-order-banner">
              <div className="confirmation-package">▣</div>
              <div><span>Order Number</span><strong>#{order.number}</strong></div>
              <div><span>Estimated Delivery</span><strong>{order.estimatedDelivery}</strong></div>
            </div>

            <div className="confirmation-actions">
              <button type="button" onClick={() => setShowDetails((value) => !value)}>View Order Details</button>
              <Link to="/products">Continue Shopping</Link>
            </div>

            {showDetails && (
              <div className="confirmation-details" aria-live="polite">
                <p><strong>{order.itemCount} item{order.itemCount === 1 ? '' : 's'}</strong> | {order.paymentMethod}</p>
                <p>{order.delivery}</p>
                <p>Order Total: <strong>Rs. {order.totals.total.toLocaleString()}</strong></p>
              </div>
            )}
          </section>

          <section className="whats-next-card">
            <h2>What’s Next?</h2>
            <div className="order-journey">
              {nextSteps.map((step, index) => (
                <article key={step.title}>
                  <span>{step.icon}</span>
                  <strong>{index + 1}. {step.title}</strong>
                  <p>{step.description}</p>
                </article>
              ))}
            </div>
          </section>
        </section>
      )}

      <SiteFooter />
    </main>
  )
}
