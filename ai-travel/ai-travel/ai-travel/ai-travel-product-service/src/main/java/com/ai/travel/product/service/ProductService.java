package com.ai.travel.product.service;

import com.ai.travel.product.dto.ProductQueryRequest;
import com.ai.travel.product.dto.ProductRequest;
import com.ai.travel.product.dto.ProductResponse;
import com.ai.travel.product.entity.Product;
import com.ai.travel.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setType(request.getType());
        product.setDestination(request.getDestination());
        product.setDays(request.getDays());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setIncludes(request.getIncludes());
        product.setTags(request.getTags());
        product.setStatus(request.getStatus());

        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully: {}", savedProduct.getName());
        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在"));

        product.setName(request.getName());
        product.setType(request.getType());
        product.setDestination(request.getDestination());
        product.setDays(request.getDays());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setIncludes(request.getIncludes());
        product.setTags(request.getTags());
        product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getName());
        return updatedProduct;
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("产品不存在");
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully: id={}", id);
    }

    public Page<Product> listProducts(ProductQueryRequest request) {
        request.check();

        Pageable pageable = PageRequest.of(
                request.getPageNum() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return productRepository.findByFilters(
                1,
                request.getType(),
                request.getDestination(),
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
        );
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在"));
    }

    @Transactional
    public boolean decreaseStock(Long id, Integer quantity) {
        int updated = productRepository.decreaseStock(id, quantity);
        return updated > 0;
    }

    @Transactional
    public boolean increaseStock(Long id, Integer quantity) {
        int updated = productRepository.increaseStock(id, quantity);
        return updated > 0;
    }

}