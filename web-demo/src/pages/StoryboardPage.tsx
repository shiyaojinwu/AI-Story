import { motion } from 'framer-motion'
import { ArrowLeft, Play, ChevronRight } from 'lucide-react'
import type { Shot } from '../mockData'

interface Props {
  title: string
  shots: Shot[]
  onShotClick: (shot: Shot) => void
  onGenerateVideo: () => void
  onBack: () => void
}

export default function StoryboardPage({ title, shots, onShotClick, onGenerateVideo, onBack }: Props) {
  return (
    <motion.div
      className="page-shell"
      initial={{ opacity: 0, x: 30 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -30 }}
      transition={{ duration: 0.35 }}
    >
      <header style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '18px 40px', borderBottom: '1px solid var(--c-border)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <button className="btn-ghost" onClick={onBack} style={{ padding: '8px 10px' }}>
            <ArrowLeft size={17} />
          </button>
          <div>
            <h1 className="display-font" style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--c-text)' }}>{title}</h1>
            <p style={{ fontSize: '0.78rem', color: 'var(--c-text-3)', marginTop: 2 }}>{shots.length} 个分镜 · 电影风格</p>
          </div>
        </div>
        <button className="btn-primary" onClick={onGenerateVideo} style={{ padding: '11px 28px', fontSize: '0.9rem' }}>
          <Play size={15} />
          生成视频
        </button>
      </header>

      {/* timeline label */}
      <div style={{ padding: '28px 40px 0', display: 'flex', alignItems: 'center', gap: 14 }}>
        <div style={{ width: 6, height: 6, borderRadius: '50%', background: 'var(--c-amber)' }} />
        <span style={{ fontSize: '0.9rem', fontWeight: 500, color: 'var(--c-text)' }}>分镜时间线</span>
        <div style={{ flex: 1, height: 1, background: 'var(--c-border)' }} />
      </div>

      {/* shots grid */}
      <main style={{ padding: '24px 40px 40px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 20 }}>
        {shots.map((shot, i) => (
          <motion.div
            key={shot.id}
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.07, duration: 0.4 }}
            className="card"
            onClick={() => onShotClick(shot)}
            style={{ cursor: 'pointer', overflow: 'hidden' }}
          >
            <div style={{ position: 'relative', aspectRatio: '16/9', background: 'var(--c-surface)', overflow: 'hidden' }}>
              <img
                src={shot.imageUrl}
                alt={shot.title}
                style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block', transition: 'transform 0.5s' }}
                onMouseOver={e => (e.currentTarget.style.transform = 'scale(1.05)')}
                onMouseOut={e => (e.currentTarget.style.transform = 'scale(1)')}
                onError={e => { (e.target as HTMLImageElement).style.display = 'none' }}
              />
              <div style={{
                position: 'absolute', top: 10, left: 10,
                background: 'rgba(10,9,8,0.7)', backdropFilter: 'blur(8px)',
                color: 'var(--c-amber)', fontSize: '0.72rem', fontWeight: 600,
                padding: '4px 10px', borderRadius: 7,
                fontFamily: 'var(--font-mono)',
              }}>
                #{shot.sortOrder}
              </div>
              <div style={{
                position: 'absolute', top: 10, right: 10,
                fontSize: '0.7rem', fontWeight: 600, padding: '4px 10px', borderRadius: 7,
                background: shot.status === 'completed' ? 'rgba(61,214,140,0.12)' : 'rgba(212,160,57,0.12)',
                color: shot.status === 'completed' ? 'var(--c-green)' : 'var(--c-amber)',
              }}>
                {shot.status === 'completed' ? '已完成' : '生成中'}
              </div>
              <div style={{
                position: 'absolute', bottom: 10, right: 10,
                background: 'rgba(10,9,8,0.6)', backdropFilter: 'blur(8px)',
                color: 'rgba(245,240,232,0.5)', fontSize: '0.68rem',
                padding: '3px 9px', borderRadius: 6,
              }}>
                {shot.transition}
              </div>
            </div>
            <div style={{ padding: '14px 16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 6 }}>
                <span style={{ fontWeight: 600, fontSize: '0.88rem', color: 'var(--c-text)' }}>{shot.title}</span>
                <ChevronRight size={14} style={{ color: 'var(--c-text-3)' }} />
              </div>
              <p className="line-clamp-2" style={{ fontSize: '0.78rem', color: 'var(--c-text-2)', lineHeight: 1.6 }}>
                {shot.narration}
              </p>
            </div>
          </motion.div>
        ))}
      </main>
    </motion.div>
  )
}