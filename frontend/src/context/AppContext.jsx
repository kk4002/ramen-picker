import { createContext, useContext, useEffect, useState } from 'react'
import { getMeta } from '../api'

const AppContext = createContext(null)

export function AppProvider({ children }) {
  const [meta, setMeta] = useState(null)
  const [metaError, setMetaError] = useState(null)

  // 마지막 추천 결과/조건을 보관해 결과 페이지에서 사용
  const [lastResult, setLastResult] = useState(null)

  useEffect(() => {
    getMeta()
      .then(setMeta)
      .catch((e) => setMetaError(e.message))
  }, [])

  return (
    <AppContext.Provider value={{ meta, metaError, lastResult, setLastResult }}>
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const ctx = useContext(AppContext)
  if (!ctx) throw new Error('useApp must be used within AppProvider')
  return ctx
}
