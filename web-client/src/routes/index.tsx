import { createBrowserRouter, RouteObject } from "react-router-dom";
import { FriendsLayout, GroupLayout, HeaderLayout, MainLayout, MainNoRightSideBarLayout, SearchLayout, UserProfileLayout } from "../layouts";
import { About, Blocked, CreateGroup, CreateStory, Favourite, ForgotPassword, Friends, GroupAbout, GroupDetails, GroupMembers, GroupPhotos, Groups, Home, Login, Main, ModerateMember, NotFound, People, Photos, Post, PostDetails, PostPhotos, ProfileAbout, Requests, Result, Saved, Settings, SignUp, Suggests, ViewStory } from "../pages";
import { AuthProvider, ChatProvider, NotificationProvider, StoryWrapper } from "../components";
import { Provider } from "../components";
import FavouriteDetails from "../pages/favourite/FavouriteDetails";
import PostViolated from "@/pages/post/PostViolated";

const publicRoutes = [
    {
        path: '/login',
        element: <Login />
    },
    {
        path: '/sign-up',
        element: <SignUp />
    },
    {
        path: '/forgot-password',
        element: <ForgotPassword />
    },
    {
        path: '*',
        element: <NotFound />
    }
];

const privateRoutes = [
    {
        path: '/',
        element: <MainLayout />,
        children: [
            { index: true, element: <Home /> },
            { path: 'about', element: <About /> },
            { 
                path: 'favourites',  
                children: [
                    {
                        index: true,
                        element: <Favourite />,
                    },
                    {
                        path: ":id",
                        element: <FavouriteDetails />,
                    },
                ]
            },
        ]
    },
    {
        element: <MainNoRightSideBarLayout />,
        children: [
            { path: '/settings', element: <Settings /> },
            { path: '/saved', element: <Saved /> },
            { path: '/groups', element: <Groups /> },
            {
                path: '/group/:id',
                element: <GroupLayout />,
                children: [
                    { index: true, element: <GroupDetails /> },
                    { path: 'members', element: <GroupMembers /> },
                    { path: 'photos', element: <GroupPhotos /> },
                    { path: 'about', element: <GroupAbout /> },
                    { path: 'moderate', element: <ModerateMember /> },
                ]
            },
        ]
    },
    { 
        path: "/post",
        element: <HeaderLayout />,
        children: [
            {
                path: ":id",
                children: [
                    {
                        index: true,
                        element: <PostDetails />
                    },
                    {
                        path: "photos",
                        element: <PostPhotos />
                    }
                ]
            },
            {
                path: "violated/:id",
                element: <PostViolated />
            }
        ]
    },
    {
        path: '/groups/create',
        element: <CreateGroup />
    },
    {
        path: '/user/:id',
        element: <UserProfileLayout />,
        children: [
            { index: true, element: <Main /> },
            { path: 'about', element: <ProfileAbout /> },
            { path: 'friends', element: <Friends /> },
            { path: 'photos', element: <Photos /> },
        ]
    },
    {
        path: '/friends',
        element: <FriendsLayout />,
        children: [
            { path: 'requests', element: <Requests /> },
            { path: 'suggestions', element: <Suggests /> },
            { path: 'blocked', element: <Blocked /> }
        ]
    },
    {
        path: "/search",
        element: <SearchLayout />,
        children: [
            { path: 'top', element: <Result /> },
            { path: 'post', element: <Post /> },
            { path: 'people', element: <People /> },
        ]
    },
    {
        path: "/story",
        children: [
            { path: 'create', element: <CreateStory /> },
            { path: 'view/:id', element: <ViewStory /> },
        ]
    },
];

export const routes = createBrowserRouter([
    {
        element: <Provider />,
        children: [
            ...publicRoutes,
            {
                element: <AuthProvider />,
                children: [
                    {
                        element: <NotificationProvider />,
                        children: [
                            {
                                element: <ChatProvider />,
                                children: [{ element: <StoryWrapper />, children: [...privateRoutes] }]
                            }
                        ]
                    }
                ]
            }
        ]
    }
] as RouteObject[]);
