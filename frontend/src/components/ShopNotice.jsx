import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useShop } from '../context/ShopContext.jsx'
export default function ShopNotice() {
  const { notice, dismissNotice } = useShop()
  useEffect(() => {
    if (!notice) return
    const timer = setTimeout(dismissNotice, 6000)
    return () => clearTimeout(timer)
  }, [notice])
  if (!notice) return null
  return <div className="shop-notice" role="status"><span>{notice.text}</span><Link to={notice.to} onClick={dismissNotice}>{notice.label} →</Link><button onClick={dismissNotice} aria-label="Dismiss notification">×</button></div>
}
