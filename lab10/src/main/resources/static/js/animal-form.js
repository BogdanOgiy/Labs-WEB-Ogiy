const TOKEN_KEY = "LAB10.jwt";

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function withAuthHeaders(headers = {}) {
    const token = getToken();
    return {
        ...headers,
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };
}

async function sendJson(url, options = {}) {
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
            ? "Log in from the catalog page before editing protected data."
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

document.addEventListener("DOMContentLoaded", async () => {
    const app = document.getElementById("animalFormApp");
    const apiBase = app.dataset.apiBase;
    const formMode = app.dataset.formMode;
    const itemId = app.dataset.itemId;
    const form = document.getElementById("animalForm");
    const errorBox = document.getElementById("formError");
    const submitButton = document.getElementById("submitButton");
    const imageUrlInput = document.getElementById("imageUrl");
    const imagePreview = document.getElementById("imagePreview");
    const previewHint = document.getElementById("previewHint");
    const loadingBox = document.getElementById("loadingBox");

    if (!getToken()) {
        errorBox.textContent = "Log in from the catalog page before creating or editing cards.";
        errorBox.classList.remove("d-none");
        submitButton.disabled = true;
    }


    function updateImageUrlPreview() {
        const url = imageUrlInput.value.trim();
        if (!url) {
            previewHint.textContent = "Paste URL to see photo preview.";
            imagePreview.classList.add("d-none");
            imagePreview.removeAttribute("src");
            return;
        }

        previewHint.textContent = "Photo loaded from URL.";
        imagePreview.src = url;
        imagePreview.classList.remove("d-none");
    }

    function fillForm(item) {
        form.name.value = item.name || "";
        form.scientificName.value = item.scientificName || "";
        form.description.value = item.description || "";
        form.populationLeft.value = item.populationLeft ?? "";
        form.imageUrl.value = item.imageUrl || "";
        updateImageUrlPreview();
    }

    async function loadItem() {
        if (formMode !== "edit" || !itemId) {
            return;
        }

        loadingBox.classList.remove("d-none");
        try {
            const item = await sendJson(`${apiBase}/${itemId}`);
            fillForm(item);
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.classList.remove("d-none");
            form.classList.add("d-none");
        } finally {
            loadingBox.classList.add("d-none");
        }
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        errorBox.classList.add("d-none");
        submitButton.disabled = true;

        const payload = {
            name: form.name.value.trim(),
            scientificName: form.scientificName.value.trim(),
            description: form.description.value.trim(),
            populationLeft: form.populationLeft.value ? Number(form.populationLeft.value) : null,
            imageUrl: form.imageUrl.value.trim()
        };

        const method = formMode === "edit" ? "PUT" : "POST";
        const url = formMode === "edit" ? `${apiBase}/${itemId}` : apiBase;

        try {
            await sendJson(url, {
                method,
                body: JSON.stringify(payload)
            });
            window.location.href = "/items";
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.classList.remove("d-none");
        } finally {
            submitButton.disabled = false;
        }
    });

    imageUrlInput.addEventListener("input", updateImageUrlPreview);
    updateImageUrlPreview();
    await loadItem();
});


