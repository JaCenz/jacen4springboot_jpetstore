package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Iterator;

@Controller
public class CartController {

    @Autowired
    private CatalogService catalogService;

    //向购物车添加商品
    @GetMapping("cart/addToCart")
    public String addItemToCart(@RequestParam("workingItemId") String workingItemId, HttpSession session, Model model){
        if(workingItemId != null){
            Cart cart = (Cart)session.getAttribute("cart");

            //如果购物车为空则新建一个购物车
            if(cart == null){
                cart = new Cart();
            }

            if(cart.containsItemId(workingItemId)){
                cart.incrementQuantityByItemId(workingItemId);
            }else{
                boolean isInStock = catalogService.isItemInStock(workingItemId);
                Item item = catalogService.getItem(workingItemId);
                cart.addItem(item,isInStock);
            }
            session.setAttribute("cart",cart);
            model.addAttribute("cart",cart);
        }

        return "cart/cart";
    }

    //查看购物车
    @GetMapping("/cart/viewCart")
    public String viewCart(HttpSession session,Model model){
        Cart cart = (Cart)session.getAttribute("cart");

        if(cart == null){
            cart = new Cart();
        }

        session.setAttribute("cart",cart);
        model.addAttribute("cart",cart);

        return "cart/cart";
    }

    //从购物车里删除一个物品
    @GetMapping("/cart/removeItemFromCart")
    public String removeItemFromCart(@RequestParam("workingItemId") String workingItemId,HttpSession session,Model model){
        if(workingItemId != null){
            Cart cart = (Cart)session.getAttribute("cart");
            Item item = cart.removeItemById(workingItemId);

            if(item == null){
                model.addAttribute("message","Attempted to remove null CartItem from Cart.");
                return "error";
            }else {
                session.setAttribute("cart",cart);
            }
        }
        return "cart/cart";
    }

   /* //更新购物车
    @PostMapping("/cart/updateCart")
    public String updateCart(HttpSession session, HttpServletRequest request){
        Cart cart = (Cart)session.getAttribute("cart");

        for(CartItem cartItem:cart.getCartItemList())
        {
            String itemId = cartItem.getItem().getItemId();
            Integer quantity = Integer.parseInt(request.getParameter(itemId));
            if(quantity > 0&& quantity <= 10000){
                cart.setInstockByItemId(itemId,true);
                cart.setQuantityByItemId(itemId,quantity);
            }
            else if (quantity > 10000)
            {
                cart.setInstockByItemId(itemId,false);
                cart.setQuantityByItemId(itemId,quantity);
            }
            else{
                cart.removeItemById(itemId);
            }

            session.setAttribute("cart",cart);
        }

        return "cart/cart";
    }
*/
    // AJAX 购物车刷新
    @GetMapping("/cart/updateCart")
    public void updateCart(@RequestParam("itemId") String itemId, @RequestParam("quantity")String quantity, HttpServletResponse response , HttpSession session){
//        logger.debug("log..."); // 输出DEBUG级别的日志
        Cart cart = (Cart)session.getAttribute("cart");
        Iterator<CartItem> cartItemIterator = cart.getAllCartItems();

        try {
            response.setContentType("text/xml;charset=utf-8");
            PrintWriter out = response.getWriter();
            response.setHeader("Cache-Control", "no-cache");
            out.println("<?xml version='1.0' encoding='"+"utf-8"+"' ?>");

            while( cartItemIterator.hasNext()){
                CartItem cartItem = (CartItem) cartItemIterator.next();
                if(itemId.equals(cartItem.getItem().getItemId())){
                    try {
                        Integer finalQuantity = Integer.parseInt(quantity);
                        cart.setQuantityByItemId(itemId, finalQuantity);

//                        System.out.println(itemId);
                        if (finalQuantity < 1) {
                            cartItemIterator.remove();
                            out.println("<Msg>"+itemId+"?0?"+cart.getSubTotal()+"</Msg>");
                        }else{
//                            System.out.println(cartItem.getTotal());
                            out.println("<Msg>"+itemId+"?"+cartItem.getTotal()+"?"+cart.getSubTotal()+"</Msg>");
//                            System.out.println(cart.getSubTotal());
                        }
                        out.flush();
                        out.close();
                        break;
                    } catch (Exception e) {
                        //ignore parse exceptions on purpose
                        e.printStackTrace();
                    }

                }

            }

            session.setAttribute("cart",cart);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
