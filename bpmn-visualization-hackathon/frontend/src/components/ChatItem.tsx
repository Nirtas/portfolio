import React from "react";
import { ChatUserQuery } from "./shared";
import { BpmnWrapper } from "./ChatPage/BpmnWrapper";
import { Bpmn } from "./BPMN";
import { Button } from "@mui/material";
import { download } from "../utils";

type Props = {
  text: string;
  isAnswer: boolean;
  audioUrl?: string;
};

export const ChatItem = ({ text, isAnswer, audioUrl }: Props) => {
  return (
    <React.Fragment>
      {!isAnswer ? (
        <ChatUserQuery>
          {audioUrl && (
            <div
              className='audio-preview'
              style={{ width: "250px", marginLeft: "auto" }}
            >
              <audio controls src={`api/download_audio/${audioUrl}`} />
            </div>
          )}
          {text}
        </ChatUserQuery>
      ) : (
        <>
          <ChatUserQuery
            style={{
              alignSelf: "flex-start",
              backgroundColor: "#CDD0F8",
            }}
          >
            Сгенерированая BPMN диаграмма по вашему запросу:
          </ChatUserQuery>
          <BpmnWrapper>
            <Bpmn bpmnDiagram={text} />
            <Button onClick={() => download("file.bpmn", text)}>
              скачать .bpmn
            </Button>
          </BpmnWrapper>
        </>
      )}
    </React.Fragment>
  );
};
