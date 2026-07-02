// 라면픽 백엔드 API 클라이언트
// 개발 시 vite 프록시가 /api → http://localhost:8095 로 전달한다.

const BASE = '/api'

async function handle(res) {
  if (!res.ok) {
    let message = `요청 실패 (${res.status})`
    try {
      const body = await res.json()
      if (body && body.message) message = body.message
    } catch (e) {
      // ignore
    }
    throw new Error(message)
  }
  if (res.status === 204) return null
  return res.json()
}

export function getMeta() {
  return fetch(`${BASE}/meta`).then(handle)
}

export function getRamenList(params = {}) {
  const qs = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') qs.append(k, v)
  })
  const q = qs.toString()
  return fetch(`${BASE}/ramen${q ? `?${q}` : ''}`).then(handle)
}

export function getRamenDetail(id) {
  return fetch(`${BASE}/ramen/${id}`).then(handle)
}

export function getSimilarRamen(id, limit = 5) {
  return fetch(`${BASE}/ramen/${id}/similar?limit=${limit}`).then(handle)
}

export function recommend(request) {
  return fetch(`${BASE}/ramen/recommend`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(request),
  }).then(handle)
}

export function quickRecommend(type) {
  return fetch(`${BASE}/ramen/quick-recommend?type=${encodeURIComponent(type)}`).then(handle)
}

// 관리자
export function createRamen(request) {
  return fetch(`${BASE}/admin/ramen`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(request),
  }).then(handle)
}

export function updateRamen(id, request) {
  return fetch(`${BASE}/admin/ramen/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(request),
  }).then(handle)
}

export function deleteRamen(id) {
  return fetch(`${BASE}/admin/ramen/${id}`, { method: 'DELETE' }).then(handle)
}
