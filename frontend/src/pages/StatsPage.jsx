import { useEffect, useState } from 'react'
import { getStats } from '../api'

// 프론트 경로 → 사람이 읽을 메뉴명
const PAGE_LABELS = {
  '/': '메인',
  '/recommend': '추천 조건 선택',
  '/recommend/result': '추천 결과',
  '/ramen': '라면 목록',
  '/stats': '지표',
  '/admin': '관리자',
}

function labelPath(kind, path) {
  if (kind === 'PAGE') {
    if (PAGE_LABELS[path]) return PAGE_LABELS[path]
    if (path.startsWith('/ramen/')) return '라면 상세'
    return path
  }
  return path
}

// 일자별 그룹 막대 (API/PAGE)
function DailyChart({ byDay }) {
  const max = Math.max(1, ...byDay.map((d) => Math.max(d.apiCount, d.pageCount)))
  return (
    <div className="chart-wrap">
      <div className="chart-legend">
        <span><i className="dot dot-api" /> API 호출</span>
        <span><i className="dot dot-page" /> 페이지 방문</span>
      </div>
      <div className="daily-chart" role="img" aria-label="일자별 트래픽 추이">
        {byDay.map((d) => (
          <div className="day-col" key={d.day} title={`${d.day} · API ${d.apiCount} / 방문 ${d.pageCount}`}>
            <div className="bars">
              <div className="bar bar-api" style={{ height: `${(d.apiCount / max) * 100}%` }} />
              <div className="bar bar-page" style={{ height: `${(d.pageCount / max) * 100}%` }} />
            </div>
            <span className="day-label">{d.day.slice(5)}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

// 경로별 순위 바
function PathBars({ items }) {
  if (!items.length) return <p className="empty-state">아직 집계된 데이터가 없습니다.</p>
  const max = Math.max(1, ...items.map((i) => i.count))
  return (
    <ul className="path-bars">
      {items.map((it, idx) => (
        <li key={`${it.kind}-${it.path}-${idx}`} className="path-row">
          <span className={`kind-chip ${it.kind === 'API' ? 'chip-api' : 'chip-page'}`}>{it.kind}</span>
          <span className="path-name" title={it.path}>{labelPath(it.kind, it.path)}</span>
          <span className="path-track">
            <span
              className={`path-fill ${it.kind === 'API' ? 'fill-api' : 'fill-page'}`}
              style={{ width: `${(it.count / max) * 100}%` }}
            />
          </span>
          <span className="path-count">{it.count.toLocaleString()}</span>
        </li>
      ))}
    </ul>
  )
}

export default function StatsPage() {
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    getStats(14)
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  if (loading) return <p className="loading">지표를 불러오는 중...</p>
  if (error) return <p className="error-box">{error}</p>
  if (!data) return null

  const total = data.totalApiCalls + data.totalPageViews

  return (
    <div className="stats-page">
      <div className="stats-head">
        <div>
          <h1 className="page-title">서비스 지표</h1>
          <p className="section-sub">서비스: {data.serviceName} · 최근 14일 추이</p>
        </div>
        <button className="btn btn-ghost btn-sm" onClick={load}>새로고침</button>
      </div>

      <div className="kpi-row">
        <div className="kpi-tile">
          <span className="kpi-label">총 트래픽</span>
          <span className="kpi-value">{total.toLocaleString()}</span>
        </div>
        <div className="kpi-tile">
          <span className="kpi-label">API 호출수</span>
          <span className="kpi-value kpi-api">{data.totalApiCalls.toLocaleString()}</span>
        </div>
        <div className="kpi-tile">
          <span className="kpi-label">페이지 방문수</span>
          <span className="kpi-value kpi-page">{data.totalPageViews.toLocaleString()}</span>
        </div>
      </div>

      <section className="stats-section">
        <h2>일자별 추이</h2>
        <DailyChart byDay={data.byDay} />
      </section>

      <section className="stats-section">
        <h2>경로별 집계 (상위 {data.byPath.length}건)</h2>
        <PathBars items={data.byPath} />
      </section>
    </div>
  )
}
