package net.typedrest.links

import okhttp3.Response

/**
 * Extracts links from HTTP headers.
 */
class HeaderLinkExtractor : LinkExtractor {
    companion object {
        private val regexHeaderLinks = Regex("""<[^>]*>\s*(;\s*[^()<>@,;:"/\[\]?={} \t]+=(([^()<>@,;:"/\[\]?={} \t]+)|("[^"]*")))*(,|$)""")
        private val regexLinkFields = Regex("""[^()<>@,;:"/\[\]?={} \t]+=(([^()<>@,;:"/\[\]?={} \t]+)|("[^"]*"))""")
    }

    override fun getLinks(response: Response): List<Link> =
        response.headers("Link")
            .flatMap { headerValue -> regexHeaderLinks.findAll(headerValue).toList() }
            .map { parseLink(it.value) }
            .toList()

    private fun parseLink(value: String): Link {
        val split = value.split('>', limit = 2)
        val href = split[0].substring(1)
        var rel: String? = null
        var title: String? = null
        var templated = false

        regexLinkFields.findAll(split[1]).forEach { matchResult ->
            val param = matchResult.groups.first()!!.value
            val paramSplit = param.split('=', limit = 2)
            if (paramSplit.size != 2) return@forEach
            when (paramSplit[0]) {
                "rel" -> rel = paramSplit[1]
                "title" -> title =
                    if (paramSplit[1].startsWith("\"") && paramSplit[1].endsWith("\"")) {
                        paramSplit[1].substring(1, paramSplit[1].length - 2)
                    } else paramSplit[1]
                "templated" -> templated = paramSplit[1].toBoolean()
            }
        }

        return Link(
            rel ?: throw IllegalArgumentException("The link header is lacking the mandatory 'rel' field."),
            href,
            title,
            templated
        )
    }

    private fun String.unquote() = removeSurrounding("\"")
}
