import { useQuery } from "@tanstack/react-query";
import { QueryKeys } from "./QueryKeys";
import { getChatList } from "../api/getChatList";

export const useGetChatList = () => {
  return useQuery<{ chats: { chat_id: string; chat_name: string }[] }>({
    queryKey: [QueryKeys.CHAT_LIST_QUERY_KEY],
    queryFn: () => getChatList(),
  });
};
