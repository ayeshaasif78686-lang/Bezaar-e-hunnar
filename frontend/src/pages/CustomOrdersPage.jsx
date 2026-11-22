import { useState } from 'react'
import { apiRequest } from '../services/api.js'
import { Link } from 'react-router-dom'
import SiteFooter from '../components/SiteFooter.jsx'

const examples = [
  { title: 'Custom Potli Bag', image: '/assets/gift-banner.png', text: 'Personalized embroidery and fabric choices' },
  { title: 'Personalized Tote Bag', image: '/assets/collection-hero.png', text: 'A unique everyday keepsake' },
  { title: 'Custom Jewelry', image: '/assets/artisan-support-banner.png', text: 'Made for your occasion and style' }
]

export default function CustomOrdersPage() {
  const [submitted, setSubmitted] = useState(false)
  const [sending, setSending] = useState(false)
  const [error, setError] = useState('')
  const [form, setForm] = useState({ name: '', email: '', type: '', style: '', details: '', budget: '', consent: false })
  function update(e) { const { name, value, checked, type } = e.target; setForm(v => ({ ...v, [name]: type === 'checkbox' ? checked : value })) }
  async function submit(e) {
    e.preventDefault()
    if (sending) return
    setSending(true)
    setError('')
    try {
      await apiRequest('/custom-orders', { method: 'POST', body: JSON.stringify(form) })
      setSubmitted(true)
    } catch (requestError) {
      setError(requestError.message || 'Your custom order could not be submitted. Please try again.')
    } finally {
      setSending(false)
    }
  }
  return <main className="heritage-page custom-orders-page">
    <section className="heritage-page-hero custom-orders-hero"><div><p className="heritage-eyebrow">MADE JUST FOR YOU</p><h1>Custom Orders</h1><p className="heritage-script">Your Vision, Our Craftsmanship ♡</p><p>Get personalized handmade pieces, just the way you want them.</p><a className="heritage-outline-button" href="#custom-request">Let's Create Together →</a></div></section>
    {submitted ? <section className="custom-success"><span>✓</span><h2>Your Custom Order Request is Submitted!</h2><p>Thank you for choosing Bazaar-e-Hunar. Your request has been saved securely. Our team will review it and contact you.</p><button onClick={() => { setSubmitted(false); setError(''); setForm({ name:'', email:'', type:'', style:'', details:'', budget:'', consent:false }) }}>Submit another request</button><Link to="/">Back to Home →</Link></section> : <>
      <section className="custom-process"><h2>How It Works</h2><div><article><b>01</b><strong>Share Your Idea</strong><p>Tell us what you have in mind, design, style or purpose.</p></article><i>→</i><article><b>02</b><strong>We Design It</strong><p>Our artisan plans the design and prepares a preview.</p></article><i>→</i><article><b>03</b><strong>Confirm Details</strong><p>Review the details and confirm your order.</p></article><i>→</i><article><b>04</b><strong>Receive Your Order</strong><p>Your custom piece is carefully prepared and delivered.</p></article></div></section>
      <section className="custom-workspace" id="custom-request">
        <form className="custom-form" onSubmit={submit}><h2>✧ &nbsp; Request Your Custom Order</h2><div className="custom-form-fields">
          <label>Your Name *<input name="name" value={form.name} onChange={update} required placeholder="Enter your name"/></label>
          <label>Your Email *<input name="email" type="email" value={form.email} onChange={update} required placeholder="you@example.com"/></label>
          <label>Select Product Type *<select name="type" value={form.type} onChange={update} required><option value="">Choose an option</option>{['Ajrak & Textiles','Bags','Jewelry','Home Decor','Pottery','Calligraphy','Paintings','Khussa','Other'].map(x=><option key={x}>{x}</option>)}</select></label>
          <label>Select Style / Theme<select name="style" value={form.style} onChange={update}><option value="">Select style</option>{['Traditional','Contemporary','Minimal','Bridal / Occasion','Personalized Gift'].map(x=><option key={x}>{x}</option>)}</select></label>
          <label className="custom-full">Describe Your Idea in Detail *<textarea name="details" value={form.details} onChange={update} required rows="4" placeholder="Describe your idea, preferred colours, size, materials and any personalization..."/></label>
          <label>Estimated Budget<select name="budget" value={form.budget} onChange={update}><option value="">Select budget range</option>{['Under Rs. 2,000','Rs. 2,000 - 5,000','Rs. 5,000 - 10,000','Rs. 10,000 - 20,000','Above Rs. 20,000'].map(x=><option key={x}>{x}</option>)}</select></label>
          <label className="custom-full custom-consent"><input name="consent" type="checkbox" checked={form.consent} onChange={update} required/> I agree to the Terms & Conditions and Privacy Policy.</label>
          {error && <p className="form-error" role="alert">{error}</p>}<button className="heritage-primary-button custom-submit" type="submit" disabled={sending}>{sending ? 'Submitting…' : '➤  Submit Request →'}</button>
        </div></form>
        <aside className="custom-aside"><h2>✧ &nbsp; Customization Options</h2><div className="custom-options">{[['✧','Embroidery & Stitching'],['▤','Fabric Choice'],['◉','Colors'],['◷','Size & Fit'],['❀','Design Pattern'],['✎','Personalized Name']].map(([icon,title])=><div key={title}><b>{icon}</b><span>{title}</span></div>)}</div><h2 className="custom-examples-title">Our Custom Work Examples</h2><div className="custom-examples">{examples.map(x=><article key={x.title}><img src={x.image} alt={x.title}/><strong>{x.title}</strong><small>{x.text}</small></article>)}</div></aside>
      </section>
      <section className="custom-benefits"><span>◇ &nbsp; Premium Quality</span><span>♡ &nbsp; Handcrafted with Love</span><span>♧ &nbsp; On-Time Delivery</span><span>☏ &nbsp; Dedicated Support</span></section>
      <section className="custom-cta">Turn Your Ideas Into Timeless Handcrafted Treasures <Link to="/products">Explore Our Collections →</Link></section>
    </>}
    <SiteFooter newsletter/>
  </main>
}
