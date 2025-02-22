import { InfiniteData, Query, useInfiniteQuery, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useContext, useEffect } from "react"
import { createPost, deletePost, getDetachedPost, getGroupPosts, getNewsFeed, getPost, getSavedPosts, getUserPosts, handleSavePost, sharePost, updatePost } from "../services";
import { AuthContext } from "../context";
import { format } from "date-fns";
import { BaseResponse, CreatePostRequest, PaginationResponse, Post, SharePostRequest, UpdatePostRequest } from "../types";
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import { getErrorRespMsg } from "../utils";

export const filterListPostQuery = (query: Query) => {
    const firstQueryKey = String(query.queryKey[0]);

    return ["group-posts", "news-feed", "user-posts"].includes(firstQueryKey);
}

export const useQueryNewsFeed = () => {
    const { accessTime } = useContext(AuthContext);

    const { fetchNextPage, hasNextPage, isFetchingNextPage, ...queryResult } = useInfiniteQuery({
        queryKey: ["news-feed"],
        queryFn: ({ pageParam }) => getNewsFeed(format(accessTime, "dd/MM/yyyy HH:mm:ss"), pageParam),
        refetchOnMount: false,
        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined
        },
        initialPageParam: 1
    });

    useEffect(() => {
        const handleScroll = () => {
            const scrollPosition = window.innerHeight + window.scrollY;
            const threshold = document.documentElement.scrollHeight - 500;

            if (scrollPosition >= threshold && hasNextPage && !isFetchingNextPage) {
                fetchNextPage();
            }
        };

        window.addEventListener("scroll", handleScroll);

        return () => window.removeEventListener("scroll", handleScroll);
    }, [fetchNextPage, hasNextPage, isFetchingNextPage]);

    return { ...queryResult, isFetchingNextPage }
}

export const useQueryUserPosts = (userId: string) => {
    const { fetchNextPage, hasNextPage, isFetchingNextPage, ...queryResult } = useInfiniteQuery({
        queryKey: ["user-posts", userId],
        queryFn: ({ pageParam }) => getUserPosts(userId, pageParam),
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined
        },
        initialPageParam: 1
    });

    useEffect(() => {
        const handleScroll = () => {
            const scrollPosition = window.innerHeight + window.scrollY;
            const threshold = document.documentElement.scrollHeight - 500;

            if (scrollPosition >= threshold && hasNextPage && !isFetchingNextPage) {
                fetchNextPage();
            }
        };

        window.addEventListener("scroll", handleScroll);

        return () => window.removeEventListener("scroll", handleScroll);
    }, [fetchNextPage, hasNextPage, isFetchingNextPage]);

    return { ...queryResult, isFetchingNextPage }
}

export const useQueryGroupPosts = (groupId?: string) => {
    const { fetchNextPage, hasNextPage, isFetchingNextPage, ...queryResult } = useInfiniteQuery({
        queryKey: ["group-posts", groupId],
        queryFn: ({ pageParam }) => getGroupPosts(groupId!, pageParam),
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined
        },
        initialPageParam: 1,
        enabled: !!groupId
    });

    useEffect(() => {
        const handleScroll = () => {
            const scrollPosition = window.innerHeight + window.scrollY;
            const threshold = document.documentElement.scrollHeight - 500;

            if (scrollPosition >= threshold && hasNextPage && !isFetchingNextPage) {
                fetchNextPage();
            }
        };

        window.addEventListener("scroll", handleScroll);

        return () => window.removeEventListener("scroll", handleScroll);
    }, [fetchNextPage, hasNextPage, isFetchingNextPage]);

    return { ...queryResult, isFetchingNextPage }
}


export const useCreatePost = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: CreatePostRequest) => createPost(payload),
        onMutate: (variables) => {
            return { groupId: variables.groupId, type: variables.type };
        },
        onSuccess: (data, _) => {
            queryClient.setQueriesData<any>({
                predicate: filterListPostQuery
            }, (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => ({
                ...oldData,
                pages: [{
                    ...oldData.pages[0],
                    data: {
                        ...oldData.pages[0].data,
                        data: [data.data, ...oldData.pages[0].data.data]
                    }
                }, ...oldData.pages.slice(1, oldData.pages.length)]
            }));

        }
    });
}

export const useUpdatePost = (groupId?: string, userId?: string) => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: UpdatePostRequest) => updatePost(payload),
        onSuccess: (data) => {
            const postId = data.data.id;
            const updatedPost = data.data;

            toast.success(`Đã cập nhật bài viết ${postId}`);

            const updateFilterPostQuery = (query: Query) => {
                const [key, id] = query.queryKey || [];
            
                return (
                    (key === "user-posts" && id === userId) ||
                    (key === "group-posts" && id === groupId) ||
                    key === "news-feed"
                );
            };

            queryClient.setQueriesData<any>({
                predicate: updateFilterPostQuery
            }, (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => ({
                ...oldData,
                pages: oldData.pages.map((page) => ({
                    ...page,
                    data: {
                        ...page.data,
                        data: page.data.data.map((post) => {
                            if(post.id === updatedPost.id) 
                                return ({
                                    ...post,
                                    content: updatedPost.content,
                                    privacy: updatedPost.privacy,
                                    medias: updatedPost.medias,
                                    background: updatedPost.background,
                                    taggedUsers: updatedPost.taggedUsers
                                } as Post);

                            return post;
                        })
                    }
                }))
            }))
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useSharePost = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: SharePostRequest) => sharePost(payload),
        onMutate: (data) => {
            const oldData = queryClient.getQueriesData({
                predicate: filterListPostQuery
            });

            queryClient.setQueriesData<any>({
                predicate: filterListPostQuery
            }, (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => ({
                ...oldData,
                pages: oldData.pages.map((page) => ({
                    ...page,
                    data: {
                        ...page.data,
                        data: page.data.data.map((post) => {
                            if (post.id === data.postId)
                                return {
                                    ...post,
                                    noShared: post.noShared + 1
                                }

                            return post;
                        })
                    }
                }))
            }))

            return { oldData }
        },
        onSuccess: (data) => {
            toast.success(data.message);
        },
        onError: (error: AxiosError, _, context) => {
            if (context?.oldData) {
                context.oldData.forEach(([queryKey, queryData]) => {
                    queryClient.setQueryData(queryKey, queryData);
                })
            }

            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useQueryPost = (id?: string) => {
    return useQuery({
        queryKey: ["post", id],
        queryFn: () => getPost(id!),
        enabled: !!id,
        retry: false
    });
}

export const useQueryDetachedPost = (id?: string) => {
    return useQuery({
        queryKey: ["detached-post", id],
        queryFn: () => getDetachedPost(id!),
        enabled: !!id,
        retry: false
    });
}

export const useHandleSavePost = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (postId: string) => handleSavePost(postId),
        onSuccess: (data) => {
            queryClient.invalidateQueries({
                queryKey: ["saved-posts"]
            });
            toast.success(data.message);
        },
        onMutate: async (postId: string) => {
            await queryClient.cancelQueries({
                predicate: filterListPostQuery,
            });

            const oldData = queryClient.getQueriesData<InfiniteData<BaseResponse<PaginationResponse<Post>>>>({
                predicate: filterListPostQuery
            });

            queryClient.setQueriesData<any>({
                predicate: filterListPostQuery
            }, (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => ({
                ...oldData,
                pages: oldData.pages.map((page) => ({
                    ...page,
                    data: {
                        ...page.data,
                        data: page.data.data.map((post) => post.id === postId ? {
                            ...post,
                            saved: !post.saved
                        } : post)
                    }
                }))
            }));

            return { oldData };
        },
        onError: (error: AxiosError, _, context) => {
            toast.error(getErrorRespMsg(error));

            if (context?.oldData) {
                context.oldData.forEach(([queryKey, data]) => {
                    queryClient.setQueryData<InfiniteData<BaseResponse<PaginationResponse<Post>>>>(queryKey, data);
                });
            }
        }
    })
}

export const useQuerySavedPosts = () => {
    return useQuery({
        queryKey: ["saved-posts"],
        queryFn: () => getSavedPosts()
    });
}

export const useDeletePost = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (postId: string) => deletePost(postId),
        onSuccess: (data) => {
            toast.success(data.message);
        },
        onMutate: async (postId: string) => {
            await queryClient.cancelQueries({
                predicate: filterListPostQuery,
            });

            const oldData = queryClient.getQueriesData<InfiniteData<BaseResponse<PaginationResponse<Post>>>>({
                predicate: filterListPostQuery
            });

            queryClient.setQueriesData<any>({
                predicate: filterListPostQuery
            }, (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => ({
                ...oldData,
                pages: oldData.pages.map((page) => {
                    return {
                        ...page,
                        data: {
                            ...page.data,
                            data: page.data.data.filter((post) => post.id !== postId)
                        }
                    }
                })
            }));

            return { oldData };
        },
        onError: (error: AxiosError, _, context) => {
            if (context?.oldData) {
                context.oldData.forEach(([queryKey, data]) => {
                    queryClient.setQueryData<InfiniteData<BaseResponse<PaginationResponse<Post>>>>(queryKey, data);
                })
            }

            toast.error(getErrorRespMsg(error));
        }
    })
}

export const usePreprocessCreatePayload = () => {
    
}