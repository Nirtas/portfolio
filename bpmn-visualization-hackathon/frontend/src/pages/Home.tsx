import { Button } from "@mui/material";
import styled from "styled-components";

const HomePageWrapper = styled.div`
  display: grid;
  place-items: cemter;
  height: 100vh;
`;

export const Home = () => {
  const onClickCreateChat = () => {
    // mutate({ text });
  };

  return (
    <HomePageWrapper>
      <Button variant="text" onClick={onClickCreateChat}>
        Создать чат
      </Button>
    </HomePageWrapper>
  );
};
