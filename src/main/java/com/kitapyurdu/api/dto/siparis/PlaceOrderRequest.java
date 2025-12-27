package com.kitapyurdu.api.dto.siparis;

public class PlaceOrderRequest {

	private Integer adresId;
	private String odemeYontemi;

	public PlaceOrderRequest() { }

	public Integer getAdresId() { return adresId; }
	public void setAdresId(Integer adresId) { this.adresId = adresId; }

	public String getOdemeYontemi() { return odemeYontemi; }
	public void setOdemeYontemi(String odemeYontemi) { this.odemeYontemi = odemeYontemi; }
}
