import { useMutation } from "@tanstack/react-query";
import { refetchAsk } from "../api/refetchAsk";

export const useRefetchAsk = () => useMutation({ mutationFn: refetchAsk });
