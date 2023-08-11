export const hexToRgb = (hex: string) => {

    if (!hex) {
        throw new Error("Hex value must be provided.");
      }
    // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
    const shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
    const hexParse: string = hex.replace(shorthandRegex, (m, r, g, b) => `${r}${r}${g}${g}${b}${b}`);

    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hexParse);
    return result ? {
      r: parseInt(result[1] ?? '0', 16),
      g: parseInt(result[2] ?? '0', 16),
      b: parseInt(result[3] ?? '0', 16)
    } : null;
  }

  export const rgbToHex = (r: number, g: number, b: number) =>
  // eslint-disable-next-line no-bitwise
  `#${(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1)}`;

