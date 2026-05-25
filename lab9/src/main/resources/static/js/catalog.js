const TOKEN_KEY = "lab9.jwt";

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function withAuthHeaders(headers = {}) {
    const token = getToken();
    return {
        ...headers,
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };
}

async function fetchJson(url, options = {}) {
    const headers = withAuthHeaders({
        Accept: "application/hal+json, application/json",
        "Content-Type": "application/json",
        ...(options.headers || {})
    });

    const response = await fetch(url, {
        ...options,
        headers
    });

    if (!response.ok) {
        let message = response.status === 401
            ? "Log in to access the protected catalog API."
            : `Request failed with status ${response.status}`;
        try {
            const errorBody = await response.json();
            message = errorBody.message || errorBody.error || message;
        } catch (error) {
            const text = await response.text();
            if (text) {
                message = text;
            }
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

function renderImage(url, alt) {
    if (!url) {
        return `
            <div class="animal-image-placeholder">
                Image not specified
            </div>
        `;
    }

    return `<img class="animal-image" src="${url}" alt="${alt}">`;
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function createCard(item, index) {
    const population = item.populationLeft ?? 0;
    const displayId = index + 1;
    return `
        <article class="animal-card h-100 d-flex flex-column">
            ${renderImage(item.imageUrl, escapeHtml(item.name || "Rare animal"))}
            <div class="p-3 p-lg-4 d-flex flex-column h-100">
                <div class="d-flex justify-content-between align-items-start gap-2 mb-2">
                    <h2 class="h4 mb-0 page-title">${escapeHtml(item.name || "Unnamed animal")}</h2>
                    <span class="chip">ID ${displayId}</span>
                </div>
                <p class="animal-meta mb-2">${escapeHtml(item.scientificName || "Scientific name not specified.")}</p>
                <p class="animal-description mb-3">${escapeHtml(item.description || "Description not specified.")}</p>
                <div class="mt-auto d-flex justify-content-between align-items-center gap-2 flex-wrap">
                    <span class="chip">Population left: ${escapeHtml(population)}</span>
                    <div class="d-flex gap-2">
                        <a class="btn btn-sm btn-edit" href="/items/${item.id}/edit">Edit</a>
                        <button class="btn btn-sm btn-delete" data-action="delete" data-id="${item.id}" type="button">Delete</button>
                    </div>
                </div>
            </div>
        </article>
    `;
}

document.addEventListener("DOMContentLoaded", async () => {
    const app = document.getElementById("catalogApp");
    const apiBase = app.dataset.apiBase;
    const listContainer = document.getElementById("animalList");
    const emptyState = document.getElementById("emptyState");
    const loadingState = document.getElementById("loadingState");
    const errorBox = document.getElementById("errorBox");
    const totalCards = document.getElementById("totalCards");
    const addCardLink = document.getElementById("addCardLink");
    const authForm = document.getElementById("authForm");
    const authStatus = document.getElementById("authStatus");
    const logoutButton = document.getElementById("logoutButton");
    const authUsername = document.getElementById("authUsername");
    const authPassword = document.getElementById("authPassword");

    function updateAuthUi() {
        const isLoggedIn = Boolean(getToken());
        authStatus.textContent = isLoggedIn
            ? "You are logged in."
            : "Log in or register to load and edit catalog data.";
        logoutButton.classList.toggle("d-none", !isLoggedIn);
        addCardLink.classList.toggle("disabled", !isLoggedIn);
        addCardLink.setAttribute("aria-disabled", String(!isLoggedIn));
    }

    async function loadItems() {
        loadingState.classList.remove("d-none");
        errorBox.classList.add("d-none");
        listContainer.innerHTML = "";

        try {
            const payload = await fetchJson(`${apiBase}?sort=id,asc`);
            const items = Array.isArray(payload) ? payload : payload?._embedded?.rareAnimals ?? [];

            totalCards.textContent = `Total cards: ${items.length}`;
            emptyState.classList.toggle("d-none", items.length > 0);
            listContainer.innerHTML = items
                .map((item, index) => `<div class="col-12 col-md-6 col-xl-4">${createCard(item, index)}</div>`)
                .join("");
        } catch (error) {
            totalCards.textContent = "Total cards: 0";
            emptyState.classList.add("d-none");
            errorBox.textContent = error.message;
            errorBox.classList.remove("d-none");
        } finally {
            loadingState.classList.add("d-none");
        }
    }

    async function login(username, password) {
        const payload = await fetchJson("/login", {
            method: "POST",
            body: JSON.stringify({ username, password })
        });
        setToken(payload.token);
    }

    async function register(username, password) {
        await fetchJson("/register", {
            method: "POST",
            body: JSON.stringify({ username, password })
        });
        await login(username, password);
    }

    authForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const submitter = event.submitter;
        const action = submitter?.dataset.authAction || "login";
        const username = authUsername.value.trim();
        const password = authPassword.value;

        errorBox.classList.add("d-none");
        submitter.disabled = true;

        try {
            if (action === "register") {
                await register(username, password);
            } else {
                await login(username, password);
            }
            authPassword.value = "";
            updateAuthUi();
            await loadItems();
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.classList.remove("d-none");
        } finally {
            submitter.disabled = false;
        }
    });

    logoutButton.addEventListener("click", async () => {
        clearToken();
        updateAuthUi();
        totalCards.textContent = "Total cards: 0";
        listContainer.innerHTML = "";
        emptyState.classList.add("d-none");
        errorBox.textContent = "You are logged out. Log in to load protected catalog data.";
        errorBox.classList.remove("d-none");
    });

    listContainer.addEventListener("click", async (event) => {
        const button = event.target.closest("[data-action='delete']");
        if (!button) {
            return;
        }

        const { id } = button.dataset;
        const confirmed = window.confirm("Delete this animal card?");
        if (!confirmed) {
            return;
        }

        button.disabled = true;
        try {
            await fetchJson(`${apiBase}/${id}`, { method: "DELETE" });
            await loadItems();
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.classList.remove("d-none");
        } finally {
            button.disabled = false;
        }
    });

    updateAuthUi();
    await loadItems();
});
