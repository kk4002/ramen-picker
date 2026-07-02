import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getRamenDetail, getSimilarRamen } from '../api'
import SimilarRamenList from '../components/SimilarRamenList'

export default function RamenDetailPage() {
  const { id } = useParams()
  const [item, setItem] = useState(null)
  const [similar, setSimilar] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    setError(null)
    setItem(null)
    getRamenDetail(id).then(setItem).catch((e) => setError(e.message))
    getSimilarRamen(id, 5).then(setSimilar).catch(() => {})
  }, [id])

  if (error) return <p className="error-box">{error}</p>
  if (!item) return <p className="loading">불러오는 중...</p>

  const isPpogeuli = item.cookType === 'PPOGEULI'

  return (
    <div className="detail-page">
      <Link to="/ramen" className="btn btn-link">← 목록으로</Link>

      <div className="detail-head">
        <div className="detail-image">
          {item.imageUrl ? (
            <img src={item.imageUrl} alt={item.name} />
          ) : (
            <div className="image-placeholder">🍜</div>
          )}
        </div>
        <div className="detail-title">
          <h1>{item.name}</h1>
          <p className="detail-brand">{item.brand}</p>
          <div className="detail-badges">
            <span className="badge">{item.cookTypeLabel}</span>
            <span className="badge">{item.ramenTypeLabel}</span>
            {item.spicyLabel && <span className="badge badge-spicy">🌶 {item.spicyLabel} (Lv.{item.spicyLevel})</span>}
            {item.cookTimeMinutes != null && <span className="badge">⏱ {item.cookTimeMinutes}분</span>}
          </div>
        </div>
      </div>

      {isPpogeuli && (
        <p className="warning-box">
          ⚠ 뽀글이는 제조사가 권장하는 공식 조리법이 아닐 수 있습니다. 제품 포장재와 조리 안내를 확인하고,
          가능한 경우 냄비나 전용 용기를 사용하는 것을 권장합니다.
        </p>
      )}

      {item.description && <p className="detail-desc">{item.description}</p>}

      <section className="detail-section">
        <h2>맛 태그</h2>
        <div className="rec-tags">
          {(item.flavorTags || []).map((t) => (
            <span key={t} className="tag tag-flavor">#{t}</span>
          ))}
        </div>
      </section>

      <section className="detail-section">
        <h2>어울리는 상황</h2>
        <div className="rec-tags">
          {(item.situationTags || []).map((t) => (
            <span key={t} className="tag tag-situation">#{t}</span>
          ))}
        </div>
      </section>

      <section className="detail-section">
        <h2>구매처</h2>
        <div className="rec-tags">
          {(item.purchaseTags || []).map((t) => (
            <span key={t} className="tag">🛒 {t}</span>
          ))}
        </div>
      </section>

      <section className="detail-section">
        <h2>비슷한 라면</h2>
        <SimilarRamenList data={similar} />
      </section>
    </div>
  )
}
