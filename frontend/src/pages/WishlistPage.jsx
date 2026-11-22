import { Link } from 'react-router-dom'
import { products } from '../data/storeData.js'
import { useShop } from '../context/ShopContext.jsx'
import MarketplaceProductCard from '../components/MarketplaceProductCard.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
export default function WishlistPage() {
  const { wishlist } = useShop()
  const saved = products.filter((product) => wishlist.includes(product.id))
  return <main className="page-shell catalog-page wishlist-page"><div className="catalog-breadcrumb"><Link to="/">Home</Link> › Favourites</div><div className="cart-title-row"><h1>My Favourites ({saved.length})</h1><Link to="/products">Continue Shopping →</Link></div>{saved.length ? <div className="catalog-grid">{saved.map((product) => <MarketplaceProductCard key={product.id} product={product} />)}</div> : <div className="empty-cart"><h2>Your favourite handmade pieces belong here.</h2><p>Tap the heart on any product to save it.</p><Link to="/products">Shop Our Collection →</Link></div>}<SiteFooter /></main>
}
