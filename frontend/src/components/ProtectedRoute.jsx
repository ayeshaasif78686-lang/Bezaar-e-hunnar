import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Works both as a wrapper (<ProtectedRoute><Page /></ProtectedRoute>)
// and as a layout route (<Route element={<ProtectedRoute />}>...</Route>).
export default function ProtectedRoute({ children }) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname + location.search }} replace />
  }

  return children ?? <Outlet />
}
