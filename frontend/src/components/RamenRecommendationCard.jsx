import { Link } from 'react-router-dom'

// 추천 결과 카드
export default function RamenRecommendationCard({ item }) {
  return (
    <div className="rec-card">
      <div className="rec-rank">{item.rank}</div>
      <div className="rec-body">
        <div className="rec-head">
          <h3>
            {item.name}
            {item.brand && <span className="rec-brand"> · {item.brand}</span>}
          </h3>
          <span className="rec-score" title={`원점수 ${item.rawScore}`}>
            적합도 {item.matchScore}%
          </span>
        </div>

        <div className="rec-meta">
          <span className="badge">{item.cookTypeLabel}</span>
          <span className="badge">{item.ramenTypeLabel}</span>
          {item.spicyLabel && <span className="badge badge-spicy">🌶 {item.spicyLabel}</span>}
          {item.cookTimeMinutes != null && <span className="badge">⏱ {item.cookTimeMinutes}분</span>}
        </div>

        <p className="rec-reason">{item.reason}</p>

        <div className="rec-tags">
          {(item.flavorTags || []).map((t) => (
            <span key={`f-${t}`} className="tag tag-flavor">#{t}</span>
          ))}
          {(item.situationTags || []).map((t) => (
            <span key={`s-${t}`} className="tag tag-situation">#{t}</span>
          ))}
        </div>

        {item.warning && <p className="warning-box">⚠ {item.warning}</p>}

        <Link to={`/ramen/${item.id}`} className="btn btn-ghost btn-sm">
          상세 보기
        </Link>
      </div>
    </div>
  )
}
