package com.ortest.controller;

import com.ortest.exceptions.InvalidCommandeException;
import com.ortest.mapstruct.ProductDTO;
import com.ortest.mapstruct.mappers.ProductMapper;
import com.ortest.model.Products;
import com.ortest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
  private final ProductService productService;
  private final ProductMapper productMapper;
  @Autowired
  public ProductController(ProductService productService, ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @GetMapping("/all")
  public ResponseEntity<List<ProductDTO>> findAll() {
    return ResponseEntity.ok(productMapper.toProductsDTO(productService.findProducts()));
  }
  @GetMapping("/product/{id}")
  public ProductDTO findById(@PathVariable Long id) {
    return productMapper.toProductDTO(productService.findById(id).get());
  }

  @PostMapping("/add")
  public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO productDTO) {
    productService.saveProduct(productMapper.toProduct(productDTO));
    return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
  }

    @PutMapping("/update/{id}")
    public ResponseEntity<Products> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long id) {
      return new ResponseEntity<>(productService.updateProduct(productMapper.toProduct(productDTO), id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws InvalidCommandeException {
    productService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/pagi")
  public ResponseEntity<List<Products>> getAllProductsPagination(
          @RequestParam(defaultValue = "0") Integer p, @RequestParam(defaultValue = "10") Integer z)
  {
    List<Products> listOfProductRequired = productService.getAllProductsPagination(p, z);
    return new ResponseEntity<List<Products>>(listOfProductRequired, new HttpHeaders(), HttpStatus.OK);
  }

  @GetMapping("/pagi/{p}/{z}")
  public ResponseEntity<List<Products>> getAllProductsPagination2(
          @PathVariable Integer p, @PathVariable Integer z)
  {
    List<Products> listOfProductRequired = productService.getAllProductsPagination(p, z);
    return new ResponseEntity<>(listOfProductRequired, new HttpHeaders(), HttpStatus.OK);
  }
}


