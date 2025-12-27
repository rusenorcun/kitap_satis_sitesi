package com.kitapyurdu.api.service;

import com.kitapyurdu.api.dto.book.*;
import com.kitapyurdu.api.repository.BookDetailRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookDetailService {

	private final BookDetailRepo repo;

	public BookDetailService(BookDetailRepo repo) {
		this.repo = repo;
	}

	public Integer resolveUserId(String username) {
		if (username == null || username.isBlank())
			return null;

		return repo.findUserIdByUsername(username);
	}

	public BookDetailView getDetail(int kitapId, Integer kullaniciId) {
		return repo.getBookDetail(kitapId, kullaniciId);
	}

	public List<BookImageView> images(int kitapId) { return repo.listImages(kitapId); }
	public List<AuthorView> authors(int kitapId) { return repo.listAuthors(kitapId); }
	public List<CategoryView> categories(int kitapId) { return repo.listCategories(kitapId); }
	public List<ReviewView> reviews(int kitapId) { return repo.listReviews(kitapId); }

	public void toggleFavorite(int kullaniciId, int kitapId) {
		repo.toggleFavorite(kullaniciId, kitapId);
	}

	public int addToCart(int kullaniciId, int kitapId, int adet) {
		return repo.addToCart(kullaniciId, kitapId, adet);
	}
}