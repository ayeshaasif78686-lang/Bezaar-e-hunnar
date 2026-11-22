import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Login / Register are only for visitors who are NOT signed in.
// Once a user is authenticated they are sent back to where they came from (or Home).
export default function GuestRoute({ children }) {
  const { user } = useAuth()
  const location = useLocation()

  if (user) {
    return <Navigate to={location.state?.from || '/'} replace />
  }

  return children ?? <Outlet />
}
