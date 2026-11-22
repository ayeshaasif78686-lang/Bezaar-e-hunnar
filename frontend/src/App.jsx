import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import GuestRoute from './components/GuestRoute.jsx'
import WishlistPage from './pages/WishlistPage.jsx'
import ShopNotice from './components/ShopNotice.jsx'
import HomePage from './pages/HomePage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import ProductDetailPage from './pages/ProductDetailPage.jsx'
import ProductsPage from './pages/ProductsPage.jsx'
import ArtisanProfilePage from './pages/ArtisanProfilePage.jsx'
import CartPage from './pages/CartPage.jsx'
import CheckoutPage from './pages/CheckoutPage.jsx'
import PaymentPage from './pages/PaymentPage.jsx'
import OrderConfirmedPage from './pages/OrderConfirmedPage.jsx'
import CustomOrdersPage from './pages/CustomOrdersPage.jsx'
import ContactPage from './pages/ContactPage.jsx'
import OurStoryPage from './pages/OurStoryPage.jsx'

export default function App() {
  return (
    <div className="app-shell">
      <Navbar />
      <ShopNotice />
      <Routes>
        {/* Public: only Login & Register are reachable without an account.
            Signed-in users visiting these are redirected to Home. */}
        <Route element={<GuestRoute />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Everything else requires login. Guests are sent to /login first. */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/heritage" element={<OurStoryPage />} />
          <Route path="/our-story" element={<OurStoryPage />} />
          <Route path="/custom-orders" element={<CustomOrdersPage />} />
          <Route path="/contact" element={<ContactPage />} />
          <Route path="/orders" element={<ProfilePage />} />
          <Route path="/wishlist" element={<WishlistPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:productId" element={<ProductDetailPage />} />
          <Route path="/artisans/:artisanId" element={<ArtisanProfilePage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/order-confirmed" element={<OrderConfirmedPage />} />
          <Route path="/dashboard" element={<ProfilePage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  )
}
