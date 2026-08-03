package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PublicPageConsistencyContractTest {

    @Test
    void longPublicPagesUseACompactFourStageJourney() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(count(home, "<section")).isEqualTo(4);
        assertThat(count(about, "<section")).isEqualTo(4);
        assertThat(count(pricing, "<section") + count(pricing, "<header")).isEqualTo(4);

        assertThat(home).doesNotContain("Support feedback", "Precision Engineered", "Cycle of Progress");
        assertThat(about).doesNotContain("Platform Features", "Personalized", "Programs", "centralized");
        assertThat(pricing).doesNotContain("Everything Premium Unlocks", "Frequently Asked Questions");
    }

    @Test
    void publicRoutesRetainTheirFunctionalDestinations() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String faq = read("src/main/resources/templates/public-views/public/faq.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(home)
                .contains("th:href=\"@{/signup}\"")
                .contains("th:href=\"@{/explore}\"")
                .contains("th:href=\"@{/support}\"");
        assertThat(about).contains("th:href=\"@{/signup}\"").contains("th:href=\"@{/explore}\"");
        assertThat(faq).contains("id=\"faq-search\"").contains("data-faq-item").contains("th:href=\"@{/support}\"");
        assertThat(pricing)
                .contains("th:each=\"plan : ${plans}\"")
                .contains("@{/pricing/checkout(plan=${plan.plan})}")
                .contains("pricingSuccess")
                .contains("paymentProviderConfigured");
    }

    @Test
    void publicSectionMotionUsesExistingTokensAndAccessibleFallbacks() throws IOException {
        String css = read("src/main/resources/static/css/components/misc/public-sections.css");
        String script = read("src/main/resources/static/js/public/public-sections.js");
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String faq = read("src/main/resources/templates/public-views/public/faq.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(css)
                .contains("--motion-duration-route")
                .contains("--motion-ease-enter")
                .contains("prefers-reduced-motion")
                .doesNotContain("transition: all");
        assertThat(script)
                .contains("IntersectionObserver")
                .contains("prefers-reduced-motion")
                .contains("observer.unobserve")
                .contains("requestAnimationFrame")
                .contains("window.addEventListener('scroll', queueRevealCheck, { passive: true })")
                .contains("document.addEventListener('focusin', revealFocusedSection)")
                .contains("section.scrollIntoView({ block: 'nearest', behavior: 'auto' })");
        assertThat(home).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(about).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(faq).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(pricing).contains("/js/public/public-sections.js(v=${assetVersion})");
    }

    @Test
    void homeInteractivePreviewsRemainAccessibleAndSeparated() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String css = read("src/main/resources/static/css/components/misc/home-public.css");
        String script = read("src/main/resources/static/js/public/public-page.js");

        assertThat(home)
                .contains("role=\"tablist\"")
                .contains("data-preview-tab=\"today\"")
                .contains("data-workspace-tab=\"client\"")
                .contains("aria-controls=\"workspace-client\"")
                .contains("data-brand-sculpture")
                .contains("data-brand-slices=\"34\"")
                .contains("data-brand-depth=\"124\"")
                .contains("class=\"brand-sculpture__volume brand-sculpture__volume--wordmark\"")
                .contains("class=\"brand-sculpture__slice\"")
                .contains("th:src=\"@{/img/home/one-to-one-sculpture-v2.png}\"")
                .contains("class=\"brand-object__reflection\"")
                .contains("th:src=\"@{/img/logo.png}\"")
                .contains("data-product-nav=\"coach\"")
                .contains("data-complete-exercise")
                .contains("data-demo-reset")
                .contains("data-coach-message")
                .contains("data-i18n-session-finished")
                .contains("class=\"home-member-loop\"")
                .contains("data-standard-controller")
                .contains("data-standard-tab=\"credentials\"")
                .contains("data-standard-panel=\"progress\"")
                .contains("th:src=\"@{/img/home/verified-coach-alex.webp}\"")
                .contains("data-workspace-scene")
                .contains("th:src=\"@{/img/home/role-client.webp}\"")
                .contains("th:src=\"@{/img/home/role-trainer.webp}\"")
                .contains("th:src=\"@{/img/home/role-gym.webp}\"")
                .contains("class=\"home-final-motion\"")
                .contains("data-final-cta")
                .contains("data-final-cta-panel")
                .contains("class=\"home-final__route home-final__route--primary\"")
                .contains("class=\"home-final__route home-final__route--secondary\"")
                .contains("th:src=\"@{/img/brand/tab_logo.png}\"")
                .doesNotContain("<style", "style=\"");
        assertThat(css)
                .contains(".workspace-panel[hidden]")
                .contains("@keyframes brandFloat")
                .contains("@keyframes brandFieldScan")
                .contains(".brand-sculpture__volume--mark")
                .contains(".brand-sculpture__volume--wordmark")
                .contains("--slice-z")
                .contains("clip-path: inset(64% 0 0 0)")
                .contains(".brand-object__render-fallback")
                .contains("--stage-rotate-x")
                .contains("translateZ(72px)")
                .contains("@keyframes coachDrawerIn")
                .contains(".standard-proof__panel[hidden]")
                .contains("--standard-pointer-x")
                .contains("@keyframes standardPanelIn")
                .contains("@keyframes workspaceSceneIn")
                .contains("--workspace-image-x")
                .contains(".workspace-panel__image")
                .contains("--final-rotate-x")
                .contains(".home-final-motion")
                .contains("@keyframes finalMarkFloat")
                .contains(".home-final__route:focus-visible")
                .contains("@media (hover: none), (pointer: coarse)")
                .contains("prefers-reduced-motion")
                .contains("will-change: transform")
                .doesNotContain("transition: transform 520ms var(--motion-ease-enter)");
        assertThat(script)
                .contains("ArrowLeft", "ArrowRight", "aria-selected")
                .contains("createProductDemoController")
                .contains("renderSessionState")
                .contains("completedExercises")
                .contains("translated('i18nSyncSent'")
                .contains("formatTranslated")
                .contains("supportsStageTilt")
                .contains("supportsFinePointerMotion")
                .contains("usesCoarsePointer")
                .contains("prepareBrandSculpture")
                .contains("document.createDocumentFragment()")
                .contains("volume.dataset.brandDepth")
                .contains("interactionBounds")
                .contains("Math.exp(-11 * elapsed)")
                .contains("(x * 42)")
                .contains("requestAnimationFrame(animateBrandPose)")
                .contains("pointercancel")
                .contains("window.addEventListener('scroll', resetBrandPose, { passive: true })")
                .contains("createStandardExperience")
                .contains("data-standard-controller")
                .contains("supportsProofDepth")
                .contains("activateOnHover")
                .contains("supportsWorkspaceDepth")
                .contains("supportsFinalDepth")
                .contains("data-final-cta")
                .contains("animateFinalPose")
                .contains("syncFinalTargetToPointer")
                .contains("window.addEventListener('scroll', syncFinalTargetToPointer")
                .contains("Math.exp(-responsiveness * elapsed)")
                .contains("prefers-reduced-motion");
    }

    @Test
    void homeWorkspaceImagesExistAndAreNotEmpty() throws IOException {
        assertPublicImageExists("role-client.webp");
        assertPublicImageExists("role-trainer.webp");
        assertPublicImageExists("role-gym.webp");
        assertPublicImageExists("one-to-one-sculpture-v2.png");
    }

    @Test
    void developmentNoticeMorphsToAndFromItsVisibleTrigger() throws IOException {
        String css = read("src/main/resources/static/css/components/misc/home-public.css");
        String script = read("src/main/resources/static/js/public/public-page.js");

        assertThat(script)
                .contains("setMorphTarget(trigger)")
                .contains("const activeTrigger = setMorphTarget(trigger)")
                .contains("bounceTrigger(activeTrigger, 'is-launching-notice', 520)");
        assertThat(css)
                .contains("var(--dev-notice-target-x)")
                .contains("var(--dev-notice-target-y)")
                .contains("var(--dev-notice-target-scale-x)")
                .contains("var(--dev-notice-target-scale-y)")
                .contains("@keyframes devNoticeIn")
                .contains("@keyframes devNoticeOut");
    }

    @Test
    void signedOutPagesFollowTheSystemThemeWithoutAFlash() throws IOException {
        String base = read("src/main/resources/templates/base.html");
        String preload = read("src/main/resources/static/js/core/theme-preload.js");
        String guestTheme = read("src/main/resources/static/css/components/core/public-guest-theme.css");
        String publicSections = read("src/main/resources/static/css/components/misc/public-sections.css");

        assertThat(base)
                .contains(": 'system'")
                .contains("' public-guest'")
                .contains("media=\"(prefers-color-scheme: light)\"")
                .contains("media=\"(prefers-color-scheme: dark)\"");
        assertThat(preload)
                .contains("const applyTheme")
                .contains("root.classList.toggle(\"dark\"")
                .contains("systemTheme.addEventListener(\"change\", applyTheme)");
        assertThat(guestTheme)
                .contains("body.public-guest")
                .contains("html.dark body.public-guest")
                .contains("color-scheme: light")
                .contains("color-scheme: dark");
        assertThat(publicSections)
                .contains("html.dark .public-journey")
                .contains("--public-surface: #07120e");
    }

    @Test
    void homeShowsTheReturnControlAfterTheFirstJourneyCue() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String css = read("src/main/resources/static/css/components/misc/home-public.css");
        String script = read("src/main/resources/static/js/public/public-page.js");

        assertThat(count(home, " data-home-chapter ")).isEqualTo(4);
        assertThat(home)
                .contains("data-home-chapter-next")
                .contains("#{home.021.hero.scroll.text}")
                .contains("data-home-to-top-trigger")
                .contains("data-home-to-top")
                .contains("#{home.294.to.top}");
        assertThat(css)
                .contains("scroll-padding-top: 4.75rem")
                .contains(".home-to-top.is-visible")
                .contains("@keyframes chapterArrow")
                .contains(".home-hero::after")
                .contains(".home-scroll-cue {\n        display: inline-flex;")
                .contains("@media (hover: none), (pointer: coarse)")
                .contains("#opening-overlay {\n        display: none;")
                .contains(".home-hero__visual {\n        transform: none !important;")
                .doesNotContain("scroll-snap-type", "scroll-behavior: smooth");
        assertThat(script)
                .contains("createHomeChapterNavigation")
                .contains("scrollIntoView")
                .contains("home-chapters-ready")
                .contains("hasPassedHowItWorks")
                .contains("toTopTrigger.getClientRects().length > 0")
                .contains("visibleTriggerBottom <= navigationOffset")
                .contains("window.addEventListener('scroll', queueDepthUpdate, { passive: true })")
                .contains("prefersReducedMotion || usesCoarsePointer")
                .contains("!devModeNotification || usesCoarsePointer")
                .contains("toTop.tabIndex");
    }

    private static int count(String source, String needle) {
        return (source.length() - source.replace(needle, "").length()) / needle.length();
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }

    private static void assertPublicImageExists(String filename) throws IOException {
        Path image = Path.of("src/main/resources/static/img/home", filename);

        assertThat(image)
                .as("Homepage workspace image should be packaged as a public static resource: %s", filename)
                .isRegularFile();
        assertThat(Files.size(image))
                .as("Homepage workspace image should contain image data: %s", filename)
                .isGreaterThan(0L);
    }
}
