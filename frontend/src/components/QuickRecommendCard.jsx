// 빠른 추천 카드. 클릭 시 프리셋 타입으로 추천 API를 호출한다.
const EMOJI = {
  hangover: '🥴',
  night_snack: '🌙',
  convenience_cup: '🏪',
  ppogeuli: '🎒',
  mild: '😌',
  spicy_soup: '🌶️',
  stir_fried: '🍳',
  heavy_meal: '💪',
}

export default function QuickRecommendCard({ preset, onClick, loading }) {
  return (
    <button
      className="quick-card"
      onClick={() => onClick(preset.type)}
      disabled={loading}
    >
      <span className="quick-emoji">{EMOJI[preset.type] || '🍜'}</span>
      <span className="quick-label">{preset.label}</span>
    </button>
  )
}
