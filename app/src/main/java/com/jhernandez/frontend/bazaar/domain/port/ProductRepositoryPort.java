package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Product;

import java.io.File;
import java.util.List;

/*
 * Interface representing the ProductRepositoryPort.
 */
public interface ProductRepositoryPort {

    void createProduct (Product product, List<File> imageFiles, SuccessCallback callback);
    void findAllProducts (TypeCallback<List<Product>> callback);
    void findProductsByUserId (Long userId, TypeCallback<List<Product>> callback);
    void findProductsByCategoryId (Long categoryId, TypeCallback<List<Product>> callback);
    void findProductsByName (String name, TypeCallback<List<Product>> callback);
    void findRecentProducts (TypeCallback<List<Product>> callback);
    void findTopSellingProducts (TypeCallback<List<Product>> callback);
    void findTopRatedProducts (TypeCallback<List<Product>> callback);
    void findDiscountedProducts (TypeCallback<List<Product>> callback);
    void findProductById (Long id, TypeCallback<Product> callback);
    void updateProduct (Product product, List<File> imageFiles, SuccessCallback callback);
    void enableProductById (Long id, SuccessCallback callback);
    void disableProductById (Long id, SuccessCallback callback);

}
