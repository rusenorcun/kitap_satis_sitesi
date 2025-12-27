package com.kitapyurdu.api.dto.siparis;

public class AddressDto {

	private int adresId;
	private String baslik;
	private String il;
	private String ilce;
	private String mahalle;
	private String adresDetay;
	private String postaKodu;

	public AddressDto() { }

	public AddressDto(int adresId, String baslik, String il, String ilce, String mahalle, String adresDetay, String postaKodu) {
		this.adresId = adresId;
		this.baslik = baslik;
		this.il = il;
		this.ilce = ilce;
		this.mahalle = mahalle;
		this.adresDetay = adresDetay;
		this.postaKodu = postaKodu;
	}
	// Getters and Setters
	public int getAdresId() { return adresId; }
	public void setAdresId(int adresId) { this.adresId = adresId; }

	public String getBaslik() { return baslik; }
	public void setBaslik(String baslik) { this.baslik = baslik; }

	public String getIl() { return il; }
	public void setIl(String il) { this.il = il; }

	public String getIlce() { return ilce; }
	public void setIlce(String ilce) { this.ilce = ilce; }

	public String getMahalle() { return mahalle; }
	public void setMahalle(String mahalle) { this.mahalle = mahalle; }

	public String getAdresDetay() { return adresDetay; }
	public void setAdresDetay(String adresDetay) { this.adresDetay = adresDetay; }

	public String getPostaKodu() { return postaKodu; }
	public void setPostaKodu(String postaKodu) { this.postaKodu = postaKodu; }
}
