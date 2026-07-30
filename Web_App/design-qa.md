# Role Perspectives Section Design QA

## Comparison target

- Source visual truth: `C:\Users\jhuds\AppData\Local\Temp\codex-clipboard-27e7cdea-794c-4d15-8b04-ab42cde30ac1.png`
- Final composition capture: `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One\Web_App\design-qa-roles-composition.png`
- Client scene capture: `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One\Web_App\design-qa-roles-client.png`
- Trainer scene capture: `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One\Web_App\design-qa-roles-trainer.png`
- Gym scene capture: `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One\Web_App\design-qa-roles-gym.png`
- Full composition comparison: `G:\No OneDrive Work\My Website\Crystal-Powers-OneToOne\One To One\One-To-One\Web_App\design-qa-roles-comparison.png`
- Route and state: public homepage, signed out, system dark mode, initial Client view plus Trainer and Gym interaction states.
- Browser viewport: 1270 × 714 CSS pixels at device pixel ratio 1.
- Source pixels: 1919 × 1219 at 1× density.
- Implementation pixels: 1270 × 714 at 1× density.
- Normalisation: the comparison scales the source and implementation into equal 1260 × 710 panels. The supplied image is the deliberately basic baseline requested for redesign, so the comparison judges preserved role meaning and the requested improvement in layout, imagery, controls and visual hierarchy rather than pixel fidelity to the old arrangement. The source is light while the live capture follows the visitor's dark system preference.

## Full-view comparison evidence

The old centred introduction, flat three-column tab bar and generated phone/dashboard objects have been replaced by a more editorial split heading and a clean perspective rail leading into one cinematic scene. The section remains recognisably part of the same homepage through its typography, mint action colour, fine borders and restrained spacing, but it no longer repeats the trust section's architectural proof-panel design.

## Focused comparison evidence

The Client, Trainer and Gym captures show three distinct photographic environments using the same crop, tonal direction and overlay hierarchy. Each state has role-specific copy, outcomes, CTA and a concise live snapshot. Important text stays within the calm left side of each photograph, while subjects remain visible on the right.

## Required fidelity surfaces

- Fonts and typography: the existing Inter/system stack remains consistent with the homepage. The new headline uses a two-weight editorial treatment; role headings, metadata and snapshot labels have clear optical hierarchy without truncation at the verified desktop viewport.
- Spacing and layout rhythm: the split introduction, vertical role rail and wide image stage create a deliberately different composition from the trust chapter. The stage maintains consistent radius, padding and alignment across all three states, with no desktop overlap or horizontal overflow.
- Colours and visual tokens: deep forest, charcoal and mint continue the site palette. The photographs add a restrained copper practical-light accent without introducing a competing UI colour. Light and dark page shells retain the same section structure.
- Image quality and asset fidelity: Client, Trainer and Gym use three purpose-made 1440-pixel WebP photographs at 49 KB, 59 KB and 57 KB. Subjects, crops, negative space and lighting were designed for the actual stage. No placeholder, CSS illustration or fake device object remains.
- Copy and content: every role has distinct, realistic platform copy. The client focuses on guidance, the trainer on coaching context and the gym on operating visibility. CTAs retain the existing signup destinations.
- Icons: the section only reuses the established CTA arrow treatment, avoiding a new ornamental icon family.
- States and interactions: click, pointer-preview and roving Arrow-key tab navigation were tested. All three panel images loaded at 1440 pixels, active/selected/visible state stayed synchronised and pointer movement updated image depth variables.
- Accessibility: native tabs and tabpanels retain `aria-selected`, `aria-controls`, roving `tabindex` and visible focus treatment. Scene images have meaningful alternative text, progress uses native `progress` elements and coarse-pointer/reduced-motion fallbacks remove parallax.
- Browser diagnostics: no console errors or warnings appeared after loading and switching through all three scenes.

## Comparison history

### Iteration 1

- The baseline relied on centred marketing copy, low-affordance tabs and CSS-built device/dashboard objects, making each role feel like the same layout with different content.
- The section was rebuilt around three real environments, a vertical perspective rail, image-led role storytelling and compact operational snapshots.

### Iteration 2

- Desktop composition and all three scene states were inspected in the live browser.
- Role switching, keyboard navigation, image loading and pointer depth were verified. No P0, P1 or P2 visual or interaction issue remained.

## Findings

No actionable P0, P1 or P2 issues remain for the requested clean, differentiated redesign.

## Follow-up polish

- P3: the global assistant orb overlaps the lower-right atmosphere of the photography. It does not cover the role CTA, snapshot text or role navigation.
- P3 test gap: the in-app browser is fixed at 1270 × 714. Tablet/mobile, light-theme and coarse-pointer rules passed the production build and code review but were not captured in a separate physical-device viewport.

## Primary interactions tested

- Open Client, Trainer and Gym using the role controls.
- Switch from Trainer to Gym with the Right Arrow key.
- Confirm active tab, visible panel, selected state and keyboard focus remain synchronised.
- Hover the role rail to preview a perspective.
- Move across the active scene and confirm image-depth variables update.
- Confirm all three generated WebP photographs load at 1440 pixels.
- Check browser console warnings and errors.

final result: passed
