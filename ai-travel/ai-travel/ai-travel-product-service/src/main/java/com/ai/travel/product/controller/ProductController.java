package com.ai.travel.product.controller;

import com.ai.travel.common.vo.PageResult;
import com.ai.travel.common.vo.Result;
import com.ai.travel.product.dto.ProductQueryRequest;
import com.ai.travel.product.dto.ProductRequest;
import com.ai.travel.product.dto.ProductResponse;
import com.ai.travel.product.entity.Product;
import com.ai.travel.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public Result<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        log.info("Add product request: {}", request.getName());
        Product product = productService.addProduct(request);
        return Result.success("添加成功", convertToResponse(product));
    }

    @PutMapping("/update/{id}")
    public Result<ProductResponse> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("Update product request: id={}", id);
        Product product = productService.updateProduct(id, request);
        return Result.success("更新成功", convertToResponse(product));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProduct(@PathVariable("id") Long id) {
        log.info("Delete product request: id={}", id);
        productService.deleteProduct(id);
        return Result.success("删除成功");
    }

    @GetMapping("/list")
    public Result<PageResult<ProductResponse>> listProducts(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "minPrice", required = false) java.math.BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        log.info("List products request: type={}, destination={}, minPrice={}, maxPrice={}",
                type, destination, minPrice, maxPrice);

        ProductQueryRequest request = new ProductQueryRequest();
        request.setType(type);
        request.setDestination(destination);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);

        Page<Product> page = productService.listProducts(request);

        PageResult<ProductResponse> result = PageResult.of(
                page.getTotalElements(),
                pageNum,
                pageSize,
                page.getContent().stream().map(this::convertToResponse).toList()
        );

        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<ProductResponse> getProductDetail(@PathVariable("id") Long id) {
        log.info("Get product detail request: id={}", id);
        Product product = productService.getProductById(id);
        return Result.success(convertToResponse(product));
    }

    @PutMapping("/stock/decrease")
    public Result<Void> decreaseStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("Decrease stock request: productId={}, quantity={}", productId, quantity);
        boolean success = productService.decreaseStock(productId, quantity);
        if (success) {
            return Result.success("库存扣减成功");
        } else {
            return Result.error("库存扣减失败，库存不足");
        }
    }

    @PutMapping("/stock/increase")
    public Result<Void> increaseStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("Increase stock request: productId={}, quantity={}", productId, quantity);
        boolean success = productService.increaseStock(productId, quantity);
        if (success) {
            return Result.success("库存增加成功");
        } else {
            return Result.error("库存增加失败");
        }
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setType(product.getType());
        response.setDestination(product.getDestination());
        response.setDays(product.getDays());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDescription(product.getDescription());
        response.setIncludes(product.getIncludes());
        response.setTags(product.getTags());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

}