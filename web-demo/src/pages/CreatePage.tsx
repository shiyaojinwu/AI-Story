import { useState } from 'react'
import { motion } from 'framer-motion'
import { Sparkles, ChevronRight } from 'lucide-react'
import { STYLE_OPTIONS, DEMO_STORY_INPUT } from '../mockData'

interface Props {
  onGenerate: (content: string, style: string) => void
}

export default function CreatePage({ onGenerate }: Props) {
  const [content, setContent] = useState('')
  const [selectedStyle, setSelectedStyle] = useState('movie')

  const handleSubmit = () => {
    const text = content.trim() || DEMO_STORY_INPUT
    onGenerate(text, selectedStyle)
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.4 }}
      className="min-h-screen flex flex-col"
    >
      {/* Header */}
      <header className="px-8 py-6 flex items-center justify-between border-b border-border/50">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-violet-600 to-purple-500 flex items-center justify-center text-white text-sm font-bold">
            镜
          </div>
          <span className="text-lg font-semibold text-text-primary">镜语 AI</span>
        </div>
        <div className="text-sm text-text-muted">多模态AI结构化视频创作平台</div>
      </header>

      {/* Hero + Form */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 py-12 max-w-4xl mx-auto w-full">
        {/* Hero */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="text-center mb-12"
        >
          <h1 className="text-5xl font-bold mb-4 gradient-text leading-tight">
            一段故事，逐镜打磨，一部影片
          </h1>
          <p className="text-text-secondary text-lg max-w-2xl mx-auto">
            输入你的故事，AI 将自动拆解分镜、生成画面、合成配音，
            <br />
            让你像导演一样掌控每一个镜头
          </p>
        </motion.div>

        {/* Story Input */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="w-full mb-8"
        >
          <label className="block text-sm text-text-secondary mb-2 font-medium">
            你的故事
          </label>
          <textarea
            value={content}
            onChange={e => setContent(e.target.value)}
            placeholder={DEMO_STORY_INPUT}
            rows={6}
            className="w-full bg-bg-input border border-border rounded-xl px-5 py-4 text-text-primary placeholder:text-text-muted/50 resize-none focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent-glow transition-all text-base leading-relaxed"
          />
        </motion.div>

        {/* Style Selection */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="w-full mb-10"
        >
          <label className="block text-sm text-text-secondary mb-3 font-medium">
            视觉风格
          </label>
          <div className="grid grid-cols-3 gap-4">
            {STYLE_OPTIONS.map(style => (
              <button
                key={style.id}
                onClick={() => setSelectedStyle(style.id)}
                className={`glass-card rounded-xl p-5 text-left cursor-pointer transition-all ${
                  selectedStyle === style.id
                    ? 'border-accent ring-1 ring-accent-glow bg-gradient-to-br ' + style.gradient
                    : ''
                }`}
              >
                <span className="text-2xl mb-2 block">{style.icon}</span>
                <div className="font-semibold text-text-primary mb-1">{style.name}</div>
                <div className="text-sm text-text-secondary">{style.description}</div>
              </button>
            ))}
          </div>
        </motion.div>

        {/* Generate Button */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <button
            onClick={handleSubmit}
            className="glow-btn text-white font-semibold px-10 py-4 rounded-xl text-lg flex items-center gap-3 cursor-pointer"
          >
            <Sparkles size={20} />
            开始创作
            <ChevronRight size={18} />
          </button>
        </motion.div>
      </main>

      {/* Footer */}
      <footer className="px-8 py-4 border-t border-border/30 text-center text-xs text-text-muted">
        Powered by Multi-Modal AI Orchestration Engine
      </footer>
    </motion.div>
  )
}