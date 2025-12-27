(function () {
	const KEY = "ky_theme"; // "light" | "dark"

	function applyTheme(theme) {
		document.body.classList.remove("theme-light", "theme-dark");
		document.body.classList.add(theme === "dark" ? "theme-dark" : "theme-light");
	}

	function getInitialTheme() {
		const saved = localStorage.getItem(KEY);
		if (saved === "light" || saved === "dark") return saved;

		// sistem tercihi
		try {
			if (window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches) {
				return "dark";
			}
		} catch (_) {}
		return "light";
	}

	function setTheme(theme) {
		localStorage.setItem(KEY, theme);
		applyTheme(theme);
		updateButton();
	}

	function currentTheme() {
		return document.body.classList.contains("theme-dark") ? "dark" : "light";
	}

	function updateButton() {
		const btn = document.querySelector("[data-theme-toggle]");
		if (!btn) return;

		const t = currentTheme();
		btn.setAttribute("aria-pressed", t === "dark" ? "true" : "false");
		btn.dataset.theme = t;

		// ikon + label (çok minimal)
		btn.innerHTML = (t === "dark")
			? `<span class="themeIcon">🌙</span><span class="themeText">Koyu</span>`
			: `<span class="themeIcon">☀️</span><span class="themeText">Açık</span>`;
	}

	// init
	const initial = getInitialTheme();
	applyTheme(initial);

	// click binding
	document.addEventListener("click", function (e) {
		const btn = e.target.closest("[data-theme-toggle]");
		if (!btn) return;

		const t = currentTheme();
		setTheme(t === "dark" ? "light" : "dark");
	});

	// after DOM ready
	document.addEventListener("DOMContentLoaded", updateButton);

	// expose (opsiyonel)
	window.KYTheme = {
		set: setTheme,
		get: () => currentTheme()
	};
})();
