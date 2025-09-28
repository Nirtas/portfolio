import { axiosInstance } from "../utils/axios";

type Props = {
  name: string;
};

export const createChat = async ({ name }: Props) => {
  const { data } = await axiosInstance.post("api/new_chat", {
    chat_name: name,
  });
  return data;
};
