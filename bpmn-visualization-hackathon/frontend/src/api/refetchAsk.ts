import { axiosInstance } from "../utils/axios";

type Props = {
  chat_id: string;
  text: string;
  msg_id: string;
};

export const refetchAsk = async ({ text, chat_id, msg_id }: Props) => {
  const { data } = await axiosInstance.post("api/generate_llm_answer", {
    chat_id,
    text,
    msg_id,
  });
  return data;
};
