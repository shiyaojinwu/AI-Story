import { useState } from 'react'
import { motion } from 'framer-motion'
import { Sparkles, ChevronRight, Film, Palette, Camera, Check } from 'lucide-react'
import { DEMO_STORY_INPUT } from '../mockData'

interface Props {
  onGenerate: (content: string, style: string) => void
}

const STYLES = [
  { id: 'movie', name: '电影风格', desc: '胶片质感 · 电影级构图与光影', Icon: Film, accent: '#d4a039' },
  { id: 'animation', name: '动画风格', desc: '精致手绘 · 日系动画质感', Icon: Palette, accent: '#e08a6d' },
  { id: 'realistic', name: '写实风格', desc: '超高清 · 真实影像效果', Icon: Camera, accent: '#8ab4a8' },
]

const stagger = { hidden: {}, show: { transition: { staggerChildren: 0.1 } } }
const fadeUp = { hidden: { opacity: 0, y: 24 }, show: { opacity: 1, y: 0, transition: { duration: 0.55, ease: [0.25, 0.46, 0.45, 0.94] } } }

export default function CreatePage({ onGenerate }: Props) {
  const [content, setContent] = useState('')
  const [style, setStyle] = useState('movie')

  return (
    <motion.div
      className="page-shell"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0, y: -16 }}
      transition={{ duration: 0.45 }}
    >
      {/* ambient orbs — warm tones */}
      <div style={{ position: 'fixed', inset: 0, overflow: 'hidden', pointerEvents: 'none', zIndex: 0 }}>
        <div className="orb" style={{ width: 500, height: 500, top: '-12%', right: '-8%', background: 'radial-gradient(circle, rgba(212,160,57,0.07) 0%, transparent 70%)' }} />
        <div className="orb" style={{ width: 600, height: 600, bottom: '-18%', left: '-10%', background: 'radial-gradient(circle, rgba(194,136,77,0.05) 0%, transparent 70%)', animationDelay: '-5s' }} />
        <div className="orb" style={{ width: 300, height: 300, top: '40%', left: '60%', background: 'radial-gradient(circle, rgba(246,193,92,0.04) 0%, transparent 70%)', animationDelay: '-9s' }} />
      </div>

      {/* header */}
      <header style={{
        position: 'relative', zIndex: 1,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '20px 40px',
        borderBottom: '1px solid var(--c-border)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 38, height: 38, borderRadius: 10,
            background: 'linear-gradient(135deg, var(--c-gold-dim), var(--c-amber))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: '#0a0908', fontWeight: 700, fontSize: 15,
            fontFamily: 'var(--font-display)',
            boxShadow: '0 0 20px var(--c-gold-glow)',
          }}>镜</div>
          <span style={{ fontSize: '1.1rem', fontWeight: 600, color: 'var(--c-text)', letterSpacing: 1 }}>
            镜语 AI
          </span>
        </div>
        <span style={{ fontSize: '0.78rem', color: 'var(--c-text-3)', letterSpacing: 2, fontWeight: 400 }}>
          MULTI-MODAL AI VIDEO CREATION
        </span>
      </header>

      {/* main */}
      <motion.main
        variants={stagger}
        initial="hidden"
        animate="show"
        style={{
          position: 'relative', zIndex: 1,
          flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
          justifyContent: 'center',
          width: '100%', maxWidth: 880,
          marginLeft: 'auto', marginRight: 'auto',
          paddingLeft: 32, paddingRight: 32,
          paddingTop: 56, paddingBottom: 48,
        }}
      >
        {/* hero */}
        <motion.div variants={fadeUp} style={{ textAlign: 'center', marginBottom: 52 }}>
          <h1
            className="gradient-title display-font"
            style={{ fontSize: 'clamp(2.2rem, 4vw, 3.2rem)', fontWeight: 700, lineHeight: 1.3, marginBottom: 18 }}
          >
            一段故事，逐镜打磨，一部影片
          </h1>
          <p style={{ fontSize: '1.05rem', color: 'var(--c-text-2)', lineHeight: 1.8, maxWidth: 520, margin: '0 auto' }}>
            输入你的故事，AI 将自动拆解分镜、生成画面、合成配音
            <br />
            让你像导演一样掌控每一个镜头
          </p>
        </motion.div>

        {/* story input */}
        <motion.div variants={fadeUp} style={{ width: '100%', marginBottom: 36 }}>
          <label className="label-text">你的故事</label>
          <textarea
            className="input-field"
            value={content}
            onChange={e => setContent(e.target.value)}
            placeholder={DEMO_STORY_INPUT}
            rows={6}
          />
        </motion.div>

        {/* style picker */}
        <motion.div variants={fadeUp} style={{ width: '100%', marginBottom: 48 }}>
          <label className="label-text">视觉风格</label>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            {STYLES.map(s => {
              const active = style === s.id
              return (
                <button
                  key={s.id}
                  onClick={() => setStyle(s.id)}
                  className="card"
                  style={{
                    padding: '24px 20px',
                    textAlign: 'left',
                    cursor: 'pointer',
                    borderColor: active ? s.accent + '55' : undefined,
                    boxShadow: active ? `0 0 30px ${s.accent}12, inset 0 0 40px ${s.accent}06` : undefined,
                    position: 'relative',
                  }}
                >
                  <div style={{
                    width: 44, height: 44, borderRadius: 11,
                    background: active
                      ? `linear-gradient(135deg, ${s.accent}28, ${s.accent}0c)`
                      : 'var(--c-card-up)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    marginBottom: 16, transition: 'background 0.3s',
                  }}>
                    <s.Icon size={20} style={{ color: active ? s.accent : 'var(--c-text-2)' }} />
                  </div>
                  <div style={{ fontWeight: 600, fontSize: '0.95rem', color: 'var(--c-text)', marginBottom: 4 }}>
                    {s.name}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--c-text-3)', lineHeight: 1.5 }}>
                    {s.desc}
                  </div>
                  {active && (
                    <div style={{
                      position: 'absolute', top: 14, right: 14,
                      width: 22, height: 22, borderRadius: '50%',
                      background: s.accent, display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}>
                      <Check size={13} color="#0a0908" strokeWidth={3} />
                    </div>
                  )}
                </button>
              )
            })}
          </div>
        </motion.div>

        {/* submit */}
        <motion.div variants={fadeUp}>
          <button
            className="btn-primary"
            onClick={() => onGenerate(content.trim() || DEMO_STORY_INPUT, style)}
          >
            <Sparkles size={19} />
            开始创作
            <ChevronRight size={17} />
          </button>
        </motion.div>
      </motion.main>

      {/* footer */}
      <footer style={{
        position: 'relative', zIndex: 1,
        padding: '16px 40px',
        borderTop: '1px solid var(--c-border)',
        textAlign: 'center',
        fontSize: '0.72rem', color: 'var(--c-text-3)', letterSpacing: 2,
      }}>
        POWERED BY MULTI-MODAL AI ORCHESTRATION ENGINE
      </footer>
    </motion.div>
  )
}