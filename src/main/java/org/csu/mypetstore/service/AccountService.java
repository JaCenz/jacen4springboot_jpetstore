package org.csu.mypetstore.service;
import org.csu.mypetstore.domain.Account;


public interface AccountService {

    // 根据用户名取得用户，注册的时候用到
    public Account getAccount(String username);

    // 根据用户名和密码取得用户，登录的时候用到
    public Account getAccount(Account account);

    // 插入一个新用户，注册的时候用到
    public void insertAccount(Account account);

    // 更新一个用户，修改用户用到
    public void updateAccount(Account account);
}
