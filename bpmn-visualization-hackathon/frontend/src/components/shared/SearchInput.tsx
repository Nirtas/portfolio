import { useState } from "react";
import Paper from "@mui/material/Paper";
import InputBase from "@mui/material/InputBase";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import ArrowUpward from "@mui/icons-material/ArrowUpward";
import { useChatStore } from "../../state/chat";
import { Visualizer } from "./Visualazer";
import VoiceRecorder from "./VoiceRecorder";
import { SttAnswer } from "../../enitities/SttAnswer";

interface IProps {
  onClickSendBtn: (text: string) => void;
  refetchQuery: (props: SttAnswer) => void;
  sendVoiceMutate: (props: any, options: any) => void;
}

export const SearchInput = ({
  onClickSendBtn,
  refetchQuery,
  sendVoiceMutate,
}: IProps) => {
  const [text, setText] = useState("");
  const { isRecording, audioURL } = useChatStore();

  const onTextChange: React.ChangeEventHandler<
    HTMLInputElement | HTMLTextAreaElement
  > = (e) => {
    e.preventDefault();
    setText(e.target.value);
  };

  return (
    <Paper
      sx={{
        p: "2px 4px",
        display: "flex",
        alignItems: "center",
        width: "100%",
        border: "2px solid #f1f1f1",
      }}
    >
      {isRecording ? (
        <div style={{ width: "250px", marginLeft: "auto" }}>
          <Visualizer />
        </div>
      ) : audioURL ? (
        <div
          className='audio-preview'
          style={{ width: "250px", marginLeft: "auto" }}
        >
          <audio controls src={audioURL} />
        </div>
      ) : (
        <InputBase
          rows={4}
          multiline
          sx={{ ml: 1, flex: 1 }}
          placeholder={"Опишите бизнес процесс"}
          onChange={onTextChange}
          value={text}
        />
      )}
      <Divider sx={{ height: 28, m: 0.5 }} orientation='vertical' />
      {text.length ? (
        <IconButton
          color='primary'
          sx={{ p: "10px" }}
          aria-label='directions'
          onClick={() => {
            onClickSendBtn(text);
            setText("");
          }}
        >
          <ArrowUpward />
        </IconButton>
      ) : (
        <VoiceRecorder
          refetchQuery={refetchQuery}
          sendVoiceMutate={sendVoiceMutate}
        />
      )}
    </Paper>
  );
};
