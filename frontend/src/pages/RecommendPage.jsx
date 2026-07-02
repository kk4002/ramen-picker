import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { recommend } from '../api'
import RecommendStepForm from '../components/RecommendStepForm'

export default function RecommendPage() {
  const { meta, metaError, setLastResult } = useApp()
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async (request) => {
    setSubmitting(true)
    setError(null)
    try {
      const result = await recommend(request)
      setLastResult(result)
      navigate('/recommend/result')
    } catch (e) {
      setError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (metaError) {
    return <p className="error-box">메타 정보를 불러오지 못했습니다: {metaError}</p>
  }
  if (!meta) {
    return <p className="loading">조건 정보를 불러오는 중...</p>
  }

  return (
    <div className="recommend-page">
      <h1 className="page-title">라면 추천받기</h1>
      <p className="section-sub">단계별로 조건을 골라주세요. 건너뛰어도 됩니다.</p>
      {error && <p className="error-box">{error}</p>}
      <RecommendStepForm meta={meta} onSubmit={handleSubmit} submitting={submitting} />
    </div>
  )
}
