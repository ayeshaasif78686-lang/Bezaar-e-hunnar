import { Link } from 'react-router-dom'
import { useShop } from '../context/ShopContext.jsx'

function stars(rating) {
  const full = Math.round(rating)
  return '★★★★★'.slice(0, full) + '☆☆☆☆☆'.slice(0, 5 - full)
}

export default function MarketplaceProductCard({ product, compact = false }) {
  const { addToCart, toggleWishlist, isWishlisted } = useShop()
  return (
    <article className={`catalog-product-card ${compact ? 'compact' : ''}`}>
      <div className="catalog-product-image">
        {product.badge && <span className="badge">{product.badge}</span>}
        <button type="button" aria-label={isWishlisted(product.id) ? `Remove ${product.name} from favourites` : `Add ${product.name} to favourites`} aria-pressed={isWishlisted(product.id)} className={`wish-btn ${isWishlisted(product.id) ? 'active' : ''}`} onClick={() => toggleWishlist(product.id)}>{isWishlisted(product.id) ? '♥' : '♡'}</button>
        <Link to={`/products/${product.id}`}><img src={product.image} alt={product.name} /></Link>
      </div>
      <div className="catalog-product-content">
        <h3><Link to={`/products/${product.id}`}>{product.name}</Link></h3>
        {!compact && <p className="catalog-artisan">{product.artisan}</p>}
        <strong>Rs. {product.price.toLocaleString()}</strong>
        <div className="catalog-rating"><span>{stars(product.rating)}</span> <small>({product.reviews})</small></div>
        <button type="button" onClick={() => addToCart(product)}>Add to Cart</button>
      </div>
    </article>
  )
}
