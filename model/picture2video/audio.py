import sys
sys.path.append('/5/CosyVoice/third_party/Matcha-TTS')
from cosyvoice.cli.cosyvoice import CosyVoice, CosyVoice2
from cosyvoice.utils.file_utils import load_wav
import torchaudio
import os
import json
import torch
cosyvoice = CosyVoice2('CosyVoice2-0.5B', load_jit=False, load_trt=False, load_vllm=False, fp16=False)
import shutil
def clear_vf():
    video_folder = 'audio_output'
    for item in os.listdir(video_folder):
        item_path = os.path.join(video_folder, item)
        if os.path.isfile(item_path) or os.path.islink(item_path):
            os.unlink(item_path)
            print(f"Deleted file: {item_path}")
        elif os.path.isdir(item_path):
            shutil.rmtree(item_path)
            print(f"Deleted directory: {item_path}")

def gen_voice(sentence):
    merged_audio = None
    for i, j in enumerate(cosyvoice.inference_zero_shot(sentence, '', '', zero_shot_spk_id='my_zero_shot_spk', stream=False)):
        if merged_audio is None:
            merged_audio = j['tts_speech']
        else:
            merged_audio = torch.cat((merged_audio, j['tts_speech']), dim=1) 
    return merged_audio

def get_voice_all(data):
    shots = data["shots"]
    for shot in shots:
        if not all(key in shot for key in {"image_url", "narration", "transition"}):
            print("发来的请求缺失JSON参数prompt 或者 img")
            return 0

    clear_vf()

    for idx, shot in enumerate(shots):
        prompt = shot['narration']
        if not prompt:
            print("发来的请求缺失JSON参数prompt")
            return 0
        
        output_folder = 'audio_output'
        audio_ = gen_voice(prompt)
        existing_files = os.listdir(output_folder)
        num_files = len(existing_files)
        new_filename = f"a_{num_files}.wav"
        output_path = os.path.join(output_folder, new_filename)   
        torchaudio.save(output_path, audio_, cosyvoice.sample_rate)
    del audio_
    torch.cuda.empty_cache()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python voice.py '<json_data>'")
        sys.exit(1)

    data = json.loads(sys.argv[1])
    print("##############################\n得到数据",data)
    print(f"音频脚本执行前显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
    get_voice_all(data)
    print(f"音频脚本执行后显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")
    del cosyvoice
    torch.cuda.empty_cache()
    print(f"音频脚本退出前显存占用: {torch.cuda.memory_allocated() / (1024**3):.2f} GB")