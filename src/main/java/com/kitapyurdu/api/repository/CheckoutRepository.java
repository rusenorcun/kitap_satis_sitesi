package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.siparis.AddressDto;
import com.kitapyurdu.api.dto.siparis.CartItemDto;
import com.kitapyurdu.api.dto.siparis.CartSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CheckoutRepository {

    private JdbcTemplate jdbc;
    private SimpleJdbcCall spAdresListele;
    private SimpleJdbcCall spSepetKalemListele;
    private SimpleJdbcCall spSepetOzetGetir;
    private SimpleJdbcCall spSiparisOlustur;
    private SimpleJdbcCall spOdemeKaydet;

    public CheckoutRepository(JdbcTemplate jdbc, DataSource ds) {
        this.jdbc = jdbc;

        this.spAdresListele = new SimpleJdbcCall(ds).withProcedureName("sp_CheckoutAdresListele");
        this.spSepetKalemListele = new SimpleJdbcCall(ds).withProcedureName("sp_CheckoutSepetKalemListele");
        this.spSepetOzetGetir = new SimpleJdbcCall(ds).withProcedureName("sp_CheckoutSepetOzetGetir");//fonskiyon kullanımlı sp

        this.spSiparisOlustur = new SimpleJdbcCall(ds).withProcedureName("sp_SiparisOlustur");

        // SP'deki parametre sırası ve isimleri önemli olacak şekilde tanımlınır
        this.spOdemeKaydet = new SimpleJdbcCall(ds)
                .withProcedureName("sp_OdemeKaydet")
                .declareParameters(
                        new SqlParameter("SiparisId", Types.INTEGER),
                        new SqlParameter("OdemeDurumAdi", Types.VARCHAR),
                        new SqlParameter("OdemeYontemAdi", Types.VARCHAR),
                        new SqlParameter("OdemeTutari", Types.DECIMAL),
                        new SqlParameter("Aciklama", Types.VARCHAR),
                        new SqlOutParameter("OdemeId", Types.INTEGER) // Output parametresi
                );
    }

    public int getKullaniciIdByAuthName(String authName) {
        Integer id = jdbc.queryForObject(
            "SELECT TOP(1) KullaniciId FROM dbo.Kullanici WHERE Eposta = ? OR KullaniciAdi = ?",
            Integer.class,
            authName, authName
        );
        if (id == null) throw new IllegalStateException("Kullanıcı bulunamadı: " + authName);
        return id;
    }

    //  Ödeme yöntemlerini DB'den çekiyoruz
    public List<String> getOdemeYontemleri() {
        return jdbc.queryForList("SELECT YontemAdi FROM dbo.OdemeYontem", String.class);
    }

    public List<AddressDto> getAdresler(int kullaniciId) {
        Map<String, Object> out = spAdresListele.execute(Map.of("KullaniciId", kullaniciId));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        return rows.stream().map(r -> new AddressDto(
            ((Number) r.get("AdresId")).intValue(),
            (String) r.get("Baslik"),
            (String) r.get("Il"),
            (String) r.get("Ilce"),
            (String) r.get("Mahalle"),
            (String) r.get("AdresDetay"),
            (String) r.get("PostaKodu")
        )).toList();
    }

    public List<CartItemDto> getSepetKalemler(int kullaniciId) {
        Map<String, Object> out = spSepetKalemListele.execute(Map.of("KullaniciId", kullaniciId));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        return rows.stream().map(r -> new CartItemDto(
            ((Number) r.get("KitapId")).intValue(),
            (String) r.get("Title"),
            (BigDecimal) r.get("Price"),
            ((Number) r.get("Quantity")).intValue(),
            (BigDecimal) r.get("LineTotal")
        )).toList();
    }

    public CartSummaryDto getSepetOzet(int kullaniciId) {
        Map<String, Object> out = spSepetOzetGetir.execute(Map.of("KullaniciId", kullaniciId));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        if (rows == null || rows.isEmpty()) {
            return new CartSummaryDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
        Map<String, Object> r = rows.get(0);
        return new CartSummaryDto(
            (BigDecimal) r.get("AraToplam"),
            (BigDecimal) r.get("IndirimToplam"),
            (BigDecimal) r.get("KargoUcreti"),
            (BigDecimal) r.get("GenelToplam"),
            ((Number) r.get("KalemSayisi")).intValue()
        );
    }

    public int siparisOlustur(int kullaniciId, int adresId, String kuponKoduOrNull) {
        Map<String, Object> params = new HashMap<>();
        params.put("KullaniciId", kullaniciId);
        params.put("AdresId", adresId);
        params.put("KuponKodu", kuponKoduOrNull);

        Map<String, Object> out = spSiparisOlustur.execute(params);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        if (rows == null || rows.isEmpty()) {
            throw new IllegalStateException("Sipariş oluşturulamadı. SP sonuç döndürmedi.");
        }
        
        Map<String, Object> row = rows.get(0);
        Object val = row.get("SiparisId");
        // Büyük/Küçük harf toleransı
        if (val == null) val = row.get("ID");
        if (val == null) val = row.get("id");

        if (val == null) {
            throw new IllegalStateException("Sipariş ID alınamadı.");
        }
        return ((Number) val).intValue();
    }

    public BigDecimal getSiparisGenelToplam(int siparisId) {
        BigDecimal toplam = jdbc.queryForObject(
            "SELECT GenelToplam FROM dbo.Siparis WHERE SiparisId = ?",
            BigDecimal.class,
            siparisId
        );
        return (toplam == null) ? BigDecimal.ZERO : toplam;
    }

    public int odemeKaydet(int siparisId, String odemeDurumAdi, String odemeYontemAdi, BigDecimal tutar, String aciklama) {
        Map<String, Object> params = new HashMap<>();
        params.put("SiparisId", siparisId);
        params.put("OdemeDurumAdi", odemeDurumAdi);
        params.put("OdemeYontemAdi", odemeYontemAdi);
        params.put("OdemeTutari", tutar);
        params.put("Aciklama", aciklama);

        // declareParameters kullandığımız için artık execute bize Output parametreyi de döner.
        Map<String, Object> out = spOdemeKaydet.execute(params);
        
        // Output parametresini al
        Object outId = out.get("OdemeId");
        if (outId instanceof Number n) {
            return n.intValue();
        }
        
        // Eğer SP eski usül result set dönüyorsa:
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");
        if (rows != null && !rows.isEmpty()) {
            Object rsVal = rows.get(0).get("OdemeId");
            if(rsVal instanceof Number n) return n.intValue();
        }

        throw new IllegalStateException("Ödeme ID alınamadı. SP output vermedi.");
    }
}