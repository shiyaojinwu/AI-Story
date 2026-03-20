import { useState, useCallback } from 'react'
import { AnimatePresence } from 'framer-motion'
import CreatePage from './pages/CreatePage'
import GeneratingPage from './pages/GeneratingPage'
import StoryboardPage from './pages/StoryboardPage'
import ShotDetailPage from './pages/ShotDetailPage'
import PreviewPage from './pages/PreviewPage'
import { DEFAULT_SHOTS, GENERATION_STAGES, VIDEO_GENERATION_STAGES } from './storyData'
import type { Shot } from './storyData'

type Page =
  | { name: 'create' }
  | { name: 'generating'; stages: typeof GENERATION_STAGES; nextPage: Page }
  | { name: 'storyboard' }
  | { name: 'shot-detail'; shot: Shot }
  | { name: 'preview' }

export default function App() {
  const [page, setPage] = useState<Page>({ name: 'create' })
  const [storyTitle, setStoryTitle] = useState('')
  const [, setStoryContent] = useState('')
  const [, setStoryStyle] = useState('movie')
  const [shots, setShots] = useState<Shot[]>(DEFAULT_SHOTS)

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

  const handleBackToCreate = useCallback(() => setPage({ name: 'create' }), [])
  const handleBackToStoryboard = useCallback(() => setPage({ name: 'storyboard' }), [])

  return (
    <div className="noise" style={{ minHeight: '100vh' }}>
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
            onBack={handleBackToCreate}
          />
        )}
        {page.name === 'shot-detail' && (
          <ShotDetailPage
            key="shot-detail"
            shot={page.shot}
            onBack={handleBackToStoryboard}
            onUpdate={handleShotUpdate}
          />
        )}
        {page.name === 'preview' && (
          <PreviewPage
            key="preview"
            title={storyTitle}
            shots={shots}
            onBack={handleBackToStoryboard}
          />
        )}
      </AnimatePresence>
    </div>
  )
}