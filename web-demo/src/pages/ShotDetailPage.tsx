import { useState } from 'react'
import { motion } from 'framer-motion'
import { ArrowLeft, RefreshCw, Save } from 'lucide-react'
import type { Shot } from '../mockData'

interface Props {
  shot: Shot
  onBack: () => void
  onUpdate: (shot: Shot) => void
}

const TRANSITIONS = ['Ken Burns', 'Crossfade', 'Volume Mix']

export default function ShotDetailPage({ shot, onBack, onUpdate }: Props) {
  const [prompt, setPrompt] = useState(shot.prompt)
  const [narration, setNarration] = useState(shot.narration)
  const [transition, setTransition] = useState(shot.transition)
  const [regenerating, setRegenerating] = useState(false)

  const handleRegenerate = () => {
    setRegenerating(true)
    setTimeout(() => {
      setRegenerating(false)
      onUpdate({ ...shot, prompt, narration, transition })
    }, 2000)
  }

  const handleSave = () => {
    onUpdate({ ...shot, prompt, narration, transition })
    onBack()
  }

  return (
    <motion.div
      initial={{ opacity: 0, x: 30 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -30 }}
      transition={{ duration: 0.35 }}
      className="min-h-screen flex flex-col"
    >
      {/* Header */}
      <header className="px-8 py-5 flex items-center justify-between border-b border-border/50">
        <div className="flex items-center gap-4">
          <button
            onClick={onBack}
            className="w-9 h-9 rounded-lg border border-border flex items-center justify-center text-text-secondary hover:text-text-primary hover:border-accent transition-all cursor-pointer"
          >
            <ArrowLeft size={18} />
          </button>
          <div>
            <h1 className="text-xl font-semibold text-text-primary">
              镜头 {shot.sortOrder} · {shot.title}
            </h1>
            <p className="text-sm text-text-muted">分镜详情编辑</p>
          </div>
        </div>
        <div className="flex gap-3">
          <button
            onClick={handleRegenerate}
            disabled={regenerating}
            className="border border-border text-text-secondary hover:text-text-primary hover:border-accent px-4 py-2 rounded-lg flex items-center gap-2 text-sm transition-all cursor-pointer disabled:opacity-50"
          >
            <RefreshCw size={15} className={regenerating ? 'animate-spin' : ''} />
            {regenerating ? '生成中...' : '重新生成图片'}
          </button>
          <button
            onClick={handleSave}
            className="glow-btn text-white font-medium px-5 py-2 rounded-lg flex items-center gap-2 text-sm cursor-pointer"
          >
            <Save size={15} />
            保存
          </button>
        </div>
      </header>

      {/* Content */}
      <main className="flex-1 flex gap-8 px-8 py-8">
        {/* Left: Image Preview */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.1 }}
          className="flex-1"
        >
          <div className={`glass-card rounded-xl overflow-hidden aspect-video relative ${regenerating ? 'pulse-glow' : ''}`}>
            <img
              src={shot.imageUrl}
              alt={shot.title}
              className={`w-full h-full object-cover transition-all duration-500 ${regenerating ? 'opacity-30 blur-sm' : ''}`}
              onError={(e) => {
                const target = e.target as HTMLImageElement
                target.style.display = 'none'
                target.parentElement!.classList.add('flex', 'items-center', 'justify-center', 'bg-bg-input')
                const placeholder = document.createElement('div')
                placeholder.className = 'text-center p-8'
                placeholder.innerHTML = `<div class="text-5xl mb-3">🎨</div><div class="text-text-muted">待替换为AI生成图片</div>`
                target.parentElement!.appendChild(placeholder)
              }}
            />
            {regenerating && (
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center">
                  <RefreshCw size={32} className="animate-spin text-accent mx-auto mb-3" />
                  <p className="text-sm text-text-primary">正在重新生成...</p>
                </div>
              </div>
            )}
          </div>

          {/* Image info bar */}
          <div className="flex items-center justify-between mt-4 text-sm text-text-muted">
            <span>分辨率: 1920 x 1080</span>
            <span>模型: Stable Diffusion XL</span>
          </div>
        </motion.div>

        {/* Right: Edit Panel */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
          className="w-[400px] flex flex-col gap-6"
        >
          {/* Prompt */}
          <div>
            <label className="block text-sm text-text-secondary mb-2 font-medium">
              画面描述 (Prompt)
            </label>
            <textarea
              value={prompt}
              onChange={e => setPrompt(e.target.value)}
              rows={5}
              className="w-full bg-bg-input border border-border rounded-xl px-4 py-3 text-text-primary text-sm resize-none focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent-glow transition-all leading-relaxed"
            />
          </div>

          {/* Narration */}
          <div>
            <label className="block text-sm text-text-secondary mb-2 font-medium">
              旁白文本
            </label>
            <textarea
              value={narration}
              onChange={e => setNarration(e.target.value)}
              rows={3}
              className="w-full bg-bg-input border border-border rounded-xl px-4 py-3 text-text-primary text-sm resize-none focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent-glow transition-all leading-relaxed"
            />
          </div>

          {/* Transition */}
          <div>
            <label className="block text-sm text-text-secondary mb-2 font-medium">
              转场效果
            </label>
            <div className="flex gap-3">
              {TRANSITIONS.map(t => (
                <button
                  key={t}
                  onClick={() => setTransition(t)}
                  className={`flex-1 py-2.5 rounded-lg text-sm font-medium border transition-all cursor-pointer ${
                    transition === t
                      ? 'border-accent bg-accent/10 text-accent-light'
                      : 'border-border text-text-secondary hover:border-accent/50'
                  }`}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>

          {/* Status */}
          <div className="glass-card rounded-xl p-4 mt-auto">
            <div className="flex items-center justify-between text-sm">
              <span className="text-text-muted">状态</span>
              <span className="text-success font-medium flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-success inline-block" />
                已完成
              </span>
            </div>
            <div className="flex items-center justify-between text-sm mt-2">
              <span className="text-text-muted">生成模型</span>
              <span className="text-text-secondary">SDXL + LoRA</span>
            </div>
            <div className="flex items-center justify-between text-sm mt-2">
              <span className="text-text-muted">生成耗时</span>
              <span className="text-text-secondary">3.2s</span>
            </div>
          </div>
        </motion.div>
      </main>
    </motion.div>
  )
}