import { axiosInstance } from "../utils/axios";

type Props = {
  voice: Blob | null;
  chat_id: string;
};

export const sendVoice = async ({ voice, chat_id }: Props) => {
  if (!voice) return null;
  const formData = new FormData();
  formData.append("audio_file", voice, "recording.wav");
  formData.append("chat_id", chat_id);
  const { data } = await axiosInstance.post(
    "api/send_voice_message",
    formData,
    {
      headers: { "Content-Type": "multipart/form-data" },
    }
  );
  return data;
};
