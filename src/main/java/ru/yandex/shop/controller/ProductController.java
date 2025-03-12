package ru.yandex.shop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.ProductService;

@Controller
@RequestMapping
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/main")
    public String listProducts(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "sort", defaultValue = "NO") String sort,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                               Model model) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, getSort(sort));
        Page<Product> productPage;

        if (search != null && !search.isEmpty()) {
            productPage = productService.findByTitleContaining(search, pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        model.addAttribute("items", productPage.getContent());
        model.addAttribute("paging", productPage);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("pageSize", pageSize);

        return "main";
    }

    @GetMapping("/items/{id}")
    public String getProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/error";
        }

        model.addAttribute("item", product);

        return "item";
    }

    @PostMapping("/main/items/{id}/plus")
    public String countAction(
            @PathVariable("id") Long id,
            @RequestParam("action") String action,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            Model model) {

        try {
            switch (action) {
                case "minus":
                    if (productService.findById(id).getCount() > 0) {
                        productService.decreaseCountByOne(id);
                        break;
                    }
                    break;
                case "plus":
                    productService.increaseCountByOne(id);
                    break;
                case "addToCart":
                    cartService.addToCart(id);
                    break;
                case "deleteFromCart":
                    cartService.deleteFromCart(id);
                    break;
                default:
                    return "redirect:/error";
            }
        } catch (Exception e) {
            return "redirect:/error";
        }

        return "redirect:/main?search=" + search + "&sort=" + sort + "&pageSize=" + pageSize + "&pageNumber=" + pageNumber;
    }

    @PostMapping("/items/{id}")
    public String handleCartActions(
            @PathVariable("id") Long id,
            @RequestParam("action") String action) {

        try {
            switch (action) {
                case "minus":
                    productService.decreaseCountByOne(id);
                    break;
                case "plus":
                    productService.increaseCountByOne(id);
                    break;
                case "addToCart":
                    cartService.addToCart(id);
                    break;
                case "deleteFromCart":
                    cartService.deleteFromCart(id);
                    break;
                default:
                    return "redirect:/error";
            }
        } catch (Exception e) {
            return "redirect:/error";
        }

        return "redirect:/items/{id}";
    }

    @GetMapping("/new")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/new";
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };
    }

}
