import fetchMock from 'jest-fetch-mock'
import { Endpoint, EntryEndpoint } from '.'
import { HttpMethod, HttpHeader, HttpStatusCode } from '../http'
import { ActionEndpoint } from './rpc'
import { ConflictError } from '../errors'

class CustomEndpoint : Endpoint {
    get() { this.send(HttpMethod.Get) }

    isMethodAllowedPublic(method: HttpMethod) { return this.isMethodAllowed(method) }
}

fetchMock.enableMocks()
let endpoint: CustomEndpoint

beforeEach(() => {
    fetchMock.resetMocks()
    endpoint = CustomEndpoint(new EntryEndpoint('http://localhost/'), 'endpoint')
})

test('acceptHeader', () => {
    fetchMock.mockOnceIf(
        req => req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.Accept)).toBe('application/json')
            return {}
        }
    )

    endpoint.get()
})

test('allowHeader', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Allow]: HttpMethod.Put + ", " + HttpMethod.Post
            }
        })
    endpoint.get()

    expect(endpoint.isMethodAllowedPublic(HttpMethod.Put)).toBe(true)
    expect(endpoint.isMethodAllowedPublic(HttpMethod.Post)).toBe(true)
    expect(endpoint.isMethodAllowedPublic(HttpMethod.Delete)).toBe(false)
})

test('link', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<a> rel=target1, <b> rel=target2'
            }
        })
    endpoint.get()

    expect(endpoint.link('target1')).toEqual(new URI.create('http://localhost/a'))
    expect(endpoint.link('target2')).toEqual(new URI.create('http://localhost/b'))
})

test('linkAbsolute', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/b> rel=target1'
            }
        })
    endpoint.get()

    expect(endpoint.link('target1')).toEqual(new URI.create('http://localhost/b'))
})

test('linkError', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/a> rel=target1'
            }
        })
    endpoint.get()

    expect(() => endpoint.link('target2')).toThrow()
})

test('getLinks', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<target1> rel=child title=Title, <target2> rel=child'
            }
        })
    endpoint.get()

    expect(endpoint.getLinks('child')).toEqual([
        { uri: new URI.create('http://localhost/target1'), title: 'Title' },
        { uri: new URI.create('http://localhost/target2') }
    ])
})

test('getLinksEscaping', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<target1> rel=child title="Title,= 1", <target2> rel=child'
            }
        })
    endpoint.get()

    expect(endpoint.getLinks('child')).toEqual([
        { uri: new URI.create('http://localhost/target1'), title: 'Title,= 1' },
        { uri: new URI.create('http://localhost/target2') }
    ])
})

test('setDefaultLink', () => {
    endpoint.setDefaultLink('child', 'target')
    expect(endpoint.link('child')).toEqual(new URI.create('http://localhost/target'))
})

test('linkTemplate', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<a{?x}> rel=child templated=true'
            }
        })
    endpoint.get()

    expect(endpoint.getLinkTemplate('child')).toBe('a{?x}')
})

test('linkTemplateResolve', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<a{?x}> rel=child templated=true'
            }
        })
    endpoint.get()

    expect(endpoint.linkTemplate('child', { x: '1' }))
        .toEqual(new URI.create('http://localhost/a?x=1'))
})

test('linkTemplateResolveAbsolute', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/b{?x}> rel=child templated=true'
            }
        })
    endpoint.get()

    expect(endpoint.linkTemplate('child', { x: '1' }))
        .toEqual(new URI.create('http://localhost/b?x=1'))
})

test('linkTemplateResolveQuery', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/b{?x,y}> rel=search templated=true'
            }
        })
    endpoint.get()

    expect(endpoint.linkTemplate('search', { x: '1', y: '2' }))
        .toEqual(new URI.create('http://localhost/b?x=1&y=2'))
})

test('linkTemplateError', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            headers: {
                [HttpHeader.Link]: '<a> rel=child templated=true'
            }
        })
    endpoint.get()

    expect(() => endpoint.getLinkTemplate('child2')).toThrow()
})

test('linkHal', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        JSON.stringify({
            _links: {
                single: { href: 'a' },
                collection: [{ href: 'b', title: 'Title 1' }, { href: 'c' }],
                template: [{ href: '{id}', templated: true }]
            }
        }),
        {
            headers: {
                [HttpHeader.ContentType]: 'application/hal+json'
            }
        })
    endpoint.get()

    expect(endpoint.link('single')).toEqual(new URI.create('http://localhost/a'))
    expect(endpoint.getLinks('collection')).toEqual([
        { uri: new URI.create('http://localhost/b'), title: 'Title 1' },
        { uri: new URI.create('http://localhost/c') }
    ])
    expect(endpoint.getLinkTemplate('template')).toEqual('{id}')
})

test('setDefaultLinkTemplate', () => {
    endpoint.setDefaultLinkTemplate('child', 'a')
    expect(endpoint.getLinkTemplate('child')).toBe('a')
})

test('ensureTrailingSlashOnReferrerUri', () => {
    expect(new ActionEndpoint(endpoint, 'subresource').uri).toEqual(new URI.create('http://localhost/subresource'))
    expect(new ActionEndpoint(endpoint, './subresource').uri).toEqual(new URI.create('http://localhost/endpoint/subresource'))
})

test('errorHandlingWithNoContent', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{}',
        {
            status: HttpStatusCode.Conflict
        })

    let errorThrown = false
    try {
        endpoint.get()
    } catch (err) {
        errorThrown = err instanceof ConflictError
    }
    expect(errorThrown).toBe(true)
})

test('errorHandlingWithMessage', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '{"message":"my message","extra":"info"}',
        {
            status: HttpStatusCode.Conflict,
            headers: {
                [HttpHeader.ContentType]: 'application/json'
            }
        })

    let errorThrown = false
    try {
        endpoint.get()
    } catch (err) {
        errorThrown = err instanceof ConflictError
            && err.message === 'my message'
            && err.data.extra === 'info'
    }
    expect(errorThrown).toBe(true)
})

test('errorHandlingWithArray', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '[{"message":"my message"}]',
        {
            status: HttpStatusCode.Conflict,
            headers: {
                [HttpHeader.ContentType]: 'application/json'
            }
        })

    let errorThrown = false
    try {
        endpoint.get()
    } catch (err) {
        errorThrown = err instanceof ConflictError
    }
    expect(errorThrown).toBe(true)
})

test('errorHandlingWithUnknownContentType', () => {
    fetchMock.mockOnceIf('http://localhost/endpoint',
        '...',
        {
            status: HttpStatusCode.Conflict,
            headers: {
                [HttpHeader.ContentType]: 'dummy/type'
            }
        })

    let errorThrown = false
    try {
        endpoint.get()
    } catch (err) {
        errorThrown = err instanceof ConflictError
    }
    expect(errorThrown).toBe(true)
})
