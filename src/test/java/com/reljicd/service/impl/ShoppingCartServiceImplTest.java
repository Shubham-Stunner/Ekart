package com.reljicd.service.impl;

import com.reljicd.exception.NotEnoughProductsInStockException;
import com.reljicd.model.Product;
import com.reljicd.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private Product createProduct(Long id, int quantity, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setName("name" + id);
        return product;
    }

    @Before
    public void setUp() {
        shoppingCartService.getProductsInCart().clear();
    }

    @Test
    public void addProductNewProductAddsToCart() {
        Product product = createProduct(1L, 5, BigDecimal.TEN);
        shoppingCartService.addProduct(product);
        assertEquals(Integer.valueOf(1), shoppingCartService.getProductsInCart().get(product));
    }

    @Test
    public void addProductExistingProductIncrementsQuantity() {
        Product product = createProduct(1L, 5, BigDecimal.TEN);
        shoppingCartService.addProduct(product);
        shoppingCartService.addProduct(product);
        assertEquals(Integer.valueOf(2), shoppingCartService.getProductsInCart().get(product));
    }

    @Test
    public void removeProductRemovesFromCart() {
        Product product = createProduct(1L, 5, BigDecimal.TEN);
        shoppingCartService.addProduct(product);
        shoppingCartService.removeProduct(product);
        assertTrue(shoppingCartService.getProductsInCart().isEmpty());
    }

    @Test
    public void getTotalSumsProductPrices() {
        Product product1 = createProduct(1L, 5, new BigDecimal("2.00"));
        Product product2 = createProduct(2L, 5, new BigDecimal("3.00"));
        shoppingCartService.addProduct(product1);
        shoppingCartService.addProduct(product1);
        shoppingCartService.addProduct(product2);
        assertEquals(new BigDecimal("7.00"), shoppingCartService.getTotal());
    }

    @Test
    public void checkoutWithEnoughStockClearsCart() throws Exception {
        Product cartProduct = createProduct(1L, 1, BigDecimal.TEN);
        shoppingCartService.addProduct(cartProduct);
        Product repoProduct = createProduct(1L, 2, BigDecimal.TEN);
        when(productRepository.findOne(eq(1L))).thenReturn(repoProduct);

        shoppingCartService.checkout();

        verify(productRepository).save(anyCollection());
        verify(productRepository).flush();
        assertTrue(shoppingCartService.getProductsInCart().isEmpty());
    }

    @Test(expected = NotEnoughProductsInStockException.class)
    public void checkoutWithNotEnoughStockThrowsException() throws Exception {
        Product cartProduct = createProduct(1L, 1, BigDecimal.TEN);
        shoppingCartService.addProduct(cartProduct);
        Product repoProduct = createProduct(1L, 0, BigDecimal.TEN);
        when(productRepository.findOne(eq(1L))).thenReturn(repoProduct);

        shoppingCartService.checkout();
    }
}
