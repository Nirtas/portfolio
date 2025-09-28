import { useQuery } from "@tanstack/react-query";
import { getChatHistory } from "../api/getChatHistory";
import { QueryKeys } from "./QueryKeys";

type ChatHistoryMessage = {
  history: {
    message_id: string;
    is_user_sender: boolean;
    message_text: string;
    bpmn: string;
    audio_path?: string;
  }[];
};
// @ts-ignore
export const useGetChatHistory = (chatId?: string | null) => {
  return useQuery<ChatHistoryMessage>({
    queryKey: [QueryKeys.CHAT_LIST_QUERY_KEY, chatId],
    queryFn: () => getChatHistory({ chatId }),
    retry: 3,
  });
};
