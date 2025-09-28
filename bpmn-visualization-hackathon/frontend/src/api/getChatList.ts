import { axiosInstance } from "../utils/axios";

export const getChatList = async () => {
  const { data } = await axiosInstance.get(`api/get_all_chats`);
  return data;
};
