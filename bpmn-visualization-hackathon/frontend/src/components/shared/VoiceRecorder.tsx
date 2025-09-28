import { useEffect, useRef, useState } from "react";
import { Close, KeyboardVoice, Stop } from "@mui/icons-material";
import "./ind.css";
import { Button, IconButton } from "@mui/material";
import { useChatStore } from "../../state/chat";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useCreateChat } from "../../queries/useCreateChat";
import { SttAnswer } from "../../enitities/SttAnswer";

interface IProps {
  refetchQuery: (props: SttAnswer) => void;
  sendVoiceMutate: (props: any, options: any) => void;
}

const VoiceRecorder = ({ refetchQuery, sendVoiceMutate }: IProps) => {
  const {
    isRecording,
    setIsRecording,
    setMediaRecorder,
    mediaRecorder,
    audioURL,
    setAudioURL,
  } = useChatStore();
  const { mutate: createChat } = useCreateChat();

  const navigate = useNavigate();
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [volume, setVolume] = useState(0);
  const [searchParams] = useSearchParams();
  const chatId = searchParams.get("chatId");
  const audioChunks = useRef<Blob[]>([]);
  const audioContext = useRef<AudioContext | null>(null);
  const analyser = useRef<AnalyserNode | null>(null);
  const intervalRef = useRef<any>(null);
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
      .toString()
      .padStart(2, "0");
    const secs = (seconds % 60).toString().padStart(2, "0");
    return `${mins}:${secs}`;
  };

  useEffect(() => {
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (isRecording) {
      const updateVolume = () => {
        if (!analyser.current) return;

        const dataArray = new Uint8Array(analyser.current.frequencyBinCount);
        analyser.current.getByteFrequencyData(dataArray);
        const avg = dataArray.reduce((a, b) => a + b) / dataArray.length;
        setVolume(avg);

        requestAnimationFrame(updateVolume);
      };
      updateVolume();
    }
  }, [isRecording]);

  const startRecording = async () => {
    try {
      setElapsedTime(0);
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mr = new MediaRecorder(stream);
      setMediaRecorder(mr);

      // Запуск секундомера
      intervalRef.current = setInterval(() => {
        setElapsedTime((prev) => prev + 1);
      }, 1000);
      audioContext.current = new AudioContext();
      const source = audioContext.current.createMediaStreamSource(stream);
      analyser.current = audioContext.current.createAnalyser();
      source.connect(analyser.current);

      mr.ondataavailable = (e) => {
        audioChunks.current.push(e.data);
      };

      mr.onstop = () => {
        const blob = new Blob(audioChunks.current, { type: "audio/wav" });
        setAudioBlob(blob);
        setAudioURL(URL.createObjectURL(blob));
        audioChunks.current = [];
      };

      mr.start();
      setIsRecording(true);
    } catch (err) {
      console.error("Error accessing microphone:", err);
    }
  };

  const stopRecording = () => {
    if (mediaRecorder) {
      // Остановка секундомера
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }

      mediaRecorder.stop();
      setIsRecording(false);
      if (audioContext.current) {
        audioContext.current.close();
      }
    }
  };

  const clearAudio = () => {
    setAudioURL("");
    setAudioBlob(null);
  };

  const sendAudio = async () => {
    if (!audioBlob) return;

    type Data = {
      audio_path: string;
      created_at: string;
      message: string;
      transcribed_text: string;
      user_message_id: string;
    };
    const onSuccessAudioTranscribed = (data: Data, chatId: string) => {
      refetchQuery({
        text: data.transcribed_text,
        msg_id: data.user_message_id,
        audio_path: data.audio_path,
        chatId,
      });
    };

    try {
      const formData = new FormData();
      formData.append("audio", audioBlob, "recording.wav");
      if (!chatId) {
        createChat(
          { name: "Бизнес процесс..." },
          {
            onSuccess(data) {
              if (data?.chat_id) {
                sendVoiceMutate(
                  { voice: audioBlob, chat_id: data?.chat_id },
                  {
                    onSuccess: (msgData: any) => {
                      onSuccessAudioTranscribed(msgData, data?.chat_id);
                      navigate(`/chat?chatId=${data.chat_id}`);
                    },
                  }
                );
              }
            },
          }
        );
        clearAudio();
        return;
      }
      sendVoiceMutate(
        { voice: audioBlob, chat_id: chatId },
        { onSuccess: (data: any) => onSuccessAudioTranscribed(data, chatId) }
      );
      clearAudio();
    } catch (error) {
      console.error("Upload error:", error);
    }
  };

  return (
    <div className='voice-recorder'>
      {!audioURL && (
        <div className='recording-container'>
          {isRecording && (
            <div className='timer-display'>{formatTime(elapsedTime)}</div>
          )}
          <IconButton
            className={`record-button ${isRecording ? "recording" : ""}`}
            onClick={isRecording ? stopRecording : startRecording}
          >
            {isRecording ? (
              <>
                <Stop />
                <div
                  className='volume-indicator'
                  style={{ transform: `scale(${volume / 50})` }}
                />
              </>
            ) : (
              <KeyboardVoice />
            )}
          </IconButton>
        </div>
      )}
      {audioURL && (
        <div className='audio-preview'>
          <IconButton onClick={clearAudio}>
            <Close />
          </IconButton>
          <Button onClick={sendAudio}>отправить</Button>
        </div>
      )}
    </div>
  );
};

export default VoiceRecorder;
