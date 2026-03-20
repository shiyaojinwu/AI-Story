import { motion } from 'framer-motion'
import { ArrowLeft, Download, Volume2 } from 'lucide-react'
import type { Shot } from '../storyData'

interface Props { title: string; shots: Shot[]; onBack: () => void }

export default function PreviewPage({ title, shots, onBack }: Props) {
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
            <h1 className="display-font" style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--c-text)' }}>{title}</h1>
            <p style={{ fontSize: '0.78rem', color: 'var(--c-text-3)', marginTop: 2 }}>视频预览</p>
          </div>
        </div>
        <button className="btn-primary" style={{ padding: '11px 24px', fontSize: '0.85rem' }}>
          <Download size={15} />
          导出视频
        </button>
      </header>

      <main style={{ flex: 1, display: 'flex', gap: 28, padding: '32px 40px' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          <div className="card" style={{
            overflow: 'hidden', aspectRatio: '16/9', position: 'relative',
            background: '#000', display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <video
              src="/shots/demo-video.mp4"
              controls
              style={{ width: '100%', height: '100%', objectFit: 'contain' }}
              poster={shots[0]?.imageUrl}
              onError={e => { (e.target as HTMLVideoElement).style.display = 'none' }}
            />
            <div style={{
              position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column',
              alignItems: 'center', justifyContent: 'center', pointerEvents: 'none',
            }}>
            </div>
          </div>

          <div className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 20px', marginTop: 16 }}>
            <div style={{ display: 'flex', gap: 24, fontSize: '0.78rem', color: 'var(--c-text-3)', fontFamily: 'var(--font-mono)' }}>
              <span>{shots.length * 5}s</span>
              <span>1920×1080</span>
              <span>24fps</span>
              <span>MP4</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--c-text-2)' }}>
              <Volume2 size={13} />
              <span>含语音旁白</span>
            </div>
          </div>
        </div>

        <div style={{ width: 260, display: 'flex', flexDirection: 'column' }}>
          <span className="label-text" style={{ marginBottom: 14 }}>分镜序列</span>
          <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 10, paddingRight: 4 }}>
            {shots.map((shot, i) => (
              <motion.div
                key={shot.id}
                initial={{ opacity: 0, x: 10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.05 }}
                className="card"
                style={{ padding: 10, display: 'flex', gap: 12, borderRadius: 10 }}
              >
                <div style={{ width: 72, height: 44, borderRadius: 7, overflow: 'hidden', background: 'var(--c-surface)', flexShrink: 0 }}>
                  <img src={shot.imageUrl} alt={shot.title} style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
                    onError={e => { (e.target as HTMLImageElement).style.display = 'none' }} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--c-text)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {shot.sortOrder}. {shot.title}
                  </div>
                  <div style={{ fontSize: '0.7rem', color: 'var(--c-text-3)', marginTop: 3, fontFamily: 'var(--font-mono)' }}>
                    {shot.transition} · 5s
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </main>
    </motion.div>
  )
}