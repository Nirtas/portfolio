import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { createGlobalStyle } from "styled-components";
import reset from "styled-reset";
import { Outlet } from "react-router-dom";
const GlobalStyle = createGlobalStyle`
  ${reset}
  /* other styles */
`;
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: Infinity,
    },
  },
});

export const AppWrapper = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <GlobalStyle />
      <Outlet />
    </QueryClientProvider>
  );
};
