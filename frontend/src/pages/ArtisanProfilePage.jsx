import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import MarketplaceProductCard from '../components/MarketplaceProductCard.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { getArtisanById, getProductsByArtisan } from '../data/storeData.js'
import { apiRequest } from '../services/api.js'

export default function ArtisanProfilePage() {
  const { artisanId } = useParams()
  const localArtisan = getArtisanById(artisanId)
  const [artisan, setArtisan] = useState(localArtisan)
  const [activeTab, setActiveTab] = useState('about')
  const [isFollowing, setIsFollowing] = useState(() => {
    try {
      return localStorage.getItem(`beh_following_${artisanId}`) === 'true'
    } catch {
      return false
    }
  })

  useEffect(() => {
    setArtisan(localArtisan)
    setActiveTab('about')
    try {
      setIsFollowing(localStorage.getItem(`beh_following_${artisanId}`) === 'true')
    } catch {
      setIsFollowing(false)
    }
    apiRequest(`/artisans/${artisanId}`)
      .then((data) => data && setArtisan((current) => ({ ...current, ...data })))
      .catch(() => {})
  }, [artisanId])

  const artisanProducts = getProductsByArtisan(artisan.id)
  const tabContent = useMemo(() => ({
    about: <p>{artisan.bio || `Every piece created by ${artisan.name} carries a story of regional tradition, careful craftsmanship and a commitment to keeping heritage skills alive.`}</p>,
    products: <p>{artisan.name}'s available handmade pieces are shown below. Select any product to see its story, details and cart options.</p>,
    reviews: <div className="artisan-review-copy"><strong>{artisan.rating} out of 5</strong><span>Based on {artisan.reviews} verified customer reviews.</span><p>Buyers appreciate the careful finishing, authentic heritage details and the personal story behind each piece.</p></div>,
    policy: <p>Each item is handmade, so small variations are part of its character. Orders are packed carefully and support is available for shipping or product questions.</p>
  }), [artisan])

  function toggleFollow() {
    const nextValue = !isFollowing
    setIsFollowing(nextValue)
    try {
      localStorage.setItem(`beh_following_${artisanId}`, String(nextValue))
    } catch {
      // The page remains usable if browser storage is unavailable.
    }
  }

  function openFullStory() {
    setActiveTab('about')
    document.getElementById('artisan-story')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  return (
    <main className="artisan-profile-page page-shell">
      <section className="artisan-hero">
        <div>
          <h1>Meet<br />Our Artisans</h1>
          <p>Real people. Real stories.<br />Preserving heritage, one craft at a time.</p>
        </div>
      </section>

      <div className="catalog-breadcrumb">Home &nbsp;›&nbsp; Artisans &nbsp;›&nbsp; {artisan.name}</div>

      <section className="artisan-profile-card">
        <div className="artisan-main-profile">
          <img src={artisan.image} alt={artisan.name} />
          <div>
            <h2>{artisan.name}</h2>
            <p className="artisan-title">{artisan.role}</p>
            <p className="artisan-location">⌖ {artisan.city}</p>
            <div className="artisan-stars">★★★★★ <span>{artisan.rating} ({artisan.reviews} reviews)</span></div>
            <div className="artisan-actions">
              <button type="button" className={`follow-btn ${isFollowing ? 'is-following' : ''}`} onClick={toggleFollow} aria-pressed={isFollowing}>
                {isFollowing ? '✓ Following' : '♧ Follow'}
              </button>
              <button type="button" className="visit-store-btn" onClick={openFullStory}>Visit Full Story →</button>
            </div>
          </div>
        </div>

        <div className="artisan-stats">
          <div><span>◴</span><strong>{artisan.years}</strong><small>Years of Experience</small></div>
          <div><span>◇</span><strong>{artisan.sold}</strong><small>Pieces Sold</small></div>
          <div><span>♙</span><strong>Handcrafted</strong><small>with Traditional Techniques</small></div>
          <div><span>♧</span><strong>Supports</strong><small>Local Community</small></div>
        </div>
      </section>

      <section className="artisan-tabs-panel" id="artisan-story">
        <div className="artisan-tabs">
          <button type="button" className={activeTab === 'about' ? 'active' : ''} onClick={() => setActiveTab('about')}>About</button>
          <button type="button" className={activeTab === 'products' ? 'active' : ''} onClick={() => setActiveTab('products')}>Products ({artisanProducts.length})</button>
          <button type="button" className={activeTab === 'reviews' ? 'active' : ''} onClick={() => setActiveTab('reviews')}>Reviews ({artisan.reviews})</button>
          <button type="button" className={activeTab === 'policy' ? 'active' : ''} onClick={() => setActiveTab('policy')}>Store Policy</button>
        </div>
        <div className="artisan-tab-content">{tabContent[activeTab]}</div>
      </section>

      <section className="artisan-quote-strip">
        <blockquote>“{artisan.quote}”<br /><span>– {artisan.name}</span></blockquote>
        <div className="artisan-pottery-visual" />
      </section>

      <section className="artisan-products-section">
        <div className="section-head">
          <h2>Featured Products from {artisan.name}</h2>
          <Link to={`/products?artisan=${encodeURIComponent(artisan.name)}`}>View All →</Link>
        </div>
        <div className="artisan-product-grid">
          {artisanProducts.slice(0, 4).map((product) => <MarketplaceProductCard key={product.id} product={product} compact />)}
        </div>
      </section>

      <section className="artisan-support-banner">
        <div>
          <h2>Support<br />Our Artisans</h2>
          <p>Every purchase helps preserve traditional skills and support local communities.</p>
          <button type="button" onClick={openFullStory}>Learn More →</button>
        </div>
      </section>

      <SiteFooter />
    </main>
  )
}
