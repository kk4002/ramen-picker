import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { quickRecommend } from '../api'
import QuickRecommendCard from '../components/QuickRecommendCard'

export default function HomePage() {
  const { meta, metaError, setLastResult } = useApp()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleQuick = async (type) => {
    setLoading(true)
    setError(null)
    try {
      const result = await quickRecommend(type)
      setLastResult(result)
      navigate('/recommend/result')
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  const presets = meta?.quickRecommends || []

  return (
    <div className="home">
      <section className="hero">
        <h1>라면픽</h1>
        <p className="hero-sub">
          컵라면, 봉지라면, 뽀글이까지<br />
          지금 상황에 맞는 라면을 골라드립니다.
        </p>
        <div className="hero-cta">
          <button className="btn btn-primary btn-lg" onClick={() => navigate('/recommend')}>
            라면 추천받기
          </button>
          <button className="btn btn-ghost btn-lg" onClick={() => navigate('/ramen')}>
            라면 목록 보기
          </button>
        </div>
      </section>

      <section className="quick-section">
        <h2>빠른 추천</h2>
        <p className="section-sub">상황만 골라주세요. 라면픽이 바로 추천해드립니다.</p>
        {metaError && <p className="error-box">메타 정보를 불러오지 못했습니다: {metaError}</p>}
        {error && <p className="error-box">{error}</p>}
        <div className="quick-grid">
          {presets.map((p) => (
            <QuickRecommendCard key={p.type} preset={p} onClick={handleQuick} loading={loading} />
          ))}
        </div>
        {loading && <p className="loading">추천을 계산하는 중...</p>}
      </section>
    </div>
  )
}
