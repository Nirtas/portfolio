import { useMutation } from "@tanstack/react-query";
import { sendVoice } from "../api/sendVoice";
export const useSendVoice = () => useMutation({ mutationFn: sendVoice });
