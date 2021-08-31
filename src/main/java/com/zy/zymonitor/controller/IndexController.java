package com.zy.zymonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/2
 */
@Controller
public class IndexController {
    @RequestMapping(value={"/index","/"})
    public String register(){
        return "index";
    }
}
