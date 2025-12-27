package com.kitapyurdu.api.dto.siparis;

import java.math.BigDecimal;

public class CartSummaryDto {

	private BigDecimal araToplam;
	private BigDecimal indirimToplam;
	private BigDecimal kargoUcreti;
	private BigDecimal genelToplam;
	private int kalemSayisi;

	public CartSummaryDto() { }

	public CartSummaryDto(BigDecimal araToplam, BigDecimal indirimToplam, BigDecimal kargoUcreti, BigDecimal genelToplam, int kalemSayisi) {
		this.araToplam = araToplam;
		this.indirimToplam = indirimToplam;
		this.kargoUcreti = kargoUcreti;
		this.genelToplam = genelToplam;
		this.kalemSayisi = kalemSayisi;
	}

	// Getters and Setters
	public BigDecimal getAraToplam() { return araToplam; }
	public void setAraToplam(BigDecimal araToplam) { this.araToplam = araToplam; }

	public BigDecimal getIndirimToplam() { return indirimToplam; }
	public void setIndirimToplam(BigDecimal indirimToplam) { this.indirimToplam = indirimToplam; }

	public BigDecimal getKargoUcreti() { return kargoUcreti; }
	public void setKargoUcreti(BigDecimal kargoUcreti) { this.kargoUcreti = kargoUcreti; }

	public BigDecimal getGenelToplam() { return genelToplam; }
	public void setGenelToplam(BigDecimal genelToplam) { this.genelToplam = genelToplam; }

	public int getKalemSayisi() { return kalemSayisi; }
	public void setKalemSayisi(int kalemSayisi) { this.kalemSayisi = kalemSayisi; }
}
