package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.catalog.BookListItem;
import com.kitapyurdu.api.dto.catalog.CatalogFilter;
import com.kitapyurdu.api.dto.catalog.IdName;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CatalogRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public CatalogRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<BookListItem> BOOK_MAPPER = (rs, i) -> {
        BookListItem b = new BookListItem();
        b.id = rs.getInt("KitapId");
        b.isbn = rs.getString("ISBN");
        b.title = rs.getString("Ad");
        b.price = rs.getBigDecimal("Fiyat");
        b.stock = rs.getInt("Stok");

        int pubId = rs.getInt("YayineviId");
        b.publisherId = rs.wasNull() ? null : pubId;

        b.publisherName = rs.getString("YayineviAdi");
        b.categoriesText = rs.getString("Kategoriler");

        var ts = rs.getTimestamp("EklenmeTarihi");
        b.createdAt = (ts == null) ? null : ts.toLocalDateTime();

        b.coverUrl = null;

        return b;
    };

    public List<IdName> listCategories() {
        String sql = """
            SELECT KategoriId AS id, Ad AS name
            FROM dbo.Kategori
            ORDER BY Ad
        """;
        return jdbc.query(sql, Map.of(), (rs, i) -> new IdName(rs.getInt("id"), rs.getString("name")));
    }

    public List<IdName> listPublishers() {
        String sql = """
            SELECT YayineviId AS id, Ad AS name
            FROM dbo.Yayinevi
            ORDER BY Ad
        """;
        return jdbc.query(sql, Map.of(), (rs, i) -> new IdName(rs.getInt("id"), rs.getString("name")));
    }

    public int countBooks(CatalogFilter f) {
        var ctx = buildWhere(f);

        String sql = """
            SELECT COUNT(1)
            FROM dbo.Kitap k
            LEFT JOIN dbo.Yayinevi y ON y.YayineviId = k.YayineviId
        """ + ctx.whereSql;

        Integer c = jdbc.queryForObject(sql, ctx.params, Integer.class);
        return (c == null) ? 0 : c;
    }

    public List<BookListItem> searchBooks(CatalogFilter f) {
        var ctx = buildWhere(f);

        String orderBy = switch (safe(f.sort)) {
            case "price_asc" -> " k.Fiyat ASC, k.KitapId DESC ";
            case "price_desc" -> " k.Fiyat DESC, k.KitapId DESC ";
            case "name_asc" -> " k.Ad ASC, k.KitapId DESC ";
            case "name_desc" -> " k.Ad DESC, k.KitapId DESC ";
            case "newest" -> " k.EklenmeTarihi DESC, k.KitapId DESC ";
            default -> " k.EklenmeTarihi DESC, k.KitapId DESC ";
        };

        String sql =//stored procedure olmadan kitap arama
            """
            SELECT
                k.KitapId, k.ISBN, k.Ad, k.Fiyat, k.Stok, k.YayineviId,
                y.Ad AS YayineviAdi,
                Kategoriler = COALESCE((
                    SELECT STRING_AGG(ka2.Ad, N', ') WITHIN GROUP (ORDER BY ka2.Ad)
                    FROM dbo.KitapKategori kk2
                    INNER JOIN dbo.Kategori ka2 ON ka2.KategoriId = kk2.KategoriId
                    WHERE kk2.KitapId = k.KitapId
                ), N''),
                k.EklenmeTarihi
            FROM dbo.Kitap k
            LEFT JOIN dbo.Yayinevi y ON y.YayineviId = k.YayineviId
            """
            + ctx.whereSql + "\n"
            + "ORDER BY " + orderBy + "\n"
            + "OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY\n";

        Map<String, Object> p = new HashMap<>(ctx.params);
        p.put("offset", Math.max(0, f.offset()));
        p.put("size", Math.max(1, f.size));

        return jdbc.query(sql, p, BOOK_MAPPER);
    }

    // ---- helpers ----
    private record WhereCtx(String whereSql, Map<String, Object> params) {}

    private WhereCtx buildWhere(CatalogFilter f) {
        StringBuilder w = new StringBuilder(" WHERE k.Durum = 1 ");
        Map<String, Object> p = new HashMap<>();

        if (f.publisherId != null) {
            w.append(" AND k.YayineviId = :publisherId ");
            p.put("publisherId", f.publisherId);
        }
        if (f.categoryId != null) {
            w.append("""
                AND EXISTS (
                    SELECT 1
                    FROM dbo.KitapKategori kkx
                    WHERE kkx.KitapId = k.KitapId
                      AND kkx.KategoriId = :categoryId
                )
            """);
            p.put("categoryId", f.categoryId);
        }
        if (f.minPrice != null) {
            w.append(" AND k.Fiyat >= :minPrice ");
            p.put("minPrice", f.minPrice);
        }
        if (f.maxPrice != null) {
            w.append(" AND k.Fiyat <= :maxPrice ");
            p.put("maxPrice", f.maxPrice);
        }
        if (Boolean.TRUE.equals(f.inStock)) {
            w.append(" AND k.Stok > 0 ");
        }
        if (f.q != null && !f.q.isBlank()) {
            w.append(" AND (k.Ad LIKE :q OR k.ISBN LIKE :q) ");
            p.put("q", "%" + f.q.trim() + "%");
        }

        return new WhereCtx(w.toString(), p);
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
    
}
