<script setup>
import { computed, ref } from 'vue';

const animals = ref([]);
const token = ref(localStorage.getItem('token') || '');
const username = ref('admin');
const password = ref('password');
const status = ref('');
const loading = ref(false);

const form = ref({
  name: '',
  scientificName: '',
  description: '',
  populationLeft: 0,
  imageUrl: ''
});

const isAuthenticated = computed(() => Boolean(token.value));
const totalPopulation = computed(() =>
  animals.value.reduce((sum, animal) => sum + Number(animal.populationLeft || 0), 0)
);

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (options.auth !== false && token.value) {
    headers.Authorization = `Bearer ${token.value}`;
  }

  const response = await fetch(path, {
    ...options,
    headers
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get('content-type') || '';
  return contentType.includes('application/json') ? response.json() : response.text();
}

async function register() {
  loading.value = true;
  status.value = '';

  try {
    await request('/register', {
      method: 'POST',
      auth: false,
      body: JSON.stringify({ username: username.value, password: password.value })
    });
    status.value = 'User registered. You can log in now.';
  } catch (error) {
    status.value = error.message;
  } finally {
    loading.value = false;
  }
}

async function login() {
  loading.value = true;
  status.value = '';

  try {
    const data = await request('/login', {
      method: 'POST',
      auth: false,
      body: JSON.stringify({ username: username.value, password: password.value })
    });
    token.value = data.token;
    localStorage.setItem('token', data.token);
    status.value = 'Logged in.';
    await loadAnimals();
  } catch (error) {
    status.value = error.message;
  } finally {
    loading.value = false;
  }
}

function logout() {
  token.value = '';
  localStorage.removeItem('token');
  animals.value = [];
}

async function loadAnimals() {
  if (!token.value) {
    status.value = 'Log in to load the catalog.';
    return;
  }

  loading.value = true;
  status.value = '';

  try {
    animals.value = await request('/api/animals');
  } catch (error) {
    status.value = error.message;
  } finally {
    loading.value = false;
  }
}

async function createAnimal() {
  loading.value = true;
  status.value = '';

  try {
    await request('/api/animals', {
      method: 'POST',
      body: JSON.stringify({
        ...form.value,
        populationLeft: Number(form.value.populationLeft)
      })
    });
    form.value = {
      name: '',
      scientificName: '',
      description: '',
      populationLeft: 0,
      imageUrl: ''
    };
    await loadAnimals();
  } catch (error) {
    status.value = error.message;
  } finally {
    loading.value = false;
  }
}

async function removeAnimal(id) {
  loading.value = true;
  status.value = '';

  try {
    await request(`/api/animals/${id}`, { method: 'DELETE' });
    await loadAnimals();
  } catch (error) {
    status.value = error.message;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="shell">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">Rare species registry</p>
        <h1>Rare Animals Catalog</h1>
        <p class="lead">
          Track endangered species, population estimates, notes, and reference images in one compact catalog.
        </p>
      </div>
      <div class="hero-stats">
        <div>
          <span>{{ animals.length }}</span>
          <p>records</p>
        </div>
        <div>
          <span>{{ totalPopulation }}</span>
          <p>animals left</p>
        </div>
      </div>
      <button v-if="isAuthenticated" class="ghost-button" type="button" @click="logout">Log out</button>
    </section>

    <div class="workspace">
      <aside class="panel auth-panel">
        <div class="panel-heading">
          <h2>Access</h2>
          <p>{{ isAuthenticated ? 'Token saved locally' : 'Register or sign in' }}</p>
        </div>
        <div class="field">
          <label for="username">Username</label>
          <input id="username" v-model="username" autocomplete="username" />
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" autocomplete="current-password" />
        </div>
        <div class="button-row">
          <button type="button" :disabled="loading" @click="register">Register</button>
          <button type="button" :disabled="loading" @click="login">Log in</button>
        </div>
      </aside>

      <section class="panel form-panel">
        <div class="panel-heading wide">
          <h2>Add a species</h2>
          <p>Authenticated requests are sent to the Spring Boot API through the frontend container.</p>
        </div>
        <div class="field">
          <label for="name">Name</label>
          <input id="name" v-model="form.name" placeholder="Amur leopard" />
        </div>
        <div class="field">
          <label for="scientificName">Scientific name</label>
          <input id="scientificName" v-model="form.scientificName" placeholder="Panthera pardus orientalis" />
        </div>
        <div class="field">
          <label for="population">Population left</label>
          <input id="population" v-model="form.populationLeft" type="number" min="0" />
        </div>
        <div class="field wide">
          <label for="imageUrl">Image URL</label>
          <input id="imageUrl" v-model="form.imageUrl" placeholder="https://..." />
        </div>
        <div class="field wide">
          <label for="description">Description</label>
          <textarea id="description" v-model="form.description" rows="3" placeholder="Short conservation note"></textarea>
        </div>
        <div class="button-row wide">
          <button type="button" :disabled="loading || !isAuthenticated" @click="createAnimal">
            Add animal
          </button>
          <button class="secondary-button" type="button" :disabled="loading || !isAuthenticated" @click="loadAnimals">
            Refresh
          </button>
        </div>
      </section>
    </div>

    <p v-if="status" class="status">{{ status }}</p>

    <section v-if="!animals.length" class="empty-state">
      <h2>No animals loaded</h2>
      <p>Log in and press Refresh, or add the first record using the form above.</p>
    </section>

    <section class="grid">
      <article v-for="animal in animals" :key="animal.id" class="card">
        <div class="image-frame">
          <img v-if="animal.imageUrl" :src="animal.imageUrl" :alt="animal.name" />
          <div v-else class="image-placeholder">{{ animal.name?.slice(0, 1) || '?' }}</div>
        </div>
        <div class="card-body">
          <h2>{{ animal.name }}</h2>
          <p class="scientific">{{ animal.scientificName }}</p>
          <p>{{ animal.description }}</p>
          <div class="meta">
            <span>{{ animal.populationLeft ?? 0 }} left</span>
            <button type="button" :disabled="loading" @click="removeAnimal(animal.id)">Delete</button>
          </div>
        </div>
      </article>
    </section>
  </main>
</template>
