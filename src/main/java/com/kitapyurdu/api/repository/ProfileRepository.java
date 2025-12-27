package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.profile.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ProfileRepository {

    private final JdbcTemplate jdbc;

    public ProfileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Integer> findUserIdByPrincipal(String principalName) {
        if (principalName == null || principalName.isBlank()) return Optional.empty();

        Integer id1 = queryForIntOrNull(
                "SELECT TOP 1 KullaniciId FROM dbo.Kullanici WHERE KullaniciAdi = ?",
                principalName
        );
        if (id1 != null) return Optional.of(id1);

        Integer id2 = queryForIntOrNull(
                "SELECT TOP 1 KullaniciId FROM dbo.Kullanici WHERE Eposta = ?",
                principalName
        );
        return Optional.ofNullable(id2);
    }

    public Optional<UserProfile> getProfile(int userId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT TOP 1
              KullaniciId, KullaniciAdi, Eposta, Ad, Soyad, Telefon, Rol, Durum, KayitTarihi
            FROM dbo.Kullanici
            WHERE KullaniciId = ?
        """, userId);

        if (rows.isEmpty()) return Optional.empty();

        Map<String, Object> r = rows.get(0);

        UserProfile p = new UserProfile();
        p.setKullaniciId(toInt(r.get("KullaniciId")));
        p.setKullaniciAdi(toStr(r.get("KullaniciAdi")));
        p.setEposta(toStr(r.get("Eposta")));
        p.setAd(toStr(r.get("Ad")));
        p.setSoyad(toStr(r.get("Soyad")));
        p.setTelefon(toStr(r.get("Telefon")));
        p.setRol(toStr(r.get("Rol")));
        p.setDurum(toBoolNullable(r.get("Durum")));
        p.setKayitTarihi(toLdt(r.get("KayitTarihi")));

        return Optional.of(p);
    }

    public List<UserAddress> listAddresses(int userId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT
              AdresId, KullaniciId, Baslik, Il, Ilce, Mahalle, AdresDetay, PostaKodu, Varsayilan, OlusturmaTarihi
            FROM dbo.Adres
            WHERE KullaniciId = ?
            ORDER BY Varsayilan DESC, OlusturmaTarihi DESC
        """, userId);

        List<UserAddress> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            UserAddress a = new UserAddress();
            a.setAdresId(toInt(r.get("AdresId")));
            a.setKullaniciId(toInt(r.get("KullaniciId")));
            a.setBaslik(toStr(r.get("Baslik")));
            a.setIl(toStr(r.get("Il")));
            a.setIlce(toStr(r.get("Ilce")));
            a.setMahalle(toStr(r.get("Mahalle")));
            a.setAdresDetay(toStr(r.get("AdresDetay")));
            a.setPostaKodu(toStr(r.get("PostaKodu")));
            a.setVarsayilan(toBoolNullable(r.get("Varsayilan")));
            a.setOlusturmaTarihi(toLdt(r.get("OlusturmaTarihi")));
            out.add(a);
        }
        return out;
    }

    public int spAdresEkle(int userId, AddressCreateForm f) {
        // OUTPUT param almak için DECLARE + EXEC yöntemi en stabil
        // sp_AdresEkle zaten SELECT @AdresId döndürüyor; onu direkt queryForObject ile alıyoruz.
        Integer newId = jdbc.queryForObject("""
            DECLARE @AdresId int;
            EXEC dbo.sp_AdresEkle
              @KullaniciId = ?,
              @Baslik = ?,
              @Il = ?,
              @Ilce = ?,
              @Mahalle = ?,
              @AdresDetay = ?,
              @PostaKodu = ?,
              @Varsayilan = ?,
              @AdresId = @AdresId OUTPUT;
            SELECT @AdresId;
        """, Integer.class,
                userId,
                f.getBaslik(),
                f.getIl(),
                f.getIlce(),
                f.getMahalle(),
                f.getAdresDetay(),
                f.getPostaKodu(),
                boolToBitNumber(f.getVarsayilan())
        );

        if (newId == null) throw new RuntimeException("Adres eklenemedi.");
        return newId;
    }

    public void setDefaultAddress(int userId, int adresId) {
        // güvenli: sadece kendi adresini etkiler
        jdbc.update("""
            UPDATE dbo.Adres
            SET Varsayilan = CASE WHEN AdresId = ? THEN 1 ELSE 0 END
            WHERE KullaniciId = ?
        """, adresId, userId);
    }

    public void deleteAddress(int userId, int adresId) {
        int aff = jdbc.update("""
            DELETE FROM dbo.Adres
            WHERE KullaniciId = ? AND AdresId = ?
        """, userId, adresId);

        if (aff == 0) throw new RuntimeException("Adres bulunamadı.");
    }

    public String spFavoriToggle(int userId, int kitapId) {
        // sp_FavoriToggle SELECT ile "Eklendi" veya "Cikarildi" String döndürüyor.
        String sonuc = jdbc.queryForObject("""
            EXEC dbo.sp_FavoriToggle @KullaniciId = ?, @KitapId = ?;
        """, String.class, userId, kitapId);

        return (sonuc == null) ? "?" : sonuc;
    }

    public List<FavoriteBook> listFavorites(int userId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT
              k.KitapId,
              k.Ad AS Title,
              k.Fiyat    AS Price,
              k.Stok     AS Stock,
              y.Ad AS PublisherName
            FROM dbo.Favori f
            JOIN dbo.Kitap k ON k.KitapId = f.KitapId
            LEFT JOIN dbo.Yayinevi y ON y.YayineviId = k.YayineviId
            WHERE f.KullaniciId = ?
            ORDER BY f.EklenmeTarihi DESC
        """, userId);

        List<FavoriteBook> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            FavoriteBook fb = new FavoriteBook();
            fb.setKitapId(toInt(r.get("KitapId")));
            
            // DÜZELTİLEN KISIMLAR BURASI:
            fb.setKitapAdi(toStr(r.get("Title")));          // setTitle -> setKitapAdi
            fb.setFiyat((java.math.BigDecimal) r.get("Price")); // setPrice -> setFiyat
            fb.setStok(toInt(r.get("Stock")));              // setStock -> setStok
            fb.setYayineviAdi(toStr(r.get("PublisherName")));// setPublisherName -> setYayineviAdi
            
            fb.setCoverUrl(null);
            out.add(fb);
        }
        return out;
    }

    // ---- Yardımcı Fonksiyonlar ----
    private Integer queryForIntOrNull(String sql, Object... args) {
        List<Integer> list = jdbc.query(sql, (rs, i) -> rs.getInt(1), args);
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }

    private String toStr(Object o) { return o == null ? null : o.toString(); }

    private Boolean toBoolNullable(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        if (o instanceof Number n) return n.intValue() != 0; // ✅ Boolean->Number hatasını bitirir
        String s = o.toString().trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("evet");
    }

    private LocalDateTime toLdt(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDateTime ldt) return ldt;
        if (o instanceof java.sql.Timestamp ts) return ts.toLocalDateTime(); // ✅ Timestamp cast hatasını bitirir
        if (o instanceof java.util.Date d) return new java.sql.Timestamp(d.getTime()).toLocalDateTime();
        return LocalDateTime.parse(o.toString());
    }

    private int boolToBitNumber(Boolean b) { return Boolean.TRUE.equals(b) ? 1 : 0; }

    public List<UserOrder> listOrders(int userId) {
    String sql = "SELECT * FROM dbo.vw_ProfilSiparisleri WHERE KullaniciId = ? ORDER BY SiparisTarihi DESC";

    // KONSOL LOG 1: Sorgu başladı
    System.out.println(">>> DEBUG [Repo]: Siparişler aranıyor. UserID: " + userId);

    List<UserOrder> list = jdbc.query(sql, (rs, rowNum) -> {
        UserOrder o = new UserOrder();
        o.setSiparisId(rs.getInt("SiparisId"));
        
        java.sql.Timestamp ts = rs.getTimestamp("SiparisTarihi");
        if (ts != null) o.setSiparisTarihi(ts.toLocalDateTime());
        
        o.setGenelToplam(rs.getBigDecimal("GenelToplam"));
        o.setDurumAdi(rs.getString("DurumAdi"));
        o.setUrunAdedi(rs.getInt("UrunAdedi"));
        return o;
    }, userId);

    // KONSOL LOG 2: Sorgu bitti
    System.out.println(">>> DEBUG [Repo]: Bulunan kayıt sayısı: " + list.size());
    
    return list;
}
}
