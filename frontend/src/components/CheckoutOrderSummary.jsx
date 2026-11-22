import { Link } from 'react-router-dom'

function SummaryLine({ label, value, muted = false }) {
  return <div className={`checkout-summary-line ${muted ? 'muted' : ''}`}><span>{label}</span><strong>{value}</strong></div>
}

export default function CheckoutOrderSummary({ items, totals, editLabel }) {
  return (
    <aside className="checkout-order-summary">
      <div className="checkout-summary-heading">
        <h2>Order Summary <span>({items.length} {items.length === 1 ? 'item' : 'items'})</span></h2>
        {editLabel && <Link to="/cart">{editLabel}</Link>}
      </div>

      <div className="checkout-summary-items">
        {items.map((item) => (
          <article className="checkout-summary-item" key={item.id}>
            <img src={item.image} alt={item.name} />
            <div>
              <h3>{item.name}</h3>
              <strong>Rs. {item.price.toLocaleString()}</strong>
              <small>Qty: {item.quantity}</small>
            </div>
          </article>
        ))}
      </div>

      <div className="checkout-summary-totals">
        <SummaryLine label="Subtotal" value={`Rs. ${totals.subtotal.toLocaleString()}`} muted />
        <SummaryLine label="Shipping" value={`Rs. ${totals.shipping.toLocaleString()}`} muted />
        <SummaryLine label="Tax (if applicable)" value="Rs. 0" muted />
        {totals.discount > 0 && <SummaryLine label="Coupon Discount" value={`− Rs. ${totals.discount.toLocaleString()}`} muted />}
        <div className="checkout-summary-total"><span>Total</span><strong>Rs. {totals.total.toLocaleString()}</strong></div>
      </div>
    </aside>
  )
}
