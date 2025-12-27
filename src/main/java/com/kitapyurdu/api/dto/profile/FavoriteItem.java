package com.kitapyurdu.api.dto.profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FavoriteItem {
	public Integer kitapId;
	public String kitapAdi;
	public BigDecimal fiyat;
	public Integer stok;
	public String yayineviAdi;
	public LocalDateTime eklenmeTarihi;
}
