export interface Shot {
  id: number
  sortOrder: number
  title: string
  prompt: string
  narration: string
  transition: string
  imageUrl: string
  status: 'pending' | 'generating' | 'completed'
}

export interface Story {
  id: number
  title: string
  content: string
  style: string
  status: string
  shots: Shot[]
}

// ====== 预制的演示故事 ======
// 你可以替换 content 为你想演示的故事文本
export const DEMO_STORY_INPUT = `在一个被遗忘的海边小镇上，住着一个喜欢画画的女孩。每天傍晚，她都会坐在灯塔旁的礁石上，对着落日画下大海的颜色。有一天，她在海滩上捡到了一个透明的瓶子，里面装着一封来自远方的信。信上写着："如果你能看到这封信，请在下一个满月之夜，到灯塔顶上点亮一盏灯。"满月之夜来临，女孩爬上灯塔，点亮了尘封已久的灯。灯光穿透迷雾，照亮了远方海面上一艘破旧的帆船。船上站着一个男孩，他微笑着向她挥手。`

// ====== 预制的分镜数据 ======
// imageUrl 请替换为你用 Midjourney/DALL-E 生成的图片地址
export const DEMO_SHOTS: Shot[] = [
  {
    id: 1,
    sortOrder: 1,
    title: '海边小镇全景',
    prompt: 'Wide establishing shot of a forgotten coastal town at golden hour, old lighthouse on cliff, warm sunlight, cinematic composition, film grain, Studio Ghibli style',
    narration: '在一个被世界遗忘的海边小镇上，时间仿佛静止了。',
    transition: 'Crossfade',
    imageUrl: 'https://cdn.midjourney.com/35e8a20b-ae80-40e8-9011-27a9c197a534/0_1.png',
    status: 'completed',
  },
  {
    id: 2,
    sortOrder: 2,
    title: '女孩在礁石上作画',
    prompt: 'Medium shot of a young girl sitting on rocks near lighthouse, painting on canvas, sunset ocean background, warm golden light, soft wind blowing hair, anime style cinematic',
    narration: '每天傍晚，她都会坐在灯塔旁的礁石上，用画笔捕捉大海的每一种颜色。',
    transition: 'Ken Burns',
    imageUrl: 'https://cdn.midjourney.com/6daa47c1-3e74-42d3-9c4a-b5c8e1574d97/0_3.png',
    status: 'completed',
  },
  {
    id: 3,
    sortOrder: 3,
    title: '发现漂流瓶',
    prompt: 'Close-up shot of a transparent glass bottle with a letter inside, lying on wet sand, ocean waves gently touching it, magical glow, bokeh background, cinematic lighting',
    narration: '直到有一天，她在沙滩上发现了一个漂流瓶，里面装着一封来自远方的信。',
    transition: 'Crossfade',
    imageUrl: 'https://cdn.midjourney.com/197d45a8-3492-4a4b-babc-6960f554f23f/0_0.png',
    status: 'completed',
  },
  {
    id: 4,
    sortOrder: 4,
    title: '阅读信件',
    prompt: 'Over-the-shoulder shot of girl reading a handwritten letter, warm indoor lighting, candlelight, emotional atmosphere, detailed paper texture, cinematic depth of field',
    narration: '信上写着——如果你能看到这封信，请在满月之夜，到灯塔顶上点亮一盏灯。',
    transition: 'Ken Burns',
    imageUrl: 'https://cdn.midjourney.com/906a7a9c-7037-4a8b-86f4-8e3a6e0cf273/0_3.png',
    status: 'completed',
  },
  {
    id: 5,
    sortOrder: 5,
    title: '点亮灯塔',
    prompt: 'Dramatic low-angle shot of girl lighting the old lighthouse lamp, full moon visible through window, beam of light cutting through fog, volumetric lighting, epic cinematic moment',
    narration: '满月之夜，女孩爬上灯塔，点亮了尘封已久的灯火。',
    transition: 'Crossfade',
    imageUrl: 'https://media.discordapp.net/attachments/1481854420550553756/1481878674830790687/vetsp6557_Dramatic_low-angle_shot_of_girl_lighting_the_old_ligh_d22fef40-6c06-4f5c-88a3-b6fa43a01434.png?ex=69b4ea90&is=69b39910&hm=44a0fd52d762ae86073c01774212143efddeb5721c45100c4beefb29d78ecf7e&=&format=webp&quality=lossless&width=1165&height=653 ',
    status: 'completed',
  },
  {
    id: 6,
    sortOrder: 6,
    title: '远方的帆船与重逢',
    prompt: 'Wide cinematic shot of lighthouse beam illuminating a sailboat on moonlit ocean, silhouette of a boy waving from the boat, magical atmosphere, starry sky, emotional reunion, film grain',
    narration: '灯光穿透迷雾，照亮了远方海面上的一艘帆船。船上的男孩，微笑着向她挥手。',
    transition: 'Volume Mix',
    imageUrl: 'https://cdn.midjourney.com/ca996634-2aa5-40f1-a669-68cb370dcfc6/0_0.png',
    status: 'completed',
  },
]

export const DEMO_STORY: Story = {
  id: 1,
  title: '灯塔之约',
  content: DEMO_STORY_INPUT,
  style: 'movie',
  status: 'completed',
  shots: DEMO_SHOTS,
}

// ====== 风格选项 ======
export const STYLE_OPTIONS = [
  {
    id: 'movie',
    name: '电影风格',
    description: '胶片质感，电影级构图与光影',
    icon: '🎬',
    gradient: 'from-amber-500/20 to-orange-500/20',
    borderColor: 'border-amber-500/30',
  },
  {
    id: 'animation',
    name: '动画风格',
    description: '精致手绘，日系动画质感',
    icon: '🎨',
    gradient: 'from-pink-500/20 to-purple-500/20',
    borderColor: 'border-pink-500/30',
  },
  {
    id: 'realistic',
    name: '写实风格',
    description: '超高清真实影像效果',
    icon: '📷',
    gradient: 'from-cyan-500/20 to-blue-500/20',
    borderColor: 'border-cyan-500/30',
  },
]

// ====== 生成阶段文案 ======
export const GENERATION_STAGES = [
  { text: '正在解析故事结构...', icon: '📖', duration: 1500 },
  { text: 'AI 正在理解叙事逻辑...', icon: '🧠', duration: 1200 },
  { text: '正在拆解分镜序列...', icon: '🎬', duration: 1500 },
  { text: '正在为每个镜头生成画面描述...', icon: '✍️', duration: 1300 },
  { text: '正在生成关键帧图像...', icon: '🎨', duration: 2000 },
  { text: '创作完成！', icon: '✨', duration: 800 },
]

// ====== 重新生成的备用图片 ======
// 用于演示"重新生成"功能，key 为 shot id，value 为备用图片URL
// TODO: 用 Midjourney 对镜头3（漂流瓶）再生成一张不同构图的图，替换下面的URL
export const ALTERNATE_IMAGES: Record<number, string> = {
  3: 'https://cdn.midjourney.com/a098a2ca-cd38-4bcd-868a-f76cb5284e7c/0_2.png',
}

export const VIDEO_GENERATION_STAGES = [
  { text: '正在合成语音旁白...', icon: '🎙️', duration: 1500 },
  { text: '正在将关键帧转化为动态画面...', icon: '🎞️', duration: 2000 },
  { text: '正在添加转场效果...', icon: '🔄', duration: 1200 },
  { text: '正在混音与字幕合成...', icon: '🎵', duration: 1500 },
  { text: '视频合成完成！', icon: '🎬', duration: 800 },
]