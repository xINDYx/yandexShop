package ru.yandex.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/main")
    public Mono<ResponseEntity<Map<String, Object>>> listProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, getSort(sort));

        Mono<List<Product>> productListMono;

        if (search != null && !search.isEmpty()) {
            productListMono = productService.findByTitleContaining(search, pageable)
                    .collectList();
        } else {
            productListMono = productService.findAll()
                    .collectList();
        }

        return productListMono
                .map(productList -> {
                    long totalItems = productList.size();
                    int totalPages = (int) Math.ceil((double) totalItems / pageSize);

                    Page<Product> productPage = new PageImpl<>(productList, pageable, totalItems);

                    Map<String, Object> response = new HashMap<>();
                    response.put("items", productPage.getContent());
                    response.put("paging", productPage);
                    response.put("search", search);
                    response.put("sort", sort);
                    response.put("pageSize", pageSize);

                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/items/{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable("id") Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/main/items/{id}/minus")
    public Mono<ResponseEntity<Void>> decreaseItemCount(@PathVariable Long id) {
        return productService.findById(id)
                .filter(product -> product.getCount() > 0)
                .flatMap(product -> productService.decreaseCountByOne(id))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/main/items/{id}/plus")
    public Mono<ResponseEntity<Void>> increaseItemCount(@PathVariable Long id) {
        return productService.increaseCountByOne(id)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/main/items/{id}/addToCart")
    public Mono<ResponseEntity<Void>> addToCart(@PathVariable Long id) {
        return cartService.addToCart(id)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/main/items/{id}/deleteFromCart")
    public Mono<ResponseEntity<Void>> deleteFromCart(@PathVariable Long id) {
        return cartService.deleteFromCart(id)
                .thenReturn(ResponseEntity.ok().build());
    }

//    @PostMapping("/items/{id}")
//    public Mono<ResponseEntity<Void>> handleCartActions(
//            @PathVariable("id") Long id,
//            @RequestParam("action") String action) {
//
//        switch (action) {
//            case "minus":
//                productService.decreaseCountByOne(id).subscribe();
//                break;
//            case "plus":
//                productService.increaseCountByOne(id).subscribe();
//                break;
//            case "addToCart":
//                cartService.addToCart(id).subscribe();
//                break;
//            case "deleteFromCart":
//                cartService.deleteFromCart(id).subscribe();
//                break;
//            default:
//                return Mono.error(new IllegalArgumentException("Invalid action"));
//        }
//
//        return Mono.just(ResponseEntity.ok().build());
//    }

    @PostMapping("/items/{id}/minus")
    public Mono<ResponseEntity<Void>> decreaseProductCount(@PathVariable Long id) {
        return productService.findById(id)
                .filter(product -> product.getCount() > 0)
                .flatMap(product -> productService.decreaseCountByOne(id))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/items/{id}/plus")
    public Mono<ResponseEntity<Void>> increaseProductCount(@PathVariable Long id) {
        return productService.increaseCountByOne(id)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/items/{id}/addToCart")
    public Mono<ResponseEntity<Void>> addProductToCart(@PathVariable Long id) {
        return cartService.addToCart(id)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/items/{id}/deleteFromCart")
    public Mono<ResponseEntity<Void>> deleteProductFromCart(@PathVariable Long id) {
        return cartService.deleteFromCart(id)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/save")
    public Mono<ResponseEntity<Void>> saveProduct(@RequestBody Product product) {
        return productService.save(product)
                .thenReturn(ResponseEntity.ok().build());
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };
    }
}
