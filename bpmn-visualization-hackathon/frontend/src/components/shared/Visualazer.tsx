import { LiveAudioVisualizer } from "react-audio-visualize";
import { useChatStore } from "../../state/chat";

export const Visualizer = () => {
  const { mediaRecorder } = useChatStore();

  return (
    <div>
      {mediaRecorder && (
        <LiveAudioVisualizer
          mediaRecorder={mediaRecorder}
          width={250}
          height={40}
        />
      )}
    </div>
  );
};
