#!/usr/bin/env node
/**
 * Genera un SVG animado por cada diagrama, para incrustar en el README.
 *
 *   node docs/arquitectura/generar-svg-animado.mjs
 *
 * POR QUE EXISTE
 * GitHub no renderiza HTML dentro de un README, asi que los .html interactivos
 * de archify no se pueden incrustar. Pero SI ejecuta animacion declarativa
 * dentro de un SVG referenciado como imagen: el navegador lo carga en "modo
 * animado seguro" — sin scripts ni interactividad, pero los @keyframes CSS
 * corren. Eso da animacion inline sin subir nada a ningun servicio.
 *
 * SMIL no se usa a proposito: hay informes de que GitHub lo elimina en
 * imagenes de markdown. CSS no tiene ese problema.
 *
 * ENTRADA / SALIDA
 *   _export-<nombre>.svg  export SVG del visor de archify (Export -> SVG).
 *                         Ya es autocontenido, de doble tema y sin scripts; se
 *                         versiona para que regenerar no exija abrir el visor.
 *   <nombre>.json         fuente de verdad del diagrama: de aqui sale el ORDEN
 *                         de revelado, no de adivinar el DOM.
 *   -> <nombre>.svg       lo que referencia el README.
 *
 * ASIDEROS DEL EXPORT (verificados, no supuestos)
 *   limites      [data-composition-frame-id="0"]
 *   componentes  #node-<id>
 *   aristas      path[data-edge-id="<id>"]  la linea
 *                g[data-edge-id="<id>"]     su etiqueta
 *
 * Si cambias un diagrama: edita el .json, regenera el .html con archify,
 * reexporta el SVG desde el visor y corre este script.
 */

import { readFileSync, writeFileSync, existsSync } from "node:fs";
import { join, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const DIR = dirname(fileURLToPath(import.meta.url));
const DIAGRAMAS = [
  "contexto", "componentes", "secuencia-login", "secuencia-refresco",
  "estructura-modulo", "comunicacion-modulos",
];

/* ---------------------------------------------------------------- ritmo --
   Un README no es una presentacion: la mayor parte del tiempo el lector
   necesita el diagrama COMPLETO y quieto. El ciclo construye, sostiene largo
   y reinicia; quien llegue tarde ve el diagrama entero, que es lo correcto. */
const T_INICIO = 0.5;
const PASO_MARCO = 0.22;
const PASO_NODO = 0.24;
const PASO_ENLACE = 0.42;
const DUR_APARICION = 0.5;
const DUR_TRAZO = 0.62;
const DUR_ETIQUETA = 0.42;
const SOSTENIDO = 9.0;
const DUR_SALIDA = 0.9;

// ---------------------------------------------------------------- utiles --

const esc = (s) => s.replace(/[.*+?^${}()|[\]\\-]/g, "\\$&");
const clave = (id) => `x${id.replace(/[^\w]/g, "_")}`;

/** Longitud exacta de una polilinea M/L/H/V. null si lleva curvas. */
function largoDeRuta(d) {
  if (/[QqCcSsAaTt]/.test(d)) return null;
  const tk = d.match(/[MmLlHhVvZz]|-?\d*\.?\d+(?:e[-+]?\d+)?/gi);
  if (!tk) return null;
  let i = 0, x = 0, y = 0, sx = 0, sy = 0, total = 0, cmd = null;
  const num = () => parseFloat(tk[i++]);
  const paso = (nx, ny) => { total += Math.hypot(nx - x, ny - y); x = nx; y = ny; };
  while (i < tk.length) {
    if (/^[MmLlHhVvZz]$/.test(tk[i])) { cmd = tk[i]; i++; }
    else if (cmd === null) return null;
    if (i >= tk.length && !/^[Zz]$/.test(cmd)) break;
    switch (cmd) {
      case "M": x = num(); y = num(); sx = x; sy = y; cmd = "L"; break;
      case "m": x += num(); y += num(); sx = x; sy = y; cmd = "l"; break;
      case "L": paso(num(), num()); break;
      case "l": paso(x + num(), y + num()); break;
      case "H": paso(num(), y); break;
      case "h": paso(x + num(), y); break;
      case "V": paso(x, num()); break;
      case "v": paso(x, y + num()); break;
      case "Z": case "z": paso(sx, sy); break;
      default: return null;
    }
  }
  return total > 4 ? Math.ceil(total) + 2 : null;
}

const lineaDeArista = (svg, eid) =>
  new RegExp(`<path[^>]*data-edge-id="${esc(eid)}"[^>]*>`).exec(svg)?.[0] ?? null;

const tieneEtiqueta = (svg, eid) =>
  new RegExp(`<g[^>]*data-edge-id="${esc(eid)}"`).test(svg);

/** Orden de revelado, derivado del .json. */
function guion(spec) {
  const marcos = [], nodos = [], enlaces = [];
  if (spec.diagram_type === "sequence") {
    for (const p of spec.participants ?? []) nodos.push(p.id);
    (spec.segments ?? []).forEach((_, i) => marcos.push(String(i)));
    for (const m of spec.messages ?? []) if (m.id) enlaces.push(m.id);
  } else {
    (spec.boundaries ?? []).forEach((_, i) => marcos.push(String(i)));
    for (const c of spec.components ?? []) nodos.push(c.id);
    for (const c of spec.connections ?? []) if (c.id) enlaces.push(c.id);
  }
  return { marcos, nodos, enlaces };
}

// ------------------------------------------------------------------ main --

let hechos = 0;
for (const nombre of DIAGRAMAS) {
  const fuente = join(DIR, `_export-${nombre}.svg`);
  if (!existsSync(fuente)) {
    console.error(`  ! falta _export-${nombre}.svg — exportalo desde el visor (Export -> SVG)`);
    continue;
  }
  const svg = readFileSync(fuente, "utf8");
  const spec = JSON.parse(readFileSync(join(DIR, `${nombre}.json`), "utf8"));
  const { marcos, nodos, enlaces } = guion(spec);

  // solo lo que existe de verdad en el export
  const M = marcos.filter((i) => svg.includes(`data-composition-frame-id="${i}"`));
  const N = nodos.filter((i) => svg.includes(`id="node-${i}"`));
  const E = enlaces.filter((i) => svg.includes(`data-edge-id="${i}"`));

  let t = T_INICIO;
  const en = new Map();
  for (const id of M) { en.set(id, t); t += PASO_MARCO; }
  t += 0.15;
  for (const id of N) { en.set(id, t); t += PASO_NODO; }
  t += 0.3;
  for (const id of E) { en.set(id, t); t += PASO_ENLACE; }

  const CICLO = +(t + DUR_TRAZO + DUR_ETIQUETA + SOSTENIDO + DUR_SALIDA).toFixed(2);
  const pct = (s) => +((s / CICLO) * 100).toFixed(3);
  const salida = pct(CICLO - DUR_SALIDA);

  const reglas = [], keys = [];
  let dibujadas = 0;

  const aparecer = (sel, k, ini, dur) => {
    keys.push(
      `@keyframes ${k}{0%,${pct(ini)}%{opacity:0}` +
      `${pct(ini + dur)}%,${salida}%{opacity:1}100%{opacity:0}}`
    );
    reglas.push(`${sel}{animation:${k} var(--ciclo) linear infinite both}`);
  };

  for (const id of M) aparecer(`[data-composition-frame-id="${id}"]`, `m_${clave(id)}`, en.get(id), DUR_APARICION);
  for (const id of N) aparecer(`#node-${id}`, `n_${clave(id)}`, en.get(id), DUR_APARICION);

  for (const id of E) {
    const ini = en.get(id);
    const linea = lineaDeArista(svg, id);
    const cls = linea ? (/class="([^"]*)"/.exec(linea)?.[1] ?? "") : "";

    // Las flechas de seguridad y asincronas usan stroke-dasharray como
    // SEMANTICA. Dibujarlas con stroke-dashoffset las volveria solidas y
    // destruiria el significado: esas se desvanecen en vez de dibujarse.
    const puedeDibujarse = linea && !/a-security|a-dashed/.test(cls);
    const d = puedeDibujarse ? (/\bd="([^"]+)"/.exec(linea)?.[1] ?? null) : null;
    const largo = d ? largoDeRuta(d) : null;

    if (largo) {
      const k = `t_${clave(id)}`;
      keys.push(
        `@keyframes ${k}{0%,${pct(ini)}%{stroke-dashoffset:${largo};opacity:0}` +
        `${pct(ini + 0.08)}%{opacity:1}` +
        `${pct(ini + DUR_TRAZO)}%,${salida}%{stroke-dashoffset:0;opacity:1}` +
        `100%{opacity:0}}`
      );
      reglas.push(
        `path[data-edge-id="${id}"]{stroke-dasharray:${largo};` +
        `animation:${k} var(--ciclo) linear infinite both}`
      );
      dibujadas++;
    } else if (linea) {
      aparecer(`path[data-edge-id="${id}"]`, `e_${clave(id)}`, ini, DUR_APARICION);
    }

    if (tieneEtiqueta(svg, id)) {
      const tE = ini + (largo ? DUR_TRAZO * 0.75 : 0.15);
      aparecer(`g[data-edge-id="${id}"]`, `l_${clave(id)}`, tE, DUR_ETIQUETA);
    }
  }

  const css =
    `\n/* ---- animacion generada por generar-svg-animado.mjs ---- */\n` +
    `:root{--ciclo:${CICLO}s}\n` +
    `@media (prefers-reduced-motion:reduce){` +
    `*{animation:none!important;opacity:1!important;stroke-dashoffset:0!important}}\n` +
    reglas.join("\n") + "\n" + keys.join("\n") + "\n";

  const cierre = svg.lastIndexOf("</svg>");
  const salida2 =
    `<!-- GENERADO por docs/arquitectura/generar-svg-animado.mjs — no editar a mano.\n` +
    `     Fuente: _export-${nombre}.svg (export del visor) + ${nombre}.json (orden de revelado).\n` +
    `     Ciclo de ${CICLO}s: construye, sostiene ${SOSTENIDO}s y reinicia. -->\n` +
    svg.slice(0, cierre) + `<style>${css}</style>\n` + svg.slice(cierre);

  writeFileSync(join(DIR, `${nombre}.svg`), salida2, "utf8");
  console.log(
    `  ✓ ${nombre}.svg  ${(Buffer.byteLength(salida2) / 1024).toFixed(0)} KB  ciclo ${CICLO}s  ` +
    `(${M.length} marcos, ${N.length} nodos, ${E.length} aristas — ${dibujadas} se dibujan)`
  );
  hechos++;
}

console.log(`\n${hechos}/${DIAGRAMAS.length} SVG animados en docs/arquitectura/`);
