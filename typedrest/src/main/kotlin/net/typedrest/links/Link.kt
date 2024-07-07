package net.typedrest.links

/**
 * Represents a link to another resource.
 * @param rel The relation type of the link.
 * @param href The href/target of the link.
 * @param title The title of the link (optional).
 * @param templated  Indicates whether the link is an URI Template (RFC 6570).
 */
class Link(
    val rel: String,
    val href: String,
    val title: String? = null,
    val templated: Boolean = false
)
