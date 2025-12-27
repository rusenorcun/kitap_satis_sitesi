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
            fb.setTitle(toStr(r.get("Title")));
            fb.setPrice((java.math.BigDecimal) r.get("Price"));
            fb.setStock(toInt(r.get("Stock")));
            fb.setPublisherName(toStr(r.get("PublisherName")));
            fb.setCoverUrl(null); // resim sistemini sonra bağlayacağız
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
        // View ismimiz: vw_ProfilSiparisleri
        // Kolonlar: SiparisId, SiparisTarihi, GenelToplam, DurumAdi, UrunAdedi
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT 
                SiparisId, 
                SiparisTarihi, 
                GenelToplam, 
                DurumAdi, 
                UrunAdedi
            FROM dbo.vw_ProfilSiparisleri
            WHERE KullaniciId = ?
            ORDER BY SiparisTarihi DESC
        """, userId);
            //view kullanımı
        List<UserOrder> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            UserOrder o = new UserOrder();
            o.setSiparisId(toInt(r.get("SiparisId")));
            o.setSiparisTarihi(toLdt(r.get("SiparisTarihi")));
            o.setGenelToplam((java.math.BigDecimal) r.get("GenelToplam"));
            o.setDurumAdi(toStr(r.get("DurumAdi")));
            o.setUrunAdedi(toInt(r.get("UrunAdedi")));
            out.add(o);
        }
        return out;
    }
}
