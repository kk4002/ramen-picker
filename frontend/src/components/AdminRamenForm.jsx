import { useState, useEffect } from 'react'

const EMPTY = {
  name: '',
  brand: '',
  ramenType: 'SOUP',
  cookType: 'CUP',
  spicyLevel: 5,
  spicyLabel: '',
  flavorTags: '',
  situationTags: '',
  purchaseTags: '',
  cookTimeMinutes: 4,
  description: '',
  imageUrl: '',
}

// 관리자 라면 등록/수정 폼
export default function AdminRamenForm({ meta, editing, onSubmit, onCancel, submitting }) {
  const [form, setForm] = useState(EMPTY)

  useEffect(() => {
    if (editing) {
      setForm({
        name: editing.name || '',
        brand: editing.brand || '',
        ramenType: editing.ramenType || 'SOUP',
        cookType: editing.cookType || 'CUP',
        spicyLevel: editing.spicyLevel ?? 5,
        spicyLabel: editing.spicyLabel || '',
        flavorTags: (editing.flavorTags || []).join(', '),
        situationTags: (editing.situationTags || []).join(', '),
        purchaseTags: (editing.purchaseTags || []).join(', '),
        cookTimeMinutes: editing.cookTimeMinutes ?? 4,
        description: editing.description || '',
        imageUrl: editing.imageUrl || '',
      })
    } else {
      setForm(EMPTY)
    }
  }, [editing])

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const toList = (s) =>
    s.split(',').map((x) => x.trim()).filter((x) => x.length > 0)

  const submit = (e) => {
    e.preventDefault()
    onSubmit({
      name: form.name,
      brand: form.brand,
      ramenType: form.ramenType,
      cookType: form.cookType,
      spicyLevel: Number(form.spicyLevel),
      spicyLabel: form.spicyLabel,
      flavorTags: toList(form.flavorTags),
      situationTags: toList(form.situationTags),
      purchaseTags: toList(form.purchaseTags),
      cookTimeMinutes: Number(form.cookTimeMinutes),
      description: form.description,
      imageUrl: form.imageUrl || null,
    })
  }

  const cookTypes = (meta?.cookTypes || []).filter((c) => c.value !== 'ANY')
  const ramenTypes = (meta?.ramenTypes || []).filter((r) => r.value !== 'ANY')

  return (
    <form className="admin-form" onSubmit={submit}>
      <h2>{editing ? `수정: ${editing.name}` : '새 라면 등록'}</h2>

      <label>라면명 *
        <input value={form.name} onChange={set('name')} required />
      </label>
      <label>브랜드
        <input value={form.brand} onChange={set('brand')} />
      </label>

      <div className="form-row">
        <label>라면 타입 *
          <select value={form.ramenType} onChange={set('ramenType')}>
            {ramenTypes.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
          </select>
        </label>
        <label>조리 방식 *
          <select value={form.cookType} onChange={set('cookType')}>
            {cookTypes.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
          </select>
        </label>
      </div>

      <div className="form-row">
        <label>매운 정도 (1~10)
          <input type="number" min="1" max="10" value={form.spicyLevel} onChange={set('spicyLevel')} />
        </label>
        <label>매운맛 라벨
          <input value={form.spicyLabel} onChange={set('spicyLabel')} placeholder="예: 신라면급" />
        </label>
        <label>조리 시간(분)
          <input type="number" min="0" value={form.cookTimeMinutes} onChange={set('cookTimeMinutes')} />
        </label>
      </div>

      <label>맛 태그 (쉼표 구분)
        <input value={form.flavorTags} onChange={set('flavorTags')} placeholder="얼큰, 소고기맛, 짭짤" />
      </label>
      <label>상황 태그 (쉼표 구분)
        <input value={form.situationTags} onChange={set('situationTags')} placeholder="해장, 야식" />
      </label>
      <label>구매처 태그 (쉼표 구분)
        <input value={form.purchaseTags} onChange={set('purchaseTags')} placeholder="편의점, 마트" />
      </label>
      <label>설명
        <textarea value={form.description} onChange={set('description')} rows={3} />
      </label>
      <label>이미지 URL
        <input value={form.imageUrl} onChange={set('imageUrl')} />
      </label>

      <div className="form-actions">
        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? '저장 중...' : editing ? '수정 저장' : '등록'}
        </button>
        {editing && (
          <button className="btn btn-ghost" type="button" onClick={onCancel}>
            취소
          </button>
        )}
      </div>
    </form>
  )
}
