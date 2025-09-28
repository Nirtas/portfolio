import { useMutation } from "@tanstack/react-query";
import { createChat } from "../api/createChat";

export const useCreateChat = () => useMutation({ mutationFn: createChat });
