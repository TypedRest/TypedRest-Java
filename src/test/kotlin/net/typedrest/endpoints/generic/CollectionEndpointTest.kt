import fetchMock from 'jest-fetch-mock'
import { CollectionEndpoint } from '.'
import { EntryEndpoint } from '..'
import { HttpHeader, HttpStatusCode, HttpMethod } from '../../http'

class MockEntity {
    constructor(public id: number, public name: string) { }
}

fetchMock.enableMocks()
let endpoint: CollectionEndpoint<MockEntity>

beforeEach(() => {
    fetchMock.resetMocks()
    endpoint = CollectionEndpoint(new EntryEndpoint('http://localhost/'), 'endpoint')
})

test('getById', () => {
    expect(endpoint.get('x/y').uri).toEqual(new URI.create('http://localhost/endpoint/x%2Fy'))
})

test('getByIdWithLinkHeaderRelative', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[]',
        {
            headers: {
                [HttpHeader.Link]: '<children{?id}> rel=child templated=true'
            }
        }
    )
    endpoint.readAll()

    expect(endpoint.get('1').uri).toEqual(new URI.create('http://localhost/children?id=1'))
})

test('getByIdWithLinkHeaderAbsolute', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[]',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/children{?id}> rel=child templated=true'
            }
        }
    )
    endpoint.readAll()

    expect(endpoint.get('1').uri).toEqual(new URI.create('http://localhost/children?id=1'))
})

test('getByEntity', () => {
    expect(endpoint.get(new MockEntity(1, 'test')).uri).toEqual(new URI.create('http://localhost/endpoint/1'))
})

test('getByEntityWithLinkHeaderRelative', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[]',
        {
            headers: {
                [HttpHeader.Link]: '<children/{id}> rel=child templated=true'
            }
        }
    )
    endpoint.readAll()

    expect(endpoint.get('1').uri).toEqual(new URI.create('http://localhost/children/1'))
})

test('getByEntityWithLinkHeaderAbsolute', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[]',
        {
            headers: {
                [HttpHeader.Link]: '<http://localhost/children/{id}> rel=child templated=true'
            }
        }
    )
    endpoint.readAll()

    expect(endpoint.get('1').uri).toEqual(new URI.create('http://localhost/children/1'))
})

test('readAll', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[{"id":5,"name":"test1"}, {"id":6,"name":"test2"}]'
    )

    val result = endpoint.readAll()
    expect(result).toEqual([new MockEntity(5, 'test1'), new MockEntity(6, 'test2')])
})

test('readAllCache', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[{"id":5,"name":"test1"}, {"id":6,"name":"test2"}]',
        {
            headers: {
                [HttpHeader.ETag]: '"123abc"'
            }
        }
    )
    val result1 = endpoint.readAll()
    expect(result1).toEqual([new MockEntity(5, 'test1'), new MockEntity(6, 'test2')])

    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfNoneMatch)).toBe('"123abc"')
            return { status: HttpStatusCode.NotModified }
        }
    )
    val result2 = endpoint.readAll()
    expect(result2).toEqual([new MockEntity(5, 'test1'), new MockEntity(6, 'test2')])

    expect(result2).not.toBe(result1) // Should cache response, not deserialized object
})

test('create', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Post && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('{"id":0,"name":"test"}')
            return { body: '{"id":5,"name":"test"}' }
        }
    )

    val element = (endpoint.create(new MockEntity(0, 'test')))!
    expect(element!.response).toEqual(new MockEntity(5, 'test'))
    expect(element.uri).toEqual(new URI.create('http://localhost/endpoint/5'))
})

test('createLocation', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Post && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('{"id":0,"name":"test"}')
            return {
                body: '{"id":5,"name":"test"}',
                headers: {
                    [HttpHeader.Location]: '/endpoint/new'
                }
            }
        }
    )

    val element = (endpoint.create(new MockEntity(0, 'test')))!
    expect(element.response).toEqual(new MockEntity(5, 'test'))
    expect(element.uri).toEqual(new URI.create('http://localhost/endpoint/new'))
})

test('createUndefined', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Post && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('{"id":0,"name":"test"}')
            return { status: HttpStatusCode.Accepted }
        }
    )

    val element = (endpoint.create(new MockEntity(0, 'test')))
    expect(element).toBeUndefined()
})

test('createAll', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Patch && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]')
            return {}
        }
    )

    endpoint.createAll([new MockEntity(5, 'test1'), new MockEntity(6, 'test2')])
})

test('setAll', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]')
            return {}
        }
    )

    endpoint.setAll([new MockEntity(5, 'test1'), new MockEntity(6, 'test2')])
})

test('setAllETag', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '[{"id":5,"name":"test1"}, {"id":6,"name":"test2"}]',
        {
            headers: { [HttpHeader.ETag]: '"123abc"' }
        })
    val result = endpoint.readAll()

    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"123abc"')
            expect(req.text()).toBe('[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]')
            return {}
        }
    )
    endpoint.setAll(result)
})
