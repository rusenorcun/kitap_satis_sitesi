(function () {
	// Sayfa bazlı anahtar: /admin, /admin/kitap gibi
	function keyForPath(pathname) {
		return "scrollY::" + pathname;
	}

	function saveScrollForCurrentPage() {
		try {
			sessionStorage.setItem(keyForPath(window.location.pathname), String(window.scrollY || 0));
		} catch (_) {}
	}

	function restoreScrollForCurrentPage() {
		try {
			const k = keyForPath(window.location.pathname);
			const v = sessionStorage.getItem(k);
			if (v == null) return;

			const y = Number(v);
			if (!Number.isFinite(y)) return;

			// Tarayıcı layout otursun diye bir tick sonra kaydır
			requestAnimationFrame(() => {
				window.scrollTo({ top: y, behavior: "auto" });
			});
		} catch (_) {}
	}

	// 1) Link tıklanınca mevcut sayfanın scroll’unu kaydet
	document.addEventListener(
		"click",
		function (e) {
			const a = e.target.closest("a");
			if (!a) return;

			const href = a.getAttribute("href");
			if (!href || href.startsWith("#") || href.startsWith("javascript:")) return;

			// Aynı origin değilse (external) dokunma
			if (/^https?:\/\//i.test(href)) return;

			saveScrollForCurrentPage();
		},
		true
	);

	// 2) Form submit olunca da kaydet (GET/POST fark etmez)
	document.addEventListener(
		"submit",
		function (e) {
			const form = e.target;
			if (!(form instanceof HTMLFormElement)) return;
			saveScrollForCurrentPage();
		},
		true
	);

	// 3) Sayfa yüklenince restore et
	window.addEventListener("load", function () {
		restoreScrollForCurrentPage();
	});

	// 4) Ek garanti: kullanıcı başka sayfaya geçerken de kaydet
	window.addEventListener("beforeunload", function () {
		saveScrollForCurrentPage();
	});
})();
