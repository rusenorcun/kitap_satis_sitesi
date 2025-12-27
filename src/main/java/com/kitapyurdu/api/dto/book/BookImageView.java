package com.kitapyurdu.api.dto.book;

public class BookImageView {
	private int kitapGorselId;
	private int kitapId;
	private String gorselYolu;
	private Integer siraNo;

	public int getKitapGorselId() { return kitapGorselId; }
	public void setKitapGorselId(int kitapGorselId) { this.kitapGorselId = kitapGorselId; }

	public int getKitapId() { return kitapId; }
	public void setKitapId(int kitapId) { this.kitapId = kitapId; }

	public String getGorselYolu() { return gorselYolu; }
	public void setGorselYolu(String gorselYolu) { this.gorselYolu = gorselYolu; }

	public Integer getSiraNo() { return siraNo; }
	public void setSiraNo(Integer siraNo) { this.siraNo = siraNo; }
}