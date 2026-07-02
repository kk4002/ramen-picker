import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { getRamenList } from '../api'

export default function RamenListPage() {
  const { meta } = useApp()
  const [keyword, setKeyword] = useState('')
  const [cookType, setCookType] = useState('')
  const [ramenType, setRamenType] = useState('')
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const load = () => {
    setLoading(true)
    setError(null)
    getRamenList({ keyword, cookType, ramenType })
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const onSearch = (e) => {
    e.preventDefault()
    load()
  }

  return (
    <div className="list-page">
      <h1 className="page-title">라면 목록</h1>

      <form className="search-bar" onSubmit={onSearch}>
        <input
          type="text"
          placeholder="라면명·브랜드·별칭 검색 (예: 신컵)"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <select value={cookType} onChange={(e) => setCookType(e.target.value)}>
          <option value="">조리 방식 전체</option>
          {(meta?.cookTypes || []).filter((c) => c.value !== 'ANY').map((c) => (
            <option key={c.value} value={c.value}>{c.label}</option>
          ))}
        </select>
        <select value={ramenType} onChange={(e) => setRamenType(e.target.value)}>
          <option value="">타입 전체</option>
          {(meta?.ramenTypes || []).filter((r) => r.value !== 'ANY').map((r) => (
            <option key={r.value} value={r.value}>{r.label}</option>
          ))}
        </select>
        <button className="btn btn-primary" type="submit">검색</button>
      </form>

      {error && <p className="error-box">{error}</p>}
      {loading ? (
        <p className="loading">불러오는 중...</p>
      ) : (
        <div className="ramen-grid">
          {items.map((item) => (
            <Link key={item.id} to={`/ramen/${item.id}`} className="ramen-tile">
              <div className="tile-name">{item.name}</div>
              <div className="tile-brand">{item.brand}</div>
              <div className="tile-meta">
                <span className="badge">{item.cookTypeLabel}</span>
                <span className="badge">{item.ramenTypeLabel}</span>
              </div>
              {item.spicyLabel && <div className="tile-spicy">🌶 {item.spicyLabel}</div>}
            </Link>
          ))}
          {items.length === 0 && <p className="empty-state">검색 결과가 없습니다.</p>}
        </div>
      )}
    </div>
  )
}
