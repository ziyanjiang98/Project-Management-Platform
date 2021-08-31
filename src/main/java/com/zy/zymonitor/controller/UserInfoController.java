package com.zy.zymonitor.controller;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.User;
import com.zy.zymonitor.exception.PageException;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.exception.UserServiceException;
import com.zy.zymonitor.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/6/27
 */
@Controller
@RequestMapping("/user")
public class UserInfoController {
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public String login(){
        return "user/login";
    }

    @RequestMapping("/register")
    public String register(){
        return "user/register";
    }

    @RequestMapping("/change")
    public String changePassword(){
        return "user/change";
    }

    @RequestMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if(subject != null)
            subject.logout();
        return "redirect:/user/login";
    }

    @PostMapping("/checkLogin")
    public String checkLogin(@RequestParam String username,
                             @RequestParam String password,
                             final RedirectAttributes redirectAttributes){
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(username,password));
            return "redirect:/index";
        } catch (UnknownAccountException e) {
            System.out.println("用户名不存在");
            redirectAttributes.addFlashAttribute("msg","用户名不存在！");
        } catch (IncorrectCredentialsException e) {
            System.out.println("密码错误");
            redirectAttributes.addFlashAttribute("msg","密码错误！");
        }
        return "redirect:/user/login";
    }

    @PostMapping("/checkRegister")
    public String checkRegister(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String phone,
                                @RequestParam String pin,
                                Model model,
                                final RedirectAttributes redirectAttributes){
        try {
            userService.registerNewUser(username, password, phone, pin);
            redirectAttributes.addFlashAttribute("msg","注册成功！请登录！");
            return "redirect:/user/login";
        } catch (UserServiceException | RedisOutOfTimeException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/user/register";
    }

    @PostMapping("/changePassword")
    public String checkChangePassword(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String phone,
                                      @RequestParam String pin,
                                      Model model,
                                      final RedirectAttributes redirectAttributes){
        try {
            userService.changePassword(username, password, phone, pin);
            redirectAttributes.addFlashAttribute("msg","修改成功！请登录！");
            return "redirect:/user/login";
        } catch (UserServiceException | RedisOutOfTimeException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/user/change";
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/allUser")
    public ModelAndView allUsers(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                           @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws PageException {
        if(pageNum <=0 || pageSize <= 0)
            throw new PageException("查询路径不合法！");
        Map<String, Object> map = new HashMap<>();
        PageInfo<User> page = userService.getUserPage(pageNum, pageSize);
        if(page.getPages() == 0)
            page.setPages(page.getPages() + 1);
        map.put("page", page);
        ModelAndView modelAndView = new ModelAndView("user/allUser", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/editUser")
    public ModelAndView editUser(@RequestParam int id) throws UserServiceException {
        User user = userService.getUserById(id);
        if(user == null)
            throw new UserServiceException("目标用户不存在！");
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        ModelAndView modelAndView = new ModelAndView("user/editUser", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/checkEditUser")
    public String checkEditUser(@RequestParam String id,
                                @RequestParam String level,
                                final RedirectAttributes redirectAttributes){
        try {
            userService.updateUserLevel(id, level);
            redirectAttributes.addFlashAttribute("msg","修改权限成功！");
            return "redirect:/index";
        } catch (RedisOutOfTimeException | UserServiceException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/index";
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/deleteUser")
    public String deleteUser(@RequestParam String id,
                             final RedirectAttributes redirectAttributes){
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("msg","删除用户成功！");
            return "redirect:/index";
        } catch (RedisOutOfTimeException | UserServiceException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/index";
    }
}
