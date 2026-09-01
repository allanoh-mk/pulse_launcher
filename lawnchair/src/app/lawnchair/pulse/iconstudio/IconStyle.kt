package app.lawnchair.pulse.iconstudio

/**
 * Post-processing styles applied on top of the shape mask by [IconRenderer].
 * Scoped to a first, robust subset rather than attempting all 13 styles
 * mentioned in the original design brief in one pass; more styles can be
 * added as additional [IconStyle] entries plus a branch in [IconRenderer].
 */
enum class IconStyle {
    /** No post-processing beyond the shape mask itself. */
    FLAT,

    /** iOS 27-inspired: bright top specular rim + darkened bottom edge (see docs/research/ios_feel_style_liquid_glass.txt). */
    LIQUID_GLASS,

    /** Bright saturated rim glow, dark center. */
    NEON,

    /** Soft inner shadow + top highlight, simulating a pressed/embossed metal disc. */
    EMBOSSED,
}

enum class IconShapePreset {
    CIRCLE,
    SQUIRCLE,
    SYSTEM,
}
