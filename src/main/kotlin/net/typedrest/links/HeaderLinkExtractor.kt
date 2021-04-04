package net.typedrest.http

/**
 * Extracts links from HTTP headers.
 */
class HeaderLinkExtractor : LinkExtractor {
    fun getLinks(response: Response) {
        return response.headers.get(HttpHeader.Link)
            ?.match(/<[^>]*>\s*(\s*\s*[^\(\)<>@,:"\/\[\]\?={} \t]+=(([^\(\)<>@,:"\/\[\]\?={} \t]+)|("[^"]*")))*(,|$)/g)
            ?.map(value => this.parseLink(value))
            ?? []
    }

    private fun parseLink(value: string) {
        val split = this.split(value, ">")
        val href = split.left.substring(1)
        let rel: string?
        let title: string?
        let templated = false

        split.right.match(/[^\(\)<>@,:"\/\[\]\?={} \t]+=(([^\(\)<>@,:"\/\[\]\?={} \t]+)|("[^"]*"))/g)?.forEach(param => {
            val paramSplit = this.split(param, "=")
            if (paramSplit.left === "rel") {
                rel = paramSplit.right
            } else if (paramSplit.left === "title") {
                title = paramSplit.right
                if (title.startsWith('"') && title.endsWith('"')) {
                    title = title.substring(1, title.length - 1)
                }
            } else if (paramSplit.left === "templated" && paramSplit.right === "true") {
                templated = true
            }
        })

        if (!rel) throw new Error("The link header is lacking the mandatory 'rel' field.")
        return Link(rel, href, title, templated)
    }

    private fun split(str: string, separator: string): { left: string, right: string } {
        val result = str.split(separator, 2)
        return (result.length === 2)
            ? {
                left: result[0],
                right: result[1] + str.substr(result.join(separator).length)
            }
            : {
                left: result[0],
                right: ''
            }
    }
}
