import { motion } from 'framer-motion'
import { ArrowLeft, Download, Volume2 } from 'lucide-react'
import type { Shot } from '../mockData'

interface Props {
  title: string
  shots: Shot[]
  onBack: () => void
}

export default function PreviewPage({ title, shots, onBack }: Props) {
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
            <p className="text-sm text-text-muted">视频预览</p>
          </div>
        </div>
        <button className="glow-btn text-white font-medium px-5 py-2.5 rounded-lg flex items-center gap-2 text-sm cursor-pointer">
          <Download size={16} />
          导出视频
        </button>
      </header>

      <main className="flex-1 flex gap-8 px-8 py-8">
        {/* Video Player Area */}
        <div className="flex-1 flex flex-col">
          <div className="glass-card rounded-xl overflow-hidden aspect-video relative flex items-center justify-center bg-black">
            {/* Try to load a video, fallback to placeholder */}
            <video
              src="/mock/demo-video.mp4"
              controls
              className="w-full h-full object-contain"
              poster={shots[0]?.imageUrl}
              onError={(e) => {
                const target = e.target as HTMLVideoElement
                target.style.display = 'none'
              }}
            />
            {/* Placeholder overlay if no video */}
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <div className="text-6xl mb-4 opacity-50">🎬</div>
              <p className="text-text-muted text-sm">放置 demo-video.mp4 到 public/mock/ 目录</p>
              <p className="text-text-muted text-xs mt-1">或将上方 video 标签替换为实际视频地址</p>
            </div>
          </div>

          {/* Video info */}
          <div className="flex items-center justify-between mt-4 glass-card rounded-xl px-5 py-3">
            <div className="flex gap-6 text-sm text-text-muted">
              <span>时长: {shots.length * 5}s</span>
              <span>分辨率: 1920×1080</span>
              <span>帧率: 24fps</span>
              <span>格式: MP4</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-text-secondary">
              <Volume2 size={14} />
              <span>含语音旁白</span>
            </div>
          </div>
        </div>

        {/* Right: Shot list / Timeline */}
        <div className="w-[280px] flex flex-col">
          <h3 className="text-sm font-medium text-text-secondary mb-4">分镜序列</h3>
          <div className="flex-1 overflow-y-auto flex flex-col gap-3 pr-1">
            {shots.map((shot, index) => (
              <motion.div
                key={shot.id}
                initial={{ opacity: 0, x: 10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }}
                className="glass-card rounded-lg p-3 flex gap-3"
              >
                <div className="w-20 h-12 rounded-md overflow-hidden bg-bg-input flex-shrink-0">
                  <img
                    src={shot.imageUrl}
                    alt={shot.title}
                    className="w-full h-full object-cover"
                    onError={(e) => {
                      const target = e.target as HTMLImageElement
                      target.style.display = 'none'
                    }}
                  />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-medium text-text-primary truncate">
                    {shot.sortOrder}. {shot.title}
                  </div>
                  <div className="text-xs text-text-muted mt-1">
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