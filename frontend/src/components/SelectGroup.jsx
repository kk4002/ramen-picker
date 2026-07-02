// 선택 버튼 그룹 (단일/복수 공용)
export default function SelectGroup({ options, value, onChange, multi = false }) {
  const isSelected = (v) => (multi ? (value || []).includes(v) : value === v)

  const toggle = (v) => {
    if (multi) {
      const cur = value || []
      onChange(cur.includes(v) ? cur.filter((x) => x !== v) : [...cur, v])
    } else {
      onChange(value === v ? null : v)
    }
  }

  return (
    <div className="select-group">
      {options.map((opt) => (
        <button
          key={opt.value}
          type="button"
          className={`chip ${isSelected(opt.value) ? 'chip-active' : ''}`}
          onClick={() => toggle(opt.value)}
        >
          {opt.label}
        </button>
      ))}
    </div>
  )
}
