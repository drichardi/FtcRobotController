"""Tiny SVG drawing helper for the curriculum illustrations."""
import html
import os

OUT = r"G:\Eng_Robotics\FtcRobotController\curriculum\images"

ACC = "#e8590c"      # callout / highlight orange
INK = "#1f2328"
MUTED = "#59636e"
LINE = "#d1d9e0"
SUBTLE = "#f6f8fa"
GREEN = "#1f883d"
BLUE = "#0969da"
RED = "#cf222e"
PURPLE = "#8250df"
YELLOW = "#bf8700"

# Android Studio (dark) palette
AS_BAR = "#2b2d30"
AS_BG = "#1e1f22"
AS_TXT = "#dfe1e5"
AS_MUTED = "#868a91"
AS_SEL = "#2e436e"
AS_BORDER = "#43454a"

SANS = "Segoe UI, Helvetica Neue, Arial, sans-serif"
MONO = "Consolas, Menlo, Courier New, monospace"


def esc(s):
    return html.escape(str(s), quote=True)


def tw(s, size=14, mono=False):
    """Rough text width estimate."""
    return len(s) * size * (0.6 if mono else 0.54)


class Svg:
    def __init__(self, w, h, title, ui=True, bg="#ffffff"):
        self.w, self.h, self.title, self.ui, self.bg = w, h, title, ui, bg
        self.parts = []
        self.markers = set()

    def _marker(self, color):
        self.markers.add(color)
        return f' marker-end="url(#m{color.lstrip("#")})"'

    # ---------- primitives ----------
    def add(self, s):
        self.parts.append(s)

    def rect(self, x, y, w, h, fill="none", stroke="none", sw=1, r=0, dash=None, opacity=None):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        o = f' opacity="{opacity}"' if opacity is not None else ""
        self.add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{fill}" '
                 f'stroke="{stroke}" stroke-width="{sw}"{d}{o}/>')

    def text(self, x, y, s, size=14, fill=INK, weight="normal", anchor="start",
             mono=False, italic=False, opacity=None):
        fam = MONO if mono else SANS
        st = ' font-style="italic"' if italic else ""
        o = f' opacity="{opacity}"' if opacity is not None else ""
        self.add(f'<text x="{x}" y="{y}" font-family="{fam}" font-size="{size}" fill="{fill}" '
                 f'font-weight="{weight}" text-anchor="{anchor}"{st}{o} xml:space="preserve">{esc(s)}</text>')

    def lines(self, x, y, items, size=14, lh=None, **kw):
        lh = lh or size * 1.4
        for i, s in enumerate(items):
            self.text(x, y + i * lh, s, size=size, **kw)

    def rich(self, x, y, segments, size=14, mono=False, anchor="start"):
        """segments: list of (text, color, weight)."""
        fam = MONO if mono else SANS
        spans = "".join(
            f'<tspan fill="{c}" font-weight="{wt}">{esc(t)}</tspan>' for t, c, wt in segments)
        self.add(f'<text x="{x}" y="{y}" font-family="{fam}" font-size="{size}" '
                 f'text-anchor="{anchor}" xml:space="preserve">{spans}</text>')

    def line(self, x1, y1, x2, y2, stroke=MUTED, sw=2, arrow=False, dash=None):
        m = self._marker(stroke) if arrow else ""
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.add(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{stroke}" '
                 f'stroke-width="{sw}"{m}{d}/>')

    def path(self, d, stroke=MUTED, sw=2, fill="none", arrow=False, dash=None):
        m = self._marker(stroke) if arrow else ""
        ds = f' stroke-dasharray="{dash}"' if dash else ""
        self.add(f'<path d="{d}" stroke="{stroke}" stroke-width="{sw}" fill="{fill}"{m}{ds} '
                 f'stroke-linecap="round" stroke-linejoin="round"/>')

    def circle(self, cx, cy, r, fill="none", stroke="none", sw=1):
        self.add(f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}"/>')

    # ---------- annotation ----------
    def callout(self, n, x, y):
        self.circle(x, y, 13, fill=ACC, stroke="#ffffff", sw=2)
        self.text(x, y + 5, str(n), size=14, fill="#ffffff", weight="bold", anchor="middle")

    def hl(self, x, y, w, h, r=6):
        self.rect(x - 3, y - 3, w + 6, h + 6, stroke=ACC, sw=3, r=r)

    def note(self, x, y, s, anchor="start", size=13, color=ACC, weight="bold"):
        self.text(x, y, s, size=size, fill=color, weight=weight, anchor=anchor)

    def tag(self, x, y, label, color, text_color="#ffffff", size=12):
        w = tw(label, size) * 1.25 + 16
        self.rect(x, y - size - 3, w, size + 9, fill=color, r=4)
        self.text(x + 8, y + 1, label, size=size, fill=text_color, weight="bold")
        return w

    # ---------- widgets ----------
    def button(self, x, y, w, h, label, kind="gray", size=14):
        styles = {
            "green": (GREEN, "#ffffff", GREEN),
            "blue": ("#0366d6", "#ffffff", "#0366d6"),
            "gray": (SUBTLE, INK, LINE),
            "white": ("#ffffff", INK, LINE),
            "dark": ("#3c3f41", AS_TXT, AS_BORDER),
            "darkblue": ("#3574f0", "#ffffff", "#3574f0"),
        }
        fill, txt, stroke = styles[kind]
        self.rect(x, y, w, h, fill=fill, stroke=stroke, r=6)
        self.text(x + w / 2, y + h / 2 + size * 0.36, label, size=size, fill=txt,
                  weight="600", anchor="middle")

    def field(self, x, y, w, h, value, dark=False, mono=False, size=14, placeholder=False):
        fill, stroke, txt = (AS_BG, AS_BORDER, AS_TXT) if dark else ("#ffffff", "#8c959f", INK)
        if placeholder:
            txt = AS_MUTED if dark else MUTED
        self.rect(x, y, w, h, fill=fill, stroke=stroke, r=5)
        self.text(x + 9, y + h / 2 + size * 0.36, value, size=size, fill=txt, mono=mono)

    def window(self, x, y, w, h, title, dark=False, bar=30):
        body = AS_BAR if dark else "#ffffff"
        tb = "#1e1f22" if dark else "#f0f0f0"
        tt = AS_TXT if dark else INK
        border = AS_BORDER if dark else "#b8b8b8"
        self.rect(x, y, w, h, fill=body, stroke=border, sw=1, r=6)
        self.add(f'<path d="M{x},{y + bar} L{x},{y + 6} Q{x},{y} {x + 6},{y} L{x + w - 6},{y} '
                 f'Q{x + w},{y} {x + w},{y + 6} L{x + w},{y + bar} Z" fill="{tb}"/>')
        self.text(x + 12, y + bar / 2 + 5, title, size=13, fill=tt)
        cx = x + w - 18
        for sym in ("\u2715", "\u2610", "\u2014"):
            self.text(cx, y + bar / 2 + 5, sym, size=12, fill=tt, anchor="middle")
            cx -= 28
        return y + bar

    def panel_label(self, x, y, s):
        self.text(x, y, s, size=15, fill=MUTED, weight="bold")

    def tri(self, x, y, down=True, color=AS_MUTED, s=5):
        if down:
            self.path(f"M{x - s},{y - s / 2} L{x},{y + s / 2} L{x + s},{y - s / 2}", stroke=color, sw=1.6)
        else:
            self.path(f"M{x - s / 2},{y - s} L{x + s / 2},{y} L{x - s / 2},{y + s}", stroke=color, sw=1.6)

    def folder(self, x, y, color="#c09553"):
        self.add(f'<path d="M{x},{y + 2} h5 l2,2 h7 v9 h-14 Z" fill="{color}"/>')

    def jfile(self, x, y):
        self.circle(x + 7, y + 7, 7, fill="#3c7dd9")
        self.text(x + 7, y + 11, "C", size=10, fill="#ffffff", weight="bold", anchor="middle")

    # ---------- output ----------
    def render(self):
        cap_h = 30 if self.ui else 0
        H = self.h + cap_h
        defs = '<defs>' + ''.join(
            f'<marker id="m{c.lstrip("#")}" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="5" '
            f'markerHeight="5" markerUnits="strokeWidth" orient="auto-start-reverse">'
            f'<path d="M0,0 L10,5 L0,10 z" fill="{c}"/></marker>' for c in sorted(self.markers)) + '</defs>'
        out = [
            f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.w}" height="{H}" '
            f'viewBox="0 0 {self.w} {H}" role="img" aria-label="{esc(self.title)}">',
            f'<title>{esc(self.title)}</title>', defs,
            f'<rect x="0.5" y="0.5" width="{self.w - 1}" height="{H - 1}" rx="10" fill="{self.bg}" stroke="{LINE}"/>',
        ]
        out += self.parts
        if self.ui:
            out.append(f'<text x="{self.w - 14}" y="{H - 11}" font-family="{SANS}" font-size="12" '
                       f'fill="{MUTED}" font-style="italic" text-anchor="end">Simplified drawing \u2014 '
                       f'your screen may look a little different.</text>')
        out.append("</svg>")
        return "\n".join(out)

    def save(self, name):
        os.makedirs(OUT, exist_ok=True)
        with open(os.path.join(OUT, name), "w", encoding="utf-8", newline="\n") as f:
            f.write(self.render())
        return name
