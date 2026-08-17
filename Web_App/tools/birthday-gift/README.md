# Mission VI birthday gift generator

This isolated helper creates the two print-ready birthday pieces and the web assets used by `/birthday/mission-vi`.

Canonical QR destination:

`https://crystal-powers.com/birthday/mission-vi`

Run from `Web_App` with the bundled Codex Python runtime or any Python environment containing Pillow and ReportLab:

```powershell
python .\tools\birthday-gift\generate_printables.py
```

Generated files:

- `output/pdf/mission-vi-birthday-poster-a5.pdf`
- `output/pdf/mission-vi-gift-card-a6.pdf`
- `output/pdf/mission-vi-qr-access-pass-a6.pdf`
- `src/main/resources/static/img/birthday/mission-vi-qr.svg`
- optimised WebP artwork in `src/main/resources/static/img/birthday/`

The source artwork is kept in `tools/birthday-gift/assets/` so the PDFs can be regenerated without relying on temporary files.

If the route or domain changes, update `GIFT_URL` once in `generate_printables.py`, regenerate the files, and retest the QR before printing.
