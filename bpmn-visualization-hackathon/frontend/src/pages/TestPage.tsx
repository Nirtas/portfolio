import { useEffect, useState } from "react";
import { TextField } from "@mui/material";
import { ChatContainer } from "../components/shared/ChatContainer";
import { MainContent } from "../components/shared/MainContent";
import { Bpmn } from "../components";
import { BpmnWrapper } from "../components/ChatPage/BpmnWrapper";
// @ts-ignore
import BpmnJS from "bpmn-js";

export const TestPage = () => {
  const [bpmn, setBpmn] = useState("");

  useEffect(() => {
    fetch("http://127.0.0.1:5000/bpmn")
      .then((a) => a.json())
      .then((a) => {
        setBpmn(a.response.bpmn);
      });
    // generateXml().then((res) => setBpmn(res));
  }, []);

  return (
    <ChatContainer>
      {/* <Sidebar></Sidebar> */}

      <MainContent>
        <BpmnWrapper>
          <Bpmn bpmnDiagram={bpmn} />
        </BpmnWrapper>
        <TextField
          label="введите xml"
          multiline
          rows={25}
          onChange={(e) => setBpmn(e.target.value)}
          defaultValue={bpmn}
        />
      </MainContent>
    </ChatContainer>
  );
};
