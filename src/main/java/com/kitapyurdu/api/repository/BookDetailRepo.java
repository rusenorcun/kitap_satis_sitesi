package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.dto.book.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class BookDetailRepo {

	private final JdbcTemplate jdbc;

	public BookDetailRepo(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Integer findUserIdByUsername(String username) {
		try {
			return jdbc.queryForObject(
				"EXEC dbo.sp_KullaniciIdGetirByKullaniciAdi ?",
				Integer.class,
				username
			);
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	public BookDetailView getBookDetail(int kitapId, Integer kullaniciId) {
		return jdbc.queryForObject(
			"EXEC dbo.sp_KitapDetayGetir ?, ?",
			(rs, rowNum) -> mapBookDetail(rs),
			kitapId,
			kullaniciId
		);
	}

	public List<BookImageView> listImages(int kitapId) {
		return jdbc.query(
			"EXEC dbo.sp_KitapGorselListele ?",
			(rs, rowNum) -> {
				BookImageView v = new BookImageView();
				v.setKitapGorselId(rs.getInt("KitapGorselId"));
				v.setKitapId(rs.getInt("KitapId"));
				v.setGorselYolu(rs.getString("GorselYolu"));
				int sira = rs.getInt("SiraNo");
				v.setSiraNo(rs.wasNull() ? null : sira);
				return v;
			},
			kitapId
		);
	}

	public List<AuthorView> listAuthors(int kitapId) {
		return jdbc.query(
			"EXEC dbo.sp_KitapYazarListele ?",
			(rs, rowNum) -> {
				AuthorView v = new AuthorView();
				v.setYazarId(rs.getInt("YazarId"));
				v.setAd(rs.getString("Ad"));
				v.setSoyad(rs.getString("Soyad"));
				return v;
			},
			kitapId
		);
	}

	public List<CategoryView> listCategories(int kitapId) {
		return jdbc.query(
			"EXEC dbo.sp_KitapKategoriListele ?",
			(rs, rowNum) -> {
				CategoryView v = new CategoryView();
				v.setKategoriId(rs.getInt("KategoriId"));
				v.setAd(rs.getString("Ad"));
				return v;
			},
			kitapId
		);
	}

	public List<ReviewView> listReviews(int kitapId) {
		return jdbc.query(
			"EXEC dbo.sp_KitapYorumListele ?",
			(rs, rowNum) -> {
				ReviewView v = new ReviewView();
				v.setYorumId(rs.getInt("YorumId"));
				v.setKitapId(rs.getInt("KitapId"));
				v.setKullaniciId(rs.getInt("KullaniciId"));
				v.setPuan(rs.getInt("Puan"));
				v.setYorumMetni(rs.getString("YorumMetni"));

				Timestamp ts = rs.getTimestamp("YorumTarihi");
				v.setYorumTarihi(ts == null ? null : ts.toLocalDateTime());

				v.setKullaniciAdi(rs.getString("KullaniciAdi"));
				v.setKullaniciAd(rs.getString("KullaniciAd"));
				v.setKullaniciSoyad(rs.getString("KullaniciSoyad"));
				return v;
			},
			kitapId
		);
	}

	public String toggleFavorite(int userId, int kitapId) {
    // SP geriye "Eklendi" veya "Cikarildi" diye String döndürüyor.
    // Bunu yakalamak için queryForObject kullanılıyor.
    String sonuc = jdbc.queryForObject("""
        EXEC dbo.sp_FavoriToggle @KullaniciId = ?, @KitapId = ?;
    """, String.class, userId, kitapId);

    return (sonuc == null) ? "?" : sonuc;
}
	public int addToCart(int kullaniciId, int kitapId, int adet) {
		return jdbc.execute((Connection con) -> {
			CallableStatement cs = con.prepareCall("{call dbo.sp_SepeteEkle(?,?,?,?)}");
			cs.setInt(1, kullaniciId);
			cs.setInt(2, kitapId);
			cs.setInt(3, adet);
			cs.registerOutParameter(4, Types.INTEGER);
			return cs;
		}, (CallableStatement cs) -> {
			cs.execute();
			return cs.getInt(4);
		});
	}

	private BookDetailView mapBookDetail(ResultSet rs) throws SQLException {
		BookDetailView v = new BookDetailView();
		v.setKitapId(rs.getInt("KitapId"));
		v.setTitle(rs.getString("Title"));
		v.setPrice(rs.getBigDecimal("Price"));
		v.setStock(rs.getInt("Stock"));

		int pc = rs.getInt("PageCount");
		v.setPageCount(rs.wasNull() ? null : pc);

		int py = rs.getInt("PublishYear");
		v.setPublishYear(rs.wasNull() ? null : py);

		v.setDescription(rs.getString("Description"));

		int pubId = rs.getInt("PublisherId");
		v.setPublisherId(rs.wasNull() ? null : pubId);

		v.setPublisherName(rs.getString("PublisherName"));
		v.setAvgRating(rs.getBigDecimal("AvgRating"));
		v.setReviewCount(rs.getInt("ReviewCount"));
		v.setFavorite(rs.getBoolean("IsFavorite"));
		return v;
	}
}