import { axiosInstance } from "../utils/axios";

type Props = {
  chat_id: string;
  text: string;
};

export const ask = async ({ text, chat_id }: Props) => {
  const { data } = await axiosInstance.post("api/send_text_message", {
    chat_id,
    text,
  });
  return data;
};
