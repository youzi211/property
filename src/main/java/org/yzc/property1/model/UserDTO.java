package org.yzc.property1.model;

import lombok.Data;

@Data
public class UserDTO {

    private String username; // 用户名
    private String password; // 密码
    private String roles; // 用户角色
    private String userAddress; // 用户地址

    // 默认构造函数
    public UserDTO() {
    }

    // 带参数的构造函数
    public UserDTO(String username, String password, String roles, String userAddress) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.userAddress = userAddress;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", userAddress='" + userAddress + '\'' +
                '}';
    }
}