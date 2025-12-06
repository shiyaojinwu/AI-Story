from flask import Flask, request, jsonify, send_from_directory
import subprocess
import json
import sys
import os
import math
from moviepy.editor import VideoFileClip, AudioFileClip, concatenate_videoclips, concatenate_audioclips, TextClip, CompositeVideoClip
from modelscope.outputs import OutputKeys
import requests
from PIL import Image
from io import BytesIO
import os
import shutil
import json
import sys
import torch
from modelscope.pipelines import pipeline
os.environ["PYTORCH_CUDA_ALLOC_CONF"] = "max_split_size_mb:128"
pipe = pipeline(task="image-to-video", model='Image-to-Video', model_revision='v1.1.0', device='cuda:0', fp16=True)
app = Flask(__name__)


def clear_vf(video_folder):
    for item in os.listdir(video_folder):
        item_path = os.path.join(video_folder, item)
        if os.path.isfile(item_path) or os.path.islink(item_path):
            os.unlink(item_path)
            print(f"Deleted file: {item_path}")
        elif os.path.isdir(item_path):
            shutil.rmtree(item_path)
            print(f"Deleted directory: {item_path}")
def get_video(data):
    shots = data["shots"]
    for shot in shots:
        if not all(key in shot for key in {"image_url", "narration", "transition"}):
            print("发来的请求JSON的数据有错")
            return
        
    clear_vf('video_tmp')
    clear_vf('video_tmp_2')

    output_folder = 'video_tmp'

    for idx, shot in enumerate(shots):
        if idx==1: break
        img_url = shot['image_url']
        prompt = shot['narration']
        # print(img_url, prompt)
        if not prompt or not img_url:
            print("发来的请求缺失JSON参数prompt 或者 img")
            return
        print(img_url)
        response = requests.get(img_url)
        if response.status_code == 200:
            print(f"视频生成前显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
            image_data = BytesIO(response.content)
            image = Image.open(image_data)

            existing_files = os.listdir(output_folder)
            num_files = len(existing_files)
            new_filename = f"v_{num_files}.mp4"
            output_path = os.path.join(output_folder, new_filename)   

            output_video_path = pipe(image, output_video=output_path, duration=10)[OutputKeys.OUTPUT_VIDEO]
            print(f"生成的视频已保存到：{output_video_path}")
            print(f"视频生成后显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
            torch.cuda.empty_cache()
            print(f"请理后显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
        else:
            print(f"图片资源不存在：{response.status_code}")


VOICE_PATH = "/5/CosyVoice/voice.py"  
VIDEO_PATH = "/5/video.py"  # 视频脚本的路径

ENV_VOICE = "source /mnt/workspace/i/bin/activate"  
ENV_VIDEO = "source /mnt/workspace/Python-3.9.25/video/bin/activate" 

VOICE_PYTHON = "/mnt/workspace/i/bin/python"
VIDEO_PYTHON = "/mnt/workspace/Python-3.9.25/video/bin/python"

def audio(data):
    print("开始音频")
    try:
        command_audio = [VOICE_PYTHON, VOICE_PATH, json.dumps(data)]
        print(f"执行命令: {' '.join(command_audio[:2])} [DATA]")
        
        result_audio = subprocess.run(
            command_audio, 
            shell=False, 
            check=True, 
            stdout=sys.stdout,    # 实时输出到控制台
            stderr=sys.stderr,    # 实时输出错误
            text=True,
            encoding='utf-8'
        )
        
        # 打印音频脚本的输出
        print("=== 音频脚本输出 ===")
        print(f"STDOUT: {result_audio.stdout}")
        print(f"STDERR: {result_audio.stderr}")
        print(f"返回码: {result_audio.returncode}")
        print("==================")
        
        if result_audio.returncode != 0:
            print(f"音频处理错误: {result_audio.stderr}")
            return False, f"音频处理失败: {result_audio.stderr}"
            
    except subprocess.CalledProcessError as e:
        print(f"音频处理异常: {e}")
        print(f"标准输出: {e.stdout}")
        print(f"标准错误: {e.stderr}")
        return False, f"音频处理异常: {str(e)}"
''' 
def video(data):
    print("开始视频")
    try:
        command_video = [VIDEO_PYTHON, VIDEO_PATH, json.dumps(data)]
        print(f"执行命令: {' '.join(command_video[:2])} [DATA]")
        
        result_video = subprocess.run(
            command_video, 
            shell=False, 
            check=True, 
            stdout=sys.stdout,    # 实时输出到控制台
            stderr=sys.stderr,    # 实时输出错误
            text=True,
            encoding='utf-8'
        )
        
        print("=== 视频脚本输出 ===")
        print("==================")
        
        if result_video.returncode != 0:
            print(f"视频处理错误: {result_video.stderr}")
            return False, f"视频处理失败: {result_video.stderr}"
            
    except subprocess.CalledProcessError as e:
        print(f"视频处理异常: {e}")
        print(f"标准输出: {e.stdout}")
        print(f"标准错误: {e.stderr}")
        return False, f"视频处理异常: {str(e)}"
    
    print("音频和视频处理完成")
    return True, "处理成功"
'''
def merge_final(story_id):
    output_folder = 'video_tmp_2'
    final_folder = 'final_video'
    audio_folder = 'audio_output'

    video_files = [os.path.join(output_folder, f) for f in os.listdir(output_folder) if f.endswith(('.mp4'))]
    video_files = sorted(video_files)
    clips = [VideoFileClip(file) for file in video_files]
    final_clip = concatenate_videoclips(clips, method="compose")
    final_video = final_clip.without_audio()
    
    
    audio_files = [os.path.join(audio_folder, f) for f in os.listdir(audio_folder) if f.endswith('.wav')]
    audio_files = sorted(audio_files, key=lambda x: int(x.split('_')[-1].split('.')[0]))
    audio_clips = [AudioFileClip(file) for file in audio_files]
    final_audio = concatenate_audioclips(audio_clips)

    final_clip = final_video.set_audio(final_audio)

    final_v = os.path.join(final_folder, f"{story_id}.mp4")
    final_clip.write_videofile(final_v, codec="libx264", fps=24) 
    print(f"合并完成，输出文件保存为：{final_v}")
    video_length = final_clip.duration

    for clip in clips:
        clip.close()
    if audio_files:
        for audio_clip in audio_clips:
            audio_clip.close()
        final_audio.close()
    final_video.close()
    final_clip.close()

    return final_v, video_length


    video_clips = []
    for i, video_path in enumerate(output_folder):
        print(f"加载第 {i} 个视频: {os.path.basename(video_path)}")
        clip = VideoFileClip(video_path)
        video_clips.append(clip)
    
    print("正在拼接视频...")
    
    final_video = concatenate_videoclips(video_clips, method="compose")
    
    
    
    print(f"超长长视频保存到: {final_v}")
    print(f"总时长: {final_video.duration:.2f}秒 ({final_video.duration/60:.2f}分钟)")
    
    final_video.write_videofile(
        final_v,
        codec='libx264',
        audio_codec='aac',
        fps=final_video.fps,
        preset='medium',
        bitrate=f"{final_video.w * final_video.h * 0.1:.0f}k",
        threads=4
    )
    print(f"✓ 成功保存最终拼接视频: {final_v}")
    
    for clip in video_clips:
        clip.close()
    final_video.close()

    return final_v
    
def merge(data):
    video_folder = 'video_tmp'
    audio_folder = 'audio_output'
    output_folder = 'video_tmp_2'

    video_files = [os.path.join(video_folder, f) for f in os.listdir(video_folder) if f.endswith('.mp4')]
    video_files = sorted(video_files, key=lambda x: int(x.split('_')[-1].split('.')[0]))
    
    audio_files = [os.path.join(audio_folder, f) for f in os.listdir(audio_folder) if f.endswith('.wav')]
    audio_files = sorted(audio_files, key=lambda x: int(x.split('_')[-1].split('.')[0]))
    
    print(f"找到 {len(video_files)} 个视频文件")
    print(f"找到 {len(audio_files)} 个音频文件")

    if len(video_files) != len(audio_files):
        print(f"警告：视频文件数({len(video_files)})和音频文件数({len(audio_files)})不匹配！")
    
    shots = data["shots"]
    for i, (video_path, audio_path) in enumerate(zip(video_files, audio_files)):
        print(f"\n{'='*50}")
        print(f"处理第 {i} 对文件：")
        print(video_path, audio_path)
        shot = shots[i]
        narration = shot["narration"]
        
        try:
            video = VideoFileClip(video_path)
            audio = AudioFileClip(audio_path)
            
            video_duration = video.duration
            audio_duration = audio.duration
            
            print(f"视频时长: {video_duration:.2f}秒")
            print(f"音频时长: {audio_duration:.2f}秒")
            
            if audio_duration < video_duration:
                print(f"音频较短，裁剪视频从 {video_duration:.2f}秒 到 {audio_duration:.2f}秒")
                video_clip = video.subclip(0, audio_duration)
                
            elif audio_duration > video_duration:
                print(f"音频{audio_path}较长，需要重复视频{video_path}")
                repeats_needed = math.ceil(audio_duration / video_duration)
                video_clips = []
                for repeat in range(repeats_needed):
                    video_clips.append(video)
                repeated_video = concatenate_videoclips(video_clips, method="compose")
                if repeated_video.duration > audio_duration:
                    video_clip = repeated_video.subclip(0, audio_duration)
                else:
                    video_clip = repeated_video
            else:
                print("视频和音频长度相等")
                video_clip = video  
            
            # final_video = video_clip.set_audio(audio)
            # final_video = video_clip

            txt_clip = TextClip(
                narration, 
                fontsize=50, 
                color='white',
                font='Arial',
                stroke_color='black',  # 描边
                stroke_width=2
            )
            txt_clip = txt_clip.set_position(('center', 'bottom')).set_duration(video_duration)
            final_video = CompositeVideoClip([video_clip, txt_clip])


            output_path = os.path.join(output_folder, f'merged_{i}.mp4')
            
            print(f"正在保存到: {output_path}")
            final_video.write_videofile(
                output_path,
                codec='libx264',
                audio_codec='aac',
                fps=video.fps,
                preset='medium',  # 编码速度/质量平衡
                bitrate=f"{video.w * video.h * 0.1:.0f}k"  # 自动计算比特率
            )
            
            print(f"✓ 成功处理并保存: {output_path}")
            
            video.close()
            audio.close()
            video_clip.close()
            final_video.close()
            if 'repeated_video' in locals():
                repeated_video.close()
                
        except Exception as e:
            print(f"✗ 处理第 {i} 对文件时出错: {str(e)}")
            continue
    
    print(f"\n{'='*50}")
    print("处理完成！现在开始合成")


import threading
tasks = {}  # 存储任务状态
def process_in_background(task_id, data):
    try:
        tasks[task_id]['status'] = 'processing'
        tasks[task_id]['progress'] = 25
        
        audio(data)  # 步骤1
        tasks[task_id]['progress'] = 50
        
        get_video(data)
        # video(data)  # 步骤2
        tasks[task_id]['progress'] = 75
        
        merge(data)  # 步骤3
        tasks[task_id]['progress'] = 90
        
        story_id = data["story_id"]
        final_v, dur = merge_final(story_id)  # 步骤4
        final_url = f'https://steamerless-damian-snaggy.ngrok-free.dev/{final_v}'
        
        # 完成
        tasks[task_id]['status'] = 'completed'
        tasks[task_id]['progress'] = 100
        tasks[task_id]['result'] = {"video_url": final_url, "duration": int(dur)}
        
    except Exception as e:
        tasks[task_id]['status'] = 'failed'
        tasks[task_id]['error'] = str(e)

@app.route('/')
def index():
    return "视频合成"

@app.route('/video', methods=['POST'])
def process_video():
    data = request.get_json()
    if not data:
        if not data: return jsonify({"error": "data needed"}), 400
    
    task_id = data["story_id"]
    # task_id = str(uuid.uuid4())[:8]  # 短ID
    tasks[task_id] = {'status': 'waiting', 'progress': 0}
    
    thread = threading.Thread(target=process_in_background, args=(task_id, data))
    thread.start()
    return jsonify({
        "task_id": task_id,
        "status": "accepted",
        "check_url": f"https://steamerless-damian-snaggy.ngrok-free.dev/check/{task_id}"
    })
    
    audio(data)
    video(data)
    merge()
    final_v, dur = merge_final()
    final_v = f'http://113.240.112.32/{final_v}'
    return jsonify({
            "video_url": final_v,
            "duration": dur
        }), 200

@app.route('/check/<task_id>', methods=['GET'])
def check_status(task_id):
    if task_id not in tasks:
        return jsonify({"error": "task is not exist"}), 404
    
    task = tasks[task_id]
    result = {"task_id": task_id, "status": task['status'], "progress": task['progress']}
    
    if task['status'] == 'completed':
        return jsonify({
            "status": "completed",
            "video_url": task['result']['video_url'],  # 这里返回URL！
            "duration": task['result']['duration']
        })
    elif task['status'] == 'failed':
        result['error'] = task.get('error', '未知错误')
    
    return jsonify({
    "status": task['status'],
    "progress": task['progress']
    })

'''
轮循
status = status_data.get("status")
progress = status_data.get("progress", 0)
if status == "完成":
    print("✅ 视频生成成功!")
    print(f"视频URL: {status_data.get('video_url')}")
    print(f"时长: {status_data.get('duration')}秒")
    return True
            
elif status == "失败":
    print(f"❌ 处理失败: {status_data.get('error')}")
    return False
    
wait_time = 3  # 等待3秒
        print(f"等待 {wait_time} 秒后重试...")
'''

@app.route('/<path:filename>')
def serve_file(filename):
    return send_from_directory('/5', filename)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)