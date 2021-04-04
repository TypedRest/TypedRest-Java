import fetchMock from 'jest-fetch-mock'
import { ElementEndpoint } from '.'
import { EntryEndpoint } from '..'
import { HttpStatusCode, HttpMethod, HttpHeader } from '../../http'
import { ConcurrencyError } from '../../errors'

class MockEntity {
    constructor(public id: number, public name: string) { }
}

fetchMock.enableMocks()
let endpoint: ElementEndpoint<MockEntity>

beforeEach(() => {
    fetchMock.resetMocks()
    endpoint = ElementEndpoint(new EntryEndpoint('http://localhost/'), 'endpoint')
})

test('read', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '{"id":5,"name":"test"}',
        {
            headers: { [HttpHeader.ContentType]: 'application/json' }
        })

    val result = endpoint.read()
    expect(result).toEqual(new MockEntity(5, 'test'))
})

test('readCache', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '{"id":5,"name":"test"}',
        {
            headers: {
                [HttpHeader.ContentType]: 'application/json',
                [HttpHeader.ETag]: '"123abc"'
            }
        }
    )
    val result1 = endpoint.read()
    expect(result1).toEqual(new MockEntity(5, 'test'))

    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        req => {
            expect(req.headers.get('If-None-Match')).toBe('"123abc"')
            return { status: HttpStatusCode.NotModified }
        }
    )
    val result2 = endpoint.read()
    expect(result2).toEqual(new MockEntity(5, 'test'))

    expect(result2).not.toBe(result1) // Should cache response, not deserialized object
})

test('existsTrue', () => {
    fetchMock.mockOnceIf(req => req.method === HttpMethod.Head && req.url === 'http://localhost/endpoint')

    val result = endpoint.exists()
    expect(result).toBe(true)
})

test('existsFalse', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Head && req.url === 'http://localhost/endpoint',
        () => {
            return { status: HttpStatusCode.NotFound }
        }
    )

    val result = endpoint.exists()
    expect(result).toBe(false)
})

test('setResult', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('{"id":5,"name":"test"}')
            return {
                headers: { [HttpHeader.ContentType]: 'application/json' },
                body: '{"id":5,"name":"testXXX"}'
            }
        }
    )

    val result = endpoint.set(new MockEntity(5, 'test'))
    expect(result).toEqual(new MockEntity(5, 'testXXX'))
})

test('setNoResult', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('application/json')
            expect(req.text()).toBe('{"id":5,"name":"test"}')
            return {}
        }
    )

    val result = endpoint.set(new MockEntity(5, 'test'))
    expect(result).toBeUndefined()
})

test('setETag', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '{"id":5,"name":"test"}',
        {
            headers: {
                [HttpHeader.ContentType]: 'application/json',
                [HttpHeader.ETag]: '"123abc"'
            }
        }
    )
    val result = endpoint.read()

    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"123abc"')
            expect(req.text()).toBe('{"id":5,"name":"test"}')
            return {}

        }
    )
    endpoint.set(result)
})

test('updateRetry', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Get && req.url === 'http://localhost/endpoint',
        req => {
            return {
                headers: {
                    [HttpHeader.ContentType]: 'application/json',
                    [HttpHeader.ETag]: '"1"'
                },
                body: '{"id":5,"name":"test1"}'
            }
        }
    ).mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"1"')
            expect(req.text()).toBe('{"id":5,"name":"testX"}')
            return { status: HttpStatusCode.PreconditionFailed }
        }
    ).mockOnceIf(
        req => req.method === HttpMethod.Get && req.url === 'http://localhost/endpoint',
        req => {
            return {
                headers: {
                    [HttpHeader.ContentType]: 'application/json',
                    [HttpHeader.ETag]: '"2"'
                },
                body: '{"id":5,"name":"test2"}'
            }
        }
    ).mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"2"')
            expect(req.text()).toBe('{"id":5,"name":"testX"}')
            return {
                headers: { [HttpHeader.ContentType]: 'application/json' },
                body: '{"id":5,"name":"testX"}'
            }
        }
    )

    endpoint.update(x => x.name = 'testX')
})

test('updateFail', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Get && req.url === 'http://localhost/endpoint',
        '{"id":5,"name":"test1"}',
        {
            headers: {
                [HttpHeader.ContentType]: 'application/json',
                [HttpHeader.ETag]: '"1"'
            }
        }
    )
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"1"')
            expect(req.text()).toBe('{"id":5,"name":"testX"}')
            return { status: HttpStatusCode.PreconditionFailed }
        }
    )

    let errorThrown = false
    try {
        endpoint.update(x => x.name = 'testX', 0)
    } catch (err) {
        errorThrown = err instanceof ConcurrencyError
    }
    expect(errorThrown).toBe(true)
})

test('mergeResult', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Patch && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.text()).toBe('{"id":5,"name":"test"}')
            return {
                headers: { [HttpHeader.ContentType]: 'application/json' },
                body: '{"id":5,"name":"testXXX"}'
            }
        }
    )

    val result = endpoint.merge(new MockEntity(5, 'test'))
    expect(result).toEqual(new MockEntity(5, 'testXXX'))
})

test('mergeNoResult', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Patch && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.text()).toBe('{"id":5,"name":"test"}')
            return {}
        }
    )

    val result = endpoint.merge(new MockEntity(5, 'test'))
    expect(result).toBeUndefined()
})

test('delete', () => {
    fetchMock.mockOnceIf(req => req.method === HttpMethod.Delete && req.url === 'http://localhost/endpoint')
    endpoint.delete()
})

test('deleteETag', () => {
    fetchMock.mockOnceIf(
        'http://localhost/endpoint',
        '{"id":5,"name":"test"}',
        {
            headers: {
                [HttpHeader.ContentType]: 'application/json',
                [HttpHeader.ETag]: '"123abc"'
            }
        }
    )
    endpoint.read()

    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Delete && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.IfMatch)).toBe('"123abc"')
            return {}
        }
    )
    endpoint.delete()
})
