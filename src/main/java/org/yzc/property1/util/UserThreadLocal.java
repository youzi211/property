package org.yzc.property1.util;


import org.yzc.property1.dao.entity.User;

/**
 * @author 86176
 */
public class UserThreadLocal {
    private UserThreadLocal(){

    }
    //线程变量隔离
    private static final ThreadLocal<User> LOCAL=new ThreadLocal<>();
    public static void put(User user){
        LOCAL.set(user);
    }
    public static User get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
