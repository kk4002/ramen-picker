import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import RamenRecommendationCard from '../components/RamenRecommendationCard'

// 조건 요약 문자열
function summarizeConditions(c) {
  if (!c) return ''
  const parts = []
  if (c.cookTypeLabel) parts.push(c.cookTypeLabel)
  if (c.ramenTypeLabel) parts.push(c.ramenTypeLabel)
  if (c.situation) parts.push(c.situation)
  if (c.flavorTags && c.flavorTags.length) parts.push(c.flavorTags.join('/'))
  if (c.spicyPreferenceLabel) parts.push(c.spicyPreferenceLabel)
  if (c.purchasePlace) parts.push(c.purchasePlace)
  return parts.join(' / ')
}

export default function RecommendResultPage() {
  const { lastResult } = useApp()
  const navigate = useNavigate()
  const [copied, setCopied] = useState(false)

  if (!lastResult) {
    return (
      <div className="empty-state">
        <p>표시할 추천 결과가 없습니다.</p>
        <button className="btn btn-primary" onClick={() => navigate('/recommend')}>
          추천받으러 가기
        </button>
      </div>
    )
  }

  const { conditions, recommendations, notice } = lastResult
  const summary = summarizeConditions(conditions)

  const handleShare = async () => {
    const lines = ['오늘의 라면픽']
    if (summary) lines.push('', `조건: ${summary}`)
    lines.push('', '추천 결과:')
    recommendations.forEach((r) => lines.push(`${r.rank}위 ${r.name}`))
    const text = lines.join('\n')
    try {
      await navigator.clipboard.writeText(text)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch (e) {
      // 클립보드 미지원 환경 폴백
      window.prompt('아래 내용을 복사하세요', text)
    }
  }

  return (
    <div className="result-page">
      <h1 className="page-title">오늘의 라면픽</h1>
      {summary && (
        <p className="condition-summary">
          <strong>선택 조건:</strong> {summary}
        </p>
      )}

      {notice && <p className="notice-box">ℹ {notice}</p>}

      <div className="rec-list">
        {recommendations.map((item) => (
          <RamenRecommendationCard key={item.id} item={item} />
        ))}
      </div>

      <div className="result-actions">
        <button className="btn btn-primary" onClick={() => navigate('/recommend')}>
          다시 추천받기
        </button>
        <button className="btn btn-ghost" onClick={handleShare}>
          {copied ? '복사됨!' : '결과 공유하기'}
        </button>
      </div>
    </div>
  )
}
