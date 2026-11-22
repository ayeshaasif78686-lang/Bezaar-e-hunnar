import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../context/AuthContext.jsx'
import { useShop } from '../context/ShopContext.jsx'
import { categories } from '../data/storeData.js'

export default function Navbar() {
  const { user, logout } = useAuth()
  const { cartCount, wishlistCount } = useShop()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const [search, setSearch] = useState('')

  function handleLogout() {
    logout()
    navigate('/login')
  }

  function submitSearch(event) {
    event.preventDefault()
    navigate(`/products${search.trim() ? `?q=${encodeURIComponent(search.trim())}` : ''}`)
  }

  return (
    <>
      <div className="heritage-topbar">
        <div>♧ &nbsp; Free Shipping Across Pakistan on Orders Above Rs. 5,000 &nbsp; | &nbsp; Celebrating Our Heritage ♡</div>
        <div className="heritage-topbar-right"><Link to="/orders">Track Your Order</Link><Link to="/contact">Become a Seller</Link><Link to="/our-story">Our Story</Link><Link to="/contact">Help</Link><span>PKR (Rs)⌄</span></div>
      </div>
      <header className="heritage-nav">
        <Link to="/" className="heritage-brand" aria-label="Bazaar-e-Hunar home">
          <img src="/assets/bazaar-logo-new.png" alt="Bazaar-e-Hunar" />
        </Link>
        <button className="mobile-menu-toggle" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle navigation">☰</button>
        <nav className={`heritage-links ${menuOpen ? 'is-open' : ''}`} aria-label="Primary navigation">
          <NavLink to="/" end onClick={() => setMenuOpen(false)}>Home</NavLink>
          <NavLink to="/products" onClick={() => setMenuOpen(false)}>Shop</NavLink>
          <div className="heritage-category-nav">
            <button type="button" onClick={() => setMenuOpen((v) => !v)} aria-expanded={menuOpen}>Categories <span>⌄</span></button>
            <div className="heritage-category-menu">
              {categories.map((category) => <Link key={category.id} to={`/products?category=${encodeURIComponent(category.title)}`} onClick={() => setMenuOpen(false)}>{category.title}</Link>)}
            </div>
          </div>
          <NavLink to="/our-story" onClick={() => setMenuOpen(false)}>Our Story</NavLink>
          <NavLink to="/custom-orders" onClick={() => setMenuOpen(false)}>Custom Orders</NavLink>
          <NavLink to="/contact" onClick={() => setMenuOpen(false)}>Contact</NavLink>
        </nav>
        <div className="heritage-actions">
          <form className="heritage-search" onSubmit={submitSearch}><button aria-label="Search">⌕</button><input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search" aria-label="Search products" /></form>
          <Link to="/wishlist" className="heritage-icon" aria-label="Wishlist">♡<i>{wishlistCount}</i></Link>
          {user ? <><Link to="/dashboard" className="heritage-icon" aria-label="My account">♙</Link><button className="heritage-icon" onClick={handleLogout} aria-label="Logout">↪</button></> : <Link to="/login" className="heritage-icon" aria-label="My account">♙</Link>}
          <Link to="/cart" className="heritage-icon" aria-label="Cart">🛒<i>{cartCount}</i></Link>
        </div>
      </header>
    </>
  )
}
