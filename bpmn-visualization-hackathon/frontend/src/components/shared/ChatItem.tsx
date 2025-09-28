import { Message } from "../../enitities/Message";

interface IProps {
  message: Message;
}

export const ChatItem = ({ message }: IProps) => {
  return (
    <div>
      <div>{message.query}</div>
    </div>
  );
};
