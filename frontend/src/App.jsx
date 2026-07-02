import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { AppProvider } from './context/AppContext'
import HomePage from './pages/HomePage'
import RecommendPage from './pages/RecommendPage'
import RecommendResultPage from './pages/RecommendResultPage'
import RamenListPage from './pages/RamenListPage'
import RamenDetailPage from './pages/RamenDetailPage'
import AdminPage from './pages/AdminPage'

function Header() {
  const location = useLocation()
  const nav = [
    { to: '/', label: '홈' },
    { to: '/recommend', label: '추천받기' },
    { to: '/ramen', label: '라면 목록' },
    { to: '/admin', label: '관리자' },
  ]
  return (
    <header className="site-header">
      <Link to="/" className="brand">
        <span className="brand-icon">🍜</span>
        <span className="brand-name">라면픽</span>
      </Link>
      <nav className="site-nav">
        {nav.map((n) => (
          <Link
            key={n.to}
            to={n.to}
            className={location.pathname === n.to ? 'active' : ''}
          >
            {n.label}
          </Link>
        ))}
      </nav>
    </header>
  )
}

export default function App() {
  return (
    <AppProvider>
      <Header />
      <main className="container">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/recommend" element={<RecommendPage />} />
          <Route path="/recommend/result" element={<RecommendResultPage />} />
          <Route path="/ramen" element={<RamenListPage />} />
          <Route path="/ramen/:id" element={<RamenDetailPage />} />
          <Route path="/admin" element={<AdminPage />} />
        </Routes>
      </main>
      <footer className="site-footer">
        라면픽 · 조건 기반 라면 추천 서비스 · 초기 MVP
      </footer>
    </AppProvider>
  )
}
