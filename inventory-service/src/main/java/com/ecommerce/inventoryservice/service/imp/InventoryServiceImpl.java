package com.ecommerce.inventoryservice.service.imp;



import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.inventoryservice.dto.request.CreateProductRequest;
import com.ecommerce.inventoryservice.dto.response.ProductResponse;
import com.ecommerce.inventoryservice.entity.Product;
import com.ecommerce.inventoryservice.mapper.ProductMapper;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import com.ecommerce.inventoryservice.service.InventoryService;
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
                                "Product not found with id : " + id));

        return mapper.toResponse(product);

    }

    @Override
    public List<ProductResponse> getAllProducts() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

    }

}