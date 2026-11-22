import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { artisans, artistCategories, categories, featuredProducts } from '../data/storeData.js'
import { useShop } from '../context/ShopContext.jsx'
import SiteFooter, { NewsletterBand } from '../components/SiteFooter.jsx'
import { loadPublicCatalog } from '../services/catalog.js'

function stars(rating) {
  const full = Math.round(rating)
  return '★★★★★'.slice(0, full) + '☆☆☆☆☆'.slice(0, 5 - full)
}

/* ---------- Hero slides (slide 1 is the reference banner; the rest reuse the collection photography) ---------- */
const heroSlides = [
  {
    id: 'stories',
    image: '/assets/home-hero-slide-1.jpg',
    position: 'center 40%',
    eyebrow: 'ARTISANS',
    title: ['Handmade Stories,', 'Timeless Beauty'],
    text: 'Discover heritage crafts that tell a story…',
    cta: { label: 'Explore Collection', to: '/products' },
  },
  {
    id: 'fabrics',
    image: '/assets/home-fabrics-textiles.jpg',
    position: 'center 58%',
    eyebrow: 'FABRICS & TEXTILES',
    title: ['Art That Tells', 'A Story'],
    text: 'Discover handcrafted pieces inspired by our heritage.',
    cta: { label: 'Shop Textiles', to: '/products?category=Ajrak%20Shawl%7CPhulkari%20Dupatta%7CSindhi%20Topi' },
  },
  {
    id: 'spices',
    image: '/assets/home-spices-teas.jpg',
    position: 'center 62%',
    eyebrow: 'SPICES & TEAS',
    title: ['Flavours Rooted', 'In Heritage'],
    text: 'Saffron, cardamom and fragrant teas, served the traditional way.',
    cta: { label: 'Explore Spices & Teas', to: '/products?q=spice' },
  },
  {
    id: 'jewellery',
    image: '/assets/home-jewellery.jpg',
    position: 'center 38%',
    eyebrow: 'JEWELLERY',
    title: ['Jewellery Made', 'To Be Treasured'],
    text: 'Gold-toned pieces and pearl jhumkas, finished by hand.',
    cta: { label: 'Shop Jewellery', to: '/products?category=Jewelry%7CJhumka' },
  },
]

/* ---------- Shop by Category tiles (reference) ---------- */
const categoryTiles = [
  { title: 'Jewellery', image: '/assets/home-jewellery.jpg', position: '50% 45%', to: '/products?category=Jewelry%7CJhumka' },
  { title: 'Fabrics & Textiles', image: '/assets/home-fabrics-textiles.jpg', position: '55% 55%', to: '/products?category=Ajrak%20Shawl%7CPhulkari%20Dupatta%7CSindhi%20Topi' },
  { title: 'Home Decor', image: '/assets/home-crafts-decor.jpg', position: '22% 40%', to: '/products?category=Home%20Decor%7CGlass%20Art%7CCarved%20Wooden%20Decorative%20Bowl%7CHandcrafted%20Wooden%20Serving%20Tray' },
  { title: 'Spices & Teas', image: '/assets/home-spices-teas.jpg', position: '40% 60%', to: '/products?q=spice' },
  { title: 'Crafts', image: '/assets/home-crafts-decor.jpg', position: '45% 85%', to: '/products?category=Pottery%7CSculptures%7CPaintings%7CCalligraphy%7CTraditional%20Brass%20Candle%20Holder%7CCrochet%20%26%20Knitting' },
  { title: 'Accessories', image: '/assets/home-accessories.jpg', position: '25% 55%', to: '/products?category=Hair%20Accessories%7Ckashmiri%20Bags%7CHandmade%20Ajrak%20Tote%20Bag%7CPerfumes%20%26%20Attars%7CKhusa' },
]

const trustItems = [
  { id: 'delivery', title: 'Fast & Reliable Delivery', text: 'Across Pakistan' },
  { id: 'secure', title: 'Secure Payments', text: '& Easy Checkout' },
  { id: 'gift', title: 'Beautiful Gift Packaging', text: 'for Your Loved Ones' },
  { id: 'support', title: 'Dedicated Support', text: 'Always Here' },
]

const testimonials = [
  { name: 'Ayesha Khan', text: 'Absolutely loved the quality! A true piece of heritage.' },
  { name: 'Hina Fatima', text: 'Beautiful packaging and amazing service.' },
  { name: 'Sara Ahmed', text: 'A perfect blend of tradition and elegance.' },
  { name: 'Maryam Siddiqui', text: 'The embroidery detail is even better in person.' },
  { name: 'Bilal Raza', text: 'Ordered a gift for my mother and she was delighted.' },
  { name: 'Noor Fatima', text: 'Delivery was quick and every piece felt truly handmade.' },
]

function TrustIcon({ id }) {
  const p = { width: 34, height: 34, viewBox: '0 0 32 32', fill: 'none', stroke: 'currentColor', strokeWidth: 1.5, strokeLinecap: 'round', strokeLinejoin: 'round', 'aria-hidden': true }
  if (id === 'delivery') return <svg {...p}><path d="M3 9h14v12H3zM17 13h6l4 4v4h-10z" /><circle cx="9" cy="23" r="2.2" /><circle cx="22" cy="23" r="2.2" /><path d="M6 13h6" /></svg>
  if (id === 'secure') return <svg {...p}><path d="M16 3 6 7v8c0 6 4.2 10.5 10 13 5.8-2.5 10-7 10-13V7z" /><path d="m11.5 16 3.5 3.5 6-7" /></svg>
  if (id === 'gift') return <svg {...p}><path d="M5 13h22v5H5zM7 18h18v10H7zM16 13v15" /><path d="M16 13c-2-5-8-5-7-1.5.700 2.2 4.5 1.5 7 1.500zM16 13c2-5 8-5 7-1.5-.700 2.2-4.5 1.5-7 1.500z" /></svg>
  return <svg {...p}><path d="M6 18v-3a10 10 0 0 1 20 0v3" /><rect x="4" y="17" width="5" height="8" rx="2" /><rect x="23" y="17" width="5" height="8" rx="2" /><path d="M26 25c0 2.5-3 3.5-8 3.5" /></svg>
}

function SectionTitle({ children, id }) {
  return (
    <div className="bh-section-title" id={id}>
      <span aria-hidden="true" />
      <h2>{children}</h2>
      <span aria-hidden="true" />
    </div>
  )
}

export default function HomePage() {
  const { addToCart, toggleWishlist, isWishlisted } = useShop()
  const [slide, setSlide] = useState(0)
  const [paused, setPaused] = useState(false)
  const [reviewStart, setReviewStart] = useState(0)
  const [homeProducts, setHomeProducts] = useState(featuredProducts)
  const [homeCategories, setHomeCategories] = useState(categories)
  const trackRef = useRef(null)
  const artisanRef = useRef(null)

  useEffect(() => {
    const reduce = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches
    if (paused || reduce) return undefined
    const timer = setInterval(() => setSlide((value) => (value + 1) % heroSlides.length), 6500)
    return () => clearInterval(timer)
  }, [paused])

  useEffect(() => {
    let active = true
    const refreshCatalog = async () => {
      try {
        const result = await loadPublicCatalog()
        if (!active) return
        setHomeProducts(result.products.slice(0, 10))
        if (result.categories.length) setHomeCategories(result.categories)
      } catch {
        // Preserve the original homepage content while the backend is unavailable.
      }
    }
    refreshCatalog()
    const timer = window.setInterval(refreshCatalog, 12000)
    return () => { active = false; window.clearInterval(timer) }
  }, [])

  const go = (step) => setSlide((value) => (value + step + heroSlides.length) % heroSlides.length)

  function scrollTrack(ref, direction) {
    const track = ref.current
    if (!track) return
    track.scrollBy({ left: direction * Math.max(240, track.clientWidth * 0.8), behavior: 'smooth' })
  }
  const scrollProducts = (direction) => scrollTrack(trackRef, direction)
  const homeArtisans = artisans.filter((artisan) => artistCategories[artisan.id])

  const visibleReviews = [0, 1, 2].map((offset) => testimonials[(reviewStart + offset) % testimonials.length])

  return (
    <main className="page-shell home-page bh-home">
      {/* ================= HERO ================= */}
      <section
        className="hero-banner bh-hero"
        aria-roledescription="carousel"
        aria-label="Featured collections"
        onMouseEnter={() => setPaused(true)}
        onMouseLeave={() => setPaused(false)}
      >
        {heroSlides.map((item, index) => (
          <div
            key={item.id}
            className={`bh-hero-slide ${index === slide ? 'is-active' : ''}`}
            aria-hidden={index === slide ? undefined : true}
          >
            <span className="bh-hero-fill" style={{ backgroundImage: `url('${item.image}')` }} />
            <img className="bh-hero-img" src={item.image} alt="" />
          </div>
        ))}
        <div className="bh-hero-shade" />

        <div className="hero-content bh-hero-content">
          <div className="hero-copy bh-hero-copy" key={heroSlides[slide].id}>
            <p className="bh-hero-eyebrow"><i /><b>✦</b><em>{heroSlides[slide].eyebrow}</em><b>✦</b><i /></p>
            <h1>{heroSlides[slide].title.map((line) => <span key={line}>{line}</span>)}</h1>
            <p className="bh-hero-text">{heroSlides[slide].text}</p>
            <div className="hero-buttons bh-hero-buttons">
              <Link to={heroSlides[slide].cta.to} className="bh-hero-cta">{heroSlides[slide].cta.label}<span aria-hidden="true">→</span></Link>
              <a href="#meet-artisans" className="btn-secondary bh-hero-secondary">Explore Artisans</a>
            </div>
          </div>

          <div className="hero-story-card bh-hero-badge">
            <svg viewBox="0 0 40 40" width="38" height="38" fill="none" stroke="currentColor" strokeWidth="1.2" aria-hidden="true"><circle cx="20" cy="20" r="4" /><path d="M20 4c3 5 3 9 0 12-3-3-3-7 0-12zM20 36c-3-5-3-9 0-12 3 3 3 7 0 12zM4 20c5-3 9-3 12 0-3 3-7 3-12 0zM36 20c-5 3-9 3-12 0 3-3 7-3 12 0zM9 9c5 1 8 3 9 8-5-1-8-3-9-8zM31 31c-5-1-8-3-9-8 5 1 8 3 9 8zM31 9c-1 5-3 8-8 9 1-5 3-8 8-9zM9 31c1-5 3-8 8-9-1 5-3 8-8 9z" /></svg>
            <h2>Tradition in Every Detail</h2>
            <div className="story-divider">✦</div>
          </div>
        </div>

        <div className="bh-hero-counter" aria-live="polite">
          <button type="button" aria-label="Previous slide" onClick={() => go(-1)}>←</button>
          <span>{slide + 1} / {heroSlides.length}</span>
          <button type="button" aria-label="Next slide" onClick={() => go(1)}>→</button>
        </div>
      </section>

      {/* ================= TRUST STRIP ================= */}
      <section className="feature-row bh-trust" id="services">
        {trustItems.map((item) => (
          <div className="feature-item bh-trust-item" key={item.id}>
            <TrustIcon id={item.id} />
            <p><strong>{item.title}</strong><span>{item.text}</span></p>
          </div>
        ))}
      </section>

      {/* ================= SHOP BY CATEGORY ================= */}
      <section className="category-strip bh-categories" id="categories">
        <SectionTitle>Shop by Category</SectionTitle>
        <div className="bh-category-tiles">
          {homeCategories.slice(0, 6).map((category, index) => {
            const fallbackTile = categoryTiles[index % categoryTiles.length]
            const image = category.image || fallbackTile.image
            return (
              <Link className="bh-category-tile" key={category.id || category.title}
                to={`/products?category=${encodeURIComponent(category.title)}`}>
                <span className="bh-category-arch">
                  <img src={image} alt="" style={{ objectPosition: fallbackTile.position }} loading="lazy" />
                </span>
                <strong>{category.title}</strong>
              </Link>
            )
          })}
        </div>

        <details className="bh-all-categories">
          <summary>Browse all {homeCategories.length} categories</summary>
          <div className="category-list">
            {homeCategories.map((category) => (
              <Link className="category-chip" key={category.id || category.title} to={`/products?category=${encodeURIComponent(category.title)}`}>
                {category.image ? (
                  <div className="category-thumb"><img src={category.image} alt={category.title} loading="lazy" /></div>
                ) : (
                  <div className="category-thumb category-thumb-icon">{category.icon}</div>
                )}
                <span>{category.title}</span>
              </Link>
            ))}
          </div>
        </details>
      </section>

      {/* ================= OUR HERITAGE ================= */}
      <section className="bh-heritage">
        <div className="bh-heritage-image">
          <img src="/assets/home-heritage-palace.jpg" alt="A historic palace glowing at sunset" loading="lazy" />
        </div>
        <div className="bh-heritage-copy">
          <h2>Our Heritage <span aria-hidden="true" /></h2>
          <p>Rooted in tradition, crafted for today — Artisan heritage meets the finest handmade pieces inspired by our rich cultural heritage.</p>
          <Link to="/our-story" className="bh-pill-btn">Learn More <span aria-hidden="true">→</span></Link>
        </div>
      </section>

      {/* ================= BEST SELLERS ================= */}
      <section className="product-section bh-bestsellers" id="featured-products">
        <SectionTitle>Our Best Sellers</SectionTitle>
        <p className="bh-section-sub">Handpicked treasures from our talented artisans · <Link to="/products">View All →</Link></p>

        <div className="bh-carousel">
          <button type="button" className="bh-carousel-arrow left" aria-label="Scroll products left" onClick={() => scrollProducts(-1)}>←</button>
          <div className="product-grid bh-product-track" ref={trackRef}>
            {homeProducts.map((product) => (
              <article className="product-card bh-product-card" key={product.id}>
                <div className="product-image-wrap">
                  {product.badge && <span className="badge">{product.badge}</span>}
                  <button type="button" className={`wish-btn ${isWishlisted(product.id) ? 'active' : ''}`} aria-label={isWishlisted(product.id) ? `Remove ${product.name} from favourites` : `Add ${product.name} to favourites`} aria-pressed={isWishlisted(product.id)} onClick={() => toggleWishlist(product.id)}>{isWishlisted(product.id) ? '♥' : '♡'}</button>
                  <Link to={`/products/${product.id}`}>
                    <img src={product.image} alt={product.name} className="product-image" loading="lazy" />
                  </Link>
                </div>
                <div className="product-body">
                  <h3><Link to={`/products/${product.id}`}>{product.name}</Link></h3>
                  <div className="product-price">Rs. {product.price.toLocaleString()}</div>
                  <div className="product-rating"><span>{stars(product.rating)}</span> <small>{product.rating} ({product.reviews})</small></div>
                  <button type="button" className="cart-btn" onClick={() => addToCart(product)}>
                    Add to Cart
                  </button>
                </div>
              </article>
            ))}
          </div>
          <button type="button" className="bh-carousel-arrow right" aria-label="Scroll products right" onClick={() => scrollProducts(1)}>→</button>
        </div>
      </section>

      {/* ================= MEET OUR ARTISANS ================= */}
      <section className="bh-artisan-banner" aria-label="Meet our artisans">
        <img src="/assets/home-meet-artisans-banner.jpg" alt="Meet our artisans: skilled hands, timeless passion" loading="lazy" />
        <Link to="/artisans/ustad-karim" className="bh-banner-link" aria-label="Know more about our artisans" />
      </section>

      <section className="artisan-section bh-artisans" id="meet-artisans">
        <div className="bh-artisans-head">
          <div>
            <h2>The Hands Behind the Craft</h2>
            <p>The talented hands behind our creations</p>
          </div>
          <Link to="/artisans/ustad-karim">View All →</Link>
        </div>
        <div className="bh-carousel bh-artisan-carousel">
          <button type="button" className="bh-carousel-arrow left" aria-label="Scroll artisans left" onClick={() => scrollTrack(artisanRef, -1)}>←</button>
          <div className="bh-artisan-track" ref={artisanRef}>
            {homeArtisans.map((artisan) => (
              <Link className="bh-artisan-card" key={artisan.id} to={`/artisans/${artisan.id}`}>
                <span className="bh-artisan-photo">
                  <img src={artisan.image} alt={`${artisan.name}, ${artisan.role}`} loading="lazy" />
                  <em>{artistCategories[artisan.id]}</em>
                </span>
                <strong>{artisan.name}</strong>
                <small>{artisan.role}</small>
              </Link>
            ))}
          </div>
          <button type="button" className="bh-carousel-arrow right" aria-label="Scroll artisans right" onClick={() => scrollTrack(artisanRef, 1)}>→</button>
        </div>
      </section>

      {/* ================= CUSTOMER REVIEWS ================= */}
      <section className="bh-reviews">
        <SectionTitle>What Our Customers Say</SectionTitle>
        <div className="bh-reviews-row">
          <button type="button" className="bh-carousel-arrow" aria-label="Previous reviews" onClick={() => setReviewStart((v) => (v - 1 + testimonials.length) % testimonials.length)}>←</button>
          <div className="bh-reviews-list">
            {visibleReviews.map((review) => (
              <figure className="bh-review-card" key={review.name}>
                <span className="bh-avatar" aria-hidden="true">{review.name.split(' ').map((part) => part[0]).join('')}</span>
                <figcaption>
                  <strong>{review.name}</strong>
                  <span className="bh-review-stars" aria-label="5 out of 5 stars">★★★★★</span>
                </figcaption>
                <blockquote>“{review.text}”</blockquote>
              </figure>
            ))}
          </div>
          <button type="button" className="bh-carousel-arrow" aria-label="Next reviews" onClick={() => setReviewStart((v) => (v + 1) % testimonials.length)}>→</button>
        </div>
      </section>

      {/* ================= NEWSLETTER + FOOTER ================= */}
      <NewsletterBand />
      <SiteFooter home />
    </main>
  )
}
