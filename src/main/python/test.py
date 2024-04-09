import whisper
import warnings
import sys
# 忽略所有警告
warnings.simplefilter("ignore")
sys.stdout.reconfigure(encoding='utf-8')
try:
    if len(sys.argv) >= 2:
        input_file = sys.argv[1]
        model = whisper.load_model("medium")
        result = model.transcribe(input_file)
        segments = result["segments"]
        text = ' '.join(segment['text'] for segment in segments)
        print(text)
except Exception as e:
    pass




