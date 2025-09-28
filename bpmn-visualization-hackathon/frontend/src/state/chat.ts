import { create } from "zustand";

export type Message = { text: string; isAnswer: boolean; audioUrl?: string };

interface ChatState {
  messages: Message[];
  addMessages: (message: Message) => void;
  resetMessages: () => void;
  isRecording: boolean;
  setIsRecording: (isRecording: boolean) => void;
  mediaRecorder: MediaRecorder | null;
  setMediaRecorder: (m: MediaRecorder) => void;
  audioURL: string;
  setAudioURL: (m: string) => void;
}

export const useChatStore = create<ChatState>()((set) => ({
  messages: [],
  isRecording: false,
  resetMessages: () => set(() => ({ messages: [] })),
  addMessages: (by: Message) =>
    set((state) => ({ messages: [...state.messages, by] })),
  setIsRecording: (isRecording: boolean) => set(() => ({ isRecording })),
  mediaRecorder: null,
  setMediaRecorder: (mediaRecorder: MediaRecorder | null) =>
    set(() => ({ mediaRecorder })),
  audioURL: "",
  setAudioURL: (audioURL: string) => set(() => ({ audioURL })),
}));
