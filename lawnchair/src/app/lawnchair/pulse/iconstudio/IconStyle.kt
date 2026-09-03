package app.lawnchair.pulse.iconstudio

/**
 * Post-processing styles applied on top of the shape mask by [IconRenderer].
 */
enum class IconStyle {
    /** No post-processing beyond the shape mask itself. */
    FLAT,

    /** iOS liquid glass: bright top specular rim + darkened bottom edge. */
    LIQUID_GLASS,

    /** Bright saturated rim glow, dark center. */
    NEON,

    /** Soft inner shadow + top highlight, simulating an embossed disc. */
    EMBOSSED,

    /** High-contrast dual-color palette mapped to icon luminance. */
    DUOTONE,

    /** Animated iridescent color-shifting shimmer effect. */
    HOLOGRAPHIC,

    /** Tactile analog film noise texture overlay. */
    FILM_GRAIN,

    /** Material You dynamic tonal extraction matching active palette. */
    MATERIAL_YOU,
}

enum class IconShapePreset {
    CIRCLE,
    SQUIRCLE,
    SYSTEM,
}
