package com.jhernandez.frontend.bazaar.domain.model;

/*
 * Class representing an Item entity.
 */
public class Item {
    private Long id;
    private Product product;
    private Double salePrice;
    private Double saleShipping;
    private Integer quantity;
    private Double totalPrice;

    public Item(Long id, Product product, Double salePrice, Double saleShipping, Integer quantity, Double totalPrice) {
        this.id = id;
        this.product = product;
        this.salePrice = salePrice;
        this.saleShipping = saleShipping;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getSaleShipping() {
        return saleShipping;
    }

    public void setSaleShipping(Double saleShipping) {
        this.saleShipping = saleShipping;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

