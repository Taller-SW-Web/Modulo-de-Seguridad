"""Comprueba enlaces y reparto documental; no ejecuta ni valida la API.

Ejecutar desde cualquier carpeta: python scripts/verificar-documentacion.py
Solo usa la biblioteca estándar y no modifica archivos.
"""

from collections import Counter
from pathlib import Path
import re
import sys
from urllib.parse import unquote


ROOT = Path(__file__).resolve().parents[1]
ERRORS = []
LINK = re.compile(r"!?\[[^\]\n]*\]\(([^)\n]+)\)")
OWNERS = [
    "Eva Lucía Moreno Zevallos",
    "Jose Luis Limachi Sarmiento",
    "Juan José Cano Vasquez",
    "Luis David Morales Brenis",
    "Sergio Alejandro Osorio Montenegro",
    "Christian Gabriel Arancivia Salas",
    "Luis David Morales Brenis",
    "Valery Cristin Gutierrez Bendezu",
]
SPECS = [[1, 2, 3, 4], [5, 6], [7, 8], [9, 10], [11], [12, 13], [14, 15], [16]]
FRONT_EPIC = [2, 2, 1, 1, 4, 3, 8, 1, 1, 4, 3, 1, 7, 6, 6]


def check(condition, message):
    if not condition:
        ERRORS.append(message)


def read(path):
    return path.read_text(encoding="utf-8-sig")


def unique_file(folder, pattern):
    files = sorted(p for p in (ROOT / folder).glob(pattern) if not p.name.endswith('.plan.md'))
    check(len(files) == 1, f"{folder}/{pattern}: se esperaba un archivo, hay {len(files)}")
    return files[0] if len(files) == 1 else None


def field(text, name):
    match = re.search(rf"^\| (?:\*\*)?{re.escape(name)}(?:\*\*)? \| (.+?) \|$", text, re.M)
    return match.group(1) if match else ""


def check_sections(path, text):
    for section in ["Contexto", "Propósito", "Alcance", "Requisitos —",
                    "Escenarios", "Requisitos no funcionales", "Fuera de alcance"]:
        check(any(line.startswith("## " + section) for line in text.splitlines()),
              f"Sección requerida ausente en {path.name}: {section}")


documents = [p for p in ROOT.rglob("*.md") if not any(
    part in {".git", "node_modules", ".obsidian"} for part in p.parts
)]
links = 0
for path in documents:
    for match in LINK.finditer(read(path)):
        target = match.group(1).strip()
        if re.match(r"^(?:[a-zA-Z][a-zA-Z0-9+.-]*:|#)", target):
            continue
        # Local file targets; heading fragments and remote resources are not checked.
        file = unquote(target.split("#", 1)[0].strip("<>"))
        links += 1
        check((path.parent / file).exists(), f"Enlace roto en {path.relative_to(ROOT)}: {target}")

epics = sorted((ROOT / "specs/epicas").glob("EP-*.md"))
check(len(epics) == 8, f"Se esperaban ocho épicas; hay {len(epics)}")
check(Counter(OWNERS).most_common(1)[0] == ("Luis David Morales Brenis", 2), "Luis David debe tener dos épicas")
check(len(set(OWNERS)) == 7, "Deben figurar siete dueños")
tasks = read(ROOT / "plan/seguimiento-epicas.md")
for number, (owner, numbers) in enumerate(zip(OWNERS, SPECS), 1):
    path = unique_file("specs/epicas", f"EP-{number:02}-*.md")
    if path is None:
        continue
    text = read(path)
    check(field(text, "Dueño único") == owner, f"Dueño incorrecto: {path.name}")
    check(f"**Dueño de las seis tareas:** {owner}." in tasks, f"Dueño de tareas ausente: EP-{number:02}")
    for task in range(1, 7):
        check(tasks.count(f"| EP-{number:02}-{task:02} ·") == 1, f"Tarea ausente/duplicada: EP-{number:02}-{task:02}")
    for spec in numbers:
        source = unique_file("specs/funcionales", f"SPEC-{spec:02}-*.md")
        if source is not None:
            data = read(source)
            check_sections(source, data)
            check(field(data, "Responsable") == owner, f"Dueño incorrecto: {source.name}")
            check(f"EP-{number:02}" in field(data, "Épica"), f"Épica incorrecta: {source.name}")
            check(f"../funcionales/{source.name}" in text, f"Spec no trazada: {source.name}")
for number in [17, 18]:
    path = unique_file("specs/funcionales", f"SPEC-{number:02}-*.md")
    if path is not None:
        text = read(path)
        check_sections(path, text)
        check(field(text, "Responsable").startswith(OWNERS[4]), f"Integración técnica sin Sergio: {path.name}")
        check(not field(text, "Épica"), f"Integración técnica convertida en novena épica: {path.name}")
for number, epic in enumerate(FRONT_EPIC):
    for folder in ["specs/front", "specs/componentes-react"]:
        path = unique_file(folder, f"FRONT-{number:02}-*.md")
        if path is not None:
            text = read(path)
            check(field(text, "Responsable").startswith(OWNERS[epic - 1]), f"Responsable incorrecto: {path}")
            check(f"EP-{epic:02}" in field(text, "Épica principal"), f"Épica principal incorrecta: {path}")

print(f"Revisados {len(documents)} documentos y {links} enlaces locales; 8 épicas, 18 specs, 30 fichas de interfaz/componentes y 48 tareas.")
for error in ERRORS:
    print(f"ERROR: {error}")
print(f"Resultado: {'CORRECTO' if not ERRORS else str(len(ERRORS)) + ' errores'}")
sys.exit(bool(ERRORS))
