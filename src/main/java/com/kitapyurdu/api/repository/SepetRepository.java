package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.cart.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.sql.*;
import java.util.List;

@Repository
public class SepetRepository {

	private final JdbcTemplate jdbc;

	public SepetRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	//Kullanıcının Aktif sepeti varsa döner, yoksa null 
	public Integer findActiveSepetId(int kullaniciId) {
		String sql = """
			SELECT TOP(1) s.SepetId
			FROM dbo.Sepet s
			WHERE s.KullaniciId = ?
				AND s.SepetDurumId = (SELECT SepetDurumId FROM dbo.SepetDurum WHERE DurumAdi = N'Aktif')
			ORDER BY s.SepetId DESC
		""";
		List<Integer> r = jdbc.query(sql, (rs, i) -> rs.getInt(1), kullaniciId);
		return r.isEmpty() ? null : r.get(0);
	}

	// sp_SepeteEkle ile DB’ye ekler veya artırır. SP zaten aktif sepeti yoksa oluşturuyor.
	public int spSepeteEkle(int kullaniciId, int kitapId, int adet) {
		return jdbc.execute((Connection con) -> {
			try (CallableStatement cs = con.prepareCall("{call dbo.sp_SepeteEkle(?,?,?,?)}")) {
				cs.setInt(1, kullaniciId);
				cs.setInt(2, kitapId);
				cs.setInt(3, adet);
				cs.registerOutParameter(4, Types.INTEGER);
				cs.execute();
				return cs.getInt(4); // @SepetId OUTPUT
			}
		});
	}
	// Sepet kalemini günceller
	public void spSepetKalemGuncelle(int sepetId, int kitapId, int yeniAdet) {
		jdbc.execute((Connection con) -> {
			try (CallableStatement cs = con.prepareCall("{call dbo.sp_SepetKalemGuncelle(?,?,?)}")) {
				cs.setInt(1, sepetId);
				cs.setInt(2, kitapId);
				cs.setInt(3, yeniAdet);
				cs.execute();
				return null;
			}
		});
	}

	// Sepet içeriğini listeler
	public List<CartItem> listSepetItems(int sepetId) {
	
	String sql = "{call dbo.sp_SepetIcerigiGetir(?)}"; // Stored Procedure çağrısı 

	return jdbc.query(sql, (rs, i) -> {
		CartItem it = new CartItem();
		it.setBookId(rs.getInt("KitapId"));
		it.setTitle(rs.getString("Baslik"));
		it.setIsbn(rs.getString("ISBN"));
		it.setQty(rs.getInt("Adet"));
		it.setUnitPrice(rs.getBigDecimal("BirimFiyat"));
		it.setStock(rs.getInt("Stok"));
		it.setLineTotal(rs.getBigDecimal("SatirToplam"));
		
		return it;
	}, sepetId);
}

	// Kullanıcının aktif sepeti mi diye kontrol eder
	public boolean isUsersActiveSepet(int kullaniciId, int sepetId) {
		String sql = """
			SELECT COUNT(1)
			FROM dbo.Sepet s
			WHERE s.SepetId = ?
				AND s.KullaniciId = ?
				AND s.SepetDurumId = (SELECT SepetDurumId FROM dbo.SepetDurum WHERE DurumAdi = N'Aktif')
		""";
		Integer c = jdbc.queryForObject(sql, Integer.class, sepetId, kullaniciId);
		return c != null && c > 0;
	}

	// Sepeti temizler (tüm kalemleri siler)
	public void clearSepet(int sepetId) {
		jdbc.update("DELETE FROM dbo.SepetKalem WHERE SepetId = ?", sepetId);
	}

	// Kullanıcının aktif sepetindeki toplam ürün adedini döner
	public int getActiveCartCount(int kullaniciId) {
		String sql = """
			SELECT COALESCE(SUM(sk.Adet), 0)
			FROM dbo.Sepet s
			INNER JOIN dbo.SepetKalem sk ON sk.SepetId = s.SepetId
			WHERE s.KullaniciId = ?
				AND s.SepetDurumId = (SELECT SepetDurumId FROM dbo.SepetDurum WHERE DurumAdi = N'Aktif')
		""";
		Integer c = jdbc.queryForObject(sql, Integer.class, kullaniciId);
		return c == null ? 0 : c;
	}
	public int getSepetUrunAdedi(int sepetId) {
		String sql = "SELECT dbo.fn_SepetUrunAdet(?)";
		// Tek bir sayı döneceği için queryForObject kullanıyoruz
		return jdbc.queryForObject(sql, Integer.class, sepetId); 
	}
}
