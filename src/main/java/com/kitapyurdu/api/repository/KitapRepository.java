package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.admin.AdminKitapForm;
import com.kitapyurdu.api.dto.kitap.KitapListeRow;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

@Repository
public class KitapRepository {

    private final JdbcTemplate jdbc;

    public KitapRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // vw_KitapListe: Admin/Katalog listesi
    public List<KitapListeRow> list(String q) {
        String sql = """
            SELECT KitapId, KitapAdi, Fiyat, Stok, Durum, YayineviAdi, Kategoriler, OrtalamaPuan, YorumSayisi
            FROM dbo.vw_KitapListe
            WHERE (? IS NULL OR LTRIM(RTRIM(?)) = '' OR KitapAdi LIKE '%' + ? + '%')
            ORDER BY KitapId DESC
            """;

        String qq = (q == null || q.trim().isEmpty()) ? null : q.trim();

        return jdbc.query(sql, (rs, rowNum) -> new KitapListeRow(
                rs.getInt("KitapId"),
                rs.getString("KitapAdi"),
                rs.getBigDecimal("Fiyat"),
                rs.getInt("Stok"),
                rs.getBoolean("Durum"),
                rs.getString("YayineviAdi"),
                rs.getString("Kategoriler"),
                rs.getBigDecimal("OrtalamaPuan"),
                rs.getLong("YorumSayisi")
        ), qq, qq, qq);
    }

    // Edit formu doldurmak için: dbo.Kitap'tan oku
    public AdminKitapForm findForm(Long kitapId) {
        if (kitapId == null) return AdminKitapForm.empty();

        String sql = """
            SELECT KitapId, ISBN, Ad, Fiyat, Stok, SayfaSayisi, BasimYili, Aciklama, YayineviId, Durum
            FROM dbo.Kitap
            WHERE KitapId = ?
            """;

        List<AdminKitapForm> list = jdbc.query(sql, (rs, i) -> {
            AdminKitapForm f = new AdminKitapForm();
            f.setKitapId(rs.getLong("KitapId"));
            f.setIsbn(rs.getString("ISBN"));
            f.setAd(rs.getString("Ad"));
            f.setFiyat(rs.getBigDecimal("Fiyat"));
            f.setStok(rs.getInt("Stok"));
            Object ss = rs.getObject("SayfaSayisi");
            f.setSayfaSayisi(ss == null ? null : ((Number) ss).intValue());
            Object by = rs.getObject("BasimYili");
            f.setBasimYili(by == null ? null : ((Number) by).intValue());
            f.setAciklama(rs.getString("Aciklama"));
            Object yi = rs.getObject("YayineviId");
            f.setYayineviId(yi == null ? null : ((Number) yi).intValue());
            f.setDurum(rs.getBoolean("Durum"));
            return f;
        }, kitapId);

        return list.isEmpty() ? AdminKitapForm.empty() : list.get(0);
    }


    public Integer kitapEkleGuncelle(
            Long kitapId,
            String isbn,
            String ad,
            BigDecimal fiyat,
            Integer stok,
            Integer sayfaSayisi,
            Integer basimYili,
            String aciklama,
            Integer yayineviId,
            boolean durum
    ) {

        String adTrim = (ad == null) ? null : ad.trim();
        if (adTrim == null || adTrim.isEmpty()) {
            throw new IllegalArgumentException("Kitap adı boş olamaz.");
        }

        String isbnTrim = (isbn == null || isbn.trim().isEmpty()) ? null : isbn.trim();
        String aciklamaTrim = (aciklama == null || aciklama.trim().isEmpty()) ? null : aciklama.trim();

        if (stok == null || stok < 0) stok = 0;
        if (fiyat == null) fiyat = new BigDecimal("0.00");

        String sql = """
            DECLARE @YeniId int;
            EXEC dbo.sp_KitapEkleGuncelle
                @KitapId = ?,
                @ISBN = ?,
                @Ad = ?,
                @Fiyat = ?,
                @Stok = ?,
                @SayfaSayisi = ?,
                @BasimYili = ?,
                @Aciklama = ?,
                @YayineviId = ?,
                @Durum = ?,
                @YeniKitapId = @YeniId OUTPUT;
            SELECT @YeniId;
            """;

        Object[] args = new Object[]{
                kitapId,
                isbnTrim,
                adTrim,
                fiyat,
                stok,
                sayfaSayisi,
                basimYili,
                aciklamaTrim,
                yayineviId,
                durum ? 1 : 0
        };

        int[] types = new int[]{
                Types.BIGINT,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.DECIMAL,
                Types.INTEGER,
                Types.INTEGER,
                Types.INTEGER,
                Types.VARCHAR,
                Types.INTEGER,
                Types.INTEGER
        };

        return jdbc.queryForObject(sql, args, types, Integer.class);
    }
    public List<KitapListeRow> list(String q, Integer yayineviId) {
        String sql = """
            SELECT
              v.KitapId, v.KitapAdi, v.Fiyat, v.Stok, v.Durum, v.YayineviAdi,
              v.Kategoriler, v.OrtalamaPuan, v.YorumSayisi
            FROM dbo.vw_KitapListe v
            LEFT JOIN dbo.Yayinevi y ON y.Ad = v.YayineviAdi
            WHERE
              (? IS NULL OR LTRIM(RTRIM(?)) = '' OR v.KitapAdi LIKE '%' + ? + '%')
              AND
              (? IS NULL OR y.YayineviId = ?)
            ORDER BY v.KitapId DESC
            """;

        String qq = (q == null || q.trim().isEmpty()) ? null : q.trim();

        return jdbc.query(sql, (rs, rowNum) -> new KitapListeRow(
                rs.getInt("KitapId"),
                rs.getString("KitapAdi"),
                rs.getBigDecimal("Fiyat"),
                rs.getInt("Stok"),
                rs.getBoolean("Durum"),
                rs.getString("YayineviAdi"),
                rs.getString("Kategoriler"),
                rs.getBigDecimal("OrtalamaPuan"),
                rs.getLong("YorumSayisi")
        ), qq, qq, qq, yayineviId, yayineviId);
    }

    public List<KitapListeRow> featured(int limit) {
        String sql = """
            SELECT TOP (?) KitapId, KitapAdi, Fiyat, Stok, Durum, YayineviAdi, Kategoriler, OrtalamaPuan, YorumSayisi
            FROM dbo.vw_KitapListe
            WHERE Durum = 1
            ORDER BY KitapId DESC
            """;

        return jdbc.query(sql, (rs, rowNum) -> new KitapListeRow(
                rs.getInt("KitapId"),
                rs.getString("KitapAdi"),
                rs.getBigDecimal("Fiyat"),
                rs.getInt("Stok"),
                rs.getBoolean("Durum"),
                rs.getString("YayineviAdi"),
                rs.getString("Kategoriler"),
                rs.getBigDecimal("OrtalamaPuan"),
                rs.getLong("YorumSayisi")
        ), limit);
    }

    //  Ana sayfa: Stokta sınırlı kalanlar
    public List<KitapListeRow> limitedStock(int threshold, int limit) {
        String sql = """
            SELECT TOP (?) KitapId, KitapAdi, Fiyat, Stok, Durum, YayineviAdi, Kategoriler, OrtalamaPuan, YorumSayisi
            FROM dbo.vw_KitapListe
            WHERE Durum = 1 AND Stok BETWEEN 1 AND ?
            ORDER BY Stok ASC, KitapId DESC
            """;

        return jdbc.query(sql, (rs, rowNum) -> new KitapListeRow(
                rs.getInt("KitapId"),
                rs.getString("KitapAdi"),
                rs.getBigDecimal("Fiyat"),
                rs.getInt("Stok"),
                rs.getBoolean("Durum"),
                rs.getString("YayineviAdi"),
                rs.getString("Kategoriler"),
                rs.getBigDecimal("OrtalamaPuan"),
                rs.getLong("YorumSayisi")
        ), limit, threshold);
    }
}
