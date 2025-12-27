package com.kitapyurdu.api.dto.catalog;

import java.math.BigDecimal;

public class CatalogFilter {
    public Integer categoryId;
    public Integer publisherId;
    public BigDecimal minPrice;
    public BigDecimal maxPrice;
    public Boolean inStock;
    public String sort; 
    public String q;

    public int page = 1;  
    public int size = 12;  

    public int offset() {
        int p = Math.max(1, page);
        int s = Math.max(1, size);//sayfadaki eleman sayısı
        return (p - 1) * s;
    }
}
