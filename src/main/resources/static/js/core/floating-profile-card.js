/**
 * Floating Profile Card - Scroll fold/unfold behavior (desktop).
 */

class FloatingProfileCard {
    constructor(profileCard) {
        this.profileCard = profileCard;
        this.lastScrollY = window.scrollY || window.pageYOffset || 0;
        this.threshold = 24;
        this.foldStartOffset = 56;
        this.isFolded = false;
        this.ticking = false;
        this.onScroll = this.onScroll.bind(this);
        this.onResize = this.onResize.bind(this);
    }

    init() {
        window.addEventListener('scroll', this.onScroll, { passive: true });
        window.addEventListener('resize', this.onResize);
        this.profileCard.addEventListener('click', () => this.unfold());
        this.profileCard.addEventListener('focusin', () => this.unfold());
        this.onResize();
    }

    onResize() {
        if (window.innerWidth < 1024) {
            this.profileCard.classList.remove('is-folded');
            this.isFolded = false;
        }
    }

    onScroll() {
        if (this.ticking) {
            return;
        }
        this.ticking = true;
        window.requestAnimationFrame(() => {
            this.handleScroll();
            this.ticking = false;
        });
    }

    handleScroll() {
        if (window.innerWidth < 1024) {
            this.lastScrollY = window.scrollY || window.pageYOffset || 0;
            return;
        }

        const currentY = window.scrollY || window.pageYOffset || 0;
        const delta = currentY - this.lastScrollY;

        if (currentY <= 10) {
            this.unfold();
        } else if (delta > this.threshold && currentY > this.foldStartOffset) {
            this.fold();
        } else if (delta < -this.threshold) {
            this.unfold();
        }

        this.lastScrollY = currentY;
    }

    fold() {
        if (this.isFolded) {
            return;
        }
        this.isFolded = true;
        this.profileCard.classList.add('is-folded');
    }

    unfold() {
        if (!this.isFolded) {
            return;
        }
        this.isFolded = false;
        this.profileCard.classList.remove('is-folded');
    }
}

function initFloatingProfileCards() {
    document.querySelectorAll('[data-floating-profile]').forEach((el) => {
        const controller = new FloatingProfileCard(el);
        controller.init();
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initFloatingProfileCards);
} else {
    initFloatingProfileCards();
}
