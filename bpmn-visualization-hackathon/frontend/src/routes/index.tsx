import { createBrowserRouter } from "react-router";
import { Chat } from "../pages/Chat";
import { AppWrapper } from "../components/AppWrapper";
import { TestPage } from "../pages/TestPage";
import { Home } from "../pages/Home";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: AppWrapper,
    children: [
      {
        path: "/chat",
        Component: Chat,
      },
      {
        path: "/home",
        Component: Home,
      },
      {
        path: "/test",
        Component: TestPage,
      },
    ],
  },
]);
