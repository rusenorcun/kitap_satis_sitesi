package com.kitapyurdu.api.service;

import com.kitapyurdu.api.dto.profile.*;
import com.kitapyurdu.api.repository.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository repo;
    private final PasswordEncoder encoder;

    public ProfileService(ProfileRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public int requireUserId(String principalName) {
        return repo.findUserIdByPrincipal(principalName)
                .orElseThrow(() -> new RuntimeException(
                        "Profil bulunamadı. principalName=" + principalName +
                        " (DB’de KullaniciAdi veya Eposta eşleşmiyor olabilir)"
                ));
    }

    public UserProfile profile(int userId) {
        UserProfile p = repo.getProfile(userId)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı. userId=" + userId));

        if (p.getDurum() != null && !p.getDurum()) {
            throw new RuntimeException("Kullanıcı pasif durumda (Durum=0).");
        }
        return p;
    }

    public List<UserAddress> addresses(int userId) {
        return repo.listAddresses(userId);
    }

    public List<FavoriteBook> favorites(int userId) {
        return repo.listFavorites(userId);
    }

    public void addAddress(int userId, AddressCreateForm f) {
        repo.spAdresEkle(userId, f);
    }

    public void setDefaultAddress(int userId, int adresId) {
        repo.setDefaultAddress(userId, adresId);
    }

    public void deleteAddress(int userId, int adresId) {
        repo.deleteAddress(userId, adresId);
    }

    public String toggleFavorite(int userId, int kitapId) {
        return repo.spFavoriToggle(userId, kitapId);
    }

    public void updateProfile(int userId, ProfileUpdateForm f) {
        if (f.getKullaniciAdi() == null || f.getKullaniciAdi().isBlank()) {
            throw new RuntimeException("Kullanıcı adı boş olamaz.");
        }
        if (f.getEposta() == null || f.getEposta().isBlank()) {
            throw new RuntimeException("E-posta boş olamaz.");
        }
        throw new RuntimeException("hata.");
    }

    public void changePassword(int userId, PasswordChangeForm f) {
        if (f.getNewPassword() == null || f.getNewPassword().length() < 6) {
            throw new RuntimeException("Yeni şifre en az 6 karakter olmalı.");
        }
        if (!f.getNewPassword().equals(f.getNewPassword2())) {
            throw new RuntimeException("Yeni şifreler eşleşmiyor.");
        }
        throw new RuntimeException("Hata");
    }

    public List<UserOrder> orders(int userId) {
        return repo.listOrders(userId);
    }
}
