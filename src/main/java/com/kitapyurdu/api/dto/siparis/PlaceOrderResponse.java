package com.kitapyurdu.api.dto.siparis;

public class PlaceOrderResponse {

	private Integer siparisId;
	private String hata;

	public PlaceOrderResponse() { }

	public PlaceOrderResponse(Integer siparisId, String hata) {
		this.siparisId = siparisId;
		this.hata = hata;
	}

	public Integer getSiparisId() { return siparisId; }
	public void setSiparisId(Integer siparisId) { this.siparisId = siparisId; }

	public String getHata() { return hata; }
	public void setHata(String hata) { this.hata = hata; }
}
