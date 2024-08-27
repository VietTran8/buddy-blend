package vn.edu.tdtu.buddyblend.callbacks;

public interface ActionCallback<T> {
    default void onSuccess(T t){}
    default void onSuccess(){}
    default void onFailure(){}
    default void onFailure(T t){}
    default void onFailure(String message){}
    default void onFailure(Integer status, String message){}
    default void onError(Exception e){}
}
