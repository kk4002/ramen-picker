import { useEffect, useState } from 'react'
import { useApp } from '../context/AppContext'
import { getRamenList, createRamen, updateRamen, deleteRamen } from '../api'
import AdminRamenForm from '../components/AdminRamenForm'

export default function AdminPage() {
  const { meta } = useApp()
  const [items, setItems] = useState([])
  const [editing, setEditing] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState(null)
  const [error, setError] = useState(null)

  const load = () => {
    getRamenList().then(setItems).catch((e) => setError(e.message))
  }

  useEffect(() => {
    load()
  }, [])

  const handleSubmit = async (payload) => {
    setSubmitting(true)
    setError(null)
    setMessage(null)
    try {
      if (editing) {
        await updateRamen(editing.id, payload)
        setMessage(`"${payload.name}" 수정 완료`)
      } else {
        await createRamen(payload)
        setMessage(`"${payload.name}" 등록 완료`)
      }
      setEditing(null)
      load()
    } catch (e) {
      setError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  const handleDelete = async (item) => {
    if (!window.confirm(`"${item.name}"을(를) 삭제할까요?`)) return
    setError(null)
    try {
      await deleteRamen(item.id)
      setMessage(`"${item.name}" 삭제 완료`)
      if (editing && editing.id === item.id) setEditing(null)
      load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div className="admin-page">
      <h1 className="page-title">관리자 · 라면 관리</h1>
      {message && <p className="notice-box">{message}</p>}
      {error && <p className="error-box">{error}</p>}

      <div className="admin-layout">
        <div className="admin-list">
          <h2>등록된 라면 ({items.length})</h2>
          <ul className="admin-item-list">
            {items.map((item) => (
              <li key={item.id} className="admin-item">
                <div>
                  <strong>{item.name}</strong>
                  <span className="admin-item-sub"> · {item.brand} · {item.cookTypeLabel}/{item.ramenTypeLabel}</span>
                </div>
                <div className="admin-item-actions">
                  <button className="btn btn-sm btn-ghost" onClick={() => setEditing(item)}>수정</button>
                  <button className="btn btn-sm btn-danger" onClick={() => handleDelete(item)}>삭제</button>
                </div>
              </li>
            ))}
          </ul>
        </div>

        <div className="admin-form-wrap">
          <AdminRamenForm
            meta={meta}
            editing={editing}
            onSubmit={handleSubmit}
            onCancel={() => setEditing(null)}
            submitting={submitting}
          />
        </div>
      </div>
    </div>
  )
}
