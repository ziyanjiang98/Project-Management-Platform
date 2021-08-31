package com.zy.zymonitor.controller;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.Resource;
import com.zy.zymonitor.exception.PageException;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.exception.ResourceException;
import com.zy.zymonitor.service.ResourceService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/3
 */
@Controller
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    //返回当前库内资源种类
    @RequiresRoles(value={"1","2","3","4","5"},logical= Logical.OR)
    @RequestMapping("/allResource")
    public ModelAndView resource(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws PageException {
        if(pageNum <=0 || pageSize <= 0)
            throw new PageException("查询路径不合法！");
        Map<String, Object> map = new HashMap<>();
        PageInfo<Resource> page = resourceService.getResourcePage(pageNum, pageSize);
        map.put("page", page);
        ModelAndView modelAndView = new ModelAndView("resource/allResource", map);
        return modelAndView;
    }

    //返回添加资源页面
    @RequiresRoles(value={"3","4","5"},logical=Logical.OR)
    @RequestMapping("/addResource")
    public String addResource(){
        return "resource/addResource";
    }

    //验证添加资源信息
    @RequiresRoles(value={"3","4","5"},logical=Logical.OR)
    @RequestMapping("/checkAddResource")
    public String checkAddResource(@RequestParam String name,
                                   @RequestParam String unit,
                                   @RequestParam String detail,
                                   Model model,
                                   final RedirectAttributes redirectAttributes){
        try {
            resourceService.addNewResource(name, unit, detail);
            redirectAttributes.addFlashAttribute("msg","添加材料成功！");
            return "redirect:/index";
        } catch (ResourceException | RedisOutOfTimeException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/index";
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/editResource")
    public ModelAndView editResource(@RequestParam int id) throws ResourceException {
        Resource resource = resourceService.getResource(id);
        if(resource == null)
            throw new ResourceException("目标材料不存在！");
        HashMap<String, Object> map = new HashMap<>();
        map.put("resource", resource);
        ModelAndView modelAndView = new ModelAndView("resource/editResource", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/checkEditResource")
    public String checkEditResource(@RequestParam String id,
                                    @RequestParam String name,
                                    @RequestParam String unit,
                                    @RequestParam String detail,
                                    final RedirectAttributes redirectAttributes){
        try {
            resourceService.editResource(id, name, unit, detail);
            redirectAttributes.addFlashAttribute("msg","修改材料成功！");
            return "redirect:/index";
        } catch (RedisOutOfTimeException | ResourceException e) {
            redirectAttributes.addFlashAttribute("msg",e.getMessage());
        }
        return "redirect:/index";
    }
}
