"""Generate the Mission VI web artwork, QR code and print-ready birthday PDFs."""

from pathlib import Path

from PIL import Image, ImageDraw, ImageOps
from reportlab.graphics import renderPDF, renderSVG
from reportlab.graphics.barcode.qr import QrCodeWidget
from reportlab.graphics.shapes import Drawing, Rect
from reportlab.lib.colors import Color, HexColor, white
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A5, A6, landscape
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas
from reportlab.platypus import Paragraph


GIFT_URL = "https://crystal-powers.com/birthday/mission-vi"
ROOT = Path(__file__).resolve().parents[2]
TOOL_DIR = Path(__file__).resolve().parent
ASSET_DIR = TOOL_DIR / "assets"
STATIC_DIR = ROOT / "src" / "main" / "resources" / "static" / "img" / "birthday"
PDF_DIR = ROOT / "output" / "pdf"

WIDE_ART = ASSET_DIR / "neon-coast-wide.png"
PORTRAIT_ART = ASSET_DIR / "neon-birthday-portrait.png"

INK = HexColor("#09071c")
DEEP = HexColor("#17102f")
CORAL = HexColor("#ff5f6d")
PINK = HexColor("#ff3fa4")
PEACH = HexColor("#ffad78")
CYAN = HexColor("#7cecff")
CREAM = HexColor("#fff3dc")


def register_fonts() -> tuple[str, str, str]:
    """Use strong Windows display fonts when present and portable PDF defaults otherwise."""
    font_candidates = {
        "MissionDisplay": Path("C:/Windows/Fonts/impact.ttf"),
        "MissionText": Path("C:/Windows/Fonts/segoeui.ttf"),
        "MissionTextBold": Path("C:/Windows/Fonts/segoeuib.ttf"),
    }
    fallbacks = {
        "MissionDisplay": "Helvetica-Bold",
        "MissionText": "Helvetica",
        "MissionTextBold": "Helvetica-Bold",
    }

    resolved = {}
    for name, path in font_candidates.items():
        if path.exists():
            pdfmetrics.registerFont(TTFont(name, str(path)))
            resolved[name] = name
        else:
            resolved[name] = fallbacks[name]

    return resolved["MissionDisplay"], resolved["MissionText"], resolved["MissionTextBold"]


DISPLAY_FONT, TEXT_FONT, BOLD_FONT = register_fonts()


def prepare_directories() -> None:
    STATIC_DIR.mkdir(parents=True, exist_ok=True)
    PDF_DIR.mkdir(parents=True, exist_ok=True)


def save_web_artwork() -> None:
    """Create compact WebP copies while preserving the generated PNG source files."""
    with Image.open(WIDE_ART) as image:
        image.convert("RGB").save(
            STATIC_DIR / "neon-coast-wide.webp",
            "WEBP",
            quality=86,
            method=6,
        )

    with Image.open(PORTRAIT_ART) as image:
        image.convert("RGB").save(
            STATIC_DIR / "neon-birthday-portrait.webp",
            "WEBP",
            quality=86,
            method=6,
        )


def create_qr_drawing(size: float, padding: float = 0) -> Drawing:
    drawing = Drawing(size, size)
    drawing.add(Rect(0, 0, size, size, fillColor=white, strokeColor=None, rx=5, ry=5))

    widget = QrCodeWidget(GIFT_URL)
    widget.barFillColor = INK
    widget.barBorder = 4
    usable = size - (padding * 2)
    widget.barWidth = usable
    widget.barHeight = usable
    widget.x = padding
    widget.y = padding
    drawing.add(widget)
    return drawing


def save_qr_svg() -> None:
    drawing = create_qr_drawing(720, 28)
    renderSVG.drawToFile(drawing, str(STATIC_DIR / "mission-vi-qr.svg"))


def cover_image(image_path: Path, width_points: float, height_points: float) -> Image.Image:
    target_ratio = width_points / height_points
    with Image.open(image_path) as source:
        source = source.convert("RGB")
        source_ratio = source.width / source.height
        if source_ratio > target_ratio:
            crop_width = round(source.height * target_ratio)
            left = (source.width - crop_width) // 2
            cropped = source.crop((left, 0, left + crop_width, source.height))
        else:
            crop_height = round(source.width / target_ratio)
            top = (source.height - crop_height) // 2
            cropped = source.crop((0, top, source.width, top + crop_height))
        return ImageOps.fit(cropped, (1800, round(1800 / target_ratio)), Image.Resampling.LANCZOS)


def add_artwork_fade(image: Image.Image, direction: str) -> Image.Image:
    """Apply a smooth raster fade so print renderers do not show PDF transparency bands."""
    width, height = image.size
    alpha = Image.new("L", image.size, 0)
    draw = ImageDraw.Draw(alpha)

    if direction == "top":
        coverage = round(height * 0.68)
        for y in range(coverage):
            value = round(235 * (1 - (y / coverage)) ** 1.7)
            draw.line((0, y, width, y), fill=value)
    elif direction == "left":
        coverage = round(width * 0.76)
        for x in range(coverage):
            value = round(242 * (1 - (x / coverage)) ** 1.6)
            draw.line((x, 0, x, height), fill=value)

    overlay = Image.new("RGBA", image.size, (9, 7, 28, 0))
    overlay.putalpha(alpha)
    return Image.alpha_composite(image.convert("RGBA"), overlay).convert("RGB")


def draw_cover(page: canvas.Canvas, image_path: Path, width: float, height: float, fade: str | None = None) -> None:
    image = cover_image(image_path, width, height)
    if fade:
        image = add_artwork_fade(image, fade)
    page.drawInlineImage(image, 0, 0, width=width, height=height)


def fill_with_alpha(page: canvas.Canvas, colour, alpha: float, x: float, y: float, width: float, height: float) -> None:
    page.saveState()
    page.setFillColor(colour)
    page.setFillAlpha(alpha)
    page.rect(x, y, width, height, fill=1, stroke=0)
    page.restoreState()


def draw_paragraph(page: canvas.Canvas, text: str, style: ParagraphStyle, x: float, y_top: float, width: float, height: float) -> float:
    paragraph = Paragraph(text, style)
    _, used_height = paragraph.wrap(width, height)
    paragraph.drawOn(page, x, y_top - used_height)
    return used_height


def base_canvas(output_path: Path, page_size: tuple[float, float], title: str) -> canvas.Canvas:
    page = canvas.Canvas(str(output_path), pagesize=page_size, pageCompression=1)
    page.setTitle(title)
    page.setAuthor("A private birthday gift")
    page.setSubject("Mission VI birthday IOU for Grand Theft Auto VI")
    page.setCreator("Mission VI birthday gift generator")
    return page


def create_a5_poster() -> Path:
    output_path = PDF_DIR / "mission-vi-birthday-poster-a5.pdf"
    width, height = A5
    page = base_canvas(output_path, A5, "Mission VI - Birthday Poster")
    draw_cover(page, PORTRAIT_ART, width, height, fade="top")
    fill_with_alpha(page, INK, 0.48, 0, 0, width, 53 * mm)

    safe = 12 * mm
    page.setFillColor(CYAN)
    page.setFont(BOLD_FONT, 7.2)
    page.drawString(safe, height - 16 * mm, "PRIVATE BIRTHDAY TRANSMISSION  /  FOR DAD")

    page.setFillColor(CREAM)
    page.setFont(DISPLAY_FONT, 39)
    page.drawString(safe, height - 41 * mm, "MISSION")
    mission_width = page.stringWidth("MISSION", DISPLAY_FONT, 39)
    page.setFillColor(PINK)
    page.setFont(DISPLAY_FONT, 39)
    page.drawString(safe + mission_width + 4 * mm, height - 41 * mm, "VI")
    page.setFillColor(CREAM)
    page.setFont(BOLD_FONT, 10)
    page.drawString(safe, height - 50 * mm, "YOUR NEXT BIG ADVENTURE IS SECURED.")

    qr_size = 36 * mm
    qr_x = width - safe - qr_size
    qr_y = 9 * mm
    renderPDF.draw(create_qr_drawing(qr_size, 1.4 * mm), page, qr_x, qr_y)

    page.setFillColor(PEACH)
    page.setFont(BOLD_FONT, 7)
    page.drawString(safe, 42 * mm, "OFFICIAL IOU")
    page.setFillColor(CREAM)
    page.setFont(DISPLAY_FONT, 18)
    page.drawString(safe, 33 * mm, "ONE COPY OF GTA VI")

    promise_style = ParagraphStyle(
        "poster-promise",
        fontName=TEXT_FONT,
        fontSize=7.5,
        leading=10.5,
        textColor=Color(1, 0.98, 0.96, alpha=0.82),
        alignment=TA_LEFT,
    )
    draw_paragraph(
        page,
        "When it releases, we will buy your copy by code or direct purchase - whichever works best for you.",
        promise_style,
        safe,
        27 * mm,
        width - (safe * 2) - qr_size - 7 * mm,
        20 * mm,
    )

    page.setFillColor(Color(1, 0.98, 0.96, alpha=0.58))
    page.setFont(TEXT_FONT, 5.8)
    page.drawString(safe, 8 * mm, "SCAN TO UNLOCK  /  crystal-powers.com/birthday/mission-vi")
    page.setFillColor(Color(1, 0.98, 0.96, alpha=0.40))
    page.setFont(TEXT_FONT, 4.8)
    page.drawRightString(width - safe, 5 * mm, "Personal birthday gift. Not affiliated with Rockstar Games.")

    page.showPage()
    page.save()
    return output_path


def create_a6_card() -> Path:
    page_size = landscape(A6)
    output_path = PDF_DIR / "mission-vi-gift-card-a6.pdf"
    width, height = page_size
    page = base_canvas(output_path, page_size, "Mission VI - Birthday Gift Card")
    draw_cover(page, WIDE_ART, width, height, fade="left")
    fill_with_alpha(page, INK, 0.42, 0, 0, width, 25 * mm)

    safe = 9 * mm
    page.setFillColor(CYAN)
    page.setFont(BOLD_FONT, 6.4)
    page.drawString(safe, height - 12 * mm, "DAD  /  MISSION ACCEPTED")

    page.setFillColor(CREAM)
    page.setFont(DISPLAY_FONT, 34)
    page.drawString(safe, height - 31 * mm, "GTA VI")
    page.setFillColor(CORAL)
    page.setFont(DISPLAY_FONT, 14)
    page.drawString(safe, height - 40 * mm, "BIRTHDAY IOU")

    card_style = ParagraphStyle(
        "card-promise",
        fontName=TEXT_FONT,
        fontSize=7.4,
        leading=10.3,
        textColor=Color(1, 0.98, 0.96, alpha=0.84),
        alignment=TA_LEFT,
    )
    draw_paragraph(
        page,
        "One copy, bought for you from launch day.<br/><b>Code or direct purchase - we have it covered.</b>",
        card_style,
        safe,
        height - 47 * mm,
        62 * mm,
        24 * mm,
    )

    qr_size = 31 * mm
    qr_x = width - safe - qr_size
    qr_y = safe
    renderPDF.draw(create_qr_drawing(qr_size, 1.2 * mm), page, qr_x, qr_y)

    page.setFillColor(PEACH)
    page.setFont(BOLD_FONT, 5.6)
    page.drawRightString(width - safe, qr_y + qr_size + 3.3 * mm, "SCAN TO OPEN YOUR MISSION")
    page.setFillColor(Color(1, 0.98, 0.96, alpha=0.52))
    page.setFont(TEXT_FONT, 5.2)
    page.drawString(safe, 6.3 * mm, "CURRENT OFFICIAL LAUNCH  /  19 NOVEMBER 2026")
    page.drawRightString(width - safe, 5.8 * mm, "PERSONAL GIFT  /  NO EXPIRY")

    page.showPage()
    page.save()
    return output_path


def create_a6_qr_access_pass() -> Path:
    """Create a phone-first birthday access pass with a large print-safe QR code."""
    output_path = PDF_DIR / "mission-vi-qr-access-pass-a6.pdf"
    width, height = A6
    page = base_canvas(output_path, A6, "Mission VI - Birthday QR Access Pass")
    draw_cover(page, PORTRAIT_ART, width, height, fade="top")
    fill_with_alpha(page, INK, 0.30, 0, 0, width, height)
    fill_with_alpha(page, INK, 0.67, 0, 0, width, 34 * mm)

    safe = 9 * mm

    # Ticket rails and small registration marks make the pass feel like a
    # collectible without placing decoration inside the QR quiet zone.
    page.saveState()
    page.setLineWidth(0.55)
    page.setStrokeColor(Color(0.49, 0.93, 1, alpha=0.58))
    page.line(safe, height - 8 * mm, width - safe, height - 8 * mm)
    page.setStrokeColor(Color(1, 0.25, 0.64, alpha=0.66))
    page.line(safe, 8 * mm, width - safe, 8 * mm)
    page.restoreState()

    page.setFillColor(CYAN)
    page.setFont(BOLD_FONT, 6.1)
    page.drawString(safe, height - 14 * mm, "PRIVATE BIRTHDAY TRANSMISSION")

    badge_width = 22 * mm
    badge_height = 7 * mm
    badge_x = width - safe - badge_width
    badge_y = height - 17.3 * mm
    page.setFillColor(PINK)
    page.roundRect(badge_x, badge_y, badge_width, badge_height, 3.5 * mm, fill=1, stroke=0)
    page.setFillColor(CREAM)
    page.setFont(BOLD_FONT, 6.2)
    page.drawCentredString(badge_x + badge_width / 2, badge_y + 2.25 * mm, "FOR DAD")

    page.setFillColor(CREAM)
    page.setFont(DISPLAY_FONT, 29)
    page.drawString(safe, height - 31.5 * mm, "MISSION")
    mission_width = page.stringWidth("MISSION", DISPLAY_FONT, 29)
    page.setFillColor(PINK)
    page.drawString(safe + mission_width + 2.3 * mm, height - 31.5 * mm, "VI")

    page.setFillColor(PEACH)
    page.setFont(BOLD_FONT, 7.4)
    page.drawString(safe, height - 38 * mm, "BIRTHDAY ACCESS PASS  /  ONE COPY SECURED")

    panel_x = 10 * mm
    panel_y = 38 * mm
    panel_width = width - 20 * mm
    panel_height = 68 * mm

    page.saveState()
    page.setFillColor(Color(0.035, 0.025, 0.11, alpha=0.90))
    page.setStrokeColor(Color(0.49, 0.93, 1, alpha=0.82))
    page.setLineWidth(0.8)
    page.roundRect(panel_x, panel_y, panel_width, panel_height, 5 * mm, fill=1, stroke=1)
    page.setStrokeColor(Color(1, 0.25, 0.64, alpha=0.55))
    page.setLineWidth(0.45)
    page.roundRect(
        panel_x + 2 * mm,
        panel_y + 2 * mm,
        panel_width - 4 * mm,
        panel_height - 4 * mm,
        4 * mm,
        fill=0,
        stroke=1,
    )
    page.restoreState()

    page.setFillColor(CYAN)
    page.setFont(BOLD_FONT, 5.6)
    page.drawString(panel_x + 5 * mm, panel_y + panel_height - 7 * mm, "ACCESS ID  /  DAD-VI-001")
    page.setFillColor(Color(1, 0.95, 0.86, alpha=0.60))
    page.setFont(TEXT_FONT, 4.7)
    page.drawRightString(panel_x + panel_width - 5 * mm, panel_y + panel_height - 7 * mm, "STATUS  /  READY")

    qr_size = 48 * mm
    qr_x = (width - qr_size) / 2
    qr_y = panel_y + 9.5 * mm
    renderPDF.draw(create_qr_drawing(qr_size, 1.5 * mm), page, qr_x, qr_y)

    page.setFillColor(CREAM)
    page.setFont(DISPLAY_FONT, 14)
    page.drawCentredString(width / 2, 29 * mm, "SCAN TO UNLOCK YOUR MISSION")
    page.setFillColor(CYAN)
    page.setFont(BOLD_FONT, 6.2)
    page.drawCentredString(width / 2, 23 * mm, "crystal-powers.com/birthday/mission-vi")

    page.setFillColor(Color(1, 0.98, 0.96, alpha=0.66))
    page.setFont(TEXT_FONT, 5.3)
    page.drawCentredString(width / 2, 15.5 * mm, "LAUNCH DAY GIFT  /  CODE OR DIRECT PURCHASE  /  NO EXPIRY")
    page.setFillColor(Color(1, 0.98, 0.96, alpha=0.40))
    page.setFont(TEXT_FONT, 4.3)
    page.drawCentredString(width / 2, 5 * mm, "Personal birthday gift. Not affiliated with Rockstar Games.")

    page.showPage()
    page.save()
    return output_path


def main() -> None:
    prepare_directories()
    missing = [path for path in (WIDE_ART, PORTRAIT_ART) if not path.exists()]
    if missing:
        raise FileNotFoundError(f"Missing source artwork: {', '.join(map(str, missing))}")

    save_web_artwork()
    save_qr_svg()
    outputs = [create_a5_poster(), create_a6_card(), create_a6_qr_access_pass()]

    print(f"QR target: {GIFT_URL}")
    print(f"Web artwork: {STATIC_DIR}")
    for output in outputs:
        print(f"Print file: {output}")


if __name__ == "__main__":
    main()
