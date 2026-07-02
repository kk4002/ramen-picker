import { Link } from 'react-router-dom'

// 비슷한 라면 목록
export default function SimilarRamenList({ data }) {
  if (!data || !data.similarItems || data.similarItems.length === 0) {
    return <p className="empty-state">비슷한 라면이 없습니다.</p>
  }
  return (
    <ul className="similar-list">
      {data.similarItems.map((s) => (
        <li key={s.id} className="similar-item">
          <Link to={`/ramen/${s.id}`} className="similar-link">
            <span className="similar-name">{s.name}</span>
            <span className="similar-score">유사도 {s.similarityScore}</span>
          </Link>
          <p className="similar-reason">{s.reason}</p>
        </li>
      ))}
    </ul>
  )
}
