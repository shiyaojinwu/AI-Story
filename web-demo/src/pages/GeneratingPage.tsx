import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'

interface Stage {
  text: string
  icon: string
  duration: number
}

interface Props {
  stages: Stage[]
  onDone: () => void
}

export default function GeneratingPage({ stages, onDone }: Props) {
  const [currentStage, setCurrentStage] = useState(0)
  const [progress, setProgress] = useState(0)

  useEffect(() => {
    if (currentStage >= stages.length) {
      const timer = setTimeout(onDone, 500)
      return () => clearTimeout(timer)
    }

    const stage = stages[currentStage]
    const progressPerStage = 100 / stages.length
    const startProgress = currentStage * progressPerStage
    const endProgress = (currentStage + 1) * progressPerStage

    // Animate progress within this stage
    const steps = 20
    const stepDuration = stage.duration / steps
    let step = 0

    const interval = setInterval(() => {
      step++
      const eased = step / steps
      setProgress(startProgress + (endProgress - startProgress) * eased)
      if (step >= steps) {
        clearInterval(interval)
        setCurrentStage(prev => prev + 1)
      }
    }, stepDuration)

    return () => clearInterval(interval)
  }, [currentStage, stages, onDone])

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="min-h-screen flex flex-col items-center justify-center px-6"
    >
      {/* Central animation area */}
      <div className="flex flex-col items-center gap-8 max-w-md w-full">
        {/* Animated orb */}
        <div className="relative w-32 h-32">
          <motion.div
            animate={{
              scale: [1, 1.2, 1],
              rotate: [0, 180, 360],
            }}
            transition={{
              duration: 3,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
            className="w-full h-full rounded-full bg-gradient-to-br from-violet-600 via-purple-500 to-fuchsia-500 opacity-20 blur-xl absolute"
          />
          <motion.div
            animate={{
              scale: [1.1, 0.9, 1.1],
              rotate: [360, 180, 0],
            }}
            transition={{
              duration: 4,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
            className="w-24 h-24 rounded-full bg-gradient-to-br from-violet-500 to-purple-600 opacity-40 blur-lg absolute top-4 left-4"
          />
          <div className="w-16 h-16 rounded-full bg-gradient-to-br from-violet-500 to-purple-600 absolute top-8 left-8 flex items-center justify-center text-2xl shadow-lg shadow-purple-500/30">
            {stages[Math.min(currentStage, stages.length - 1)]?.icon}
          </div>
        </div>

        {/* Stage text */}
        <div className="text-center">
          <motion.p
            key={currentStage}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-xl text-text-primary font-medium mb-2"
          >
            {stages[Math.min(currentStage, stages.length - 1)]?.text}
          </motion.p>
          <p className="text-sm text-text-muted">
            {Math.round(progress)}%
          </p>
        </div>

        {/* Progress bar */}
        <div className="w-full h-1.5 bg-border/50 rounded-full overflow-hidden">
          <motion.div
            className="h-full bg-gradient-to-r from-violet-600 to-purple-500 rounded-full"
            style={{ width: `${progress}%` }}
            transition={{ duration: 0.1 }}
          />
        </div>

        {/* Stage indicators */}
        <div className="flex gap-3">
          {stages.map((stage, i) => (
            <div
              key={i}
              className={`w-2 h-2 rounded-full transition-all duration-300 ${
                i < currentStage
                  ? 'bg-accent scale-100'
                  : i === currentStage
                  ? 'bg-accent-light pulse-glow scale-125'
                  : 'bg-border'
              }`}
            />
          ))}
        </div>
      </div>
    </motion.div>
  )
}