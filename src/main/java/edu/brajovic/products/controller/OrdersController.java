package edu.brajovic.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.brajovic.products.data.OrdersDataService;
import edu.brajovic.products.models.OrderModel;

@Controller
public class OrdersController {

    @Autowired
    private OrdersDataService ordersDataService;

    // 1) LIST ALL
    @GetMapping("/orders")
    public String showAllOrders(Model model) {
        model.addAttribute("title", "All Orders");
        model.addAttribute("orders", ordersDataService.getAll());
        return "allOrders";
    }

    // 2) SHOW ONE
    @GetMapping("/orders/showOrders/{id}")
    public String showOrders(@PathVariable int id, Model model) {
        model.addAttribute("title", "Order Details");
        model.addAttribute("order", ordersDataService.getById(id));
        return "showOrders";
    }

    // 3) EDIT FORM (GET)
    @GetMapping("/orders/editOrder/{id}")
    public String editOrderForm(@PathVariable int id, Model model) {
        model.addAttribute("title", "Edit Order");
        model.addAttribute("order", ordersDataService.getById(id));
        return "editOrder";
    }

    // 4) EDIT SUBMIT (POST)
    @PostMapping("/orders/processEditOrder")
    public String processEditOrder(@ModelAttribute("order") OrderModel order) {
        ordersDataService.update(order);
        return "redirect:/orders";
    }

    // 5) NEW FORM (GET)
    @GetMapping("/orders/newOrder")
    public String newOrderForm(Model model) {
        model.addAttribute("title", "New Order");
        model.addAttribute("order", new OrderModel());
        return "newOrder";
    }

    // 6) NEW SUBMIT (POST)
    @PostMapping("/orders/processNewOrder")
    public String processNewOrder(@ModelAttribute("order") OrderModel order) {
        // IMPORTANT: ensure "new" inserts don’t send a forced ID
        order.setId(0);
        ordersDataService.create(order);
        return "redirect:/orders";
    }

    // 7) DELETE
    @GetMapping("/orders/deleteOrder/{id}")
    public String deleteOrder(@PathVariable int id) {
        ordersDataService.deleteById(id);
        return "redirect:/orders";
    }
}
