package com.banreservas.product.service.impl;

import com.banreservas.product.model.Product;
import com.banreservas.product.repository.ProductRepository;
import com.banreservas.product.service.ProductEventPublishService;
import com.banreservas.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductEventPublishService productEventPublishService;

    @Override
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product)
                .flatMap(productEventPublishService::sendProductCreatedEvent);
    }

    @Override
    public Flux<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Mono<Product> getOne(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> updateProduct(Product dbProduct, Product newProductChanges) {

        dbProduct.setName(newProductChanges.getName());
        dbProduct.setPrice(newProductChanges.getPrice());
        dbProduct.setDescription(newProductChanges.getDescription());
        dbProduct.setCategory(newProductChanges.getCategory());
        dbProduct.setSku(newProductChanges.getSku());

        return productRepository.save(dbProduct)
                .flatMap(productEventPublishService::sendProductUpdatedEvent);
    }

    @Override
    public Mono<Void> deleteProduct(Product product) {
        return productRepository.delete(product)
                .then(productEventPublishService.sendProductDeletedEvent(product));
    }

    @Override
    public Flux<Product> listByCategory(String category) {
        return productRepository.findAllByCategory(category);
    }
}
