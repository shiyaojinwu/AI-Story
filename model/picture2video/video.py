from modelscope.pipelines import pipeline
from modelscope.outputs import OutputKeys
import requests
from PIL import Image
from io import BytesIO
import os
import shutil
import json
import sys
import torch
os.environ["PYTORCH_CUDA_ALLOC_CONF"] = "max_split_size_mb:128"
def clear_vf(video_folder):
    for item in os.listdir(video_folder):
        item_path = os.path.join(video_folder, item)
        if os.path.isfile(item_path) or os.path.islink(item_path):
            os.unlink(item_path)
            print(f"Deleted file: {item_path}")
        elif os.path.isdir(item_path):
            shutil.rmtree(item_path)
            print(f"Deleted directory: {item_path}")

pipe = pipeline(task="image-to-video", model='Image-to-Video', model_revision='v1.1.0', device='cuda:0', fp16=True)

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
        # if idx==1: break
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

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python video.py '<json_data>'")
        sys.exit(1)

    data = json.loads(sys.argv[1])
    print(f"视频生成前显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
    get_video(data)
    del pipe
    torch.cuda.empty_cache()
    print(f"视频生成后显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")