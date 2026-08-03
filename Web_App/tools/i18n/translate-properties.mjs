import fs from 'node:fs';
import path from 'node:path';

const [, , sourcePath, targetCode, ...options] = process.argv;
const existingPath = options.find((option) => !option.startsWith('--'));
const outputOption = options.find((option) => option.startsWith('--output='));
const outputPath = outputOption?.slice('--output='.length);

if (!sourcePath || !targetCode) {
    throw new Error(
        'Usage: node translate-properties.mjs <source> <target-code> [existing-target] [--output=<target>]',
    );
}

const targetTags = {
    cy: 'cy',
    es: 'es',
    fr: 'fr',
    de: 'de',
    it: 'it',
    pt: 'pt',
    pl: 'pl',
    nl: 'nl',
    zh: 'zh-CN',
    ja: 'ja',
    ko: 'ko',
    ar: 'ar',
    hi: 'hi',
};

const targetTag = targetTags[targetCode];
if (!targetTag) {
    throw new Error(`Unsupported target language: ${targetCode}`);
}

const propertyPattern = /^([^#!\s][^=]*?)\s*=\s*(.*)$/;
const sourceLines = fs.readFileSync(path.resolve(sourcePath), 'utf8').replace(/\r\n/g, '\n').split('\n');
const existingValues = new Map();

if (existingPath && fs.existsSync(path.resolve(existingPath))) {
    fs.readFileSync(path.resolve(existingPath), 'utf8')
        .replace(/\r\n/g, '\n')
        .split('\n')
        .forEach((line) => {
            const match = line.match(propertyPattern);
            if (match) {
                existingValues.set(match[1].trim(), match[2]);
            }
        });
}

const entries = sourceLines
    .map((line, lineIndex) => {
        const match = line.match(propertyPattern);
        return match
            ? {lineIndex, key: match[1].trim(), value: match[2]}
            : null;
    })
    .filter(Boolean)
    .filter(({key, value}) => value.trim() && !existingValues.has(key));

const translations = new Map(existingValues);

const protect = (value) => {
    const tokens = [];
    const protectedValue = value.replace(
        /One To One|https?:\/\/[^\s]+|\{\d+}|\$\{[^}]+}|%\d*\$?[a-zA-Z]|\b(?:RPE|SMS|AI|URL|API|TRX|BMI|PDF|CSV|OAuth)\b/g,
        (token) => {
            const marker = `ZXQVTKN${tokens.length.toString().padStart(3, '0')}ZXQV`;
            tokens.push([marker, token]);
            return marker;
        },
    );
    return {protectedValue, tokens};
};

const restore = (value, tokens) => {
    let restored = value;
    tokens.forEach(([marker, token]) => {
        restored = restored.replaceAll(marker, token);
    });
    return restored;
};

const translateBatch = async (batch) => {
    const protectedBatch = batch.map(({value}) => protect(value));
    const markers = batch.slice(0, -1).map((_, index) =>
        `ZXQVSEP${index.toString().padStart(4, '0')}ZXQV`,
    );
    const combined = protectedBatch
        .map(({protectedValue}, index) => index < markers.length
            ? `${protectedValue}\n${markers[index]}\n`
            : protectedValue)
        .join('');
    const endpoint = new URL('https://translate.googleapis.com/translate_a/single');
    endpoint.searchParams.set('client', 'gtx');
    endpoint.searchParams.set('sl', 'en');
    endpoint.searchParams.set('tl', targetTag);
    endpoint.searchParams.set('dt', 't');
    endpoint.searchParams.set('q', combined);

    let lastError;
    for (let attempt = 0; attempt < 4; attempt += 1) {
        try {
            const response = await fetch(endpoint, {
                headers: {'User-Agent': 'One-To-One-localisation-maintenance/1.0'},
                signal: AbortSignal.timeout(30000),
            });
            if (!response.ok) {
                throw new Error(`Translation request failed with HTTP ${response.status}`);
            }
            const payload = await response.json();
            const translatedCombined = (payload[0] || []).map((segment) => segment[0] || '').join('');
            const splitPattern = new RegExp(`\\n?(?:${markers.join('|')})\\n?`, 'g');
            const translatedValues = markers.length === 0
                ? [translatedCombined]
                : translatedCombined.split(splitPattern);
            if (translatedValues.length !== batch.length) {
                throw new Error(`Expected ${batch.length} translated values but received ${translatedValues.length}`);
            }
            translatedValues.forEach((translated, index) => {
                translations.set(
                    batch[index].key,
                    restore(translated.replace(/\s+/g, ' ').trim(), protectedBatch[index].tokens),
                );
            });
            return;
        } catch (error) {
            lastError = error;
            await new Promise((resolve) => setTimeout(resolve, 500 * (attempt + 1)));
        }
    }
    throw lastError;
};

let batch = [];
let batchLength = 0;
for (const entry of entries) {
    if (batch.length > 0 && (batch.length >= 24 || batchLength + entry.value.length > 2800)) {
        await translateBatch(batch);
        batch = [];
        batchLength = 0;
    }
    batch.push(entry);
    batchLength += entry.value.length;
}
if (batch.length > 0) {
    await translateBatch(batch);
}

const output = sourceLines.map((line) => {
    const match = line.match(propertyPattern);
    if (!match) {
        return line;
    }
    const key = match[1].trim();
    return translations.has(key) ? `${key} = ${translations.get(key)}` : line;
});

const outputText = output.join('\n');
if (outputPath) {
    fs.writeFileSync(path.resolve(outputPath), outputText, 'utf8');
    process.stdout.write(`Wrote ${translations.size} translated messages to ${outputPath}.\n`);
} else {
    process.stdout.write(outputText);
}
