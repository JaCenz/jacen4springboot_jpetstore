package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes({"account"})
public class AccountController {
    @Autowired
    private AccountService accountService;

    //跳往登陆页面
    @GetMapping("/account/viewSignOnForm")
    public String viewSignOnForm(){
        return "account/signOnForm";
    }

    //进行登录
   @PostMapping("/account/login")
    public String login(@Valid Account account, HttpSession session, Model model, BindingResult bindingResult){
       if(bindingResult.hasErrors()){
           return bindingResult.getFieldError().getDefaultMessage();
       }

       account = accountService.getAccount(account);
       if(account != null){
           session.setAttribute("account",account);
           model.addAttribute("account",account);
           return "catalog/main";
       }else{
           model.addAttribute("account",account);

           return "account/signOnForm";
       }
   }

   //登出
    @GetMapping("/account/signOut")
    public String signOut(Model model,HttpSession session){
        Account account = null;

        session.setAttribute("account",account);
        model.addAttribute("account",account);
        return "catalog/main";
    }

    //跳往注册页面
    @GetMapping("/account/viewRegisterForm")
    public String viewRegister(Model model,HttpSession session){
        List<String> languages = new ArrayList<String>();
        languages.add("English");
        languages.add("中文");
        session.setAttribute("languages",languages);

        List<String> categories = new ArrayList<>();
        categories.add("FISH");
        categories.add("DOGS");
        categories.add("REPTILES");
        categories.add("CATS");
        categories.add("BIRDS");
        session.setAttribute("categories",categories);

        return "account/newAccountForm";
    }

    //进行注册
    @PostMapping("/account/register")
    public String register(Account account,Model model){

        if(!(account.getUsername().equals("") || account.getPassword().equals(""))){
            if(account.getPassword().equals(account.getRepeatedPassword())){
                Account temp = accountService.getAccount(account.getUsername());

                // 若用户名可创建则注册用户
                if (temp == null) {
                    accountService.insertAccount(account);
                    // 这里要清空model的account?
                    Account t = null;
                    model.addAttribute("account",t);
                    return "account/signOnForm";
                }
            }

        }

        // 这里要清空model的account?
        Account t = null;
        model.addAttribute("account",t);
        return "account/register";
    }

    //进入编辑用户信息界面
    @GetMapping("/account/viewEditAccountForm")
    public String viewEditAccount(HttpSession session,Model model){

        List<String> languages = new ArrayList<String>();
        languages.add("english");
        languages.add("japanese");
        session.setAttribute("languages",languages);

        List<String> categories = new ArrayList<>();
        categories.add("FISH");
        categories.add("DOGS");
        categories.add("REPTILES");
        categories.add("CATS");
        categories.add("BIRDS");
        session.setAttribute("categories",categories);

        return "account/editAccountForm";
    }

    //确认修改账户
    @PostMapping("/account/confirmEdit")
    public String confirmEdit(@Valid Account account,Model model,HttpSession session,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return bindingResult.getFieldError().getDefaultMessage();
        }

        if(!account.getPassword().equals("") && !account.getRepeatedPassword().equals("") && account.getPassword().equals(account.getRepeatedPassword())){
            accountService.updateAccount(account);
            session.setAttribute("account",account);
            model.addAttribute("account",account);

            return "catalog/main";
        }else{
            return "account/editAccount";
        }
    }
}
