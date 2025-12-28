package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.catalog.CatalogFilter;
import com.kitapyurdu.api.service.CatalogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Katalog Denetleyicisi
 * Ürün kataloğunu görüntüleme, arama ve filtreleme işlemlerini sağlar
 */
@Controller
public class CatalogController extends BaseController{

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    /**
     * Güvenlik bağlamından mevcut kullanıcı kimliğini al
     * Giriş yapılmamışsa -1 döndür
     */
    private int currentKullaniciId() {
        // Kimlik doğrulama bilgisini al
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return -1;
        
        // Anonim kullanıcı kontrolü
        Object principal = auth.getPrincipal();
        if (principal.equals("anonymousUser")) return -1;

        // Farklı metod adlarıyla kullanıcı kimliğini bulmaya çalış
        for (String m : new String[]{"getKullaniciId", "getUserId", "getId"}) {
            try {
                Method mm = principal.getClass().getMethod(m);
                Object v = mm.invoke(principal);
                if (v instanceof Integer) return (Integer) v;
                if (v instanceof Long) return ((Long) v).intValue();
            } catch (Exception ignored) {}
        }

        return -1;
    }

    /**
     * Ürün kataloğunu göster
     * Arama, kategori ve yayınevi filtrelemesi, fiyat aralığı seçimi ve sıralama destekler
     */
    @GetMapping("/katalog")
    public String katalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer publisherId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            Model model
    ) {
        // Filtre nesnesi oluştur ve parametreleri doldur
        CatalogFilter f = new CatalogFilter();
        f.q = q;
        f.categoryId = categoryId;
        f.publisherId = publisherId;
        f.minPrice = minPrice;
        f.maxPrice = maxPrice;
        f.inStock = inStock;
        f.sort = (sort == null || sort.isBlank()) ? "popular" : sort;
        f.page = (page == null || page < 1) ? 1 : page;
        f.size = (size == null || size < 1) ? 12 : size;

        // Ürünleri ara ve toplam sayıyı al
        var books = catalog.search(f);
        int total = catalog.count(f);
        int pageCount = Math.max(1, (int) Math.ceil(total / (double) f.size));

        // Filtreleme seçeneklerini al
        var categories = catalog.categories();
        var publishers = catalog.publishers();

        // Etkin filtreleri listele
        List<String> activeFilters = new ArrayList<>();
        if (q != null && !q.isBlank()) activeFilters.add("Arama: " + q.trim());
        if (categoryId != null) activeFilters.add("KategoriId: " + categoryId);
        if (publisherId != null) activeFilters.add("YayıneviId: " + publisherId);
        if (minPrice != null) activeFilters.add("Min ₺" + minPrice);
        if (maxPrice != null) activeFilters.add("Max ₺" + maxPrice);
        if (Boolean.TRUE.equals(inStock)) activeFilters.add("Stokta");
        if (f.sort != null && !f.sort.isBlank()) activeFilters.add("Sırala: " + f.sort);

        // Sayfaya eklenecek öznitelikleri hazırla
        model.addAttribute("books", books);
        model.addAttribute("total", total);

        model.addAttribute("categories", categories);
        model.addAttribute("publishers", publishers);

        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedPublisherId", publisherId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("sort", f.sort);

        model.addAttribute("page", f.page);
        model.addAttribute("pageCount", pageCount);

        model.addAttribute("activeFilters", activeFilters);

        // Kimlik doğrulanmış kullanıcı için alışveriş sepeti sayısını al
        int kullaniciId = currentKullaniciId();
        int cartCount = 0;
        if (kullaniciId > 0) {
            cartCount = catalog.cartCount(kullaniciId);
        }
        model.addAttribute("cartCount", cartCount);

        return "katalog";
    }
}
