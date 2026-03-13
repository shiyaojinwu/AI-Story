import { useState, useCallback } from 'react'
import { AnimatePresence } from 'framer-motion'
import CreatePage from './pages/CreatePage'
import GeneratingPage from './pages/GeneratingPage'
import StoryboardPage from './pages/StoryboardPage'
import ShotDetailPage from './pages/ShotDetailPage'
import PreviewPage from './pages/PreviewPage'
import { DEMO_SHOTS, GENERATION_STAGES, VIDEO_GENERATION_STAGES } from './mockData'
import type { Shot } from './mockData'

type Page =
  | { name: 'create' }
  | { name: 'generating'; stages: typeof GENERATION_STAGES; nextPage: Page }
  | { name: 'storyboard' }
  | { name: 'shot-detail'; shot: Shot }
  | { name: 'preview' }

export default function App() {
  const [page, setPage] = useState<Page>({ name: 'create' })
  const [storyTitle, setStoryTitle] = useState('')
  const [storyContent, setStoryContent] = useState('')
  const [storyStyle, setStoryStyle] = useState('movie')
  const [shots, setShots] = useState<Shot[]>(DEMO_SHOTS)

  const handleGenerate = useCallback((content: string, style: string) => {
    setStoryContent(content)
    setStoryStyle(style)
    setStoryTitle('灯塔之约')
    setPage({
      name: 'generating',
      stages: GENERATION_STAGES,
      nextPage: { name: 'storyboard' },
    })
  }, [])

  const handleGenerateVideo = useCallback(() => {
    setPage({
      name: 'generating',
      stages: VIDEO_GENERATION_STAGES,
      nextPage: { name: 'preview' },
    })
  }, [])

  const handleGenerationDone = useCallback((nextPage: Page) => {
    setPage(nextPage)
  }, [])

  const handleShotClick = useCallback((shot: Shot) => {
    setPage({ name: 'shot-detail', shot })
  }, [])

  const handleShotUpdate = useCallback((updated: Shot) => {
    setShots(prev => prev.map(s => s.id === updated.id ? updated : s))
  }, [])

  const handleBack = useCallback((target: Page['name']) => {
    if (target === 'storyboard') setPage({ name: 'storyboard' })
    else if (target === 'create') setPage({ name: 'create' })
  }, [])

  return (
    <div className="min-h-screen bg-bg bg-grid">
      <AnimatePresence mode="wait">
        {page.name === 'create' && (
          <CreatePage key="create" onGenerate={handleGenerate} />
        )}
        {page.name === 'generating' && (
          <GeneratingPage
            key="generating"
            stages={page.stages}
            onDone={() => handleGenerationDone(page.nextPage)}
          />
        )}
        {page.name === 'storyboard' && (
          <StoryboardPage
            key="storyboard"
            title={storyTitle}
            shots={shots}
            onShotClick={handleShotClick}
            onGenerateVideo={handleGenerateVideo}
            onBack={() => handleBack('create')}
          />
        )}
        {page.name === 'shot-detail' && (
          <ShotDetailPage
            key="shot-detail"
            shot={page.shot}
            onBack={() => handleBack('storyboard')}
            onUpdate={handleShotUpdate}
          />
        )}
        {page.name === 'preview' && (
          <PreviewPage
            key="preview"
            title={storyTitle}
            shots={shots}
            onBack={() => handleBack('storyboard')}
          />
        )}
      </AnimatePresence>
    </div>
  )
}