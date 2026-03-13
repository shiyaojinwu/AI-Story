import { motion } from 'framer-motion'
import { ArrowLeft, Play, Image as ImageIcon, ChevronRight } from 'lucide-react'
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
            <h1 className="text-xl font-semibold text-text-primary">{title}</h1>
            <p className="text-sm text-text-muted">{shots.length} 个分镜 · 电影风格</p>
          </div>
        </div>
        <button
          onClick={onGenerateVideo}
          className="glow-btn text-white font-medium px-6 py-2.5 rounded-lg flex items-center gap-2 text-sm cursor-pointer"
        >
          <Play size={16} />
          生成视频
        </button>
      </header>

      {/* Storyboard Timeline */}
      <main className="flex-1 px-8 py-8">
        {/* Timeline header */}
        <div className="flex items-center gap-3 mb-6">
          <ImageIcon size={18} className="text-accent" />
          <h2 className="text-lg font-medium text-text-primary">分镜时间线</h2>
          <div className="flex-1 h-px bg-border/50" />
        </div>

        {/* Shot cards grid */}
        <div className="grid grid-cols-3 gap-5">
          {shots.map((shot, index) => (
            <motion.div
              key={shot.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.08 }}
              onClick={() => onShotClick(shot)}
              className="glass-card rounded-xl overflow-hidden cursor-pointer group"
            >
              {/* Image */}
              <div className="aspect-video bg-bg-input relative overflow-hidden">
                <img
                  src={shot.imageUrl}
                  alt={shot.title}
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  onError={(e) => {
                    // If image fails to load, show placeholder
                    const target = e.target as HTMLImageElement
                    target.style.display = 'none'
                    target.parentElement!.classList.add('flex', 'items-center', 'justify-center')
                    const placeholder = document.createElement('div')
                    placeholder.className = 'text-center p-4'
                    placeholder.innerHTML = `<div class="text-3xl mb-2">${['🌅', '🎨', '🏖️', '📜', '🏮', '⛵'][index]}</div><div class="text-xs text-text-muted">待替换图片</div>`
                    target.parentElement!.appendChild(placeholder)
                  }}
                />
                {/* Shot number badge */}
                <div className="absolute top-3 left-3 bg-black/60 backdrop-blur-sm text-white text-xs font-medium px-2.5 py-1 rounded-md">
                  镜头 {shot.sortOrder}
                </div>
                {/* Status badge */}
                <div className="absolute top-3 right-3">
                  <span className={`text-xs px-2 py-1 rounded-md font-medium ${
                    shot.status === 'completed'
                      ? 'bg-emerald-500/20 text-emerald-400'
                      : shot.status === 'generating'
                      ? 'bg-amber-500/20 text-amber-400'
                      : 'bg-gray-500/20 text-gray-400'
                  }`}>
                    {shot.status === 'completed' ? '已完成' : shot.status === 'generating' ? '生成中' : '待生成'}
                  </span>
                </div>
                {/* Transition badge */}
                <div className="absolute bottom-3 right-3 bg-black/60 backdrop-blur-sm text-white/70 text-xs px-2 py-1 rounded-md">
                  {shot.transition}
                </div>
              </div>

              {/* Info */}
              <div className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-medium text-text-primary text-sm">{shot.title}</h3>
                  <ChevronRight size={14} className="text-text-muted group-hover:text-accent transition-colors" />
                </div>
                <p className="text-xs text-text-secondary line-clamp-2 leading-relaxed">
                  {shot.narration}
                </p>
              </div>
            </motion.div>
          ))}
        </div>
      </main>
    </motion.div>
  )
}