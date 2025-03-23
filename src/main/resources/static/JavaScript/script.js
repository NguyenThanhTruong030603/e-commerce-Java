const heroForegroundImages = document.querySelectorAll('.hero-foreground-image');
const prevBtn = document.querySelector('.prev-btn');
const nextBtn = document.querySelector('.next-btn');

let currentIndex = 0;

function switchImage(index) {
    heroForegroundImages.forEach((image, i) => {
        image.classList.toggle('active', i === index);
    });
}

prevBtn.addEventListener('click', () => {
    currentIndex = (currentIndex - 1 + heroForegroundImages.length) % heroForegroundImages.length;
    switchImage(currentIndex);
});

nextBtn.addEventListener('click', () => {
    currentIndex = (currentIndex + 1) % heroForegroundImages.length;
    switchImage(currentIndex);
});

// Auto-switch images after 5 seconds
setInterval(() => {
    currentIndex = (currentIndex + 1) % heroForegroundImages.length;
    switchImage(currentIndex);
}, 5000);