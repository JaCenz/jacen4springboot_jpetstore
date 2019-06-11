package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/catalog/main")
    public String viewMain(){
        return "catalog/main";
    }

    //如何从客户端页面获取值，用@RequestParam; 如何将服务端控制器中的值传给客户端页面，Model;Model是Theymleaf的用法
    //查看大类
    @GetMapping("/catalog/category")
    public String viewCategory(@RequestParam("categoryId") String categoryId, Model model){

        if(categoryId != null){
            Category category = catalogService.getCategory(categoryId);
            List<Product> productList = catalogService.getProductListByCategory(categoryId);
            model.addAttribute("category",category);
            model.addAttribute("productList",productList);
        }

        return "catalog/category";
    }

    //查看小类
    @GetMapping("/catalog/product")
    public String viewProduct(@RequestParam("productId")String productId, Model model, HttpSession session){
        if(productId != null){
            Product product = catalogService.getProduct(productId);
            List<Item> itemList = catalogService.getItemListByProduct(productId);

            session.setAttribute("product",product);
            model.addAttribute("itemList",itemList);
            model.addAttribute("product",product);
        }
        return "catalog/product";
    }

    //查看某一具体商品
    @GetMapping("/catalog/item")
    public String viewItem(@RequestParam("itemId") String itemId,Model model,HttpSession session){
        if(itemId != null){
            Item item = catalogService.getItem(itemId);

            Product product = (Product)session.getAttribute("product");
            model.addAttribute("product",product);
            model.addAttribute("item",item);
        }
        return "catalog/item";
    }

    //关键字搜索
    @GetMapping("/searchProduct")
    public String searchProduct(@RequestParam("keyword")String keyword,Model model){
        if(keyword.equals("")){
            return "catalog/main";
        }
        List<Product> productList = catalogService.searchProductList(keyword);
        model.addAttribute("productList",productList);

        return "catalog/searchProduct";
    }
}
