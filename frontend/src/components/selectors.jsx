// 추천 조건 셀렉터 모음.
// 기획서상 CookTypeSelector / RamenTypeSelector / SituationSelector /
// FlavorTagSelector / SpicyPreferenceSelector / PurchaseSelector 를 한 파일에 정의한다.
import SelectGroup from './SelectGroup'

const tagOptions = (tags) => (tags || []).map((t) => ({ value: t, label: t }))

export function CookTypeSelector({ meta, value, onChange }) {
  return <SelectGroup options={meta?.cookTypes || []} value={value} onChange={onChange} />
}

export function RamenTypeSelector({ meta, value, onChange }) {
  return <SelectGroup options={meta?.ramenTypes || []} value={value} onChange={onChange} />
}

export function SituationSelector({ meta, value, onChange }) {
  return <SelectGroup options={tagOptions(meta?.situationTags)} value={value} onChange={onChange} />
}

export function FlavorTagSelector({ meta, value, onChange }) {
  return (
    <SelectGroup options={tagOptions(meta?.flavorTags)} value={value} onChange={onChange} multi />
  )
}

export function SpicyPreferenceSelector({ meta, value, onChange }) {
  const options = (meta?.spicyPreferences || []).map((s) => ({ value: s.value, label: s.label }))
  return <SelectGroup options={options} value={value} onChange={onChange} />
}

export function PurchaseSelector({ meta, value, onChange }) {
  return <SelectGroup options={tagOptions(meta?.purchaseTags)} value={value} onChange={onChange} />
}
