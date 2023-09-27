package com.example.springboot.controller;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/product")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/product")
    public ResponseEntity<List<ProductModel>> getAllProduct() {
        List<ProductModel> productsList = productRepository.findAll();
        if(!productsList.isEmpty()){
            for(ProductModel product : productsList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).GetOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Object> GetOneProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        product0.get().add(linkTo(methodOn(ProductController.class).getAllProduct()).withRel("product_list"));
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto productRecordDto) {

        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } else {
            var productModel = product0.get();
            BeanUtils.copyProperties(productRecordDto, productModel);

            return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } else {
            productRepository.delete(product0.get());
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted");
        }
    }

}
