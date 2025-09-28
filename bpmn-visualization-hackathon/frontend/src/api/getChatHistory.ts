import { axiosInstance } from "../utils/axios";

type Props = {
  chatId?: string | null;
};

export const getChatHistory = async ({ chatId }: Props) => {
  if (!chatId) {
    return [];
  }
  const { data } = await axiosInstance.get(`api/chat_history/${chatId}`);
  return data;
};
