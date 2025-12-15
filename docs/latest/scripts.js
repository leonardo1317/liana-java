// scripts.js
const content = document.getElementById("content");
const navLinks = document.querySelectorAll("nav a[data-section]");

// Carga contenido dinámico
async function loadSection(section) {
  try {
    const response = await fetch(`sections/${section}.html`);
    if (!response.ok) throw new Error("Section not found");
    content.innerHTML = await response.text();
    setActiveLink(section);
  } catch (err) {
    content.innerHTML = `<h1>Error</h1>
                         <p>${err.message}</p>`;
  }
}

// Marca el link activo
function setActiveLink(section) {
  navLinks.forEach(link => {
    link.classList.toggle("active", link.dataset.section === section);
  });
}

// Maneja clicks y actualiza hash
navLinks.forEach(link => {
  link.addEventListener("click", e => {
    e.preventDefault();
    location.hash = link.dataset.section;
  });
});

// Carga la sección según hash
function loadFromHash() {
  const section = location.hash.replace("#", "") || "overview";
  loadSection(section);
}

window.addEventListener("hashchange", loadFromHash);
loadFromHash(); // carga inicial
