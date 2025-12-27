package com.kitapyurdu.api.dto.kitap;

import java.math.BigDecimal;

public record KitapListeRow(
	int kitapId,
	String kitapAdi,
	BigDecimal fiyat,
	int stok,
	boolean durum,
	String yayineviAdi,
	String kategoriler,
	BigDecimal ortalamaPuan,
	long yorumSayisi
) {}
