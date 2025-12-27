(function () {
	// ---- config
	const PARAM = "mesaj";
	const DURATION = 3200; // ms

	function decodeMessage(raw) {
		if (!raw) return null;
		try {
			// URLSearchParams zaten decode eder, ama + bazen boşluk gibi gelir
			return raw.replace(/\+/g, " ").trim();
		} catch (_) {
			return raw;
		}
	}

	function ensureStack() {
		let stack = document.querySelector(".toastStack");
		if (!stack) {
			stack = document.createElement("div");
			stack.className = "toastStack";
			document.body.appendChild(stack);
		}
		return stack;
	}

	function classify(msg) {
		const m = (msg || "").toLowerCase();
		if (m.startsWith("hata") || m.includes("hata")) return "error";
		if (m.includes("silindi") || m.includes("eklendi") || m.includes("güncellendi") || m.includes("kaydedildi")) return "success";
		if (m.includes("önce") || m.includes("bağlı") || m.includes("izin")) return "warn";
		return "info";
	}

	function showToast(message, type) {
		const stack = ensureStack();

		const toast = document.createElement("div");
		toast.className = "toast toast--" + (type || "info");
		toast.setAttribute("role", "status");
		toast.setAttribute("aria-live", "polite");

		const icon = document.createElement("span");
		icon.className = "toast__icon";
		icon.textContent =
			type === "success" ? "✓" :
			type === "error"   ? "!" :
			type === "warn"    ? "!" : "i";

		const text = document.createElement("div");
		text.className = "toast__text";
		text.textContent = message;

		const close = document.createElement("button");
		close.className = "toast__close";
		close.type = "button";
		close.setAttribute("aria-label", "Kapat");
		close.textContent = "×";

		close.addEventListener("click", () => {
			toast.classList.add("is-leaving");
			setTimeout(() => toast.remove(), 180);
		});

		toast.appendChild(icon);
		toast.appendChild(text);
		toast.appendChild(close);
		stack.appendChild(toast);

		// auto hide
		setTimeout(() => {
			if (!toast.isConnected) return;
			toast.classList.add("is-leaving");
			setTimeout(() => toast.remove(), 180);
		}, DURATION);
	}

	function removeParamFromUrl(param) {
		try {
			const url = new URL(window.location.href);
			url.searchParams.delete(param);
			// diğer parametreler kalsın (q/editId vs)
			window.history.replaceState({}, "", url.pathname + url.search + url.hash);
		} catch (_) {}
	}

	// 1) URL'den mesaj oku ve toast göster
	const params = new URLSearchParams(window.location.search);
	const raw = params.get(PARAM);
	const msg = decodeMessage(raw);

	if (msg) {
		showToast(msg, classify(msg));
		removeParamFromUrl(PARAM);
	}

	// 2) (Opsiyonel) Sayfada server-side alert varsa onu da toast'a çevir
	//    admin sayfalarında eski <div class="alert"> varsa kaybolmasın diye:
	const alert = document.querySelector(".alert[data-toast='1']");
	if (alert && alert.textContent && alert.textContent.trim()) {
		const m2 = alert.textContent.trim();
		showToast(m2, classify(m2));
		alert.style.display = "none";
	}

	// global erişim (istersen controller dışında js ile de kullanırsın)
	window.KYToast = {
		show: (m, t) => showToast(String(m || ""), t || classify(m))
	};
})();

(function(){
    const p = new URLSearchParams(location.search);

    if (p.has("mesaj") && window.KYToast) {
		KYToast.show(decodeURIComponent(p.get("mesaj")), p.has("reg_hata") ? "error" : "info");
		p.delete("mesaj");
		history.replaceState({}, "", location.pathname + (p.toString()?("?"+p.toString()):""));
    }

    // mevcut login toastların kalsın:
    if (p.has("hata") && window.KYToast) {
		KYToast.show("Hata: Kullanıcı adı veya şifre yanlış.", "error");
		p.delete("hata");
		history.replaceState({}, "", location.pathname + (p.toString()?("?"+p.toString()):""));
    }

    if (p.has("cikis") && window.KYToast) {
		KYToast.show("Çıkış yapıldı.", "success");
		p.delete("cikis");
		history.replaceState({}, "", location.pathname + (p.toString()?("?"+p.toString()):""));
    }

    if (p.has("kayit") && window.KYToast) {
		KYToast.show("Kayıt başarılı. Lütfen giriş yapınız.", "success");
		p.delete("kayit");
		p.delete("tab");
		history.replaceState({}, "", location.pathname + (p.toString()?("?"+p.toString()):""));
    }
})();
