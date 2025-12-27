package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.admin.AdminOrderDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class AdminOrderRepository {

    private final JdbcTemplate jdbc;
    private final SimpleJdbcCall spAdminSiparisListele;
    private final SimpleJdbcCall spSiparisDurumGuncelle;

    public AdminOrderRepository(JdbcTemplate jdbc, DataSource ds) {
        this.jdbc = jdbc;
        this.spAdminSiparisListele = new SimpleJdbcCall(ds).withProcedureName("sp_AdminSiparisListele");//stored procedure çağrısı Siparis listeleme
        this.spSiparisDurumGuncelle = new SimpleJdbcCall(ds).withProcedureName("sp_SiparisDurumGuncelle");//stored procedure çağrısı Siparis durum güncelleme
    }

    public List<AdminOrderDto> tumSiparisleriGetir() {
        Map<String, Object> out = spAdminSiparisListele.execute();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        return rows.stream().map(r -> {
            LocalDateTime tarih = null;
            if (r.get("SiparisTarihi") instanceof Timestamp) {
                tarih = ((Timestamp) r.get("SiparisTarihi")).toLocalDateTime();
            }

            return new AdminOrderDto(
                (Integer) r.get("SiparisId"),
                tarih,
                (String) r.get("KullaniciAdi"),
                (String) r.get("SiparisDurumu"),
                (String) r.get("SonOdemeYontemi"),
                (BigDecimal) r.get("GenelToplam")
            );
        }).toList();
    }

    public void durumGuncelle(int siparisId, String yeniDurum) {
        spSiparisDurumGuncelle.execute(Map.of(
            "SiparisId", siparisId,
            "YeniDurumAdi", yeniDurum
        ));
    }
    
    // Dropdown için durum listesi (SQL'den çekmek daha iyi olur ama şimdilik statik verelim)
    public List<String> getDurumListesi() {
        return jdbc.queryForList("SELECT DurumAdi FROM dbo.SiparisDurum", String.class);//sql sorgusu
    }
}