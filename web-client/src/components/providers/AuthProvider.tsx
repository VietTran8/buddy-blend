import { FC, useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { BaseUser } from "../../types";
import { AuthContext } from "../../context";
import { useGetUserProfile, useLocalStorage } from "../../hooks";
import { Spin } from "antd";
import { ACCESS_TOKEN_PREFIX } from "../../constants";

interface IProps { }

const AuthProvider: FC<IProps> = () => {
    const [accessToken] = useLocalStorage<string | undefined>(ACCESS_TOKEN_PREFIX, undefined);
    const [user, setUser] = useState<BaseUser | null>(null);
    const [accessTime, setAccessTime] = useState<Date>();
    const { data, isLoading } = useGetUserProfile(accessToken);
    const [isAuthorized, setIsAuthorized] = useState<boolean>(false);
    const navigate = useNavigate();

    const fetchedUser = data?.data;

    useEffect(() => {
        if (!accessToken) {
            navigate("/login");
            return;
        }

        if (fetchedUser) {
            setUser({
                id: fetchedUser.id,
                email: fetchedUser.email,
                profilePicture: fetchedUser.profilePicture,
                userFullName: fetchedUser.userFullName,
                online: fetchedUser.online
            });

            setAccessTime(new Date);

            setIsAuthorized(true);
        }

    }, [fetchedUser]);

    return (
        <>
            {isLoading && !isAuthorized ? (
                <div className="bg-white w-full h-dvh flex justify-center items-center">
                    <Spin size="large" />
                </div>
            ) : (
                <AuthContext.Provider value={{ user, setUser, accessTime: accessTime || new Date() }}>
                    <Outlet />
                </AuthContext.Provider>
            )}
        </>
    );
};

export default AuthProvider;
