import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getProductById, recommendedProducts } from '../data/storeData.js'
import ProductReviews from '../components/ProductReviews.jsx'
import { useShop } from '../context/ShopContext.jsx'
import { apiRequest } from '../services/api.js'
import { normalizeCatalogProduct } from '../services/catalog.js'

const tabs = ['Description', 'Product Details', 'Shipping & Returns', 'Reviews']

function stars(rating) {
  const full = Math.round(rating)
  return '★★★★★'.slice(0, full) + '☆☆☆☆☆'.slice(0, 5 - full)
}

export default function ProductDetailPage() {
  const { productId } = useParams()
  const fallbackProduct = getProductById(productId) || getProductById('ajrak-shawl-1')
  const [product, setProduct] = useState(fallbackProduct)
  const [selectedSize, setSelectedSize] = useState(fallbackProduct.sizes?.[1] || fallbackProduct.sizes?.[0] || 'Standard')
  const [quantity, setQuantity] = useState(1)
  const [activeTab, setActiveTab] = useState(tabs[0])
  const { addToCart, toggleWishlist, isWishlisted } = useShop()
  const navigate = useNavigate()
  useEffect(() => {
    let active = true
    const fallback = getProductById(productId) || getProductById('ajrak-shawl-1')
    setProduct(fallback)
    setQuantity(1)
    setSelectedSize(fallback.sizes?.[1] || fallback.sizes?.[0] || 'Standard')
    setActiveTab(tabs[0])
    apiRequest(`/products/${encodeURIComponent(productId)}`)
      .then((remote) => {
        if (!active || !remote) return
        const mapped = normalizeCatalogProduct(remote)
        setProduct(mapped)
        setSelectedSize(mapped.sizes[1] || mapped.sizes[0] || 'Standard')
      })
      .catch(() => {
        // The supplied catalogue remains a fallback for older/static product links.
      })
    return () => { active = false }
  }, [productId])

  return (
    <main className="page-shell detail-page">
      <section className="breadcrumbs">Home &nbsp;&gt;&nbsp; {product.category} &nbsp;&gt;&nbsp; {product.name}</section>

      <section className="detail-layout shell-card">
        <div className="gallery-column single-product-image">
          <div className="main-preview">
            {product.badge && <span className="badge large">{product.badge}</span>}
            <button type="button" className={`wish-btn detail-wish ${isWishlisted(product.id) ? 'active' : ''}`} aria-label={isWishlisted(product.id) ? `Remove ${product.name} from favourites` : `Add ${product.name} to favourites`} aria-pressed={isWishlisted(product.id)} onClick={() => toggleWishlist(product.id)}>{isWishlisted(product.id) ? '♥' : '♡'}</button>
            <img src={product.image} alt={product.name} />
          </div>
        </div>

        <div className="detail-copy">
          <span className="pill-label">{product.category}</span>
          <h1>{product.name}</h1>
          <div className="detail-rating"><span>{stars(product.rating)}</span> <small>({product.reviews} reviews)</small></div>
          <div className="detail-price">Rs. {product.price.toLocaleString()}</div>
          <p className="detail-desc">{product.description}</p>

          <div className="icon-feature-grid">
            <div>100% Handmade</div>
            <div>Authentic Craft</div>
            <div>Support Artisans</div>
            <div>Secure Payments</div>
          </div>

          <div className="detail-group">
            <h4>Size</h4>
            <div className="size-row">
              {product.sizes.map((size) => (
                <button key={size} className={`size-btn ${selectedSize === size ? 'active' : ''}`} onClick={() => setSelectedSize(size)}>{size}</button>
              ))}
            </div>
          </div>

          <div className="detail-group">
            <h4>Quantity</h4>
            <div className="qty-row">
              <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}>−</button>
              <span>{quantity}</span>
              <button onClick={() => setQuantity((q) => q + 1)}>+</button>
            </div>
          </div>

          <button className="write-review-link" onClick={() => { setActiveTab('Reviews'); document.getElementById('product-tabs')?.scrollIntoView({ behavior: 'smooth' }) }}>Write a Review</button>
          <div className="buy-row">
            <button className="buy-btn primary" onClick={() => addToCart(product, quantity)}>Add to Cart</button>
            <button className="buy-btn secondary" onClick={() => { addToCart(product, quantity); navigate('/cart') }}>⚡ Buy Now</button>
          </div>
        </div>

        <aside className="artisan-side-card">
          <div className="artisan-mini">
            <img src={product.image} alt={product.artisan} />
            <div>
              <h3>{product.artisan}</h3>
              <p>{product.artisanRole}</p>
              <p>📍 {product.artisanCity}</p>
            </div>
          </div>
          <div className="detail-rating side-rating"><span>★★★★☆</span> <small>4.8 (56 reviews)</small></div>
          <Link className="visit-store-btn" to={`/artisans/${product.artisanId || 'ustad-karim'}`}>View Artisan Store →</Link>
          <ul className="artisan-points">
            <li>Handcrafted with traditional techniques</li>
            <li>Eco-friendly, high quality materials</li>
            <li>Supports local artisans</li>
            <li>Each piece is unique</li>
          </ul>
          <div className="share-row">Share &nbsp; • &nbsp; f &nbsp; WhatsApp &nbsp; Pinterest &nbsp; 🔗</div>
        </aside>
      </section>

      <section className="service-strip shell-card">
        <div>Estimated Delivery<br /><small>3-7 Business Days</small></div>
        <div>Free Shipping<br /><small>on orders above Rs. 5,000</small></div>
        <div>Easy Returns<br /><small>within 7 days</small></div>
        <div>Secure Payments<br /><small>Multiple options</small></div>
      </section>

      <section className="detail-bottom-grid">
        <div className="tabs-panel shell-card" id="product-tabs">
          <div className="tabs-row">
            {tabs.map((tab) => (
              <button key={tab} className={activeTab === tab ? 'active' : ''} onClick={() => setActiveTab(tab)}>{tab}</button>
            ))}
          </div>
          <div className="tab-content">
            {activeTab === 'Description' && <><h2>Product Description</h2><p>{product.description} Every detail is carefully finished by skilled artisans using traditional techniques passed down through generations.</p></>}
            {activeTab === 'Product Details' && <><h2>Product Details</h2><dl><dt>Material</dt><dd>{product.material}</dd><dt>Artisan</dt><dd>{product.artisan}</dd><dt>Region</dt><dd>{product.region}</dd><dt>Size</dt><dd>{selectedSize}</dd></dl></>}
            {activeTab === 'Shipping & Returns' && <><h2>Shipping &amp; Returns</h2><p>Estimated delivery: 3 to 7 business days. Free shipping on orders of Rs. 5,000 or more. Standard shipping: Rs. 300. Returns within 7 days.</p></>}
            {activeTab === 'Reviews' && <ProductReviews key={product.id} productId={product.id} />}
          </div>
        </div>
        <div className="heritage-mini-card shell-card">
          <div>Crafted by Skilled Hands</div>
          <div>Preserving Cultural Heritage</div>
          <div>Supporting Local Communities</div>
        </div>
      </section>

      <section className="product-section recommended-section">
        <div className="section-head">
          <h2>You May Also Like</h2>
          <Link to="/products">View All →</Link>
        </div>
        <div className="product-grid">
          {recommendedProducts.map((item) => (
            <article className="product-card" key={item.id}>
              <div className="product-image-wrap">
                {item.badge && <span className="badge">{item.badge}</span>}
                <button type="button" className={`wish-btn ${isWishlisted(item.id) ? 'active' : ''}`} aria-label={isWishlisted(item.id) ? `Remove ${item.name} from favourites` : `Add ${item.name} to favourites`} aria-pressed={isWishlisted(item.id)} onClick={() => toggleWishlist(item.id)}>{isWishlisted(item.id) ? '♥' : '♡'}</button>
                <Link to={`/products/${item.id}`}><img src={item.image} alt={item.name} className="product-image" /></Link>
              </div>
              <div className="product-body">
                <h3><Link to={`/products/${item.id}`}>{item.name}</Link></h3>
                <div className="product-price">Rs. {item.price.toLocaleString()}</div>
                <div className="product-rating"><span>{stars(item.rating)}</span> <small>({item.reviews})</small></div>
              </div>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}
