import { useMutation } from "@tanstack/react-query";
import { ask } from "../api/ask";
export const useAsk = () => useMutation({ mutationFn: ask });
