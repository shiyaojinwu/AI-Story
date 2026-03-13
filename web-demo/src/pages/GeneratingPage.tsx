import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'

interface Stage { text: string; icon: string; duration: number }
interface Props { stages: Stage[]; onDone: () => void }

export default function GeneratingPage({ stages, onDone }: Props) {
  const [idx, setIdx] = useState(0)
  const [progress, setProgress] = useState(0)

  useEffect(() => {
    if (idx >= stages.length) { const t = setTimeout(onDone, 400); return () => clearTimeout(t) }
    const s = stages[idx]
    const perStage = 100 / stages.length
    const start = idx * perStage, end = (idx + 1) * perStage
    let step = 0
    const steps = 24, dt = s.duration / steps
    const iv = setInterval(() => {
      step++
      setProgress(start + (end - start) * (step / steps))
      if (step >= steps) { clearInterval(iv); setIdx(p => p + 1) }
    }, dt)
    return () => clearInterval(iv)
  }, [idx, stages, onDone])

  const current = stages[Math.min(idx, stages.length - 1)]

  return (
    <motion.div
      className="page-shell"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      style={{ alignItems: 'center', justifyContent: 'center' }}
    >
      {/* ambient */}
      <div style={{ position: 'fixed', inset: 0, overflow: 'hidden', pointerEvents: 'none' }}>
        <div className="orb" style={{ width: 500, height: 500, top: '20%', left: '30%', background: 'radial-gradient(circle, rgba(139,92,246,0.12) 0%, transparent 70%)' }} />
      </div>

      <div style={{ position: 'relative', zIndex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 40, maxWidth: 420, width: '100%', padding: '0 24px' }}>
        {/* orb animation */}
        <div style={{ position: 'relative', width: 120, height: 120 }}>
          <motion.div
            animate={{ scale: [1, 1.25, 1], rotate: [0, 180, 360] }}
            transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
            style={{
              position: 'absolute', inset: 0, borderRadius: '50%',
              background: 'radial-gradient(circle, rgba(139,92,246,0.25) 0%, transparent 70%)',
              filter: 'blur(30px)',
            }}
          />
          <motion.div
            animate={{ scale: [1.1, 0.85, 1.1], rotate: [360, 180, 0] }}
            transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
            style={{
              position: 'absolute', top: 16, left: 16, width: 88, height: 88, borderRadius: '50%',
              background: 'radial-gradient(circle, rgba(109,40,217,0.35) 0%, transparent 70%)',
              filter: 'blur(20px)',
            }}
          />
          <div style={{
            position: 'absolute', top: 28, left: 28, width: 64, height: 64, borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--c-violet-dim), var(--c-violet))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 26,
            boxShadow: '0 0 30px var(--c-violet-glow)',
          }}>
            {current?.icon}
          </div>
        </div>

        {/* text */}
        <div style={{ textAlign: 'center' }}>
          <motion.p
            key={idx}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            style={{ fontSize: '1.2rem', fontWeight: 500, color: 'var(--c-text)', marginBottom: 8 }}
          >
            {current?.text}
          </motion.p>
          <p style={{ fontSize: '0.85rem', color: 'var(--c-text-3)', fontFamily: 'var(--font-mono)' }}>
            {Math.round(progress)}%
          </p>
        </div>

        {/* progress bar */}
        <div style={{ width: '100%', height: 4, background: 'var(--c-border)', borderRadius: 4, overflow: 'hidden' }}>
          <motion.div
            style={{ height: '100%', borderRadius: 4, background: 'linear-gradient(90deg, var(--c-violet-dim), var(--c-violet))' }}
            animate={{ width: `${progress}%` }}
            transition={{ duration: 0.15 }}
          />
        </div>

        {/* dots */}
        <div style={{ display: 'flex', gap: 10 }}>
          {stages.map((_, i) => (
            <div
              key={i}
              className={i === idx ? 'pulse-glow' : ''}
              style={{
                width: 8, height: 8, borderRadius: '50%',
                background: i < idx ? 'var(--c-violet)' : i === idx ? 'var(--c-amber)' : 'var(--c-border)',
                transition: 'all 0.3s',
                transform: i === idx ? 'scale(1.3)' : 'scale(1)',
              }}
            />
          ))}
        </div>
      </div>
    </motion.div>
  )
}