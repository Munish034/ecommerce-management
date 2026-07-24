package com.ecommerce.inventoryservice.service.imp;



import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.inventoryservice.dto.request.CreateProductRequest;
import com.ecommerce.inventoryservice.dto.request.ReleaseStockRequest;
import com.ecommerce.inventoryservice.dto.request.ReserveStockRequest;
import com.ecommerce.inventoryservice.dto.response.ProductResponse;
import com.ecommerce.inventoryservice.entity.Product;
import com.ecommerce.inventoryservice.mapper.ProductMapper;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import com.ecommerce.inventoryservice.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        if (repository.existsByProductCode(request.getProductCode())) {

            throw new DuplicateResourceException(
                    "Product Code already exists : " + request.getProductCode());

        }

        Product product = mapper.toEntity(request);

        Product savedProduct = repository.save(product);

        return mapper.toResponse(savedProduct);

    }

    @Override
    public ProductResponse getProduct(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id : " + id, ErrorCode.PRODUCT_NOT_FOUND));

        return mapper.toResponse(product);

    }

    @Override
    public List<ProductResponse> getAllProducts() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

    }
    @Override
    @Transactional
    public ProductResponse reserveStock(ReserveStockRequest request) {

        Product product = repository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(

                                "Product not found : " + request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getActive()) {

            throw new BusinessException(

                    "Product is inactive.",ErrorCode.PRODUCT_NOT_ACTIVE);
        }

        if (product.getAvailableQuantity() < request.getQuantity()) {

            throw new BusinessException(

                    "Insufficient stock for product : " + product.getName(),ErrorCode.INSUFFICIENT_STOCK);
        }

        product.setAvailableQuantity(
                product.getAvailableQuantity() - request.getQuantity());

        Product updatedProduct = repository.save(product);

        return mapper.toResponse(updatedProduct);

    }

    @Override
    public ProductResponse releaseStock(ReleaseStockRequest request) {
        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found : " + request.getProductId(),
                        ErrorCode.PRODUCT_NOT_FOUND));

        product.setAvailableQuantity(
                product.getAvailableQuantity() + request.getQuantity());

        Product savedProduct = repository.save(product);

        return mapper.toResponse(savedProduct);
    }

    @Override
    public void releaseReservedStock(Long orderId) {

    }

}