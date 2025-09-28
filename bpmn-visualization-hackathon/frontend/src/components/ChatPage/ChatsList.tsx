import { useGetChatList } from "../../queries/useGetChatList";
import { Button, MenuItem, MenuList, Typography } from "@mui/material";
import { Add } from "@mui/icons-material";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useEffect } from "react";

export const ChatsList = () => {
  const { data, refetch } = useGetChatList();
  const [searchParams] = useSearchParams();
  const chatId = searchParams.get("chatId");
  const navigate = useNavigate();

  const handleClickNewChat = () => {
    navigate(`/chat`);
  };

  useEffect(() => {
    refetch();
  }, [chatId]);

  return (
    <MenuList>
      <Button
        style={{ marginLeft: "10px", marginBottom: "32px", marginTop: "32px" }}
        onClick={handleClickNewChat}
      >
        Новый чат <Add />
      </Button>
      {data?.chats?.map(({ chat_id: id, chat_name: name }) => (
        <a
          href={`/chat?chatId=${id}`}
          style={{ color: "black", textDecoration: "none" }}
        >
          <MenuItem selected={id === chatId} key={id}>
            <Typography variant='inherit' noWrap>
              {name}
            </Typography>
          </MenuItem>
        </a>
      ))}
    </MenuList>
  );
};
