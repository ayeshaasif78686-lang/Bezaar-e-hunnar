import { Link } from 'react-router-dom'
import { useState } from 'react'

export function SocialIcons() {
  const common = { width: 18, height: 18, viewBox: '0 0 24 24', fill: 'currentColor', 'aria-hidden': true }
  return (
    <div className="social-row bh-social-row">
      <a href="https://facebook.com" target="_blank" rel="noreferrer" aria-label="Facebook"><svg {...common}><path d="M13.5 21v-7.5h2.6l.4-3h-3V8.6c0-.9.3-1.5 1.5-1.5h1.6V4.4c-.3 0-1.2-.1-2.3-.1-2.3 0-3.9 1.4-3.9 4v2.2H7.8v3h2.6V21z" /></svg></a>
      <a href="https://instagram.com" target="_blank" rel="noreferrer" aria-label="Instagram"><svg {...common}><path d="M12 7.3A4.7 4.7 0 1 0 12 16.7 4.7 4.7 0 0 0 12 7.3zm0 7.7a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm5-7.9a1.1 1.1 0 1 1-2.2 0 1.1 1.1 0 0 1 2.2 0zM21 8c0-2.5-1.9-4.4-4.4-4.5C15.5 3.4 8.5 3.4 7.4 3.5 4.9 3.6 3 5.5 3 8c-.1 1.1-.1 7.9 0 9 .1 2.5 1.9 4.4 4.4 4.5 1.1.1 8.1.1 9.2 0 2.5-.1 4.4-2 4.4-4.5.1-1.1.1-7.9 0-9zM19 17c0 1.5-1.1 2.6-2.6 2.7-1 .1-7.8.1-8.8 0C6.1 19.6 5 18.5 5 17c-.1-1-.1-7.8 0-8.8C5 6.7 6.1 5.6 7.6 5.5c1-.1 7.8-.1 8.8 0C17.9 5.6 19 6.7 19 8.2c.1 1 .1 7.8 0 8.8z" /></svg></a>
      <a href="https://pinterest.com" target="_blank" rel="noreferrer" aria-label="Pinterest"><svg {...common}><path d="M12.2 3C7.6 3 5.3 6.1 5.3 8.8c0 1.6.6 3 1.9 3.5.2.1.4 0 .4-.2l.2-.8c.1-.2 0-.3-.1-.5-.4-.4-.6-1-.6-1.8 0-2.3 1.7-4.3 4.5-4.3 2.5 0 3.8 1.5 3.8 3.5 0 2.6-1.2 4.8-2.9 4.8-1 0-1.7-.8-1.4-1.8.3-1.1.8-2.4.8-3.2 0-.7-.4-1.3-1.2-1.3-1 0-1.7 1-1.7 2.3 0 .8.3 1.4.3 1.4l-1.1 4.6c-.3 1.4 0 3 0 3.2 0 .1.1.1.2 0 .1-.1 1.1-1.4 1.5-2.7l.6-2.3c.3.6 1.2 1.1 2.2 1.1 2.9 0 4.9-2.6 4.9-6.1C18.8 5.5 16.5 3 12.2 3z" /></svg></a>
      <a href="https://youtube.com" target="_blank" rel="noreferrer" aria-label="YouTube"><svg {...common}><path d="M21.6 7.2a2.5 2.5 0 0 0-1.8-1.8C18.2 5 12 5 12 5s-6.2 0-7.8.4A2.5 2.5 0 0 0 2.4 7.2C2 8.8 2 12 2 12s0 3.2.4 4.8a2.5 2.5 0 0 0 1.8 1.8C5.8 19 12 19 12 19s6.2 0 7.8-.4a2.5 2.5 0 0 0 1.8-1.8C22 15.2 22 12 22 12s0-3.2-.4-4.8zM10 15V9l5.2 3z" /></svg></a>
    </div>
  )
}

export function NewsletterBand({ compact = false }) {
  const [email, setEmail] = useState('')
  const [done, setDone] = useState(false)

  function submit(event) {
    event.preventDefault()
    if (!email.trim()) return
    setDone(true)
    setEmail('')
  }

  return (
    <section className={`newsletter-section bh-newsletter ${compact ? 'compact-newsletter' : ''}`} id={compact ? undefined : 'about-us'}>
      <div className="bh-newsletter-copy">
        <span className="bh-newsletter-icon" aria-hidden="true">
          <svg viewBox="0 0 48 36" width="46" height="34" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round"><rect x="2" y="3" width="44" height="30" rx="3" /><path d="m3 6 21 16L45 6" /></svg>
        </span>
        <div>
          <h2>Stay in the Loop</h2>
          <p>Get updates on new collections, artisan stories and exclusive offers.</p>
        </div>
      </div>
      <form className="newsletter-form bh-newsletter-form" onSubmit={submit}>
        <label>
          <input type="email" placeholder="Enter your email address" value={email} onChange={(e) => { setEmail(e.target.value); setDone(false) }} aria-label="Email address" required />
        </label>
        <button>{done ? 'Subscribed ✓' : 'Subscribe'}</button>
      </form>
    </section>
  )
}

export default function SiteFooter({ newsletter = false, home = false }) {
  const year = new Date().getFullYear()
  // On the home page the quick links scroll to sections; elsewhere they go back to the home page.
  const homeLink = (hash, label) => home
    ? <a href={`#${hash}`}>{label}</a>
    : <Link to={`/#${hash}`}>{label}</Link>

  return (
    <>
      {newsletter && <NewsletterBand compact />}
      <footer className="site-footer bh-footer">
        <div className="footer-grid">
          <div className="bh-footer-brand">
            <img src="/assets/bazaar-logo-white.png" alt="Bazaar-e-Hunar" className="footer-logo footer-logo-white" />
            <p>A platform to celebrate traditional crafts and support skilled artisans from across Pakistan and beyond.</p>
            <SocialIcons />
          </div>
          <div>
            <h4>Quick Links</h4>
            <Link to="/">Home</Link>
            <Link to="/products">Products</Link>
            {homeLink('services', 'Services')}
            {homeLink('experience', 'Experience')}
            {homeLink('custom-orders', 'Gift & Custom Orders')}
            <Link to="/our-story">About Us</Link>
            <a href="#">Blog</a>
          </div>
          <div>
            <h4>Customer Support</h4>
            <a href="#">FAQs</a>
            <a href="#">Shipping Policy</a>
            <a href="#">Returns &amp; Refunds</a>
            <Link to="/orders">Track Your Order</Link>
            <a href="#">Terms &amp; Conditions</a>
            <a href="#">Privacy Policy</a>
            <Link to="/contact">Contact Us</Link>
          </div>
          <div>
            <h4>Contact Us</h4>
            <p>✉ support@bazaar-e-hunar.com</p>
            <p>☎ +92 313 6421453</p>
            <p>⌂ Lahore, Pakistan</p>
            <div className="footer-tagline">Crafting a Brighter Tomorrow</div>
          </div>
        </div>
        <div className="bh-footer-bottom">
          <span>© {year} Bazaar-e-Hunar. All Rights Reserved.</span>
          <span><a href="#">Privacy Policy</a><i>|</i><a href="#">Terms &amp; Conditions</a></span>
        </div>
      </footer>
    </>
  )
}
