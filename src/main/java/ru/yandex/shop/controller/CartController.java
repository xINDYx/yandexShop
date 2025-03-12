package ru.yandex.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.OrderService;
import ru.yandex.shop.service.ProductService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/cart/items")
    public String listProducts(Model model) {

        List<Cart> cart = cartService.findAll();
        boolean hasItems = !cart.isEmpty();

        long totalPrice = cart.stream()
                .mapToLong(item -> item.getPrice() * item.getCount())
                .sum();

        model.addAttribute("items", cart);
        model.addAttribute("total", totalPrice);
        model.addAttribute("hasItems", hasItems);

        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String countAction(
            @PathVariable("id") Long id,
            @RequestParam("action") String action,
            Model model) {

        try {
            switch (action) {
                case "minus":
                    if (cartService.findById(id).getCount() > 0) {
                        cartService.decreaseCountByOne(id);
                        break;
                    }
                    break;
                case "plus":
                    cartService.increaseCountByOne(id);
                    break;
                case "delete":
                    cartService.deleteById(id);
                    break;
                default:
                    return "redirect:/error";
            }
        } catch (Exception e) {
            return "redirect:/error";
        }

        return "redirect:/cart/items";
    }

    @PostMapping("/buy")
    public String buyItems(RedirectAttributes redirectAttrs) {
        List<Cart> cart = cartService.findAll();

        if (cart.isEmpty()) {
            return "redirect:/error";
        }

        Order order = orderService.createOrder(cart);

        cartService.clearCart();
        redirectAttrs.addAttribute("order", order);
        redirectAttrs.addFlashAttribute("newOrder", true);

        return "redirect:/orders/" + order.getId();
    }
}
