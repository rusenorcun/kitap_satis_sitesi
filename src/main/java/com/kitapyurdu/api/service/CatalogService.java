package com.kitapyurdu.api.service;

import com.kitapyurdu.api.dto.catalog.BookListItem;
import com.kitapyurdu.api.dto.catalog.CatalogFilter;
import com.kitapyurdu.api.dto.catalog.IdName;
import com.kitapyurdu.api.repository.CatalogRepository;
import org.springframework.stereotype.Service;
import com.kitapyurdu.api.repository.SepetRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository repo;
    private final SepetRepository sepetRepo;

    public CatalogService(CatalogRepository repo, SepetRepository sepetRepo) {
        this.repo = repo;
        this.sepetRepo = sepetRepo;
    }

    public List<IdName> categories() { return repo.listCategories(); }
    public List<IdName> publishers() { return repo.listPublishers(); }

    public int count(CatalogFilter f) { return repo.countBooks(f); }
    public List<BookListItem> search(CatalogFilter f) { return repo.searchBooks(f); }

    public List<String> activeFilters(CatalogFilter f, String categoryName, String publisherName) {
        List<String> a = new ArrayList<>();
        if (f.q != null && !f.q.isBlank()) a.add("Arama: " + f.q.trim());
        if (categoryName != null) a.add("Kategori: " + categoryName);
        if (publisherName != null) a.add("Yayınevi: " + publisherName);
        if (f.minPrice != null) a.add("Min ₺" + f.minPrice);
        if (f.maxPrice != null) a.add("Max ₺" + f.maxPrice);
        if (Boolean.TRUE.equals(f.inStock)) a.add("Stokta");
        if (f.sort != null && !f.sort.isBlank()) a.add("Sırala: " + f.sort);
        return a;
    }
    public int cartCount(int kullaniciId) {
		return sepetRepo.getActiveCartCount(kullaniciId);
	}
}
