import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { FC } from "react";
import { Outlet } from "react-router-dom";

interface IProps { };

const queryClient = new QueryClient()

const Provider: FC<IProps> = ({ }) => {
    return (
        <QueryClientProvider client={queryClient}>
            <Outlet />
        </QueryClientProvider>
    )
};

export default Provider;