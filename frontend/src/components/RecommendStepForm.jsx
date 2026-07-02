import { useState } from 'react'
import {
  CookTypeSelector,
  RamenTypeSelector,
  SituationSelector,
  FlavorTagSelector,
  SpicyPreferenceSelector,
  PurchaseSelector,
} from './selectors'

// 단계형 추천 조건 입력 폼
const STEPS = [
  { key: 'cook', title: '1단계 · 어떻게 먹을 건가요?', hint: '조리 방식을 골라주세요.' },
  { key: 'type', title: '2단계 · 어떤 타입이 먹고 싶나요?', hint: '라면 타입 (선택)' },
  { key: 'situation', title: '3단계 · 지금 상황은?', hint: '상황 (선택)' },
  { key: 'flavor', title: '4단계 · 어떤 맛이 좋나요?', hint: '맛 취향 (복수 선택 가능)' },
  { key: 'spicy', title: '5단계 · 매운 정도는?', hint: '매운 정도 (선택)' },
  { key: 'purchase', title: '어디서 살 건가요?', hint: '구매처 (선택)' },
]

export default function RecommendStepForm({ meta, onSubmit, submitting }) {
  const [step, setStep] = useState(0)
  const [form, setForm] = useState({
    cookType: null,
    ramenType: null,
    situation: null,
    flavorTags: [],
    spicyPreference: null,
    purchasePlace: null,
  })

  const update = (patch) => setForm((f) => ({ ...f, ...patch }))
  const isLast = step === STEPS.length - 1
  const current = STEPS[step]

  const submit = () => {
    // ANY/null 은 백엔드에서 조건 제외 처리됨
    onSubmit({
      cookType: form.cookType || 'ANY',
      ramenType: form.ramenType || 'ANY',
      situation: form.situation || null,
      flavorTags: form.flavorTags,
      spicyPreference: form.spicyPreference || 'ANY',
      purchasePlace: form.purchasePlace || null,
      limit: 5,
    })
  }

  return (
    <div className="step-form">
      <div className="step-progress">
        {STEPS.map((s, i) => (
          <div key={s.key} className={`step-dot ${i === step ? 'on' : ''} ${i < step ? 'done' : ''}`} />
        ))}
      </div>

      <div className="step-body">
        <h2>{current.title}</h2>
        <p className="section-sub">{current.hint}</p>

        {current.key === 'cook' && (
          <CookTypeSelector meta={meta} value={form.cookType} onChange={(v) => update({ cookType: v })} />
        )}
        {current.key === 'type' && (
          <RamenTypeSelector meta={meta} value={form.ramenType} onChange={(v) => update({ ramenType: v })} />
        )}
        {current.key === 'situation' && (
          <SituationSelector meta={meta} value={form.situation} onChange={(v) => update({ situation: v })} />
        )}
        {current.key === 'flavor' && (
          <FlavorTagSelector meta={meta} value={form.flavorTags} onChange={(v) => update({ flavorTags: v })} />
        )}
        {current.key === 'spicy' && (
          <SpicyPreferenceSelector meta={meta} value={form.spicyPreference} onChange={(v) => update({ spicyPreference: v })} />
        )}
        {current.key === 'purchase' && (
          <PurchaseSelector meta={meta} value={form.purchasePlace} onChange={(v) => update({ purchasePlace: v })} />
        )}
      </div>

      <div className="step-actions">
        <button className="btn btn-ghost" onClick={() => setStep((s) => Math.max(0, s - 1))} disabled={step === 0}>
          이전
        </button>
        {!isLast ? (
          <button className="btn btn-primary" onClick={() => setStep((s) => s + 1)}>
            다음
          </button>
        ) : (
          <button className="btn btn-primary" onClick={submit} disabled={submitting}>
            {submitting ? '추천 중...' : '추천 결과 보기'}
          </button>
        )}
      </div>

      <button className="btn btn-link" onClick={submit} disabled={submitting}>
        조건 이대로 바로 추천받기
      </button>
    </div>
  )
}
