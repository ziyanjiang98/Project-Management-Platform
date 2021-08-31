package com.zy.zymonitor.controller;

import com.github.pagehelper.PageInfo;
import com.zy.zymonitor.bean.ItemResourceChange;
import com.zy.zymonitor.bean.Resource;
import com.zy.zymonitor.exception.PageException;
import com.zy.zymonitor.exception.RedisOutOfTimeException;
import com.zy.zymonitor.service.ResourceService;
import com.zy.zymonitor.bean.Item;
import com.zy.zymonitor.exception.ItemException;
import com.zy.zymonitor.service.ItemService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @author Ziyan_Jiang
 * @date 2021/7/11
 */
@Controller
@RequestMapping("/item")
public class ItemController {
    @Autowired
    ResourceService resourceService;

    @Autowired
    ItemService itemService;

    @RequiresRoles(value={"5"})
    @RequestMapping("/addItem")
    public ModelAndView addItem(){
        List<Resource> resources = resourceService.getAllResources();
        HashMap<String, Object> map = new HashMap<>();
        map.put("resources", resources);
        ModelAndView modelAndView = new ModelAndView("item/addItem", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"})
    @RequestMapping("/checkAddItem")
    public String checkAddItem(HttpServletRequest request,
                               final RedirectAttributes redirectAttributes){
        Map<String, String[]> map = request.getParameterMap();
        try {
            itemService.createItem(map);
            redirectAttributes.addFlashAttribute("msg","创建项目成功！");
            return "redirect:/index";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:/item/addItem";
    }

    @RequiresRoles(value={"1","2","3","4","5"},logical= Logical.OR)
    @RequestMapping("/allItems")
    public ModelAndView item(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws PageException {
        if(pageNum <=0 || pageSize <= 0)
            throw new PageException("查询路径不合法！");
        Map<String, Object> map = new HashMap<>();
        PageInfo<Item> page = itemService.getItemPage(pageNum, pageSize);
        if(page.getPages() == 0)
            page.setPages(page.getPages() + 1);
        map.put("page", page);
        ModelAndView modelAndView = new ModelAndView("item/allItems", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"},logical= Logical.OR)
    @RequestMapping("/editItem")
    public ModelAndView editItem(@RequestParam(value = "id") String id) throws ItemException {
        Item item = itemService.getItemById(id);
        if(item == null)
            throw new ItemException("目标项目不存在！");
        Map<String, Object> map = new HashMap<>();
        map.put("item",item);
        ModelAndView modelAndView = new ModelAndView("item/editItem", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"},logical= Logical.OR)
    @RequestMapping("/checkEditItem")
    public String checkEditItem(@RequestParam(value = "itemId") String itemId,
                                @RequestParam(value = "name") String name,
                                @RequestParam(value = "manager") String manager,
                                @RequestParam(value = "detail") String detail,
                                final RedirectAttributes redirectAttributes) throws ItemException, RedisOutOfTimeException {
        try {
            itemService.updateItemInfo(itemId, name, manager, detail);
            redirectAttributes.addFlashAttribute("msg", "修改项目信息成功！");
            return "redirect:/index";
        } catch (ItemException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:/item/editItem?id=" + itemId;
    }

    @RequiresRoles(value={"3","4","5"},logical= Logical.OR)
    @RequestMapping("/checkChangeItemResource")
    public String changeItemResource(@RequestParam(value = "itemId") String itemId,
                                     @RequestParam(value = "resourceId") String resourceId,
                                     @RequestParam(value = "change") String change,
                                     @RequestParam(value = "detail") String detail,
                                     final RedirectAttributes redirectAttributes) throws RedisOutOfTimeException {
        try {
            itemService.updateItemResource(itemId, resourceId, Integer.parseInt(change), detail);
            redirectAttributes.addFlashAttribute("msg", "修改项目信息成功！");
            return "redirect:/index";
        } catch (ItemException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:/item/editItem";
    }

    @RequiresRoles(value={"2","3","4","5"},logical= Logical.OR)
    @RequestMapping("/allChanges")
    public ModelAndView ItemResourceChanges(@RequestParam(value = "id") String itemId,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                            final RedirectAttributes redirectAttributes) throws ItemException, PageException {
        Item item = itemService.getItemById(itemId);
        if(item == null)
            throw new ItemException("目标项目不存在！");
        if(pageNum <=0 || pageSize <= 0)
            throw new PageException("查询路径不合法！");
        Map<String, Object> map = new HashMap<>();
        PageInfo<ItemResourceChange> page = itemService.getItemResourceChangePageById(itemId, pageNum, pageSize);
        if(page.getPages() == 0)
            page.setPages(page.getPages() + 1);
        for(int i = 0; i < page.getList().size(); i++){
            for(int j = 0; j < item.getResources().size(); j++){
                if(page.getList().get(i).getResourceId().equals(item.getResources().get(j).getId())){
                    page.getList().get(i).setResource(item.getResources().get(j));
                    break;
                }
            }
        }
        map.put("page", page);
        map.put("item", item);
        ModelAndView modelAndView = new ModelAndView("item/allChanges", map);
        return modelAndView;
    }

    @RequiresRoles(value={"5"},logical= Logical.OR)
    @RequestMapping("/deleteItem")
    public String checkDeleteItem(@RequestParam(value = "id") String itemId,
                                  final RedirectAttributes redirectAttributes) throws RedisOutOfTimeException {
        try {
            itemService.deleteItemInfo(itemId);
        } catch (ItemException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/item/allItems";
        }
        redirectAttributes.addFlashAttribute("msg", "删除项目成功！");
        return "redirect:/index";
    }
}
