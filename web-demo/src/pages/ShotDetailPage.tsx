import { useState } from 'react'
import { motion } from 'framer-motion'
import { ArrowLeft, RefreshCw, Save } from 'lucide-react'
import type { Shot } from '../mockData'
import { ALTERNATE_IMAGES } from '../mockData'

interface Props { shot: Shot; onBack: () => void; onUpdate: (shot: Shot) => void }

const TRANSITIONS = ['Ken Burns', 'Crossfade', 'Volume Mix']

export default function ShotDetailPage({ shot, onBack, onUpdate }: Props) {
  const [prompt, setPrompt] = useState(shot.prompt)
  const [narration, setNarration] = useState(shot.narration)
  const [transition, setTransition] = useState(shot.transition)
  const [regenerating, setRegenerating] = useState(false)
  const [currentImage, setCurrentImage] = useState(shot.imageUrl)
  const [hasRegenerated, setHasRegenerated] = useState(false)

  const handleRegenerate = () => {
    setRegenerating(true)
    setTimeout(() => {
      const alt = ALTERNATE_IMAGES[shot.id]
      if (alt && !hasRegenerated) {
        setCurrentImage(alt)
        setHasRegenerated(true)
        onUpdate({ ...shot, prompt, narration, transition, imageUrl: alt })
      } else {
        onUpdate({ ...shot, prompt, narration, transition })
      }
      setRegenerating(false)
    }, 2500)
  }

  return (
    <motion.div
      className="page-shell"
      initial={{ opacity: 0, x: 30 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -30 }}
      transition={{ duration: 0.35 }}
    >
      {/* header */}
      <header style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '18px 40px', borderBottom: '1px solid var(--c-border)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <button className="btn-ghost" onClick={onBack} style={{ padding: '8px 10px' }}>
            <ArrowLeft size={17} />
          </button>
          <div>
            <h1 className="display-font" style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--c-text)' }}>
              镜头 {shot.sortOrder} · {shot.title}
            </h1>
            <p style={{ fontSize: '0.78rem', color: 'var(--c-text-3)', marginTop: 2 }}>分镜详情编辑</p>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <button className="btn-ghost" onClick={handleRegenerate} disabled={regenerating}>
            <RefreshCw size={14} style={regenerating ? { animation: 'spin 1s linear infinite' } : {}} />
            {regenerating ? '生成中...' : '重新生成图片'}
          </button>
          <button className="btn-primary" onClick={() => { onUpdate({ ...shot, prompt, narration, transition }); onBack() }} style={{ padding: '9px 22px', fontSize: '0.85rem' }}>
            <Save size={14} />
            保存
          </button>
        </div>
      </header>

      {/* content */}
      <main style={{ flex: 1, display: 'flex', gap: 32, padding: '32px 40px' }}>
        {/* left: image */}
        <motion.div
          initial={{ opacity: 0, scale: 0.96 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.1 }}
          style={{ flex: 1 }}
        >
          <div
            className={`card ${regenerating ? 'pulse-glow' : ''}`}
            style={{ overflow: 'hidden', aspectRatio: '16/9', position: 'relative', background: 'var(--c-surface)' }}
          >
            <img
              src={currentImage}
              alt={shot.title}
              style={{
                width: '100%', height: '100%', objectFit: 'cover', display: 'block',
                transition: 'all 0.5s',
                opacity: regenerating ? 0.25 : 1,
                filter: regenerating ? 'blur(8px)' : 'none',
              }}
              onError={e => { (e.target as HTMLImageElement).style.display = 'none' }}
            />
            {regenerating && (
              <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <div style={{ textAlign: 'center' }}>
                  <RefreshCw size={28} style={{ color: 'var(--c-violet)', animation: 'spin 1s linear infinite', marginBottom: 10 }} />
                  <p style={{ fontSize: '0.85rem', color: 'var(--c-text)' }}>正在重新生成...</p>
                </div>
              </div>
            )}
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 14, fontSize: '0.75rem', color: 'var(--c-text-3)', fontFamily: 'var(--font-mono)' }}>
            <span>1920 × 1080</span>
            <span>Stable Diffusion XL</span>
          </div>
        </motion.div>

        {/* right: edit panel */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
          style={{ width: 380, display: 'flex', flexDirection: 'column', gap: 24 }}
        >
          <div>
            <label className="label-text">画面描述 Prompt</label>
            <textarea className="input-field" value={prompt} onChange={e => setPrompt(e.target.value)} rows={5} style={{ fontSize: '0.85rem', lineHeight: 1.7 }} />
          </div>
          <div>
            <label className="label-text">旁白文本</label>
            <textarea className="input-field" value={narration} onChange={e => setNarration(e.target.value)} rows={3} style={{ fontSize: '0.85rem', lineHeight: 1.7 }} />
          </div>
          <div>
            <label className="label-text">转场效果</label>
            <div style={{ display: 'flex', gap: 10 }}>
              {TRANSITIONS.map(t => (
                <button
                  key={t}
                  onClick={() => setTransition(t)}
                  style={{
                    flex: 1, padding: '11px 0', borderRadius: 10, fontSize: '0.82rem', fontWeight: 500,
                    border: `1px solid ${transition === t ? 'var(--c-violet)' : 'var(--c-border)'}`,
                    background: transition === t ? 'rgba(139,92,246,0.08)' : 'transparent',
                    color: transition === t ? 'var(--c-violet)' : 'var(--c-text-2)',
                    cursor: 'pointer', transition: 'all 0.25s', fontFamily: 'var(--font-body)',
                  }}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>

          {/* info card */}
          <div className="card" style={{ padding: 18, marginTop: 'auto' }}>
            {[
              ['状态', <span style={{ color: 'var(--c-green)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 6 }}><span style={{ width: 7, height: 7, borderRadius: '50%', background: 'var(--c-green)', display: 'inline-block' }} />已完成</span>],
              ['生成模型', 'SDXL + LoRA'],
              ['生成耗时', '3.2s'],
            ].map(([label, val], i) => (
              <div key={i} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.82rem', color: 'var(--c-text-2)', marginTop: i ? 10 : 0 }}>
                <span style={{ color: 'var(--c-text-3)' }}>{label}</span>
                <span>{val}</span>
              </div>
            ))}
          </div>
        </motion.div>
      </main>

      <style>{`@keyframes spin { to { transform: rotate(360deg) } }`}</style>
    </motion.div>
  )
}