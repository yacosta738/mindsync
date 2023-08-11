import { assert, expect, test } from 'vitest'

import { hexToRgb, rgbToHex } from '../../src'

test('convert hexadecimal to rgb', () => {
    const hex = '#ffffff'
    const rgb = hexToRgb(hex)
    assert(rgb?.r === 255)
    assert(rgb?.g === 255)
    assert(rgb?.b === 255)

    const hex2 = '#000000'
    const rgb2 = hexToRgb(hex2)
    assert(rgb2?.r === 0)
    assert(rgb2?.g === 0)
    assert(rgb2?.b === 0)

    const hex3 = '#ff0000'
    const rgb3 = hexToRgb(hex3)
    assert(rgb3?.r === 255)
    assert(rgb3?.g === 0)
    assert(rgb3?.b === 0)
})

test('convert rgb to hexadecimal', () => {
    const hex = rgbToHex(255, 255, 255)
    assert(hex === '#ffffff')

    const hex2 = rgbToHex(0, 0, 0)
    assert(hex2 === '#000000')

    const hex3 = rgbToHex(255, 0, 0)
    assert(hex3 === '#ff0000')
})
