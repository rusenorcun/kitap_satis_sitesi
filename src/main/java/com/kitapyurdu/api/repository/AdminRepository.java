package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.LowStockRow;
import com.kitapyurdu.api.dto.EmptyPublisherRow;
import com.kitapyurdu.api.dto.admin.AdminStokRow;
import com.kitapyurdu.api.dto.admin.AdminUserRow;
import com.kitapyurdu.api.dto.yayinevi.YayineviForm;
import com.kitapyurdu.api.dto.yayinevi.YayineviOption;
import com.kitapyurdu.api.dto.yayinevi.YayineviRow;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepository {

	private final JdbcTemplate jdbc;

	public AdminRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
    public int aktifKitap() {
    String sql = "SELECT dbo.fn_KitapSay(NULL, NULL, NULL, NULL, NULL, NULL)";

    try {
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return (count != null) ? count : 0;
    } catch (Exception e) {
        e.printStackTrace();
        return 0; // Hata durumunda 0 döner
    }
}
	// ---------------- Tablo Elemanları ----------------
	public Integer toplamKitap() {
		return jdbc.queryForObject("SELECT COUNT(1) FROM dbo.vw_KitapListe", Integer.class);//view sorgusu ile toplam kitap sayısı
	}

	public Integer toplamStok() {
		return jdbc.queryForObject("SELECT COALESCE(SUM(Stok),0) FROM dbo.vw_KitapListe", Integer.class); //view sorgusu ile toplam stok sayısı
	}

	public Integer yayineviSayisi() {
		return jdbc.queryForObject("SELECT COUNT(1) FROM dbo.Yayinevi", Integer.class);//sql sorgusu ile toplam yayınevi sayısı
	}

	public List<YayineviOption> yayineviOptions() {
    String sql = "EXEC sp_Yayinevi_Listele";//stored procedure çağrısı Yayınevi listeleme
    
    return jdbc.query(sql, (rs, i) -> new YayineviOption(
            rs.getInt("Id"), // SP'de 'AS Id' dediğimiz için burası aynı kalır
            rs.getString("Ad")
    ));
}

	// ---------------- Stok tablosu (vw_KitapListe) ----------------
	public List<AdminStokRow> stokRows(String q, Integer yayineviId) {
    // Boş string gelirse null döner ki SP'de "@SearchText IS NULL" bloğu çalışsın
    String qq = (q == null || q.trim().isEmpty()) ? null : q.trim();

    String sql = "EXEC sp_Admin_Stok_Listele @SearchText = ?, @YayineviId = ?";

    return jdbc.query(sql, (rs, i) -> new AdminStokRow(//sp'den dönen verileri AdminStokRow objesine mapleme
            rs.getInt("KitapId"),
            rs.getString("KitapAdi"),
            rs.getString("ISBN"),
            rs.getString("YayineviAdi"),
            rs.getBigDecimal("Fiyat"),
            rs.getInt("Stok"),
            rs.getBoolean("Durum")
    ), qq, yayineviId);
}

	// ---------------- Stok/Durum güncelle (SP) ----------------
	public void stokDurumGuncelle(Integer kitapId, Integer stok, boolean durum) {
		String sql = "EXEC dbo.sp_KitapStokDurumGuncelle @KitapId=?, @Stok=?, @Durum=?";
		jdbc.update(sql, kitapId, stok, durum);
	}

	// ---------------- Kitap sil (SP) ----------------
	public void kitapSil(Integer kitapId) {
		jdbc.update("EXEC dbo.sp_KitapSil @KitapId=?", kitapId);
	}

	// ---------------- Yayınevi ekle/sil (SP) ----------------
	public void yayineviEkle(String ad, String aciklama) {
		jdbc.update("EXEC dbo.sp_YayineviEkle @Ad=?, @Aciklama=?", ad, aciklama);
	}

	public void yayineviSil(Integer yayineviId) {
		jdbc.update("EXEC dbo.sp_YayineviSil @YayineviId=?", yayineviId);
	}

	// username/email ile eşleşebilir(kullanıcıadı ya da email) ikisinden biri dönse yeterli.
	public void rolVer(String usernameOrMail, String role) {
		String sql = "EXEC sp_Kullanici_Rol_Guncelle @KullaniciBilgisi = ?, @Rol = ?";
		
		int n = jdbc.update(sql, usernameOrMail, role);

		if (n == 0) throw new IllegalStateException("Kullanici bulunamadi.");
	}

	public void rolKaldir(String usernameOrMail) {
	    // Aynı SP'yi kullanıyoruz, fakat rol kısmını sabit 'ROLE_USER' olarak gönderiyoruz ki default role'a dönsün
		String sql = "EXEC sp_Kullanici_Rol_Guncelle @KullaniciBilgisi = ?, @Rol = 'ROLE_USER'";
	
	    // Rol SQL içinde sabit olduğu için sadece kullanıcı bilgisini parametre geçiyoruz
		int n = jdbc.update(sql, usernameOrMail);

		if (n == 0) throw new IllegalStateException("Kullanici bulunamadi.");//hata fırlatma
	}

	// ---------------- Kullanıcı listesi ----------------
	public List<AdminUserRow> listUsers() {
        String sql = "EXEC sp_Admin_Kullanici_Listele";

        return jdbc.query(sql, (rs, i) -> new AdminUserRow(
                rs.getInt("KullaniciId"),
                rs.getString("KullaniciAdi"),
                rs.getString("Rol"),
                rs.getBoolean("Durum")
        ));
    }

    public java.util.List<YayineviRow> listYayinevleri(String q) {
    String qq = (q == null || q.trim().isEmpty()) ? null : q.trim();

    String sql = "EXEC sp_Yayinevi_Listele_With_Count @SearchText = ?"; // fonksiyon içeren stored procedure

    System.out.println("----- DEBUG BAŞLANGIÇ -----");//debug amaçlı konsola basma işlemleri
    System.out.println("Çalıştırılan SQL: " + sql);
    System.out.println("Parametre (q): " + qq);

    try {
        List<YayineviRow> list = jdbc.query(sql, (rs, i) -> {
            int id = rs.getInt("YayineviId");
            String ad = rs.getString("Ad");
            int count = -1;
            try {
                count = rs.getInt("KitapSayisi");
            } catch (Exception e) {
                System.out.println("!!! HATA: 'KitapSayisi' sütunu veritabanından dönmüyor! " + e.getMessage());
            }

            System.out.println("Satır " + i + ": ID=" + id + ", Ad=" + ad + ", KitapSayisi=" + count);

            return new YayineviRow(
                id,
                ad,
                rs.getString("Aciklama"),
                count
            );
        }, qq);

        System.out.println("Toplam Dönen Kayıt: " + list.size());
        System.out.println("----- DEBUG BİTİŞ -----");
        return list;

    } catch (Exception ex) {
        System.out.println("!!! KRİTİK SQL HATASI !!!");
        ex.printStackTrace(); // Konsola hatanın tamamını basar
        throw ex;
    }
}

	public YayineviForm findYayineviForm(Integer yayineviId) {
        if (yayineviId == null) return YayineviForm.empty();

        String sql = "EXEC sp_Yayinevi_Getir @YayineviId = ?";

        java.util.List<YayineviForm> list = jdbc.query(sql, (rs, i) -> {
            YayineviForm f = new YayineviForm();
            f.setYayineviId(rs.getInt("YayineviId"));
            f.setAd(rs.getString("Ad"));
            f.setAciklama(rs.getString("Aciklama"));
            return f;
        }, yayineviId);

        return list.isEmpty() ? YayineviForm.empty() : list.get(0);
    }

	public void yayineviGuncelle(Integer yayineviId, String ad, String aciklama) {
        String sql = "EXEC sp_Yayinevi_Guncelle @Ad = ?, @Aciklama = ?, @YayineviId = ?";

        int n = jdbc.update(sql, ad, aciklama, yayineviId);

        if (n == 0) throw new IllegalStateException("Yayınevi bulunamadı.");
    }
	public int yayineviKitapSayisi(Integer yayineviId) {
		Integer n = jdbc.queryForObject(
			"SELECT dbo.fn_YayineviKitapSayisi(?)",
			Integer.class,
			yayineviId
		);
		return n == null ? 0 : n;
	}
	public java.util.List<LowStockRow> lowStockRows(int threshold, int limit) {
        String sql = "EXEC sp_Kitap_AzStok_Listele @Limit = ?, @Threshold = ?";
        
        return jdbc.query(sql, (rs, i) -> new LowStockRow(
                rs.getInt("KitapId"),
                rs.getString("KitapAdi"),
                rs.getInt("Stok"),
                rs.getString("YayineviAdi")
        ), limit, threshold);
    }
	
	public int pasifKitapSayisi() {
		Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM dbo.Kitap WHERE Durum = 0", Integer.class);
		return n == null ? 0 : n;
	}
	
	public java.util.List<EmptyPublisherRow> emptyPublishers(int limit) {
        String sql = "EXEC sp_Yayinevi_Bos_Listele @Limit = ?";
    
        return jdbc.query(sql, (rs, i) -> new EmptyPublisherRow(
                rs.getInt("YayineviId"),
                rs.getString("Ad")
        ), limit);
    }
}
			