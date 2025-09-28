import { useEffect, useRef } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

import { ChatInputWrapper } from "../components/ChatPage/ChatInputWrapper";
import { useAsk } from "../queries/useAsk";
import {
  Loader,
  ChatContainer,
  MainContent,
  Sidebar,
  SearchInput,
  ErrorMessage,
} from "../components/shared";
import { useChatStore } from "../state/chat";
import { MessagesWrapper } from "../components/shared/MessagesWrapper";
import { ChatsList } from "../components/ChatPage/ChatsList";
import { Button } from "@mui/material";
import { Replay } from "@mui/icons-material";
import { useCreateChat } from "../queries/useCreateChat";
import { useGetChatHistory } from "../queries/useGetChatHistory";
import { ChatItem } from "../components/ChatItem";
import { useRefetchAsk } from "../queries/useRefetchAsk";
import { SttAnswer } from "../enitities/SttAnswer";
import { useSendVoice } from "../queries/useSendVoice";

export const Chat = () => {
  const [searchParams] = useSearchParams();
  const chatId = searchParams.get("chatId");
  const navigate = useNavigate();
  const { mutate: sendVoiceMutate, isPending: isPendingVoice } = useSendVoice();
  const { data: history, isPending: isPengindHistory } =
    useGetChatHistory(chatId);
  const { mutate, isPending, data: bpmn, isError } = useAsk();
  const { messages, addMessages, resetMessages } = useChatStore();
  const { mutate: createChat } = useCreateChat();
  const {
    mutate: sendTranscribedText,
    isPending: isPendingRefetch,
    data: bpmnFromStt,
  } = useRefetchAsk();
  const messagesEndRef = useRef<null | HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [history]);

  useEffect(() => {
    return () => {
      if (history?.history?.length) {
        resetMessages();
      }
    };
  }, [chatId]);

  const onClickSendBtn = (text: string) => {
    addMessages({ text, isAnswer: false });
    if (!chatId) {
      createChat(
        { name: text.slice(0, 25) },
        {
          onSuccess: (data) => {
            if (data?.chat_id) {
              navigate(`/chat?chatId=${data.chat_id}`);
              mutate({ text, chat_id: data.chat_id });
            }
          },
        }
      );
      return;
    }
    mutate({ text, chat_id: chatId });
  };

  const refetchQuery = ({ text, msg_id, audio_path, chatId }: SttAnswer) => {
    sendTranscribedText({ text, chat_id: chatId, msg_id });
    addMessages({
      text,
      audioUrl: audio_path,
      isAnswer: false,
    });
  };

  useEffect(() => {
    if (bpmnFromStt?.answer) {
      addMessages({ text: bpmnFromStt?.answer, isAnswer: true });
    }
  }, [bpmnFromStt]);

  useEffect(() => {
    if (bpmn?.answer) {
      addMessages({ text: bpmn?.answer, isAnswer: true });
    }
  }, [bpmn]);

  const onRefetchQuery = () => {
    if (chatId) {
      mutate({ chat_id: chatId, text: messages[messages?.length - 1]?.text });
    }
  };

  return (
    <ChatContainer>
      <Sidebar>
        <ChatsList />
      </Sidebar>
      <MainContent>
        <MessagesWrapper>
          {history?.history?.map(
            ({
              bpmn,
              message_text,
              is_user_sender,
              message_id,
              audio_path,
            }) => (
              <ChatItem
                key={message_id}
                text={is_user_sender ? message_text : bpmn}
                isAnswer={!is_user_sender}
                audioUrl={audio_path}
              />
            )
          )}
          {messages?.map(({ text, isAnswer, audioUrl }, i) => (
            <ChatItem
              key={i}
              text={text}
              isAnswer={isAnswer}
              audioUrl={audioUrl}
            />
          ))}
          {isError && !isPending ? (
            <div>
              <ErrorMessage>
                Произошла ошибка, попробуйте отправь запрос заново. Измените
                текст запроса, если повторная отрпавка не помогла. Подробнее
                опишите процесс (начало, конец и задачи)
              </ErrorMessage>
              <Button variant='text' onClick={onRefetchQuery}>
                <Replay /> Повторить запрос
              </Button>
            </div>
          ) : null}
          {(isPending ||
            isPengindHistory ||
            isPendingRefetch ||
            isPendingVoice) && <Loader />}
          <div ref={messagesEndRef} />
        </MessagesWrapper>
        <ChatInputWrapper>
          <SearchInput
            onClickSendBtn={onClickSendBtn}
            refetchQuery={refetchQuery}
            sendVoiceMutate={sendVoiceMutate}
          />
        </ChatInputWrapper>
      </MainContent>
    </ChatContainer>
  );
};
