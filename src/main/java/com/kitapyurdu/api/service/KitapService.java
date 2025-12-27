package com.kitapyurdu.api.service;

import com.kitapyurdu.api.dto.kitap.KitapListeRow;
import com.kitapyurdu.api.repository.KitapRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KitapService {

	private final KitapRepository repo;

	public KitapService(KitapRepository repo) { this.repo = repo; }

	public List<KitapListeRow> liste(String q) {
		if (q != null && q.isBlank()) q = null;
		return repo.list(q);
	}
}
