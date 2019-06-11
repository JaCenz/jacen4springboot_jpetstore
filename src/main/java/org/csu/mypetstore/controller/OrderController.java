package org.csu.mypetstore.controller;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@SessionAttributes({"order","cart","account"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    //new 一个订单
    @GetMapping("/order/viewOrderForm")
    public String viewOrderForm(HttpSession session, Model model){

        Account account = (Account)session.getAttribute("account");
        Cart cart = (Cart)session.getAttribute("cart");

        if(cart == null){
            cart = new Cart();
            session.setAttribute("cart",cart);
        }

        if(account == null){
            return "account/signOnForm";
        }else{
            Order order = new Order();
            order.initOrder(account,cart);
            Date date = new Date();

//            order.setExpiryDate(new java.sql.Date(date.getTime()).toString());
            order.setOrderDate(new java.sql.Date(date.getTime()));

            List<String> cardType = new ArrayList<String>();
            cardType.add("Visa");
            cardType.add("MasterCard");
            cardType.add("American Express");

            model.addAttribute("order",order);
            session.setAttribute("order",order);
            session.setAttribute("creditCardTypes",cardType);
            session.setAttribute("account",account);

            return "order/newOrderForm";
        }
    }

    //前往确认订单页面,或者shippingForm
    @PostMapping("/order/viewConfirmOrder")
    public String viewConfirmOrder(Order order,HttpSession session,Model model){
        session.setAttribute("order",order);
        model.addAttribute("order",order);

        if(order.isShippingAddressRequired()){
            return "order/shippingForm";
        }else{
            return "order/confirmOrder";
        }
    }

    //确认shippingForm,前往确认订单
    @PostMapping("/order/confirmShip")
    public String confirmShip(@Valid Order order,HttpSession session){
        Order finalOrder = (Order) session.getAttribute("order");
        finalOrder.setShipAddress1(order.getShipAddress1());
        finalOrder.setShipAddress2(order.getShipAddress2());
        finalOrder.setShipToFirstName(order.getShipToFirstName());
        finalOrder.setShipToLastName(order.getShipToLastName());
        finalOrder.setShipCity(order.getShipCity());
        finalOrder.setShipCountry(order.getShipCountry());
        finalOrder.setShipState(order.getShipState());
        finalOrder.setShipZip(order.getShipZip());

        session.setAttribute("order",finalOrder);

        return "order/confirmOrder";
    }

    // 确认订单并生成最终的订单
    @GetMapping("/order/confirmOrder")
    public String confirmOrder(Order order,HttpSession session,Model model){

        orderService.insertOrder(order);

        // 重置购物车
        Cart cart = new Cart();
        model.addAttribute("cart",cart);
        session.setAttribute("cart",cart);


        return "order/viewOrder";
    }

    // 查询用户的订单历史记录
    @GetMapping("/order/checkOrder")
    public String checkOrder(HttpSession session,Model model){
        Account account = (Account) session.getAttribute("account");
        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());


        session.setAttribute("orderList",orderList);
        model.addAttribute("orderList",orderList);
        return "order/listOrders";

    }

    // 查看某一个具体订单
    @GetMapping("/order/viewOrder")
    public String viewOrder(@RequestParam("orderId") int orderId, Model model){
        Order order = orderService.getOrder(orderId);

        model.addAttribute("order",order);
        return "order/viewOrder";
    }
}
